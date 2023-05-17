package net.wirelabs.etrex.uploader.gui.map.custom;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.geom.Point2D;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created 1/2/23 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
class GeoPortalTileFactoryTest {

    GeoportalTopoBaseFactoryInfo info = new GeoportalTopoBaseFactoryInfo();
    GeoportalTopoBaseFactory f = new GeoportalTopoBaseFactory(info);


    @Test
    void testConvertGeoPositionToPixelAndBack() {

        GeoPosition lublin = new GeoPosition(51.246452, 22.568445);
        GeoPosition meters = new GeoPosition(381577.4161435962,748958.0038486815);

        for (int zoom = 1; zoom < 14; zoom++) {
            Point2D d = f.geoToPixel(lublin, zoom);
            GeoPosition p = f.pixelToGeo(d, zoom);
            assertThat(p.getLongitude()).isCloseTo(lublin.getLongitude(), Offset.offset(0.2));
            assertThat(p.getLatitude()).isCloseTo(lublin.getLatitude(), Offset.offset(0.2));
            log.info("Zoom {}: {}-{}", zoom, p.getLatitude(), lublin.getLatitude());
        }

    }


}