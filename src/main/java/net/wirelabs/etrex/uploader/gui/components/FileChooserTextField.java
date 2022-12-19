package net.wirelabs.etrex.uploader.gui.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class FileChooserTextField extends JPanel {


    private final JTextField textfield;
    private final JButton button;


    public FileChooserTextField() {
        this(false,false);

    }

    public FileChooserTextField(boolean dirsOnly, boolean allowMultiple) {

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
            JFileChooser fileChooser = new JFileChooser();
            if (dirsOnly) {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            }
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                String selectedItem = fileChooser.getSelectedFile().getPath();
                addItem(selectedItem);
            }
        });
    }

    private void addItem(String selectedItem) {
        String s = getText();
        if (!getText().contains(selectedItem)) {
            s += s.isBlank() ? "" : ",";
            s += selectedItem;
            setText(s);
        }
    }


    public void setText(String text) {
        textfield.setText(text);
    }

    public String getText() {
        return textfield.getText();
    }
}
