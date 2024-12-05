package net.wirelabs.etrex.uploader.gui.browsers;

import net.wirelabs.etrex.uploader.common.utils.TrackFileUtils;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileNode;
import net.wirelabs.eventbus.EventBus;

import javax.swing.event.*;
import java.io.File;

import static net.wirelabs.etrex.uploader.common.EventType.MAP_DISPLAY_TRACK;

public class TrackSelectedListener implements TreeSelectionListener {
    @Override
    public void valueChanged(TreeSelectionEvent event) {
        FileNode node = (FileNode) event.getPath().getLastPathComponent();
        File file = node.getFile();
        if (file != null && file.isFile() && TrackFileUtils.isTrackFile(file)) {
            EventBus.publish(MAP_DISPLAY_TRACK, node.getFile());
        }
    }
}
