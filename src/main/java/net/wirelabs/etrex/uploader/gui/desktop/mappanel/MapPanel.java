package net.wirelabs.etrex.uploader.gui.desktop.mappanel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.desktop.GarminLogo;
import net.wirelabs.etrex.uploader.parsers.TrackParser;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.EtrexUploader;
import net.wirelabs.etrex.uploader.gui.common.base.BaseEventAwarePanel;
import net.wirelabs.etrex.uploader.gui.common.components.ChooseMapComboBox;
import net.wirelabs.etrex.uploader.gui.desktop.mappanel.common.OverlayEnabler;
import net.wirelabs.etrex.uploader.gui.desktop.mappanel.common.RoutePainter;
import net.wirelabs.etrex.uploader.gui.desktop.mappanel.common.SelectHomeLocationListener;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.IEventType;
import net.wirelabs.jmaps.map.MapViewer;
import net.wirelabs.jmaps.map.cache.db.DBCache;
import net.wirelabs.jmaps.map.cache.files.DirectoryBasedCache;
import net.wirelabs.jmaps.map.cache.redis.RedisCache;
import net.wirelabs.jmaps.map.geo.Coordinate;
import net.wirelabs.jmaps.map.painters.Painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

import static net.wirelabs.etrex.uploader.common.Constants.DEFAULT_MAP_START_ZOOM;
import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.cc;

@Slf4j
public class MapPanel extends BaseEventAwarePanel {



    private final AppConfiguration configuration;

    final MapViewer mapViewer = new MapViewer();
    @Getter
    private final transient RoutePainter routePainter;
    private final OverlayEnabler overlayEnabler;
    private final GarminLogo garminLogo = new GarminLogo();
    transient Coordinate mapHome;
    private final transient TrackParser trackParser;
    final ChooseMapComboBox mapSelector = new ChooseMapComboBox();

    public MapPanel(AppConfiguration configuration) {
        super("Map");
        this.configuration = configuration;
        this.mapHome = new Coordinate(configuration.getMapHomeLongitude(),configuration.getMapHomeLatitude());
        this.routePainter = new RoutePainter(configuration);
        this.trackParser = new TrackParser();
        this.overlayEnabler = new OverlayEnabler(mapViewer, routePainter);

        mapViewer.setShowCoordinates(true);
        mapViewer.setShowAttribution(true);
        mapViewer.setZoom(DEFAULT_MAP_START_ZOOM);
        mapViewer.setHome(mapHome);
        mapViewer.setImageCacheSize(32000);
        setSecondaryTileCache(configuration.getCacheType());
        mapViewer.setTilerThreads(configuration.getTilerThreads());
        mapViewer.addUserOverlay(routePainter);
        mapViewer.addMouseListener(new SelectHomeLocationListener(mapViewer, configuration));
        mapViewer.add(overlayEnabler.getShowOverlaysCheckbox(), cc().cell(0,0));
        mapViewer.add(garminLogo,cc().cell(0,1));
        add(mapViewer, cc().cell(0,0).grow());
        configureMapSelector();

    }

    private void setSecondaryTileCache(String cacheType) {
        if (cacheType.equals(Constants.DIR_BASED_CACHE_TYPE)) {
            mapViewer.setSecondaryTileCache(new DirectoryBasedCache());
            return;
        }
        if (cacheType.equals(Constants.DB_BASED_CACHE_TYPE)) {
            mapViewer.setSecondaryTileCache(new DBCache());
            return;
        }
        if (cacheType.equals(Constants.REDIS_CACHE)) {
            mapViewer.setSecondaryTileCache(new RedisCache(configuration.getRedisHost(), configuration.getRedisPort(), Duration.ofDays(30),100));
            return;
        }
        log.info("Tile cache badly configured: setting default - {}", Constants.DEFAULT_TILE_CACHE_TYPE);
        mapViewer.setSecondaryTileCache(new DirectoryBasedCache());
        configuration.setCacheType(Constants.DEFAULT_TILE_CACHE_TYPE);
        configuration.save();
    }

    private void configureMapSelector() {


        if (!EtrexUploader.getConfiguredMaps().isEmpty()) {

            mapSelector.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    changeMap((File) e.getItem());
                }
            });
            mapViewer.add(mapSelector, cc().cell(1,0).grow());

            // if configured file does not exist - setup first item (which is default OSM.xml)
            File mapFile = configuration.getMapFile().toFile();
            if (mapFile.exists()) {
                mapSelector.setSelectedItem(mapFile);
            } else {
                mapSelector.setSelectedItem(EtrexUploader.getConfiguredMaps().get(0));
                changeMap(mapSelector.getItemAt(0));
            }

        } else {
            SwingUtils.errorMsg("No maps defined. Check configuration");
        }


    }


    private void changeMap(File mapFile) {
       mapViewer.setCurrentMap(mapFile);
    }


    @Override
    protected void onEvent(Event evt) {
        if (evt.getEventType() == EventType.MAP_DISPLAY_TRACK) {
            drawTrackOnMap(evt);
        }
        if (evt.getEventType() == EventType.TRACK_COLOR_CHANGED) {
            routePainter.setColor((Color) evt.getPayload());
            mapViewer.repaint();
        }

        if (evt.getEventType() == EventType.MAP_RESET) {
            for (Painter<?> painter : mapViewer.getUserOverlays()) {
                painter.getObjects().clear();
            }
            // reset map to default start position, and current zoom
            mapViewer.setPositionAndZoom(mapHome, DEFAULT_MAP_START_ZOOM);
            mapViewer.repaint();

        }
        if (evt.getEventType() == EventType.MAP_HOME_CHANGED) {
            mapHome = (Coordinate) evt.getPayload();

        }

        if (evt.getEventType() == EventType.ROUTE_LINE_WIDTH_CHANGED) {
            routePainter.setLineWidth((int) evt.getPayload());
            mapViewer.repaint();
        }

        if (evt.getEventType() == EventType.MAP_CHANGED) {
            changeMap((File) evt.getPayload());
            mapSelector.setSelectedItem(evt.getPayload());
            mapViewer.repaint();
        }
    }

    @Override
    protected Collection<IEventType> subscribeEvents() {
        return List.of(
                EventType.TRACK_COLOR_CHANGED,
                EventType.MAP_DISPLAY_TRACK,
                EventType.MAP_RESET,
                EventType.MAP_HOME_CHANGED,
                EventType.MAP_CHANGED,
                EventType.ROUTE_LINE_WIDTH_CHANGED
        );
    }

    private void drawTrackOnMap(Event evt) {
        // track is inside a file
        if (evt.getPayload() instanceof File filePayload) {
            SwingUtilities.invokeLater(() -> {
                List<Coordinate> points = trackParser.parseTrackFile(filePayload);
                paintTrack(points);
            });

        }
        // track is inside a polyline string
        if (evt.getPayload() instanceof String polyLineStringPayload) {
            SwingUtilities.invokeLater(() -> {
                List<Coordinate> points = trackParser.parsePolyline(polyLineStringPayload);
                paintTrack(points);
            });
        }
        // track is a coordinate list
        if (evt.getPayload() instanceof List && !((List<?>) evt.getPayload()).isEmpty() && ((List<?>) evt.getPayload()).get(0) instanceof Coordinate) {
            SwingUtilities.invokeLater(() -> {
                List<Coordinate> coords = (List<Coordinate>) evt.getPayload();
                paintTrack(coords);
            });

        }
    }

    private void paintTrack(List<Coordinate> points) {
        if (!points.isEmpty()) {
            routePainter.setRoute(points);
            mapViewer.setBestFit(points);
        }
    }
}
