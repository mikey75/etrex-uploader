package net.wirelabs.etrex.uploader.gui.components;

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
public class BasePanel extends JPanel {
    // default layout grid 1x1 fill all space
    protected final MigLayout layout = new MigLayout("","[grow]","[grow]");

    // titled bordered panel
    public BasePanel(String title) {
        setBorderTitle(title);
    }

    protected void setBorderTitle(String title) {
        Border border = new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP);
        setBorder(border);
    }
}
