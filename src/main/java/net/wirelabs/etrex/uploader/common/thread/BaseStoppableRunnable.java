package net.wirelabs.etrex.uploader.common.thread;

import net.wirelabs.etrex.uploader.common.utils.ThreadUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base implementation of StoppableRunnable - to be used when no other classes need to be extended.
 * In case when a class already extends some other class - use StoppableRunnable interface instead.
 */
public abstract class BaseStoppableRunnable implements StoppableRunnable {

    protected final AtomicBoolean shouldExit = new AtomicBoolean(false);
    private CompletableFuture<Void> threadHandle;

    @Override
    public void start() {
        threadHandle = ThreadUtils.runAsync(this);
    }

    @Override
    public AtomicBoolean getShouldExit() {
        return shouldExit;
    }

    @Override
    public CompletableFuture<Void> getThreadHandle() {
        return threadHandle;
    }

    protected void loopUntilStopped(Runnable code) {
        while (!getShouldExit().get()) {
            code.run();
        }
    }
}
