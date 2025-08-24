package net.wirelabs.etrex.uploader.thread;

import net.wirelabs.etrex.uploader.utils.ThreadUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Use to receive automatic implementation of stop() method.
 * Requirements:
 * 1. The class implements Runnable's run() method with a loop that exits when an AtomicBoolean becomes true.
 * This AtomicBoolean is usually named shouldExit.
 * 2. The class starts its thread using {@code CompletableFuture threadHandle = ThreadUtils.runAsync(this)}
 */
public interface StoppableRunnable extends Runnable {

    /**
     * The implementation should perform: threadHandle = ThreadUtils.runAsync(this);
     * Where threadHandle should be the same object as the object returned by getThreadHandle()
     */
    void start();

    AtomicBoolean getShouldExit();

    CompletableFuture<Void> getThreadHandle();

    default void stop() {
        getShouldExit().set(true);
        ThreadUtils.waitForCompleted(getThreadHandle());
    }
}
