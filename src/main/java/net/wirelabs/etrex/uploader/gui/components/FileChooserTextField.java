package net.wirelabs.etrex.uploader.gui.components;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.utils.ListUtils;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileChooserTextField extends JPanel {

    private final JTextField textfield;
    private final JButton button;
    private final boolean allowMultiple;
    private final boolean dirsOnly;

    private List<Path> paths = new ArrayList<>();

    public FileChooserTextField() {
        this(false, false);
    }

    public FileChooserTextField(boolean dirsOnly, boolean allowMultiple) {
        this.dirsOnly = dirsOnly;
        this.allowMultiple = allowMultiple;
        setBorder(null);
        setLayout(new MigLayout("gapx 0,insets 0", "[grow][]", "[]"));

        // the textfield
        textfield = new JTextField();
        textfield.setMinimumSize(new Dimension(32, 18));
        textfield.setMaximumSize(new Dimension(2400, 18));

        // the button
        button = new JButton("...");
        button.setMinimumSize(new Dimension(18, 18));
        button.setMaximumSize(new Dimension(18, 18));

        add(button, "cell 1 0");
        add(textfield, "cell 0 0,grow");

        button.addActionListener(e -> {
            showFileChooser();
        });
    }

    private void showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        if (dirsOnly) {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            Path selectedItem = fileChooser.getSelectedFile().toPath();
            addItem(selectedItem);
        }
    }

    private void addItem(Path selectedItem) {
        if (allowMultiple) {
            if (!paths.contains(selectedItem)) {
                paths.add(selectedItem);
                textfield.setText(ListUtils.convertPathListToString(paths));
            }
        } else {
            paths.clear();
            paths.add(selectedItem);
            textfield.setText(ListUtils.convertPathListToString(paths));
        }
    }

    public List<Path> getPaths() {
        this.paths = ListUtils.convertStringListToPaths(textfield.getText());
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = new ArrayList<>(paths);
        textfield.setText(ListUtils.convertPathListToString(paths));
    }
}
