package net.wirelabs.etrex.uploader.gui.components;

import net.wirelabs.eventbus.swing.EventAwarePanel;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public abstract class EventAwareBorderedPanel extends EventAwarePanel {

    protected EventAwareBorderedPanel(String title) {
        setBorderTitle(title);
    }

    protected void setBorderTitle(String title) {
        Border border = new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP);
        setBorder(border);
    }
}
