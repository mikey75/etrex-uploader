package net.wirelabs.etrex.uploader.gui.desktop.mappanel.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;
import net.wirelabs.jmaps.map.MapViewer;
import net.wirelabs.jmaps.map.painters.Painter;


import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.cell;
@Slf4j
public class OverlayEnabler extends BasePanel {
    private final MapViewer mapViewer;
    int index = 0;
    @Getter
    private final Map<Painter<MapViewer> ,JCheckBox> painters = new HashMap<>();

    public OverlayEnabler(MapViewer mapViewer) {
        super("gapx 0, insets 0", "[][]", "[]");
        this.mapViewer = mapViewer;
    }

    public void addPainter(Painter<MapViewer> painter, String chkboxName, boolean initiallySelected, boolean initiallyActive) {
        JCheckBox chkbox = new JCheckBox(chkboxName, initiallySelected);
        chkbox.setForeground(Color.BLACK);
        add(chkbox, cell(index, 0));
        if (initiallySelected) mapViewer.addUserOverlay(painter);
        if (!initiallySelected) mapViewer.getUserOverlays().remove(painter);
        chkbox.setEnabled(initiallyActive);
        painters.put(painter, chkbox);
        chkbox.addActionListener(a -> {
            boolean selected = chkbox.isSelected();
            if (!selected) {
                mapViewer.getUserOverlays().remove(painter);
            } else {
                mapViewer.addUserOverlay(painter);
            }
            mapViewer.repaint();
        });
        index++;
    }

}
