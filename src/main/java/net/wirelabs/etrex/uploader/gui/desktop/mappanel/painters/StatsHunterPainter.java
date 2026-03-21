package net.wirelabs.etrex.uploader.gui.desktop.mappanel.painters;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.statshunters.QuadVertexPolygon;
import net.wirelabs.etrex.uploader.statshunters.StatsHuntersHelper;
import net.wirelabs.etrex.uploader.statshunters.model.Square;
import net.wirelabs.etrex.uploader.statshunters.model.Tile;
import net.wirelabs.etrex.uploader.statshunters.model.TileData;
import net.wirelabs.jmaps.map.MapViewer;
import net.wirelabs.jmaps.map.painters.Painter;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StatsHunterPainter extends Painter<MapViewer> {

    private final AppConfiguration configuration;

    private List<QuadVertexPolygon> allClusters = new ArrayList<>();
    private List<QuadVertexPolygon> allSquares = new ArrayList<>();
    private QuadVertexPolygon maxSquare;

    private final StatsHuntersHelper statsHuntersHelper = new StatsHuntersHelper();

    public StatsHunterPainter(AppConfiguration configuration) {
        this.configuration = configuration;

        if (isConfigured()) {

            String json = statsHuntersHelper.getStatsHuntersJson(configuration.getStatsHuntersUrl());
            if (!json.isEmpty()) {
                try {
                    TileData tiledata = new Gson().fromJson(json, TileData.class);

                    // prepare drawing sets
                    List<Tile> allTiles = tiledata.getTiles();
                    List<Tile> cluster = tiledata.getCluster();
                    List<Tile> restCluster = tiledata.getRestCluster();
                    Square square = tiledata.getSquare();

                    // all tiles minus clusters - to first draw tiles that would never be overpainted by another tile
                    allTiles.removeAll(cluster);
                    allTiles.removeAll(restCluster);
                    // now get all clusters (clusters and restclusters)
                    cluster.addAll(restCluster);

                    allSquares = statsHuntersHelper.convertTilesToGeoSquares(allTiles);
                    allClusters = statsHuntersHelper.convertTilesToGeoSquares(cluster);
                    maxSquare = statsHuntersHelper.convertSquareToGeoMaxSquare(square);

                    log.info("[StatsHunters] Initialization finished");
                } catch (JsonSyntaxException ex) {
                    log.error("[StatsHunters] Exception trying to parse provided json {}", ex.getMessage());
                }
            }
        }

    }

    @Override
    public void doPaint(Graphics2D graphics, MapViewer mapViewer, int width, int height) {

        if (!allSquares.isEmpty()) {
            // store changed settings
            Stroke s = graphics.getStroke();
            Color color = graphics.getColor();

            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

            // draw all squares
            graphics.setColor(new Color(254, 78, 75, 100));
            graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            drawSquares(graphics, mapViewer);

            // draw max square
            graphics.setColor(new Color(110, 162, 232, 100));
            graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            drawMaxSquare(graphics, mapViewer);

            // draw clusters
            graphics.setColor(new Color(78, 164, 85, 100));
            graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            drawClusters(graphics, mapViewer);

            // restore changed settings
            graphics.setColor(color);
            graphics.setStroke(s);

        }
    }

    private void drawMaxSquare(Graphics2D graphics, MapViewer map) {

        // first fill polygon of max square
        drawGeoSquare(graphics, map, maxSquare, false);
        graphics.setColor(Color.GRAY);
        // now just don't fill, just draw square with 3px gray line
        // this would give a nice border to max square
        graphics.setStroke(new BasicStroke(3));
        drawGeoSquare(graphics, map, maxSquare, true);

    }

    private void drawClusters(Graphics2D graphics, MapViewer map) {
        for (QuadVertexPolygon tile : allClusters) {
            boolean inside = statsHuntersHelper.isSquareInsideMaxSquare(tile, maxSquare);
            drawGeoSquare(graphics, map, tile, inside);
        }

    }

    private void drawSquares(Graphics2D graphics, MapViewer map) {
        for (QuadVertexPolygon square : allSquares) {
            boolean inside = statsHuntersHelper.isSquareInsideMaxSquare(square, maxSquare);
            drawGeoSquare(graphics, map, square, inside);

        }
    }

    private static void drawGeoSquare(Graphics2D graphics, MapViewer map, QuadVertexPolygon square, boolean shouldFill) {
        if (square != null) {
            Point2D topLeft = map.getCurrentMap().getBaseLayer().latLonToPixel(square.topLeft(), map.getZoom());
            Point2D bottomRight = map.getCurrentMap().getBaseLayer().latLonToPixel(square.bottomRight(), map.getZoom());
            Point2D topRight = map.getCurrentMap().getBaseLayer().latLonToPixel(square.topRight(), map.getZoom());
            Point2D bottomLeft = map.getCurrentMap().getBaseLayer().latLonToPixel(square.bottomLeft(), map.getZoom());

            // adapt world pixels to visible screen
            topLeft.setLocation(topLeft.getX() - map.getTopLeftCornerPoint().x, topLeft.getY() - map.getTopLeftCornerPoint().y);
            topRight.setLocation(topRight.getX() - map.getTopLeftCornerPoint().x, topRight.getY() - map.getTopLeftCornerPoint().y);
            bottomLeft.setLocation(bottomLeft.getX() - map.getTopLeftCornerPoint().x, bottomLeft.getY() - map.getTopLeftCornerPoint().y);
            bottomRight.setLocation(bottomRight.getX() - map.getTopLeftCornerPoint().x, bottomRight.getY() - map.getTopLeftCornerPoint().y);

            int[] xs = {(int) topLeft.getX(), (int) topRight.getX(), (int) bottomRight.getX(), (int) bottomLeft.getX()};
            int[] ys = {(int) topLeft.getY(), (int) topRight.getY(), (int) bottomRight.getY(), (int) bottomLeft.getY()};

            // we draw polygons instead of squares , because the tile is not square in non-WebMercator projection
            Polygon p = new Polygon(xs, ys, 4);
            if (!shouldFill) {
                graphics.fillPolygon(p);
            }
            graphics.drawPolygon(p);

        }
    }

    public boolean isConfigured() {
        return !configuration.getStatsHuntersUrl().isEmpty();
    }
}
