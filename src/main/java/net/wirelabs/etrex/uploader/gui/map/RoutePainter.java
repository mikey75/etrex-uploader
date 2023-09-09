package net.wirelabs.etrex.uploader.gui.map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Paints a route
 *
 * @author Martin Steiger
 */
@Slf4j
public class RoutePainter implements Painter<JXMapViewer> {

    @Setter
    private static Color color = Color.RED;

    @Getter
    @Setter
    private List<GeoPosition> track;
    private BufferedImage startFlagIcon;
    private BufferedImage endFlagIcon;

    public RoutePainter(AppConfiguration configuration) {
        this(new ArrayList<>(), configuration);
    }

    /**
     * @param track the track
     */
    public RoutePainter(List<GeoPosition> track, AppConfiguration configuration) {
        color = Color.decode(configuration.getMapTrackColor());
        // copy the list so that changes in the 
        // original list do not have an effect here
        setTrack(track);
        // prepare flag gfx
        try {
            startFlagIcon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/icons/gpx/start-point.png")));
            endFlagIcon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/icons/gpx/end-point.png")));
        } catch (Exception e) {
            log.info("Failed to load icons for start or finish {}", e.getMessage());
        }
    }

    @Override
    public void paint(Graphics2D graphicsContext, JXMapViewer map, int width, int height) {
        if (!track.isEmpty()) {
            graphicsContext = (Graphics2D) graphicsContext.create();

            // convert from viewport to world bitmap
            Rectangle rect = map.getViewportBounds();
            graphicsContext.translate(-rect.x, -rect.y);

            graphicsContext.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            graphicsContext.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // do the drawing
            graphicsContext.setColor(color);
            graphicsContext.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            drawRoute(graphicsContext, map);

            graphicsContext.dispose();
        }

    }

    /**
     * @param graphicsContext the graphics object
     * @param map             the map
     */
    private void drawRoute(Graphics2D graphicsContext, JXMapViewer map) {

        int lastX = 0;
        int lastY = 0;

        boolean firstPoint = true;

        for (GeoPosition gp : track) {
            // convert geo-coordinate to world bitmap pixel
            Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());

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

    private void paintStartAndFinishIcons(Graphics2D graphicsContext, JXMapViewer map) {
            GeoPosition startPosition = track.get(0);
            GeoPosition endPosition = track.get(track.size() - 1);

            paintFlag(graphicsContext, map, startPosition, startFlagIcon);
            paintFlag(graphicsContext, map, endPosition, endFlagIcon);

    }

    private void paintFlag(Graphics2D graphicsContext,
                           JXMapViewer map,
                           GeoPosition position,
                           BufferedImage image) {

        if (image != null) {

            Point2D point = map.getTileFactory().geoToPixel(position, map.getZoom());

            int x = (int) point.getX();
            int y = (int) point.getY() - image.getHeight();

            graphicsContext.drawImage(image, x, y, null);
        }
    }


}