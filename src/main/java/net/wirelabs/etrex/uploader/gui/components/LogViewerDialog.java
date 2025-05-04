package net.wirelabs.etrex.uploader.gui.components;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.utils.GzipUtils;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class LogViewerDialog extends JDialog {

    public static final String LOG_VIEWER_WINDOW_TITLE = "Application Log viewer";

    private final JButton closeBtn = new JButton("Close");
    private final JLabel lblLogfiles = new JLabel("Logfile:");
    private final JScrollPane scrollPane = new JScrollPane();
    private final JComboBox<String> comboBox = new JComboBox<>();
    private final JTextArea textArea = new JTextArea();

    private final LayoutManager layout = new MigLayout("", "[grow]", "[][grow][]");

    public LogViewerDialog() {

        setTitle(LOG_VIEWER_WINDOW_TITLE);
        setSize(800, 600);

        getContentPane().setLayout(layout);
        getContentPane().add(lblLogfiles, "flowx,cell 0 0");
        getContentPane().add(scrollPane, "cell 0 1,grow");
        getContentPane().add(comboBox, "cell 0 0,growx");
        getContentPane().add(closeBtn, "cell 0 2,alignx right");

        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);

        setLocationRelativeTo(getParent());
        closeBtn.addActionListener(dialog -> dispose());
        comboBox.addActionListener(combo -> displayContentOfSelectedFile());
    }

    public void open() {
        setVisible(true);
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
