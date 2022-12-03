package net.wirelabs.etrex.uploader.gui.browsers;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.gui.components.EventAwarePanel;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileTree;
import net.wirelabs.etrex.uploader.common.utils.ListUtils;
import net.wirelabs.etrex.uploader.eventbus.Event;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



@Slf4j
public class LocalStorageBrowser extends EventAwarePanel {

    private final FileTree fileTree;
    private final List<File> allRoots = new ArrayList<>();
    private final Configuration configuration;


    public LocalStorageBrowser(Configuration configuration) {
        this.configuration = configuration;
        setBorder(new TitledBorder("Local repository"));
        setLayout(new MigLayout("", "[grow]", "[grow]"));
        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 0,grow");

        setupRoots();

        fileTree = new FileTree(allRoots);
        scrollPane.setViewportView(fileTree);

    }

    private void setupRoots() {

        File defaultStorageRoot = new File(configuration.getStorageRoot());
        allRoots.add(defaultStorageRoot);
        allRoots.addAll(getCustomStorageRoots());
    }

    private List<File> getCustomStorageRoots() {
        
        List<File> customRoots = new ArrayList<>();
        String userRoots = configuration.getUserStorageRoots();
        if (userRoots != null && !userRoots.isEmpty()) {
            String[] dirs = userRoots.split(",");
            for (String d : dirs) {
                File nf = new File(d);
                customRoots.add(nf);
            }
        }
        return customRoots;
    }

    @Override
    protected void onEvent(Event evt) {
        if (evt.getEventType().equals(EventType.EVT_NEW_FILES_DETECTED)) {
            List<Files> newFiles = (List<Files>) evt.getPayload();
            if (!newFiles.isEmpty()) {
                JOptionPane.showMessageDialog(getParent(), "There are new files. Upload to strava?");
            }
            fileTree.loadModel();
        }
    }

    @Override
    protected Collection<EventType> subscribeEvents() {
        return ListUtils.listOf(EventType.EVT_NEW_FILES_DETECTED);
    }
}
