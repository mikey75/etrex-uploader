package net.wirelabs.etrex.uploader.hardware.threads;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base implementation of StoppableRunnable - to be used when no other classes need to be extended.
 * In case when a class already extends some other class - use StoppableRunnable interface instead.
 */
public abstract class BaseStoppableRunnable implements StoppableRunnable {

    protected final AtomicBoolean shouldExit = new AtomicBoolean(false);
    private CompletableFuture<?> threadHandle;

    @Override
    public void start() {
        threadHandle = ThreadUtils.runAsync(this);
    }

    @Override
    public AtomicBoolean getShouldExit() {
        return shouldExit;
    }

    @Override
    public CompletableFuture<?> getThreadHandle() {
        return threadHandle;
    }
}
