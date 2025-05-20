package net.wirelabs.etrex.uploader.gui.components;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.EventBus;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.border.*;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class BorderedPanelsTest extends BaseTest {

    private static final String TEST_PANEL_TITLE = "test panel";
    private static final String servicedEventPayload = "123";
    private static final String unservicedEventPayload = "456";

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
        Event ev = new Event(TestEvent.TEST_EVENT_1, servicedEventPayload);
        EventBus.publish(ev);

        // check if event is serviced
        Awaitility.waitAtMost(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    verifyLogged("Event TEST_EVENT_1 serviced");
                    // the value should be set to the event payload value
                    assertThat(testPanel.textField.getText()).isEqualTo(servicedEventPayload);
                });

        // publish the event that is unsubscribed and has different payload
        ev = new Event(TestEvent.TEST_EVENT_2, unservicedEventPayload);
        EventBus.publish(ev);

        // check event never serviced
        Awaitility.waitAtMost(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    verifyNeverLogged("Event TEST_EVENT_2 serviced");
                    // the value changed by previous event service should not be changed
                    assertThat(testPanel.textField.getText()).isEqualTo(servicedEventPayload);
                });

    }

    private static void assertPanelIsBorderedAndHasCorrectTitle(JPanel testPanel) {
        assertThat(testPanel.getBorder()).isInstanceOf(TitledBorder.class)
                .hasFieldOrPropertyWithValue("title", TEST_PANEL_TITLE);
    }

}