package net.wirelabs.etrex.uploader.gui.map;


import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.jmaps.map.MapViewer;
import net.wirelabs.jmaps.map.geo.Coordinate;
import net.wirelabs.jmaps.map.painters.Painter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created 6/8/23 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class RoutePainter extends Painter<MapViewer> {

    private Color routeColor = Color.RED;
    private BufferedImage startFlagIcon;
    private BufferedImage endFlagIcon;

    public void setColor(Color color) {
        routeColor = color;
    }

    public RoutePainter(AppConfiguration configuration) {
        try {
            routeColor = Color.decode(configuration.getMapTrackColor());
            startFlagIcon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/icons/gpx-start-point.png")));
            endFlagIcon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/icons/gpx-end-point.png")));
        } catch (IOException e) {
            log.info("Failed to load icons for start or finish {}", e.getMessage());
        } catch (NumberFormatException nfe) {
            log.info("Unparsable color, setting red");
            routeColor = Color.RED;
        }
    }

    @Override
    public void doPaint(Graphics2D graphics, MapViewer mapViewer, int width, int height) {
        if (!getObjects().isEmpty()) {
            // store changed settings
            Stroke s = graphics.getStroke();
            Color color = graphics.getColor();
            // not sure if needed
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            // do the drawing
            graphics.setColor(routeColor);
            graphics.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            drawRoute(graphics, mapViewer);
            // restore changed settings
            graphics.setColor(color);
            graphics.setStroke(s);

        }
    }



    public void clearRoute() {
        getObjects().clear();
    }
    
    private void drawRoute(Graphics2D graphicsContext, MapViewer map) {

        int lastX = 0;
        int lastY = 0;

        boolean firstPoint = true;

        for (Coordinate gp : getObjects()) {
            // convert geo-coordinate to world bitmap pixel
            Point2D pt = map.getCurrentMap().getBaseLayer().latLonToPixel(gp, map.getZoom());
            //!!!! trzeba odjac topleftcorner zeby uzyskac pixel na aktualnym g canvas !!!
            pt.setLocation(pt.getX() - map.getTopLeftCornerPoint().x, pt.getY() - map.getTopLeftCornerPoint().y);

            if (firstPoint) {
                firstPoint = false;
            } else {
                graphicsContext.drawLine(lastX, lastY, (int) pt.getX(), (int) pt.getY());
            }

            lastX = (int) pt.getX();
            lastY = (int) pt.getY();
        }
        paintStartAndFinishIcons(graphicsContext, map);
    }

    private void paintStartAndFinishIcons(Graphics2D graphicsContext, MapViewer map) {
        Coordinate startPosition = getObjects().get(0);
        Coordinate endPosition = getObjects().get(getObjects().size() - 1);

        paintFlag(graphicsContext, map, startPosition, startFlagIcon);
        paintFlag(graphicsContext, map, endPosition, endFlagIcon);
    }

    private void paintFlag(Graphics2D graphicsContext,
                           MapViewer map,
                           Coordinate position,
                           BufferedImage image) {

        if (image != null) {

            Point2D point = map.getCurrentMap().getBaseLayer().latLonToPixel(position, map.getZoom());
            point.setLocation(point.getX() - map.getTopLeftCornerPoint().x, point.getY() - map.getTopLeftCornerPoint().y);

            int x = (int) point.getX();
            int y = (int) point.getY() - image.getHeight();

            graphicsContext.drawImage(image, x, y, null);
        }
    }

    public void setRoute(List<Coordinate> route) {
        setObjects(route);
    }

}
