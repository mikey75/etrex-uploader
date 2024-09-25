package net.wirelabs.etrex.uploader.gui.components;

import net.wirelabs.eventbus.swing.EventAwarePanel;

import javax.swing.border.*;

public abstract class EventAwareBorderedPanel extends EventAwarePanel {

    protected EventAwareBorderedPanel(String title) {
        setBorderTitle(title);
    }

    protected void setBorderTitle(String title) {
        Border border = new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP);
        setBorder(border);
    }
}
