package net.wirelabs.etrex.uploader.gui.common.base;

import net.wirelabs.etrex.uploader.gui.common.TestEvent;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.EventBus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.border.*;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class BorderedPanelsTest extends BaseTest {

    private static final String TEST_PANEL_TITLE = "test panel";
    private static final String SERVICED_EVENT_PAYLOAD = "123";
    private static final String UNSERVICED_EVENT_PAYLOAD = "456";

    @Test
    void BorderedPanelTest() {
        BasePanel testPanel = new BasePanel(TEST_PANEL_TITLE);
        assertPanelIsBorderedAndHasCorrectTitle(testPanel);
    }

    @Test
    void EventAwareBorderedPanelTest() {
        // Since EventAwareBorderPanel is abstract - we need to create test implementation
        // The implementation: ->
        //  - listens for TEST_EVENT
        //  - has titled border with  "test panel" title
        TestEventAwarePanel testPanel = Mockito.spy(new TestEventAwarePanel(TEST_PANEL_TITLE));

        assertPanelIsBorderedAndHasCorrectTitle(testPanel);

        // verify panel listens for TEST_EVENT_1 event
        assertThat(testPanel.subscribeEvents())
                .hasSize(1)
                .contains(TestEvent.TEST_EVENT_1);

        // publish event
        Event ev = new Event(TestEvent.TEST_EVENT_1, SERVICED_EVENT_PAYLOAD);
        EventBus.publish(ev);

        // check if event is serviced
        waitUntilAsserted(Duration.ofSeconds(2), () -> {
                    verifyLogged("Event TEST_EVENT_1 serviced");
                    // the value should be set to the event payload value
                    assertThat(testPanel.textField.getText()).isEqualTo(SERVICED_EVENT_PAYLOAD);
                });

        // publish the event that is unsubscribed and has different payload
        ev = new Event(TestEvent.TEST_EVENT_2, UNSERVICED_EVENT_PAYLOAD);
        EventBus.publish(ev);

        // check event never serviced
        waitUntilAsserted(Duration.ofSeconds(1), () -> {
                    verifyNeverLogged("Event TEST_EVENT_2 serviced");
                    // the value changed by previous event service should not be changed
                    assertThat(testPanel.textField.getText()).isEqualTo(SERVICED_EVENT_PAYLOAD);
                });

    }

    private static void assertPanelIsBorderedAndHasCorrectTitle(JPanel testPanel) {
        assertThat(testPanel.getBorder()).isInstanceOf(TitledBorder.class)
                .hasFieldOrPropertyWithValue("title", TEST_PANEL_TITLE);
    }

}