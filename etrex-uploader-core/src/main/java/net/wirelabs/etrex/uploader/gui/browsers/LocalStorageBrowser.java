package net.wirelabs.etrex.uploader.gui.browsers;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileTree;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;



@Slf4j
public class LocalStorageBrowser extends JPanel {

    private final FileTree fileTree;
    private final Configuration configuration;

    public LocalStorageBrowser(Configuration configuration) {
        this.configuration = configuration;
        setBorder(new TitledBorder("Local repository"));
        setLayout(new MigLayout("", "[grow]", "[grow]"));
        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 0,grow");

        fileTree = new FileTree();
        setupRoots();
        scrollPane.setViewportView(fileTree);

    }

    private void setupRoots() {

        File defaultStorageRoot = new File(configuration.getStorageRoot());
        fileTree.addDrive(defaultStorageRoot);
        for (File root: getCustomStorageRoots()) {
            fileTree.addDrive(root);
        }
    }

    private List<File> getCustomStorageRoots() {
        
        List<File> customRoots = new ArrayList<>();
        String userRoots = configuration.getUserStorageRoots();
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
