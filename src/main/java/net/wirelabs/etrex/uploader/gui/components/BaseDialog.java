package net.wirelabs.etrex.uploader.gui.components;

import lombok.NoArgsConstructor;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * Base dialog -> no-arg constructor = generic
 * String constructor -> setTitle
 */
@NoArgsConstructor
public class BaseDialog extends JDialog {
    // default layout grid 1x1 fill all space
    protected final MigLayout layout = new MigLayout("","[grow]","[grow]");

    public BaseDialog(String title) {
        setTitle(title);
    }
}
