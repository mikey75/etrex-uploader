package net.wirelabs.etrex.uploader.gui.common.base;

import lombok.NoArgsConstructor;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.*;

/**
 * Base application JPanel - titled/bordered and generic in one class
 * - want generic: call no-arg constructor
 * - want titled: call string-argument constructor
 */
@NoArgsConstructor
public class BasePanel extends JPanel implements LayoutConstraintsSettable {
    // default layout grid 1x1 fill all space
    protected final MigLayout layout = new MigLayout("","[grow]","[grow]");
    // no border no title, constraints specified
    public BasePanel(String layoutConstraints, String columnConstraints, String rowConstraints) {
        setConstraints(layout, layoutConstraints,columnConstraints,rowConstraints);
        setLayout(layout);
    }
    // border, title , constraints specified
    public BasePanel(String title, String layoutConstraints, String columnConstraints, String rowConstraints) {
        setConstraints(layout, layoutConstraints, columnConstraints, rowConstraints);
        setBorderTitle(title);
        setLayout(layout);
    }
    // border, title, constraints not specified (means: default constraints)
    public BasePanel(String title) {
        setBorderTitle(title);
        setLayout(layout);
    }

    protected void setBorderTitle(String title) {
        Border border = new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP);
        setBorder(border);
    }
}
