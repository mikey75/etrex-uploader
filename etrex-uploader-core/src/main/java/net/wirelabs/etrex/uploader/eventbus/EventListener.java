package net.wirelabs.etrex.uploader.eventbus;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;


/**
 * Created 6/21/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Getter
public abstract class EventListener {

    protected final AtomicBoolean shouldStop = new AtomicBoolean(false);
    protected final AtomicBoolean isRunning = new AtomicBoolean(false);

    public void stopListener() {
        shouldStop.set(true);
    }

    void startListener(EventBusListenerCallback eventListenerCallback) {
        new Thread(() -> {
            while (!shouldStop.get()) {
                isRunning.set(true);
                eventListenerCallback.execute();
                //todo make configurable delay
                Sleeper.sleepMillis(50);
            }
            isRunning.set(false);
        }).start();
    }
}
