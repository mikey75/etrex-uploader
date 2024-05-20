package net.wirelabs.etrex.uploader.gui.map;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.eventbus.Event;
import net.wirelabs.etrex.uploader.common.utils.ListUtils;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.components.EventAwarePanel;
import net.wirelabs.etrex.uploader.gui.components.OverlayEnabler;
import net.wirelabs.etrex.uploader.gui.map.parsers.TrackParser;
import net.wirelabs.etrex.uploader.gui.settings.ChooseMapComboBox;
import net.wirelabs.jmaps.map.MapViewer;
import net.wirelabs.jmaps.map.cache.DirectoryBasedCache;
import net.wirelabs.jmaps.map.geo.Coordinate;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.Collection;
import java.util.List;

@Slf4j
public class MapPanel extends EventAwarePanel {

    private static final Coordinate LUBLIN_PL = new Coordinate(22.565628, 51.247717);

    private final AppConfiguration configuration;
    private final MapViewer mapViewer = new MapViewer();

    private final transient RoutePainter routePainter;
    private final OverlayEnabler overlayEnabler;

    private final transient TrackParser trackParser;


    public MapPanel(AppConfiguration configuration) {
        this.configuration = configuration;
        this.routePainter = new RoutePainter(configuration);
        this.trackParser = new TrackParser();
        this.overlayEnabler = new OverlayEnabler(mapViewer, routePainter);

        mapViewer.setShowCoordinates(true);
        mapViewer.setShowAttribution(true);
        mapViewer.setZoom(12);
        mapViewer.setHome(LUBLIN_PL);
        mapViewer.setImageCacheSize(32000);
        mapViewer.setSecondaryTileCache(new DirectoryBasedCache());
        mapViewer.setTilerThreads(configuration.getTilerThreads());
        mapViewer.addUserOverlay(routePainter);
        mapViewer.add(overlayEnabler.getShowOverlaysCheckbox(), "cell 0 0");

        setBorder(new TitledBorder("Map"));
        setLayout(new MigLayout("", "[grow]", "[grow]"));
        add(mapViewer, "cell 0 0,grow");


        configureMapSelector();

    }

    private void configureMapSelector() {

        final ChooseMapComboBox mapSelector = new ChooseMapComboBox(configuration);

        if (mapSelector.getMapFiles().length > 0) {

            mapSelector.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    changeMap((File) e.getItem());
                }
            });
            mapViewer.add(mapSelector, "cell 1 0, grow");
            mapSelector.setSelectedItem(configuration.getMapFile().toFile());
        } else {
            SwingUtils.errorMsg("No maps defined. Check configuration");
        }


    }


    private void changeMap(File mapFile) {

       mapViewer.setCurrentMap(mapFile);
        if (routePainter.getObjects().isEmpty()) {
            mapViewer.setHome(mapViewer.getHome());
        } else {
            mapViewer.setBestFit(routePainter.getObjects());
        }

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
    }

    @Override
    protected Collection<EventType> subscribeEvents() {
        return ListUtils.listOf(EventType.TRACK_COLOR_CHANGED, EventType.MAP_DISPLAY_TRACK);
    }

    private void drawTrackOnMap(Event evt) {
        // track is inside a file
        if (evt.getPayload() instanceof File) {
            File file = (File) evt.getPayload();
            SwingUtilities.invokeLater(() -> {
                List<Coordinate> points = trackParser.parseToGeoPosition(file);
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
