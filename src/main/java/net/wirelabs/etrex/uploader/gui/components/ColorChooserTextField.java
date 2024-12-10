package net.wirelabs.etrex.uploader.gui.components;

import lombok.NoArgsConstructor;
import net.wirelabs.etrex.uploader.common.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Created 9/9/23 by Michał Szwaczko (mikey@wirelabs.net)
 */

@NoArgsConstructor
public class ColorChooserTextField extends ButtonedTextField {

    private static final ImageIcon icon = new ImageIcon(Objects.requireNonNull(ColorChooserTextField.class.getResource("/icons/colorpal.png")));

    public ColorChooserTextField(String text) {
        super(text, Constants.EMPTY_STRING, icon);
    }

    @Override
    protected void onButtonClick() {
        Color chosen = (getText().isBlank()) ? Color.BLACK : Color.decode(getText());
        Color c = JColorChooser.showDialog(null, "Choose color", chosen);
        if (c != null) {
            setText(convertColorToAscii(c));
        }
    }

    private String convertColorToAscii(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

}
