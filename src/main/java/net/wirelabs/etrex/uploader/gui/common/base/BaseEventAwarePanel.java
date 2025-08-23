package net.wirelabs.etrex.uploader.gui.common.base;

import lombok.NoArgsConstructor;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.eventbus.swing.EventAwarePanel;

import javax.swing.border.*;

/**
 * Event aware panels - border-titled, and generic in one class
 * - want generic: call no-arg constructor
 * - want titled: call string-argument constructor
 */
@NoArgsConstructor
public abstract class BaseEventAwarePanel extends EventAwarePanel implements LayoutConstraintsSettable {
    // default layout grid 1x1 fill all space
    protected final MigLayout layout = new MigLayout("","[grow]","[grow]");

    // no borderTitle present, constraints specified
    protected BaseEventAwarePanel(String layoutConstraints, String columnConstraints, String rowConstraints) {
        setConstraints(layout, layoutConstraints,columnConstraints,rowConstraints);
        setLayout(layout);
    }
    // borderTitle present, constraints specified
    protected BaseEventAwarePanel(String title, String layoutConstraints, String columnConstraints, String rowConstraints) {
        this(layoutConstraints, columnConstraints, rowConstraints);
        setBorderTitle(title);
    }
    // borderTitle present, constraints not specified (means: default constraints)
    protected BaseEventAwarePanel(String title) {
        setBorderTitle(title);
        setLayout(layout);
    }

    protected void setBorderTitle(String title) {
        Border border = new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP);
        setBorder(border);
    }
}
