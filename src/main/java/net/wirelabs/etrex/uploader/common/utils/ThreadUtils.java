package net.wirelabs.etrex.uploader.common.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created 11/11/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadUtils {
    @Getter
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, getExecutorService());
    }

    public static void waitForCompleted(CompletableFuture<Void> future) {
        if (future != null) {
            future.join();
        }
    }

    /**
     * Shut down executor service - since we call it only on application exit or
     * in order to exit application from anywhere - no other tasks should run
     * when no other tasks should run - we do not wait for any tasks to finish,
     * just close the service and shutdown app, but emit warning if any task was queued but not started
     * before calling shutdown
     */
    public static void shutdownExecutorService() {
        log.info("Shutting down executor service");
        List<Runnable> queuedTasks = getExecutorService().shutdownNow();
        if (!queuedTasks.isEmpty()) {
            log.warn("Queued tasks left: {}", queuedTasks.size());
        }
        log.info("Executor service shut down!");

    }

}
