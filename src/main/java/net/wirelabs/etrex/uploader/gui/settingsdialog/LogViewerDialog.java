package net.wirelabs.etrex.uploader.gui.settingsdialog;

import net.wirelabs.etrex.uploader.gui.common.base.BaseDialog;
import net.wirelabs.etrex.uploader.utils.GzipUtils;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.*;


public class LogViewerDialog extends BaseDialog {

    public static final String LOG_VIEWER_WINDOW_TITLE = "Application Log viewer";
    private static final Dimension windowSize = new Dimension(800,600);
    private final JButton closeBtn = new JButton("Close");
    private final JLabel lblLogfiles = new JLabel("Logfile:");
    private final JScrollPane scrollPane = new JScrollPane();
    private final JComboBox<String> comboBox = new JComboBox<>();
    private final JTextArea textArea = new JTextArea();


    public LogViewerDialog() {
        super(LOG_VIEWER_WINDOW_TITLE,"","[grow]","[][grow][]");
        setSize(windowSize);

        add(lblLogfiles, cell(0,0).flowX());
        add(scrollPane, cell(0,1).grow());
        add(comboBox, cell(0,0).growX());
        add(closeBtn, cell(0,2).alignX("right"));

        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);

        setLocationRelativeTo(getParent());
        closeBtn.addActionListener(dialog -> dispose());
        comboBox.addActionListener(combo -> displayContentOfSelectedFile());
    }

    public void open() {
        setVisible(true);
        setSize(windowSize);
        populateCombo();
    }

    private void populateCombo() {
        setTitle("Loading available log files ...");
        for (File f : findLogfilesAndSortThemByModDate()) {
            comboBox.addItem(f.getName());
        }
        setTitle(LOG_VIEWER_WINDOW_TITLE);

    }

    private void displayContentOfSelectedFile() {
        String selectedItem = (String) comboBox.getSelectedItem();

        if (selectedItem != null) {
            File f = new File("logs", selectedItem);
            try {
                // unpack and load if gzipped, just load otherwise
                String fileContent = GzipUtils.isGzipped(f) ? GzipUtils.decompress(f) : FileUtils.readFileToString(f, UTF_8);
                textArea.setText(fileContent);
            } catch (IOException e) {
                textArea.setText("Can't open file: " + e);
            }
        }
    }

    private List<File> findLogfilesAndSortThemByModDate() {

        return FileUtils.listFiles(new File("logs"), new String[]{"gz", "log"}, false)
                .stream()
                .sorted(Comparator.comparingLong(File::lastModified).reversed())
                .toList();

    }
}
