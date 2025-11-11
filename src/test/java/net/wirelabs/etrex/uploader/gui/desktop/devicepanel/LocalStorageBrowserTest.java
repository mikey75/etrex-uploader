package net.wirelabs.etrex.uploader.gui.desktop.devicepanel;

import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common.filetree.FileNode;
import net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common.LocalStorageBrowser;
import net.wirelabs.etrex.uploader.strava.UploadService;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.event.*;
import javax.swing.tree.*;
import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.wirelabs.etrex.uploader.common.EventType.MAP_DISPLAY_TRACK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocalStorageBrowserTest extends BaseTest {

    private LocalStorageBrowser localStorageBrowser;
    private AppConfiguration appConfiguration;
    private final UploadService uploadService = mock(UploadService.class);

    @BeforeEach
    void before() {
        // after adding MapSettingsPanelTest, MAP_DISPLAY_TRACK which is used here, is already subscribed in map panel,
        // so because EventBus is still static - we need to clear the state of the bus here
        eventBusReset();

        appConfiguration = new AppConfiguration("src/test/resources/config/test.properties");
        localStorageBrowser = new LocalStorageBrowser(appConfiguration, uploadService);
    }

    @Test
    void shouldCheckInitialState() {

        assertThat(getFilesOnTree()).hasSize(3).contains(appConfiguration.getStorageRoot());

        for (Path f : appConfiguration.getUserStorageRoots()) {
            assertThat(getFilesOnTree()).contains(f);
        }
        verifyNeverLogged("Roots changed, modifying tree");
    }


    @Test
    void shouldServiceUserRootsRemoval() {

        EventBus.publish(EventType.USER_STORAGE_ROOTS_CHANGED, new ArrayList<>()); // empty list, no user roots

        // we removed all user roots so the tree should contain only system root
        waitUntilAsserted(Duration.ofSeconds(3), () -> {
            verifyLogged("Roots changed, modifying tree");
            assertThat(getFilesOnTree()).hasSize(1).contains(appConfiguration.getStorageRoot());
        });
    }

    @Test
    void shouldServiceUserRootAddition() {

        Path newRoot = Path.of("/some-new-root");
        EventBus.publish(EventType.USER_STORAGE_ROOTS_CHANGED, List.of(newRoot));

        // we added one user root so there should be a system root plus one new user root
        waitUntilAsserted(Duration.ofSeconds(3), () -> {
            verifyLogged("Roots changed, modifying tree");
            assertThat(getFilesOnTree()).hasSize(2).contains(newRoot);
        });
    }


    @Test
    void shouldTestTrackSelectionListener() {
        // prepare track selection event
        File file = new File("src/test/resources/trackfiles/gpx11.gpx");
        FileNode fileNode = mock(FileNode.class);
        when(fileNode.getFile()).thenReturn(file);
        TreePath path = new TreePath(fileNode);
        TreeSelectionEvent event = new TreeSelectionEvent(this, path, false, null, path);

        final AtomicBoolean exists = new AtomicBoolean(false);
        localStorageBrowser.getTrackSelectedListener().valueChanged(event);

        // since nothing expects this event (no map active) - check if it is emitted and landed in dead-events
        waitUntilAsserted(Duration.ofSeconds(1), () -> {
            for (Event ev: EventBus.getDeadEvents()) {
                exists.set(ev.getEventType().equals(MAP_DISPLAY_TRACK) && ev.getPayload().equals(file));
            }
            assertThat(exists.get()).isTrue();
        });

    }


    private List<Path> getFilesOnTree() {
        List<Path> files = new ArrayList<>();
        for (TreeNode node : localStorageBrowser.getFileTree().getRootNodes()) {
            Path filename = ((FileNode) node).getFile().toPath();
            files.add(filename);
        }
        return files;
    }

}