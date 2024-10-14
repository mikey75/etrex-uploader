package net.wirelabs.etrex.uploader.gui.components;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.IEventType;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

@Slf4j
public class TestEventAwarePanel extends EventAwareBorderedPanel {

    public JTextField textField = new JTextField();

    protected TestEventAwarePanel(String title) {
        super(title);
    }

    @Override
    protected void onEvent(Event event) {
        textField.setText((String) event.getPayload());
        log.info("Event {} serviced", event.getEventType());
    }

    @Override
    protected Collection<IEventType> subscribeEvents() {
        return Collections.singletonList(EventType.RATELIMIT_INFO_UPDATE);
    }
}
