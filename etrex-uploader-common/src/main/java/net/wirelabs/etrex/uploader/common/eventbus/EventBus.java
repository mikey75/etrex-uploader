package net.wirelabs.etrex.uploader.common.eventbus;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventBus {

    static final Map<Object, Set<EventBusListener>> subscribersByEventType = new HashMap<>();
    static final Set<EventBusListener> uniqueListeners = new HashSet<>();
    static final List<Event> deadEvents = new ArrayList<>();
    
    
    public static void stop() {
        for (EventBusListener listener: uniqueListeners) {
            log.info("Stopping listener: {}", listener);
            listener.stopListener();
        }
    }
    public static void register(EventBusListener listener, Object... eventTypes) {
        for (Object evt : eventTypes) {
            subscribersByEventType.computeIfAbsent(evt, k -> new HashSet<>());
            subscribersByEventType.get(evt).add(listener);
            uniqueListeners.add(listener);
            
        }

    }


    /**
     * Publish event
     * @param event event object
     */
    public static void publish(Event event) {
        
        Object evt = event.getEventType();

        if (subscribersByEventType.containsKey(evt)) {
            Set<EventBusListener> subs = subscribersByEventType.get(evt);
            for (EventBusListener listener : subs) {
                listener.getEventsQueue().add(event);
            }
        } else {
            deadEvents.add(event);
        }
    }

    /**
     * Publish event
     * @param eventType event type
     * @param payload payload object
     */
    public static void publish(Object eventType, Object payload) {
        // get all listners subscribed to the event
        Event event = new Event(eventType, payload);
        publish(event);


    }
}
