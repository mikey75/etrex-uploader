package net.wirelabs.etrex.uploader.gui.browsers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileTree;


@Slf4j
public class LocalStorageBrowser extends JPanel {

    private final FileTree fileTree;

    public LocalStorageBrowser(AppConfiguration appConfiguration) {
        
        setBorder(new TitledBorder("Local repository"));
        setLayout(new MigLayout("", "[grow]", "[grow]"));
        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 0,grow");

        fileTree = new FileTree();
        fileTree.setCellRenderer(new FileTreeCellRenderer());
        fileTree.addTreeSelectionListener(new TrackSelectedListener());
        fileTree.addPopupMenu(new FileOperationsPopupMenu(fileTree));
        
        setupRoots(appConfiguration);
        scrollPane.setViewportView(fileTree);

    }

    private void setupRoots(AppConfiguration appConfiguration) {

        File defaultStorageRoot = new File(appConfiguration.getStorageRoot());
        fileTree.addDrive(defaultStorageRoot);
        for (File root: getCustomStorageRoots(appConfiguration)) {
            fileTree.addDrive(root);
        }
    }

    private List<File> getCustomStorageRoots(AppConfiguration appConfiguration) {
        
        List<File> customRoots = new ArrayList<>();
        String userRoots = appConfiguration.getUserStorageRoots();
        if (userRoots != null && !userRoots.isEmpty()) {
            String[] roots = userRoots.split(",");
            for (String root : roots) {
                File newRoot = new File(root);
                customRoots.add(newRoot);
            }
        }
        return customRoots;
    }

}
