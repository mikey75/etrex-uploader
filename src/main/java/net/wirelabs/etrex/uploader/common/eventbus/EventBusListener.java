package net.wirelabs.etrex.uploader.common.eventbus;

import lombok.Getter;
import net.wirelabs.etrex.uploader.common.thread.BaseStoppableRunnable;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created 6/21/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Getter
public abstract class EventBusListener extends BaseStoppableRunnable {

    private final CopyOnWriteArrayList<Event> eventsQueue = new CopyOnWriteArrayList<>();

    protected abstract void onEvent(Event evt);

    protected EventBusListener() {
        start();
    }

    @Override
    public void run() {
        loopUntilStopped(this::processEvents);
    }

    void loopUntilStopped(Runnable code) {
        while (!getShouldExit().get()) {
            code.run();
        }
    }

    private void processEvents() {
        Optional<Event> evt = eventsQueue.stream().findFirst();
        evt.ifPresent(event -> {
            onEvent(event);
            eventsQueue.remove(event);
        });
        Sleeper.sleepMillis(50);
    }

    public void subscribe(Object... eventTypes) {
        EventBus.register(this, eventTypes);
    }

}
