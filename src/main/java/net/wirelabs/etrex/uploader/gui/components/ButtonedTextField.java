package net.wirelabs.etrex.uploader.gui.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Common 'Textfield with button' component
 */
public abstract class ButtonedTextField extends JPanel  {

    protected final JTextField textfield = new JTextField();
    protected final JButton button = new JButton();
    private final LayoutManager layout = new MigLayout("gapx 0,insets 0", "[grow][]", "[]");

    protected ButtonedTextField() {

        setBorder(null);
        setLayout(layout);

        textfield.setMinimumSize(new Dimension(32, 18));
        textfield.setMaximumSize(new Dimension(2400, 18));

        button.setMinimumSize(new Dimension(18, 18));
        button.setMaximumSize(new Dimension(18, 18));

        add(textfield, "cell 0 0,grow");
        add(button, "cell 1 0");

        button.addActionListener(e -> onButtonClick());
    }

    public void setText(String text) {
        textfield.setText(text);
    }

    public String getText() {
        return textfield.getText();
    }

    protected void setButtonText(String text) {
        button.setText(text);
    }

    protected void setButtonIcon(Icon icon) {
        button.setIcon(icon);
    }

    // override this method in subclass to invoke button click action
    protected void onButtonClick() {}

}
