package net.wirelabs.etrex.uploader.hardware.threads;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadUtils {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executorService);
    }

    public static void waitForCompleted(CompletableFuture<?> future) {
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
