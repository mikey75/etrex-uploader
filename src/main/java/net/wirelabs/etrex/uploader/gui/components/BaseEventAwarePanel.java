package net.wirelabs.etrex.uploader.gui.components;

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
public abstract class BaseEventAwarePanel extends EventAwarePanel {
    // default layout grid 1x1 fill all space
    protected final MigLayout layout = new MigLayout("","[grow]","[grow]");
    protected BaseEventAwarePanel(String title) {
        setBorderTitle(title);
    }

    protected void setBorderTitle(String title) {
        Border border = new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP);
        setBorder(border);
    }
}
