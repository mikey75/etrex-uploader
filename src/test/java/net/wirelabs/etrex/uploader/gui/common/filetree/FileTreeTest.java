package net.wirelabs.etrex.uploader.gui.common.filetree;

import net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common.FileOperationsPopupMenu;
import net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common.filetree.FileTree;
import net.wirelabs.etrex.uploader.strava.UploadService;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class FileTreeTest extends BaseTest {

    private FileTree fileTree;
    private final File testDrive1 = new File("src/test/resources/fakerootdir/fakedisk1");
    private final File testDrive2 = new File("src/test/resources/fakerootdir/fakedisk2");
    private final UploadService uploadService = mock(UploadService.class);

    @BeforeEach
    void before() {
        fileTree = new FileTree();
        assertThat(fileTree.getRootNodes()).isEmpty();

    }

    @Test
    void shouldAddAndRemoveDrives() {
        // add two drives - drive 1 and drive 2
        fileTree.addDrive(testDrive1);
        fileTree.addDrive(testDrive2);
        // check
        assertThat(fileTree.getRootNodes()).hasSize(2);
        assertThat(fileTree.getRootNodes().get(0)).hasToString(testDrive1.getPath());
        assertThat(fileTree.getRootNodes().get(1)).hasToString(testDrive2.getPath());

        // now remove drive 2
        fileTree.removeDrive(testDrive2);
        // check
        assertThat(fileTree.getRootNodes()).hasSize(1);
        assertThat(fileTree.getRootNodes().get(0)).hasToString(testDrive1.getPath());
    }


    @Test
    void addPopupMenu() {
        JPopupMenu menu = new FileOperationsPopupMenu(fileTree, uploadService);
        fileTree.addPopupMenu(menu);
        assertThat(fileTree.getMouseListeners()).isNotEmpty();
    }


}