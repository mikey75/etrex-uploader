package net.wirelabs.etrex.uploader.gui.map.custom;

import org.junit.jupiter.api.Test;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created 1/2/23 by Michał Szwaczko (mikey@wirelabs.net)
 */
class GeoPortalTileFactoryTest {
    GeoportalMapFactoryInfo info = new GeoportalMapFactoryInfo();
    GeoPortalTileFactory f = new GeoPortalTileFactory(info);
    GeoPosition lublin = new GeoPosition(51.246452, 22.568445);

    @Test
    void t(){
        Point2D d = f.geoToPixel(lublin,7);
        double x = d.getX();
        double y = d.getY();
        GeoPosition p = f.pixelToGeo(d,7);
    }


}