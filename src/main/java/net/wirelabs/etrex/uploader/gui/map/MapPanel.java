package net.wirelabs.etrex.uploader.gui.map;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.eventbus.Event;
import net.wirelabs.etrex.uploader.common.utils.ListUtils;
import net.wirelabs.etrex.uploader.gui.components.EventAwarePanel;
import net.wirelabs.etrex.uploader.gui.map.custom.GeoportalTopoBaseFactory;
import net.wirelabs.etrex.uploader.gui.map.custom.GeoportalTopoBaseFactoryInfo;
import net.wirelabs.etrex.uploader.gui.map.custom.MtbMapFactoryInfo;
import net.wirelabs.etrex.uploader.gui.map.custom.OSMTopoMapFactoryInfo;
import net.wirelabs.etrex.uploader.gui.map.custom.TFMapType;
import net.wirelabs.etrex.uploader.gui.map.custom.ThunderForestMapsFactoryInfo;
import net.wirelabs.etrex.uploader.gui.map.parsers.TrackParser;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.AbstractTileFactory;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.MouseInputListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Slf4j
public class MapPanel extends EventAwarePanel {

    public static final double MAX_FRACTION = 0.9;

    private final AppConfiguration configuration;
    private final JXMapViewer mapViewer = new JXMapViewer();

    private final transient RoutePainter routePainter = new RoutePainter();
    private final transient AttributionPainter attributionPainter = new AttributionPainter();
    private final transient TrackParser trackParser = new TrackParser();


    public MapPanel(AppConfiguration configuration) {
        this.configuration = configuration;
        setBorder(new TitledBorder("Map"));
        setLayout(new MigLayout("", "[grow]", "[grow]"));
        add(mapViewer, "cell 0 0,grow");

        configureMouseInteractionListeners();
        configureMapSelector();
        setupPainters();
        configureTileFactory(configuration.getDefaultMapType());
        setHomeLocation();

    }

    private void configureMapSelector() {

        final JComboBox<MapType> mapSelector = new JComboBox<>(MapType.values());

        mapSelector.setSelectedItem(configuration.getDefaultMapType());
        mapSelector.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                MapType mapType = (MapType) e.getItem();
                changeMap(mapType);
            }
        });
        // this is a hackish way to overlay JComboBox on the map
        mapViewer.setLayout(new MigLayout("", "[90%][]", "[]"));
        mapViewer.add(mapSelector, "cell 1 0, grow");
    }

    private void setupPainters() {
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>();
        painter.addPainter(routePainter);
        painter.addPainter(attributionPainter);
        mapViewer.setOverlayPainter(painter);
    }

    private void changeMap(MapType mapType) {

        configureTileFactory(mapType);
        if (routePainter.getTrack().isEmpty()) {
            setHomeLocation();
        } else {
            mapViewer.zoomToBestFit(new HashSet<>(routePainter.getTrack()), MAX_FRACTION);
        }

    }

    private void configureMouseInteractionListeners() {

        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        MouseWheelListener mwl = new ZoomMouseWheelListenerCursor(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseWheelListener(mwl);
    }

    private void setHomeLocation() {
        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(new GeoPosition(51.246452, 22.568445)); // LUBLIN,PL :)

    }

    private void configureTileFactory(MapType mapType) {


        AbstractTileFactory tileFactory;
        String apiKey = Constants.EMPTY_STRING;

        if (mapType.isRequiresKey()) {
            apiKey = configuration.getProperty("map.api.key." + mapType.name());
        }

        switch (mapType) {
            case OPENSTREETMAP: {
                TileFactoryInfo info = new OSMTileFactoryInfo();
                tileFactory = new DefaultTileFactory(info);
                break;
            }
            case TF_OUTDOOR: {
                TileFactoryInfo info = new ThunderForestMapsFactoryInfo(TFMapType.TF_OUTDOORS, apiKey);
                tileFactory = new DefaultTileFactory(info);
                break;
            }
            case TF_CYCLE: {
                TileFactoryInfo info = new ThunderForestMapsFactoryInfo(TFMapType.TF_CYCLE, apiKey);
                tileFactory = new DefaultTileFactory(info);
                break;
            }
            case TF_LANDSCAPE: {
                TileFactoryInfo info = new ThunderForestMapsFactoryInfo(TFMapType.TF_LANDSCAPE, apiKey);
                tileFactory = new DefaultTileFactory(info);
                break;
            }
            case MTB_CZ: {
                TileFactoryInfo info = new MtbMapFactoryInfo();
                tileFactory = new DefaultTileFactory(info);
                break;
            }
            case GEOPORTAL: {
                TileFactoryInfo info = new GeoportalTopoBaseFactoryInfo();
                tileFactory = new GeoportalTopoBaseFactory(info);
                break;
            }
            case VIRTEARTH: {
                TileFactoryInfo info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.HYBRID);
                tileFactory = new DefaultTileFactory(info);
                break;
            }
            case OSM_TOPO: {
                TileFactoryInfo info = new OSMTopoMapFactoryInfo();
                tileFactory = new DefaultTileFactory(info);
                break;
            }
            default:
                TileFactoryInfo info = new OSMTileFactoryInfo();
                tileFactory = new DefaultTileFactory(info);
                break;
        }
        //DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));
        tileFactory.setThreadPoolSize(configuration.getTilerThreads());

        mapViewer.setHorizontalWrapped(false);
        mapViewer.setInfiniteMapRendering(false);
        mapViewer.setTileFactory(tileFactory);

    }

    @Override
    protected void onEvent(Event evt) {
        drawTrackOnMap(evt);
    }

    @Override
    protected Collection<EventType> subscribeEvents() {
        return ListUtils.listOf(EventType.MAP_DISPLAY_TRACK);
    }

    private void drawTrackOnMap(Event evt) {
        // track is inside a file
        if (evt.getPayload() instanceof File) {
            File file = (File) evt.getPayload();
            SwingUtilities.invokeLater(() -> {
                List<GeoPosition> points = trackParser.parseToGeoPosition(file);
                paintTrack(points);
            });

        }
        // track is inside a polyline string
        if (evt.getPayload() instanceof String) {
            SwingUtilities.invokeLater(() -> {
                String polyLine = (String) evt.getPayload();
                List<GeoPosition> points = trackParser.parsePolyline(polyLine, 1E5F);
                paintTrack(points);
            });
        }
    }

    private void paintTrack(List<GeoPosition> points) {
        routePainter.setTrack(points);
        mapViewer.zoomToBestFit(new HashSet<>(points), MAX_FRACTION);
    }
}
