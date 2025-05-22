package net.wirelabs.etrex.uploader.gui.components;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.IEventType;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

@Slf4j
@NoArgsConstructor
class TestEventAwarePanel extends BaseEventAwarePanel {

    public final JTextField textField = new JTextField();
    TestEventAwarePanel(String title) {
        super(title);
    }

    @Override
    protected void onEvent(Event event) {
        textField.setText((String) event.getPayload());
        log.info("Event {} serviced", event.getEventType());
    }

    @Override
    protected Collection<IEventType> subscribeEvents() {
        return Collections.singletonList(TestEvent.TEST_EVENT_1);
    }
}
