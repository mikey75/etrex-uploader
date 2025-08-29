package net.wirelabs.etrex.uploader.gui.settingsdialog;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.utils.FileUtils;
import net.wirelabs.etrex.uploader.utils.SystemUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

@Slf4j
class LogViewerDialogTest extends BaseTest {

    @BeforeAll
    static void setup() throws IOException {
        // since in tests we cannot assure the application logger is already configured,
        // so the 'log' dir might not have been created - so create it if it does not exist
        FileUtils.createDirIfDoesNotExist(new File(SystemUtils.getWorkDir() + "/logs"));
    }

    @Test
    void testLogViewerDialog() throws IOException {

        // add some test log files
        deleteTestLogs();
        createLogs();

        LogViewerDialog dialog = spy(new LogViewerDialog());
        doNothing().when(dialog).setVisible(anyBoolean());

        // open/init dialog
        dialog.open();

        // find the combo - no getter in dialog so getComponents()
        Component combo = Arrays.stream(dialog.getContentPane().getComponents())
                .filter(f -> f.getClass().equals(JComboBox.class))
                .findFirst()
                .orElseThrow();

        // check if the combo contains the given files
        assertThat(containsItem((JComboBox<?>) combo,"testlog1.log")).isTrue();
        assertThat(containsItem((JComboBox<?>) combo,"testlog2.log")).isTrue();


        // delete files after test
        deleteTestLogs();

    }

    private void createLogs() throws IOException {
        Files.createFile(Path.of("logs/testlog1.log"));
        Files.createFile(Path.of("logs/testlog2.log"));
    }

    private void deleteTestLogs() throws IOException {
        Files.deleteIfExists(Path.of("logs/testlog1.log"));
        Files.deleteIfExists(Path.of("logs/testlog2.log"));
    }

    private boolean containsItem(JComboBox<?> comboBox, Object item) {
        ComboBoxModel<?> model = comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Object element = model.getElementAt(i);
            if (item.equals(element)) {
                return true;
            }
        }
        return false;
    }

}