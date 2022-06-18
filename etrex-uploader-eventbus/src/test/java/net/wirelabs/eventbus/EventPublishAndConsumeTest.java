package net.wirelabs.eventbus;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class EventPublishAndConsumeTest extends BaseTest {



    @BeforeEach
    void before() {
        EventBus.subscribersByEventType.clear();
        EventBus.deadEvents.clear();
    }


    @Test
    void shouldPublishEventToAllSubscribers() {

        TestEventListener listener1 = new TestEventListener();
        TestEventListener listener2 = new TestEventListener();
        TestEventListener listener3 = new TestEventListener();

        listener1.subscribe(EVENT_1, EVENT_2);
        listener2.subscribe(EVENT_1, EVENT_2);
        listener3.subscribe(EVENT_1);

        Event ev1 = new Event(EVENT_1,"ev1");
        Event ev2 = new Event(EVENT_2,"ev2");

        EventBus.publish(ev1);
        EventBus.publish(ev2);
        EventBus.publish(ev2);
        EventBus.publish(ev2);
        EventBus.publish(ev2);

        Sleeper.sleepSeconds(1);
        assertThat(listener1.eventsConsumed).containsExactly(ev1,ev2,ev2,ev2,ev2);
        assertThat(listener2.eventsConsumed).containsExactly(ev1,ev2,ev2,ev2,ev2);
        assertThat(listener3.eventsConsumed).containsOnly(ev1);
        stopListeners(listener1,listener2, listener3);

    }


    @Test
    void shouldRegisterDeadEventWhenPublishingBeforeASubscriberIsSubscribed() {
        Object obj = "1234";
        EventBus.publish(EVENT_1, obj);
        assertThat(EventBus.deadEvents).hasSize(1);
        assertThat(EventBus.deadEvents.get(0).getEventType()).isEqualTo(EVENT_1);
        assertThat(EventBus.deadEvents.get(0).getPayload()).isEqualTo(obj);

    }
}
