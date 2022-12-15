package net.wirelabs.etrex.uploader.gui.browsers;

import javax.swing.*;

import net.wirelabs.etrex.uploader.gui.components.filetree.FileNode;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileTree;
import net.wirelabs.etrex.uploader.gui.components.filetree.UploadDialog;

public class FileOperationsPopupMenu extends JPopupMenu {

    public FileOperationsPopupMenu(FileTree tree) {
        this(tree, null);
    }
    
    public FileOperationsPopupMenu(FileTree tree, UploadDialog uploadDialog) {

        JMenuItem menuItemUpload = new JMenuItem("Upload to Strava");
        JMenuItem menuItemDelete = new JMenuItem("Delete file");
        add(menuItemUpload);
        add(menuItemDelete);
        menuItemUpload.setEnabled(false);
       
        if (uploadDialog != null) {
            menuItemUpload.setEnabled(true);
            menuItemUpload.addActionListener(e -> {

                FileNode node = (FileNode) tree.getLastSelectedPathComponent();
                uploadDialog.setTrackFile(node.getFile());
                uploadDialog.clearInputAndStatus();
                uploadDialog.setVisible(true);

            });
        }
    }


}
