package net.wirelabs.etrex.uploader.gui.common.components;

import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Common 'TextField with button' component
 */
public abstract class ButtonedTextField extends BasePanel {

    private final JTextField textField = new JTextField();
    private final JButton button = new JButton();

    // default constructor: no text, 3 dots as button text, and no icon
    protected ButtonedTextField() {
        this(Constants.EMPTY_STRING, "...", null);
    }

    // custom constructor: specify all attributes
    protected ButtonedTextField(String text, String buttonText, Icon buttonIcon) {
        super("gapx 0,insets 0", "[grow][]","[]");
        setBorder(null);
        textField.setMinimumSize(new Dimension(32, 18));
        textField.setMaximumSize(new Dimension(2400, 18));

        button.setMinimumSize(new Dimension(18, 18));
        button.setMaximumSize(new Dimension(18, 18));

        add(textField, "cell 0 0,grow");
        add(button, "cell 1 0");

        setupComponent(text, buttonText, buttonIcon);
    }

    private void setupComponent(String text, String buttonText, Icon buttonIcon) {

        if (!StringUtils.isBlank(text)) {
            setText(text);
        }

        if (!StringUtils.isBlank(buttonText)) {
            setButtonText(buttonText);
        }

        if (!Objects.isNull(buttonIcon)) {
            setButtonIcon(buttonIcon);
        }

        button.addActionListener(e -> onButtonClick());

    }

    public void setText(String text) {
        textField.setText(text);
    }

    public String getText() {
        return textField.getText();
    }

    public void setButtonText(String text) {
        button.setText(text);
    }

    public String getButtonText() {
        return button.getText();
    }

    public void setButtonIcon(Icon icon) {
        button.setIcon(icon);
    }

    public Icon getButtonIcon() {
        return button.getIcon();
    }

    // override this method in subclass to invoke button click action
    protected void onButtonClick() {
    }

}
