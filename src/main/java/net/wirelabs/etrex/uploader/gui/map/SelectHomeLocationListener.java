package net.wirelabs.etrex.uploader.gui.map;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.eventbus.EventBus;
import net.wirelabs.jmaps.map.MapViewer;
import net.wirelabs.jmaps.map.geo.Coordinate;
import net.wirelabs.jmaps.map.layer.Layer;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


@Slf4j
public class SelectHomeLocationListener implements MouseListener {

    private final MapViewer mapViewer;
    private final AppConfiguration appConfiguration;

    public SelectHomeLocationListener(MapViewer mapViewer, AppConfiguration appConfiguration) {
        this.mapViewer = mapViewer;
        this.appConfiguration = appConfiguration;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

            if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                Layer baseLayer = mapViewer.getCurrentMap().getBaseLayer();
                Coordinate mouseLatLon = baseLayer.pixelToLatLon(mapViewer.getMouseHandler().getCurrentMousePosition(), mapViewer.getZoom());
                String msg = String.format("Select (%.4f,%.4f) as your map home?", mouseLatLon.getLongitude(), mouseLatLon.getLatitude());
                int result = SwingUtils.yesNoMsg(msg);
                // yes=0/no=1
                if (result == 0) {
                    log.info("Setting new map home position");
                    appConfiguration.setMapHomeLattitude(mouseLatLon.getLatitude());
                    appConfiguration.setMapHomeLongitude(mouseLatLon.getLongitude());
                    appConfiguration.save();
                    EventBus.publish(EventType.MAP_HOME_CHANGED, new Coordinate(mouseLatLon.getLongitude(), mouseLatLon.getLatitude()));
                }

            }
    }

    @Override
    public void mousePressed(MouseEvent e) {
       /* empty - not used */
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        /* empty - not used */
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        /* empty - not used */
    }

    @Override
    public void mouseExited(MouseEvent e) {
        /* empty - not used */
    }
}
