package net.wirelabs.etrex.uploader.gui.desktop.mappanel.common;

import lombok.Getter;
import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;
import net.wirelabs.jmaps.map.MapViewer;

import javax.swing.*;
import java.awt.*;

public class OverlayEnabler extends BasePanel {

    @Getter
    private final JCheckBox showOverlaysCheckbox = new JCheckBox("Show user overlays", true);

    public OverlayEnabler(MapViewer mapViewer, RoutePainter routePainter) {
        showOverlaysCheckbox.setForeground(Color.BLACK);
        showOverlaysCheckbox.addActionListener(a -> {
            boolean selected = showOverlaysCheckbox.isSelected();
            if (!selected) {
                mapViewer.getUserOverlays().clear();
            } else {
                mapViewer.addUserOverlay(routePainter);
            }
            mapViewer.repaint();
        });
    }
}
