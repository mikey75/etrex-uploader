package net.wirelabs.eventbus;

import org.awaitility.core.ThrowingRunnable;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Created 8/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public abstract class BaseTest {

    public static final String EVENT_2 = "event2";
    public static final String EVENT_1 = "event1";
    public static final String EVENT_3 = "event3";
    public static final String EVENT_4 = "event4";

    protected void waitUntilAsserted(Duration duration, ThrowingRunnable assertion) {
        await().atMost(duration).untilAsserted(assertion);
    }

    protected void stopListeners(EventBusListener... listeners) {

        for (EventBusListener listener : listeners) {
            listener.stopListener();
        }

        waitUntilAsserted(Duration.ofSeconds(10), () -> {
            for (EventBusListener listener : listeners) {
                assertThat(listener.getIsRunning()).isFalse();
            }

        });
    }
}
