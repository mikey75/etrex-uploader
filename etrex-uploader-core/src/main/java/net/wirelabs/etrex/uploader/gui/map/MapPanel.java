package net.wirelabs.etrex.uploader.gui.map;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.gui.components.EventAwarePanel;

import net.wirelabs.etrex.uploader.common.utils.ListUtils;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.eventbus.Event;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.Collection;
import java.util.List;

@Slf4j
public class MapPanel extends EventAwarePanel {
    
    private final JXMapViewer mapViewer = new JXMapViewer();
    private final GPXPainter gpxPainter; 

    public MapPanel() {
        log.info("Initializing map panel");
        setBorder(new TitledBorder("Map"));
        setLayout(new MigLayout("", "[grow]", "[grow]"));
        gpxPainter = new GPXPainter(mapViewer);
        add(mapViewer, "cell 0 0,grow");

        configureTileFactory();
        configureMouseInteractionListeners();
        
        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(new GeoPosition(51.246452,  22.568445)); // LUBLIN,PL :)
       
    }

    private void configureMouseInteractionListeners() {
        
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        MouseWheelListener mwl = new ZoomMouseWheelListenerCursor(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseWheelListener(mwl);
    }

    private void configureTileFactory() {
        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        
        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));
        tileFactory.setThreadPoolSize(8); // Use 8 threads in parallel to load the tiles
        //mapViewer.setDrawTileBorders(true);
        mapViewer.setHorizontalWrapped(false);
        mapViewer.setInfiniteMapRendering(false);
        mapViewer.setTileFactory(tileFactory);
        
    }

    @Override
    protected void onEvent(Event evt) {

        if (evt.getEventType().equals(EventType.MAP_DISPLAY_GPX_FILE) && evt.getPayload() instanceof File) {
            SwingUtilities.invokeLater(() -> {
                PaintResult result = gpxPainter.paintRouteFromGpxFile((File) evt.getPayload());
                displayErrorMessage(result);
            });
        }
        
        if (evt.getEventType().equals(EventType.MAP_DISPLAY_FIT_FILE) && evt.getPayload() instanceof File) {
            SwingUtilities.invokeLater(() -> {
                PaintResult result = gpxPainter.paintRouteFromFitFile((File) evt.getPayload());
                displayErrorMessage(result);
            });
        }
        
        if (evt.getEventType().equals(EventType.MAP_DISPLAY_TRACK) && evt.getPayload() instanceof List) {
            SwingUtilities.invokeLater(() -> {
                PaintResult result = gpxPainter.paintRouteFromTrackPoints((List) evt.getPayload());
                displayErrorMessage(result);
            });
        }
    }

    private void displayErrorMessage(PaintResult result) {
        if (result != PaintResult.SUCCESS) {
            SwingUtils.errorMsg(result.name());
        }
    }

    @Override
    protected Collection<EventType> subscribeEvents() {
        return ListUtils.listOf(EventType.MAP_DISPLAY_GPX_FILE,
                EventType.MAP_DISPLAY_FIT_FILE,
                EventType.MAP_DISPLAY_TRACK);
    }
}
