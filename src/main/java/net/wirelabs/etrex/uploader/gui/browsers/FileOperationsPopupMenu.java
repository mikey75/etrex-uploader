package net.wirelabs.etrex.uploader.gui.browsers;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.UploadService;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileNode;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileTree;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public class FileOperationsPopupMenu extends JPopupMenu {

    private File selectedFile;

    public FileOperationsPopupMenu(FileTree tree) {
        this(tree, null);
    }

    public FileOperationsPopupMenu(FileTree tree, UploadService uploadService) {

        JMenuItem menuItemUpload = new JMenuItem("Upload to Strava");
        JMenuItem menuItemDelete = new JMenuItem("Delete file");
        JMenuItem menuitemUploadToDevice = new JMenuItem("Upload to device");

        add(menuItemUpload);
        add(menuItemDelete);
        add(menuitemUploadToDevice);

        // delete is always enabled
        enableMenuItem(menuItemDelete);
        menuItemDelete.addActionListener( e -> deleteSelectedFileAndCorrespondingTreeNode(tree));

        // upload to strava/device is dependant on how it is called
        if (uploadService == null) {
            disableMenuItem(menuItemUpload);
            enableMenuItem(menuitemUploadToDevice);
            menuitemUploadToDevice.addActionListener(e ->  uploadSelectedToDevice(tree));
        } else {
            enableMenuItem(menuItemUpload);
            disableMenuItem(menuitemUploadToDevice);
            menuItemUpload.addActionListener(e -> uploadSelectedToStrava(tree, uploadService));
        }

    }

    private void uploadSelectedToDevice(FileTree tree) {
        FileNode node = (FileNode) tree.getLastSelectedPathComponent();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        selectedFile = node.getFile();

        log.info("uploading {} to device", selectedFile);

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            Path destinationDir = fileChooser.getSelectedFile().toPath();
            File destinationFile = new File(destinationDir.toFile(),selectedFile.getName());

            try {
                FileUtils.copyFile(selectedFile,destinationFile);
                SwingUtils.infoMsg("Uploaded " + selectedFile.getAbsolutePath() +" to device at: " + destinationFile.getAbsolutePath());
            } catch (IOException ex) {
                SwingUtils.errorMsg("Failed to upload " + selectedFile.getAbsolutePath() +" to device\r\n" + ex.getMessage());

            }
        }
    }

    private void uploadSelectedToStrava(FileTree tree, UploadService uploadService) {
        FileNode node = (FileNode) tree.getLastSelectedPathComponent();
        uploadService.uploadFile(node.getFile());
    }

    private void deleteSelectedFileAndCorrespondingTreeNode(FileTree tree) {
        FileNode node = (FileNode)  tree.getLastSelectedPathComponent();
        try {
            // delete file
            selectedFile = node.getFile();
            FileUtils.delete(selectedFile);
            log.info("Deleted {}", selectedFile);

            // update treemodel
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            parentNode.remove(node);
            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);


        } catch (IOException ex) {
            SwingUtils.errorMsg("Could not delete "+ selectedFile.getAbsolutePath() +"\r\n" + ex.getMessage());
        }
    }

    private void disableMenuItem(JMenuItem menuItem) {
        menuItem.setEnabled(false);
        menuItem.setVisible(false);
    }

    private void enableMenuItem(JMenuItem menuItem) {
        menuItem.setVisible(true);
        menuItem.setEnabled(true);
    }

}
