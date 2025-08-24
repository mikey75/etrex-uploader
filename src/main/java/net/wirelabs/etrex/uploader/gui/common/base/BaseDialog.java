package net.wirelabs.etrex.uploader.gui.common.base;

import lombok.NoArgsConstructor;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * Base dialog -> no-arg constructor = generic
 * String constructor -> setTitle
 */
@NoArgsConstructor
public class BaseDialog extends JDialog implements LayoutConstraintsSettable {
    // default layout grid 1x1 fill all space
    protected final MigLayout layout = new MigLayout("","[grow]","[grow]");

    public BaseDialog(String title) {
        setTitle(title);
    }
    // border, title , constraints specified
    public BaseDialog(String title, String layoutConstraints, String columnConstraints, String rowConstraints) {
        setTitle(title);
        setConstraints(layout, layoutConstraints, columnConstraints, rowConstraints);
        setLayout(layout);
    }
}
