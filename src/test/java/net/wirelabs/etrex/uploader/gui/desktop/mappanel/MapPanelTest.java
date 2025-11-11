package net.wirelabs.etrex.uploader.gui.desktop.mappanel;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.EtrexUploader;
import net.wirelabs.etrex.uploader.gui.desktop.mappanel.common.RoutePainter;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.eventbus.EventBus;
import net.wirelabs.jmaps.map.MapViewer;
import net.wirelabs.jmaps.map.cache.db.DBCache;
import net.wirelabs.jmaps.map.cache.files.DirectoryBasedCache;
import net.wirelabs.jmaps.map.geo.Coordinate;
import net.wirelabs.jmaps.map.painters.Painter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.awt.*;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Slf4j
class MapPanelTest extends BaseTest {

    private static MapPanel mp;
    private static AppConfiguration appConfiguration;
    private static final File map1 = new File("src/test/resources/config/maps/goodMapDef.xml");
    private static final File map2 = new File("src/test/resources/config/maps/otherMap.xml");
    private static final List<File> maps = List.of(map1,map2);

    @BeforeEach
    void before() {

        try (MockedStatic<SwingUtils> swingUtils = Mockito.mockStatic(SwingUtils.class);
             MockedStatic<EtrexUploader> ex = mockStatic(EtrexUploader.class)) {
            swingUtils.when(() -> SwingUtils.errorMsg("No maps defined. Check configuration")).thenAnswer(invocation -> null);
            appConfiguration = new AppConfiguration("src/test/resources/config/test.properties");
            ex.when(EtrexUploader::getConfiguredMaps).thenReturn(maps);

            mp = new MapPanel(appConfiguration);
        }
    }

    @Test
    void shouldTestInitialPanelState() {

        assertThat(mp.mapViewer.isShowCoordinates()).isTrue();
        assertThat(mp.mapViewer.isShowAttribution()).isTrue();
        assertThat(mp.mapViewer.getZoom()).isEqualTo(Constants.DEFAULT_MAP_START_ZOOM);
        assertThat(mp.mapHome.getLatitude()).isEqualTo(appConfiguration.getMapHomeLatitude());
        assertThat(mp.mapHome.getLongitude()).isEqualTo(appConfiguration.getMapHomeLongitude());

        assertThat(mp.mapViewer.getUserOverlays()).isNotEmpty();
        assertThat(mp.mapViewer.getUserOverlays().stream().findFirst().orElseThrow()).isInstanceOf(RoutePainter.class);
        assertThat(mp.mapViewer.getTilerThreads()).isEqualTo(appConfiguration.getTilerThreads());

    }

    @Test
    void shouldChangeMapHome() {
        Coordinate newMapHome = new Coordinate(20, 20);

        EventBus.publish(EventType.MAP_HOME_CHANGED, newMapHome);

        waitUntilAsserted(Duration.ofSeconds(3),() -> assertThat(mp.mapHome).isEqualTo(newMapHome));
    }

    @Test
    void shouldChangeRouteColorProcessEvents() {
        // given
        Color newRouteColor = Color.BLACK;

        // when
        EventBus.publish(EventType.TRACK_COLOR_CHANGED, newRouteColor);

        // then
        waitUntilAsserted(Duration.ofSeconds(3), () -> assertThat(mp.getRoutePainter().getRouteColor()).isEqualTo(newRouteColor));
    }

    @Test
    void shouldChangeRouteWidth() {
        int newLineWidth = 5;

        EventBus.publish(EventType.ROUTE_LINE_WIDTH_CHANGED, newLineWidth);

        waitUntilAsserted(Duration.ofSeconds(3), () -> assertThat(mp.getRoutePainter().getRouteLineWidth()).isEqualTo(newLineWidth));
    }

    @Test
    void shouldPaintFileTrack() {
        File trackFile = new File("src/test/resources/trackfiles/gpx11.gpx");

        //  the file contains among hundreds of others - these:
        //  <trkpt lat="51.23217" lon="22.49083">
        //  <trkpt lat="51.23242" lon="22.49152">
        //  <trkpt lat="51.2329" lon="22.49108">

        //  <trkpt lat="51.23314" lon="22.4909">
        //  <trkpt lat="51.23331" lon="22.4909">
        //  <trkpt lat="51.23349" lon="22.49097">

        Collection<Coordinate> expectedCoords = new ArrayList<>();
        expectedCoords.add(new Coordinate(22.49083, 51.23217));
        expectedCoords.add(new Coordinate(22.49152, 51.23242));
        expectedCoords.add(new Coordinate(22.49108, 51.2329));
        expectedCoords.add(new Coordinate(22.4909, 51.23314));
        expectedCoords.add(new Coordinate(22.4909, 51.23331));
        expectedCoords.add(new Coordinate(22.49097, 51.23349));

        EventBus.publish(EventType.MAP_DISPLAY_TRACK, trackFile);

        checkCoords(expectedCoords);
    }


    @Test
    void shouldPaintPolyline() {

        String polyline = "_p~iF~ps|U_ulLnnqC_mqNvxq`@";
        // This polyline decodes to:
        // (38.5, -120.2), (40.7, -120.95), (43.252, -126.453)
        Collection<Coordinate> expectedCoords = new ArrayList<>();
        expectedCoords.add(new Coordinate(-120.2, 38.5));
        expectedCoords.add(new Coordinate(-120.95, 40.7));
        expectedCoords.add(new Coordinate(-126.453, 43.252));

        EventBus.publish(EventType.MAP_DISPLAY_TRACK, polyline);

        checkCoords(expectedCoords);
    }

    @Test
    void shouldPaintListOfCoords() {

        Collection<Coordinate> coords = new ArrayList<>();
        coords.add(new Coordinate(22.49083, 51.23217));
        coords.add(new Coordinate(22.49152, 51.23242));
        coords.add(new Coordinate(22.49108, 51.2329));

        EventBus.publish(EventType.MAP_DISPLAY_TRACK, coords);

        checkCoords(coords);
    }

    @Test
    void shouldResetMap() {

        // first draw something
        Collection<Coordinate> coords = new ArrayList<>();
        coords.add(new Coordinate(22.49083, 51.23217));
        coords.add(new Coordinate(22.49152, 51.23242));
        coords.add(new Coordinate(22.49108, 51.2329));
        EventBus.publish(EventType.MAP_DISPLAY_TRACK, coords);

        waitUntilAsserted(Duration.ofSeconds(2), () -> assertThat(mp.getRoutePainter().getObjects()).isNotEmpty());

        // now reset map, this should clear all painters
        EventBus.publish(EventType.MAP_RESET, Constants.DEFAULT_MAP_HOME_LOCATION);

        waitUntilAsserted(Duration.ofSeconds(2), () -> {
            for (Painter<MapViewer> p : mp.mapViewer.getUserOverlays()) {
                assertThat(p.getObjects()).isEmpty();
            }
            assertThat(mp.mapViewer.getZoom()).isEqualTo(Constants.DEFAULT_MAP_START_ZOOM);
            assertThat(mp.mapHome.getLongitude()).isEqualTo(Constants.DEFAULT_MAP_HOME_LOCATION.getLongitude());
            assertThat(mp.mapHome.getLatitude()).isEqualTo(Constants.DEFAULT_MAP_HOME_LOCATION.getLatitude());
        });
    }

    @Test
    void shouldChangeCurrentMap() {
        File mapFile = new File("src/test/resources/config/maps/goodMapDef.xml");

        EventBus.publish(EventType.MAP_CHANGED, mapFile);


        waitUntilAsserted(Duration.ofSeconds(2), () -> {
            assertThat(mp.mapViewer.getCurrentMap().getMapName()).isEqualTo("OSM Cycle");
            assertThat(mp.mapViewer.getCurrentMap().getMapCopyrightAttribution()).isEqualTo("CyclOSM | Map data © OpenStreetMap contributors");
            assertThat(mp.mapViewer.getCurrentMap().getLayers()).hasSize(1);
            verifyLogged("Creating map: [OSM Cycle]");
            verifyLogged("Added layer CyclOSM Map, CRS:EPSG:3857, TileSize:256");
        });
    }

    @Test
    void shouldSetSecondaryCache() {
        // we need to use another config, so we instantiate a new mapPanel in this test
        try (MockedStatic<SwingUtils> swingUtils = Mockito.mockStatic(SwingUtils.class)) {
            swingUtils.when(() -> SwingUtils.errorMsg("No maps defined. Check configuration")).thenAnswer(invocation -> null);
            appConfiguration = spy(new AppConfiguration("src/test/resources/config/test.properties"));
            doNothing().when(appConfiguration).save();

            // set db cache
            appConfiguration.setCacheType("Database");
            mp = new MapPanel(appConfiguration);

            assertThat(mp.mapViewer.getSecondaryTileCache()).isInstanceOf(DBCache.class);

            // set dir based cache
            appConfiguration.setCacheType(Constants.DIR_BASED_CACHE_TYPE);
            mp = new MapPanel(appConfiguration);

            assertThat(mp.mapViewer.getSecondaryTileCache()).isInstanceOf(DirectoryBasedCache.class);

            // set illegal/wrong/badly defined cache - check restoring to default and warning message
            appConfiguration.setCacheType("zzz");
            mp = new MapPanel(appConfiguration);

            assertThat(appConfiguration.getCacheType()).isEqualTo(Constants.DEFAULT_TILE_CACHE_TYPE);
            assertThat(mp.mapViewer.getSecondaryTileCache()).isInstanceOf(DirectoryBasedCache.class);
            verifyLogged("Tile cache badly configured: setting default - " + Constants.DEFAULT_TILE_CACHE_TYPE);
        }
    }

    @Test
    void TestMapSelector() {

        File[] mapFiles = mp.mapSelector.getConfiguredMapFiles();
        // default map file in test config is null so it should set first map from the list
        // which is osm cycle. but the selector should contain declared maps

        assertThat(Arrays.stream(mapFiles).toList()).containsExactlyInAnyOrder(map1, map2);
        verifyLogged("Creating map: [OSM Cycle]");
        verifyLogged("Added layer CyclOSM Map, CRS:EPSG:3857, TileSize:256");

    }


    private void checkCoords(Collection<Coordinate> coords) {
        waitUntilAsserted(Duration.ofSeconds(3), () -> assertThat(mp.getRoutePainter().getObjects()).isNotEmpty());
        for (Coordinate coord : coords) {
            boolean contains = mp.getRoutePainter().getObjects().stream()
                    .anyMatch(c -> c.getLatitude() == coord.getLatitude() && c.getLongitude() == coord.getLongitude());
            assertTrue(contains);
        }
    }
}

