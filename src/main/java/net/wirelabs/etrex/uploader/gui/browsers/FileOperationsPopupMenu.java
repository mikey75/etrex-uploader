package net.wirelabs.etrex.uploader.gui.browsers;

import net.wirelabs.etrex.uploader.gui.UploadService;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileNode;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileTree;

import javax.swing.*;

public class FileOperationsPopupMenu extends JPopupMenu {

    public FileOperationsPopupMenu(FileTree tree) {
        this(tree, null);
    }
    
    public FileOperationsPopupMenu(FileTree tree, UploadService uploadService) {

        JMenuItem menuItemUpload = new JMenuItem("Upload to Strava");
        JMenuItem menuItemDelete = new JMenuItem("Delete file");
        add(menuItemUpload);
        add(menuItemDelete);
        menuItemUpload.setEnabled(false);
       
        if (uploadService != null) {
            menuItemUpload.setEnabled(true);
            menuItemUpload.addActionListener(e -> {

                FileNode node = (FileNode) tree.getLastSelectedPathComponent();
                uploadService.uploadFile(node.getFile());

            });
        }
    }


}
