package net.wirelabs.etrex.uploader.gui.components;

import net.wirelabs.etrex.uploader.common.utils.ListUtils;

import javax.swing.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileChooserTextField extends ButtonedTextField {

    private final boolean allowMultiple;
    private final boolean dirsOnly;
    private transient List<Path> paths = new ArrayList<>();

    public FileChooserTextField(boolean dirsOnly, boolean allowMultiple) {
        this.dirsOnly = dirsOnly;
        this.allowMultiple = allowMultiple;
        setButtonText("...");
    }

    @Override
    protected void onButtonClick() {
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
                setText(ListUtils.convertPathListToString(paths));
            }
        } else {
            paths.clear();
            paths.add(selectedItem);
            setText(ListUtils.convertPathListToString(paths));
        }
    }

    public List<Path> getPaths() {
        this.paths = ListUtils.convertStringListToPaths(textfield.getText());
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = new ArrayList<>(paths);
        setText(ListUtils.convertPathListToString(paths));
    }
}
