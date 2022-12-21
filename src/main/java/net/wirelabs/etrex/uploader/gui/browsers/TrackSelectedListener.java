package net.wirelabs.etrex.uploader.gui.browsers;

import net.wirelabs.etrex.uploader.common.eventbus.EventBus;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileNode;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.io.File;

import static net.wirelabs.etrex.uploader.common.EventType.MAP_DISPLAY_TRACK;

public class TrackSelectedListener implements TreeSelectionListener {
    @Override
    public void valueChanged(TreeSelectionEvent event) {
        FileNode node = (FileNode) event.getPath().getLastPathComponent();
        File file = node.getFile();
        if (file != null && file.isFile() && FileUtils.isTrackFile(file)) {
            EventBus.publish(MAP_DISPLAY_TRACK, node.getFile());
        }
    }
}
