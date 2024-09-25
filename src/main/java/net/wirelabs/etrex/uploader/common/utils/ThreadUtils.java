package net.wirelabs.etrex.uploader.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Created 11/11/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadUtils {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executorService);
    }

    public static void waitForCompleted(CompletableFuture<Void> future) {
        if (future != null) {
            future.join();
        }
    }

    public static void shutdownExecutorService() {
        executorService.shutdown();
        waitForExecutorServiceTermination();
    }

    private static void waitForExecutorServiceTermination() {
        while (true) {
            try {
                log.info("Waiting for the main ExecutorService to terminate...");
                if (executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                    log.info("Done.");
                    break;
                }
            } catch (InterruptedException ex) {
                log.error("Error waiting for the main ExecutorService to terminate", ex);
                Thread.currentThread().interrupt();
            }
        }
    }

}
