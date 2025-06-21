package net.wirelabs.etrex.uploader.tools;

import lombok.Getter;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.components.BaseDialog;

import javax.swing.*;

public class TestDialog extends BaseDialog {

    @Getter
    private final JTextField textField = new JTextField();

    /**
     * Create the dialog.
     */
    public TestDialog() {
        super("Test", "", "[grow,fill]", "[grow]");

        setBounds(0, 0, 400, 80);
        textField.setColumns(30);

        add(textField, "cell 0 0");
        SwingUtils.centerComponent(this);
        SwingUtils.registerPasteController();
        setVisible(true);

    }

}