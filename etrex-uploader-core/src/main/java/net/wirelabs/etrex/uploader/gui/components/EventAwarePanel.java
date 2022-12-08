package net.wirelabs.etrex.uploader.gui.components;


import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.eventbus.Event;
import net.wirelabs.etrex.uploader.common.eventbus.EventBus;
import net.wirelabs.etrex.uploader.common.eventbus.EventBusListener;

import javax.swing.*;
import java.util.Collection;


public abstract class EventAwarePanel extends JPanel {

    protected EventAwarePanel() {
        eventHandlerInitialize();
    }
    
    protected abstract void onEvent(Event evt);
    protected abstract Collection<EventType> subscribeEvents();
    
    private void eventHandlerInitialize() {

        EventBusListener eventListener = new EventBusListener() {
            @Override
            protected void onEvent(Event evt) {
                SwingUtilities.invokeLater(() -> EventAwarePanel.this.onEvent(evt));
            }
        };
        
        for (EventType evt: subscribeEvents()) {
            EventBus.register(eventListener,evt);
        }
        
    }
}
