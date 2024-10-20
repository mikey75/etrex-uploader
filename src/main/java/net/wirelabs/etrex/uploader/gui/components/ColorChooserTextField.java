package net.wirelabs.etrex.uploader.gui.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Created 9/9/23 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class ColorChooserTextField extends JPanel {

    private final JTextField textfield;
    final JButton colorChooserInvokerButton;

    private final LayoutManager layout = new MigLayout("gapx 0,insets 0", "[grow][]", "[]");

    public ColorChooserTextField() {

        setBorder(null);
        setLayout(layout);

        // the textfield
        textfield = new JTextField();
        textfield.setMinimumSize(new Dimension(32, 18));
        textfield.setMaximumSize(new Dimension(2400, 18));

        // the button
        colorChooserInvokerButton = new JButton("...");
        colorChooserInvokerButton.setMinimumSize(new Dimension(18, 18));
        colorChooserInvokerButton.setMaximumSize(new Dimension(18, 18));

        add(colorChooserInvokerButton, "cell 1 0");
        add(textfield, "cell 0 0,grow");

        colorChooserInvokerButton.addActionListener(e -> showColorChooser());
    }

    private void showColorChooser() {
        Color chosen;

        if (textfield.getText().isBlank()) {
            chosen = Color.BLACK;
        } else {
            chosen = Color.decode(textfield.getText());
        }

        Color c = JColorChooser.showDialog(null, "Choose color", chosen);
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
