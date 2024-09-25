package net.wirelabs.etrex.uploader.gui.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Created 9/9/23 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class ColorChooserTextField extends JPanel {

    private final JTextField textfield;
    private final JButton button;

    public ColorChooserTextField() {

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

        button.addActionListener(e -> showColorChooser());
    }

    private void showColorChooser() {
        Color chosen;

        if (textfield.getText().isBlank()) {
            chosen = Color.BLACK;
        } else{
            chosen = Color.decode(textfield.getText());
        }

        Color c = JColorChooser.showDialog(null,"Choose color", chosen );
        if (c != null) {
         textfield.setText(convertColorToAscii(c));
        }
    }

    private String convertColorToAscii(Color c) {
         return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    public void setText(String text) {
        textfield.setText(text);
    }
    public String getText() {
        return textfield.getText();
    }
}
