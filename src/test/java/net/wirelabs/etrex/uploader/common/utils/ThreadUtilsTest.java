package net.wirelabs.etrex.uploader.common.utils;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.CALLS_REAL_METHODS;

@Slf4j
class ThreadUtilsTest extends BaseTest {

    @Test
    void shouldRunTaskAsynchronously() {

        Runnable r = () -> {
            log.info("Running task asynchronously");
            Sleeper.sleepMillis(500);
            log.info("Task finished");

        };
        CompletableFuture<Void> task = ThreadUtils.runAsync(r);
        ThreadUtils.waitForCompleted(task);

        verifyLogged("Running task asynchronously");
        verifyLogged("Task finished");
    }

    @Test
    void shouldWaitForAllTasksAndShutdownExecutorService(){
        // in this test we really shut down executor service which is static final,
        // so we need to work on non-default ExecutorService so that other tests
        // using ThreadUtils don't run into already shutdown executor service

        ExecutorService tempExecutorService = Executors.newCachedThreadPool();

        try (MockedStatic<ThreadUtils> threadUtils = Mockito.mockStatic(ThreadUtils.class, CALLS_REAL_METHODS)) {
            threadUtils.when(ThreadUtils::getExecutorService).thenReturn(tempExecutorService);

            Runnable r = () -> Sleeper.sleepMillis(500);
            ThreadUtils.runAsync(r);
            ThreadUtils.shutdownExecutorService();

            verifyLogged("Waiting for the main ExecutorService to terminate");
            verifyLogged("Done.");

        }


    }


}