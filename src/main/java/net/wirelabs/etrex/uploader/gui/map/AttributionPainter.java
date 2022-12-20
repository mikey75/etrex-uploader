package net.wirelabs.etrex.uploader.gui.map;

import org.jxmapviewer.JXMapViewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.EnumMap;
import java.util.Map;

/*
 * Created 12/20/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class AttributionPainter {

    private final String attribution;
    private final FontMetrics fontMetrics;
    private final int attributionWidth;
    private final int attributionHeight;
    private final Map<MapAttributionPosition,Point> map = new EnumMap<>(MapAttributionPosition.class);



    public AttributionPainter(Graphics g, JXMapViewer viewer) {

        attribution = "Map: " + viewer.getTileFactory().getInfo().getAttribution() +
                "  Licence: " + viewer.getTileFactory().getInfo().getLicense();
        // calculate text font metrics and text bounds
        g.setFont(new Font("Dialog", Font.BOLD, 10));
        fontMetrics = g.getFontMetrics();
        Rectangle2D textBounds = fontMetrics.getStringBounds(attribution, g);

        int margin = 2;
        attributionWidth = (int) textBounds.getWidth() + margin;
        attributionHeight = (int) textBounds.getHeight() + margin;

        map.put(MapAttributionPosition.NORTH_EAST, new Point(viewer.getWidth() - attributionWidth - 2, 2));
        map.put(MapAttributionPosition.NORTH_WEST, new Point(2, 2));
        map.put(MapAttributionPosition.SOUTH_EAST, new Point(viewer.getWidth() - attributionWidth - 2, viewer.getHeight() - attributionHeight - 2));
        map.put(MapAttributionPosition.SOUTH_WEST, new Point(2, viewer.getHeight() - attributionHeight - 2));
    }

    void paint(Graphics graphics) {
        graphics.setFont(new Font("Dialog", Font.BOLD, 10));
        // apply position
        Point position = map.get(MapAttributionPosition.NORTH_WEST);
        // draw container frame
        graphics.setColor(Color.WHITE);
        graphics.fillRect(position.x, position.y, attributionWidth, attributionHeight);
        graphics.setColor(Color.black);
        graphics.drawRect(position.x, position.y, attributionWidth, attributionHeight);
        // paint string
        graphics.setColor(Color.BLACK);
        graphics.drawString(attribution, position.x, position.y + fontMetrics.getAscent());
    }
}
