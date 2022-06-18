package net.wirelabs.eventbus;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Getter
public abstract class EventBusListener extends EventListener {

    private final CopyOnWriteArrayList<Event> eventsQueue = new CopyOnWriteArrayList<>();

    protected abstract void onEvent(Event evt);

    protected EventBusListener() {
        
        startListener(() -> {
            Optional<Event> evt = eventsQueue.stream().findFirst();
            evt.ifPresent(event -> {
                onEvent(event);
                eventsQueue.remove(event);
            });
        });


    }

    
    public void subscribe(Object... eventTypes) {
        EventBus.register(this, eventTypes);
    }


}
