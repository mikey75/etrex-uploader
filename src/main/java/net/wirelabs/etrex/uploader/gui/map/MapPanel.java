package net.wirelabs.etrex.uploader.gui.map;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.parsers.TrackParser;
import net.wirelabs.etrex.uploader.common.utils.ListUtils;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.EtrexUploader;
import net.wirelabs.etrex.uploader.gui.components.EventAwareBorderedPanel;
import net.wirelabs.etrex.uploader.gui.components.OverlayEnabler;
import net.wirelabs.etrex.uploader.gui.components.choosemapcombo.ChooseMapComboBox;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.IEventType;
import net.wirelabs.jmaps.map.MapViewer;
import net.wirelabs.jmaps.map.cache.DirectoryBasedCache;
import net.wirelabs.jmaps.map.geo.Coordinate;
import net.wirelabs.jmaps.map.painters.Painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Collection;
import java.util.List;

import static net.wirelabs.etrex.uploader.common.Constants.DEFAULT_MAP_START_ZOOM;

@Slf4j
public class MapPanel extends EventAwareBorderedPanel {



    private final AppConfiguration configuration;
    private final MapViewer mapViewer = new MapViewer();

    private final transient RoutePainter routePainter;
    private final OverlayEnabler overlayEnabler;
    private Coordinate mapHome;
    private final transient TrackParser trackParser;


    public MapPanel(AppConfiguration configuration) {
        super("Map");
        this.configuration = configuration;
        this.mapHome = new Coordinate(configuration.getMapHomeLongitude(),configuration.getMapHomeLattitude());
        this.routePainter = new RoutePainter(configuration);
        this.trackParser = new TrackParser();
        this.overlayEnabler = new OverlayEnabler(mapViewer, routePainter);

        mapViewer.setShowCoordinates(true);
        mapViewer.setShowAttribution(true);
        mapViewer.setZoom(DEFAULT_MAP_START_ZOOM);
        mapViewer.setHome(mapHome);
        mapViewer.setImageCacheSize(32000);
        mapViewer.setSecondaryTileCache(new DirectoryBasedCache());
        mapViewer.setTilerThreads(configuration.getTilerThreads());
        mapViewer.addUserOverlay(routePainter);
        mapViewer.addMouseListener(new SelectHomeLocationListener(mapViewer, configuration));
        mapViewer.add(overlayEnabler.getShowOverlaysCheckbox(), "cell 0 0");

        setLayout(new MigLayout("", "[grow]", "[grow]"));
        add(mapViewer, "cell 0 0,grow");


        configureMapSelector();

    }

    private void configureMapSelector() {

        final ChooseMapComboBox mapSelector = new ChooseMapComboBox();

        if (!EtrexUploader.getConfiguredMaps().isEmpty()) {

            mapSelector.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    changeMap((File) e.getItem());
                }
            });
            mapViewer.add(mapSelector, "cell 1 0, grow");

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
    }

    @Override
    protected Collection<IEventType> subscribeEvents() {
        return ListUtils.listOf(EventType.TRACK_COLOR_CHANGED, EventType.MAP_DISPLAY_TRACK, EventType.MAP_RESET, EventType.MAP_HOME_CHANGED);
    }

    private void drawTrackOnMap(Event evt) {
        // track is inside a file
        if (evt.getPayload() instanceof File) {
            File file = (File) evt.getPayload();
            SwingUtilities.invokeLater(() -> {
                List<Coordinate> points = trackParser.parseTrackFile(file);
                paintTrack(points);
            });

        }
        // track is inside a polyline string
        if (evt.getPayload() instanceof String) {
            SwingUtilities.invokeLater(() -> {
                String polyLine = (String) evt.getPayload();
                List<Coordinate> points = trackParser.parsePolyline(polyLine, 1E5F);
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
