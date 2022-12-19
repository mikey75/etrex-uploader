package net.wirelabs.etrex.uploader.gui.browsers;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileTree;

import javax.swing.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class LocalStorageBrowser extends BorderedPanel {

    private final FileTree fileTree;

    public LocalStorageBrowser(AppConfiguration appConfiguration) {
        
        super("Local repository");
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

        File defaultStorageRoot = appConfiguration.getStorageRoot().toFile();
        fileTree.addDrive(defaultStorageRoot);
        for (File root: getCustomStorageRoots(appConfiguration)) {
            fileTree.addDrive(root);
        }
    }

    private List<File> getCustomStorageRoots(AppConfiguration appConfiguration) {

        return appConfiguration.getUserStorageRoots().stream()
                .map(f -> Paths.get(f.toString()).toFile()).collect(Collectors.toList());

    }

}
