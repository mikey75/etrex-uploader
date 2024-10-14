package net.wirelabs.etrex.uploader.gui.components;

import net.wirelabs.etrex.uploader.common.EventType;
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

    public static final String TEST_PANEL_TITLE = "test panel";

    @Test
    void BorderedPanelTest() {
        BorderedPanel testPanel = new BorderedPanel(TEST_PANEL_TITLE);
        // verify panel is bordered and has correct border title
        assertPanelIsBorderedAndHasCorrectTitle(testPanel);
    }

    @Test
    void EventAwareBorderedPanelTest() {
        // Since EventAwareBorderPanel is abstract - we need to create test implementation
        // The implementation: ->
        //  - listens for RATELIMIT_INFO_UPDATE
        //  - has titled border with  "test panel" title
        TestEventAwarePanel testPanel = Mockito.spy(new TestEventAwarePanel(TEST_PANEL_TITLE));

        // verify panel is bordered and has correct  border title
        assertPanelIsBorderedAndHasCorrectTitle(testPanel);

        // verify panel listens for RATELIMIT_INFO_UPDATE event
        assertThat(testPanel.subscribeEvents())
                .hasSize(1)
                .contains(EventType.RATELIMIT_INFO_UPDATE);

        // publish event
        Event ev = new Event(EventType.RATELIMIT_INFO_UPDATE, "123");
        EventBus.publish(ev);

        // check if event is serviced
        Awaitility.waitAtMost(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    verifyLogged("Event RATELIMIT_INFO_UPDATE serviced");
                    // the value should be set to the event payload value
                    assertThat(testPanel.textField.getText()).isEqualTo("123");
                });

        // publish unsubscribed event
        ev = new Event(EventType.DEVICE_DRIVE_REGISTERED, "123");
        EventBus.publish(ev);

        // check event never serviced
        Awaitility.waitAtMost(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    verifyNeverLogged("Event DEVICE_DRIVE_REGISTERED serviced");
                    // the value changed by previous event service should not be changed
                    assertThat(testPanel.textField.getText()).isEqualTo("123");
                });

    }

    private static void assertPanelIsBorderedAndHasCorrectTitle(JPanel testPanel) {
        assertThat(testPanel.getBorder()).isInstanceOf(TitledBorder.class)
                .hasFieldOrPropertyWithValue("title", TEST_PANEL_TITLE);
    }

}