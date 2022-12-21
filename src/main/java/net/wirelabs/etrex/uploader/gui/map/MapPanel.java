package net.wirelabs.etrex.uploader.gui.map;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.eventbus.Event;
import net.wirelabs.etrex.uploader.gui.components.EventAwarePanel;

import net.wirelabs.etrex.uploader.common.utils.ListUtils;

import net.wirelabs.etrex.uploader.gui.map.custom.ThunderForestOutdoorMapFactoryInfo;
import net.wirelabs.etrex.uploader.gui.map.parsers.FITParser;
import net.wirelabs.etrex.uploader.gui.map.parsers.GPXParser;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.MouseInputListener;

import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Slf4j
public class MapPanel extends EventAwarePanel {

    private final JXMapViewer mapViewer;
    private final transient RoutePainter routePainter;
    private final transient GPXParser gpxParser = new GPXParser();
    private final transient FITParser fitParser = new FITParser();

    public MapPanel(AppConfiguration configuration) {

        routePainter = new RoutePainter();
        mapViewer = new JXMapViewer();

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>();
        painter.addPainter(routePainter);
        painter.addPainter(new AttributionPainter());
        mapViewer.setOverlayPainter(painter);

        log.info("Initializing map panel");
        setBorder(new TitledBorder("Map"));
        setLayout(new MigLayout("", "[grow]", "[grow]"));
        add(mapViewer, "cell 0 0,grow");

        configureTileFactory(configuration.getDefaultMapType(), configuration.getThunderforestApiKey());
        configureMouseInteractionListeners();

        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(new GeoPosition(51.246452, 22.568445)); // LUBLIN,PL :)

    }

    private void configureMouseInteractionListeners() {

        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        MouseWheelListener mwl = new ZoomMouseWheelListenerCursor(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseWheelListener(mwl);
    }

    private void configureTileFactory(MapType mapType, String apiKey) {
        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info;

        switch (mapType) {
            case OPENSTREETMAP: {
                info = new OSMTileFactoryInfo();
                break;
            }
            case OUTDOOR: {
                info = new ThunderForestOutdoorMapFactoryInfo().withApiKey(apiKey);
                break;
            }
            default:
                info = new OSMTileFactoryInfo();
                break;
        }
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));
        tileFactory.setThreadPoolSize(8); // Use 8 threads in parallel to load the tiles

        mapViewer.setHorizontalWrapped(false);
        mapViewer.setInfiniteMapRendering(false);
        mapViewer.setTileFactory(tileFactory);

    }

    @Override
    protected void onEvent(Event evt) {

        if (evt.getEventType().equals(EventType.MAP_DISPLAY_GPX_FILE) && evt.getPayload() instanceof File) {
            SwingUtilities.invokeLater(() -> {
                List<GeoPosition> points = gpxParser.parseToGeoPosition((File) evt.getPayload());
                routePainter.setTrack(points);
                mapViewer.zoomToBestFit(new HashSet<>(points), 0.7);

            });
        }

        if (evt.getEventType().equals(EventType.MAP_DISPLAY_FIT_FILE) && evt.getPayload() instanceof File) {
            SwingUtilities.invokeLater(() -> {
                List<GeoPosition> points = fitParser.parseToGeoPosition((File) evt.getPayload());
                routePainter.setTrack(points);
                mapViewer.zoomToBestFit(new HashSet<>(points), 0.7);

            });
        }

        if (evt.getEventType().equals(EventType.MAP_DISPLAY_TRACK) && evt.getPayload() instanceof List) {

            SwingUtilities.invokeLater(() -> {
                List<GeoPosition> points = ((List<GeoPosition>) evt.getPayload());
                routePainter.setTrack(points);
                mapViewer.zoomToBestFit(new HashSet<>(points), 0.7);

            });
        }
    }

    @Override
    protected Collection<EventType> subscribeEvents() {
        return ListUtils.listOf(EventType.MAP_DISPLAY_GPX_FILE,
                EventType.MAP_DISPLAY_FIT_FILE,
                EventType.MAP_DISPLAY_TRACK);
    }
}
