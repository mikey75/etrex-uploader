package net.wirelabs.etrex.uploader.gui.browsers;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import net.wirelabs.etrex.uploader.gui.components.filetree.FileNode;
import net.wirelabs.etrex.uploader.gui.map.MapUtil;

public class TrackSelectedListener implements TreeSelectionListener {
    @Override
    public void valueChanged(TreeSelectionEvent event) {
        FileNode node = (FileNode) event.getPath().getLastPathComponent();
        MapUtil.drawTrackFromFile(node.getFile());
    }
}
