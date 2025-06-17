package net.wirelabs.etrex.uploader.gui.components.filetree;

import net.wirelabs.etrex.uploader.gui.browsers.FileOperationsPopupMenu;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class FileTreeTest extends BaseTest {

    private FileTree fileTree;
    File testdrive1 = new File("src/test/resources/fakerootdir/fakedisk1");
    File testdrive2 = new File("src/test/resources/fakerootdir/fakedisk2");

    @BeforeEach
    void before() {
        fileTree = new FileTree();
        assertThat(fileTree.getRootNodes()).isEmpty();

    }

    @Test
    void shouldAddAndRemoveDrives() {
        // add two drives - drive 1 and drive 2
        fileTree.addDrive(testdrive1);
        fileTree.addDrive(testdrive2);
        // check
        assertThat(fileTree.getRootNodes()).hasSize(2);
        assertThat(fileTree.getRootNodes().get(0)).hasToString(testdrive1.getPath());
        assertThat(fileTree.getRootNodes().get(1)).hasToString(testdrive2.getPath());

        // now remove drive 2
        fileTree.removeDrive(testdrive2);
        // check
        assertThat(fileTree.getRootNodes()).hasSize(1);
        assertThat(fileTree.getRootNodes().get(0)).hasToString(testdrive1.getPath());
    }


    @Test
    void addPopupMenu() {
        JPopupMenu menu = new FileOperationsPopupMenu(fileTree);
        fileTree.addPopupMenu(menu);
        assertThat(fileTree.getMouseListeners()).isNotEmpty();
    }


}