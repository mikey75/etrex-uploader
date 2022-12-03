package net.wirelabs.etrex.uploader.eventbus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created 6/21/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class EventSubscriptionTest extends BaseTest {

    @BeforeEach
    void before() {
        EventBus.subscribersByEventType.clear();
    }

    @Test
    void shouldSubscribeMultipleListenersToMultipleEvents() {

        // given
        EventBusListener listener1 = new TestEventListener();
        EventBusListener listener2 = new TestEventListener();
        EventBusListener listener3 = new TestEventListener();

        // when
        listener1.subscribe(EVENT_1,EVENT_2,EVENT_3);
        listener2.subscribe(EVENT_1);
        listener3.subscribe(EVENT_1,EVENT_4);

        //then
        assertThat(EventBus.subscribersByEventType.get(EVENT_1)).containsOnly(listener1, listener2, listener3);
        assertThat(EventBus.subscribersByEventType.get(EVENT_2)).containsOnly(listener1);
        assertThat(EventBus.subscribersByEventType.get(EVENT_3)).containsOnly(listener1);
        assertThat(EventBus.subscribersByEventType.get(EVENT_4)).containsOnly(listener3);


        stopListeners(listener1, listener3, listener3);

    }

    @Test
    void shouldRegisterTwoEvents() {
        // given
        EventBusListener listener2 = new TestEventListener();
        EventBusListener listener3 = new TestEventListener();

        // given
        listener2.subscribe(EVENT_1, EVENT_2);
        listener3.subscribe(EVENT_1);

        assertThat(EventBus.subscribersByEventType.get(EVENT_1)).containsOnly(listener2, listener3);
        assertThat(EventBus.subscribersByEventType.get(EVENT_2)).containsOnly(listener2);

        stopListeners(listener2, listener3);
    }

    @Test
    void shouldIgnoreSameEventTypeOnSubscriber() {
        EventBusListener listener1 = new TestEventListener();

        listener1.subscribe(EVENT_1);
        listener1.subscribe(EVENT_1);
        listener1.subscribe(EVENT_1);

        assertThat(EventBus.subscribersByEventType.get(EVENT_1)).containsOnly(listener1);
        stopListeners(listener1);

    }

    @Test
    void shouldIgnoreRegistrationWithoutEvent() {
        EventBusListener listener1 = new TestEventListener();
        listener1.subscribe(); // no event
        assertThat(listener1.getEventsQueue()).isEmpty();
        stopListeners(listener1);
    }




}
