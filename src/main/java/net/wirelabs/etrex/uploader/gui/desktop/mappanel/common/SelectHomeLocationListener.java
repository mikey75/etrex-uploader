package net.wirelabs.etrex.uploader.gui.desktop.mappanel.common;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.eventbus.EventBus;
import net.wirelabs.jmaps.map.MapViewer;
import net.wirelabs.jmaps.map.geo.Coordinate;
import net.wirelabs.jmaps.map.layer.Layer;

import javax.swing.*;
import java.awt.event.*;


@Slf4j
@AllArgsConstructor
public class SelectHomeLocationListener extends MouseAdapter {

    private final MapViewer mapViewer;
    private final AppConfiguration appConfiguration;

    @Override
    public void mouseClicked(MouseEvent e) {

            if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                Layer baseLayer = mapViewer.getCurrentMap().getBaseLayer();
                Coordinate mouseLatLon = baseLayer.pixelToLatLon(mapViewer.getMouseHandler().getCurrentMousePosition(), mapViewer.getZoom());
                String msg = String.format("Select (%.4f,%.4f) as your map home?", mouseLatLon.getLongitude(), mouseLatLon.getLatitude());
                int result = SwingUtils.yesNoMsg(msg);

                if (result == JOptionPane.YES_OPTION) {
                    log.info("Setting new map home position");
                    appConfiguration.setMapHomeLatitude(mouseLatLon.getLatitude());
                    appConfiguration.setMapHomeLongitude(mouseLatLon.getLongitude());
                    appConfiguration.save();
                    EventBus.publish(EventType.MAP_HOME_CHANGED, new Coordinate(mouseLatLon.getLongitude(), mouseLatLon.getLatitude()));
                }

            }
    }

}
