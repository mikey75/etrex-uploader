package net.wirelabs.etrex.uploader.gui.components;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Created 9/9/23 by Michał Szwaczko (mikey@wirelabs.net)
 */

public class ColorChooserTextField extends ButtonedTextField {

    public ColorChooserTextField() {
        Icon folderIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/colorpal.png")));
        setButtonIcon(folderIcon);
    }

    @Override
    protected void onButtonClick() {
        Color chosen;

        if (getText().isBlank()) {
            chosen = Color.BLACK;
        } else {
            chosen = Color.decode(textfield.getText());
        }

        Color c = JColorChooser.showDialog(null, "Choose color", chosen);
        if (c != null) {
            setText(convertColorToAscii(c));
        }
    }

    private String convertColorToAscii(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

}
