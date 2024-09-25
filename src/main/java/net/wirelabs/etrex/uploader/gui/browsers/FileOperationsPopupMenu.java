package net.wirelabs.etrex.uploader.gui.browsers;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.UploadService;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileNode;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileTree;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.tree.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public class FileOperationsPopupMenu extends JPopupMenu {

    private File selectedFile;

    private final JMenuItem menuItemUpload;
    private final JMenuItem menuItemDelete;
    private final JMenuItem menuitemUploadToDevice;

    // this constructor is called from local storage browser (no upload to strava disabled)
    public FileOperationsPopupMenu(FileTree tree) {
        this(tree, null);
    }
    // this constructor is called from garmin browser (upload to strava enabled)
    // todo: maybe use only this constructor and enable uploading to strava from local storage too?
    //  need to find way to easily recognize which browser calls
    public FileOperationsPopupMenu(FileTree tree, UploadService uploadService) {

        menuItemUpload = new JMenuItem("Upload to Strava");
        menuItemDelete = new JMenuItem("Delete file");
        menuitemUploadToDevice = new JMenuItem("Upload to device");

        add(menuItemUpload);
        add(menuitemUploadToDevice);
        add(menuItemDelete);

        // delete is always enabled
        enableMenuItem(menuItemDelete);
        menuItemDelete.addActionListener(e -> deleteSelectedFileAndCorrespondingTreeNode(tree));

        // upload to strava/device is dependant on how it is called
        if (uploadService == null) {
            enableMenuItem(menuItemUpload);
            enableMenuItem(menuitemUploadToDevice);
            menuitemUploadToDevice.addActionListener(e -> uploadSelectedToDevice(tree));
        } else {
            enableMenuItem(menuItemUpload);
            disableMenuItem(menuitemUploadToDevice);
            menuItemUpload.addActionListener(e -> uploadSelectedToStrava(tree, uploadService));
        }
        // enable file oprations/uploads only if a file is selected
        addPropertyChangeListener("visible", evt -> {
            FileNode node = (FileNode) tree.getLastSelectedPathComponent();
            boolean enabled = node != null && node.getFile() != null && node.getFile().isFile();

            menuItemUpload.setEnabled(enabled);
            menuitemUploadToDevice.setEnabled(enabled);
            menuItemDelete.setEnabled(enabled);

        });
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
            File destinationFile = new File(destinationDir.toFile(), selectedFile.getName());

            try {
                FileUtils.copyFile(selectedFile, destinationFile);
                SwingUtils.infoMsg("Uploaded " + selectedFile.getAbsolutePath() + " to device at: " + destinationFile.getAbsolutePath());
            } catch (IOException ex) {
                SwingUtils.errorMsg("Failed to upload " + selectedFile.getAbsolutePath() + " to device\r\n" + ex.getMessage());

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
            SwingUtils.errorMsg("Could not delete " + selectedFile.getAbsolutePath() + "\r\n" + ex.getMessage());
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
