package net.wirelabs.etrex.uploader.gui.browsers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.components.BaseEventAwarePanel;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileTree;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.IEventType;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;


@Slf4j
public class LocalStorageBrowser extends BaseEventAwarePanel {

    @Getter
    private final FileTree fileTree;
    @Getter
    private final transient TrackSelectedListener trackSelectedListener = new TrackSelectedListener();
    private final AppConfiguration appConfiguration;

    LocalStorageBrowser(AppConfiguration appConfiguration) {
        
        super("Local repository");
        this.appConfiguration = appConfiguration;
        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 0,grow");

        fileTree = new FileTree();
        fileTree.setCellRenderer(new FileTreeCellRenderer());
        fileTree.addTreeSelectionListener(trackSelectedListener);
        fileTree.addPopupMenu(new FileOperationsPopupMenu(fileTree));
        
        setupRoots();
        scrollPane.setViewportView(fileTree);

    }

    private void setupRoots() {

        File defaultStorageRoot = appConfiguration.getStorageRoot().toFile();
        fileTree.addDrive(defaultStorageRoot);
        for (File root: getCustomStorageRoots()) {
            fileTree.addDrive(root);
        }
    }

    private List<File> getCustomStorageRoots() {

        return appConfiguration.getUserStorageRoots().stream()
                .map(f -> Paths.get(f.toString()).toFile())
                .toList();

    }


    @Override
    protected void onEvent(Event event) {
        if (event.getEventType().equals(EventType.USER_STORAGE_ROOTS_CHANGED)) {
            log.info("Roots changed, modifying tree");

            List<String> userRootsOnTree = fileTree.getRootNodes().stream()
                    .map(Object::toString)
                    .filter(s -> !s.equals(appConfiguration.getStorageRoot().toString()))
                    .toList();

            List<String> userRootsFromEventPayload = ((List<Path>)event.getPayload()).stream()
                    .map(Object::toString)
                    .toList();

            for (String s: userRootsFromEventPayload) {
                if (!userRootsOnTree.contains(s))
                    fileTree.addDrive(new File(s));
            }

            for (String s: userRootsOnTree) {
                if (!userRootsFromEventPayload.contains(s))
                    fileTree.removeDrive(new File(s));
            }
        }
    }

    @Override
    protected Collection<IEventType> subscribeEvents() {
        return List.of(EventType.USER_STORAGE_ROOTS_CHANGED);
    }
}
