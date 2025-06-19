package net.wirelabs.etrex.uploader.common.utils;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;

@Slf4j
@SuppressWarnings("ResultOfMethodCallIgnored")
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

        ExecutorService testExecutorService = Executors.newCachedThreadPool();

        try (MockedStatic<ThreadUtils> threadUtils = Mockito.mockStatic(ThreadUtils.class, CALLS_REAL_METHODS)) {
            threadUtils.when(ThreadUtils::getExecutorService).thenReturn(testExecutorService);

            Runnable r = this::longRunningTask;

            // run 4 task threads - don't wait for completion
            ThreadUtils.runAsync(r);
            ThreadUtils.runAsync(r);
            ThreadUtils.runAsync(r);
            ThreadUtils.runAsync(r);
            // shutdown executor service
            ThreadUtils.shutdownExecutorService();

            Awaitility.waitAtMost(Duration.ofSeconds(1)).untilAsserted(() -> {
                assertThat(testExecutorService.isShutdown()).isTrue();
                verifyLoggedTimes(4, "Running runnable task in a separate thread"); // assert task is run
                verifyLogged("Shutting down executor service"); // assert executor is going to be shut down
                verifyLogged("Executor service shut down!"); // assert it is shut down
                // since we stopped the tasks that was executing Sleeper.sleep() to emulate long-running
                // the sleep was interrupted surely, so check for that too
                verifyLoggedTimes(4,"Error sleeping");
            });

        }


    }

    @Test
    void shouldFindQueuedTasks() {
        // in this test we really shut down executor service which is static final,
        // so we need to work on non-default ExecutorService so that other tests
        // using ThreadUtils don't run into already shutdown executor service

        ExecutorService testExecutorService = Executors.newFixedThreadPool(1);
        // one-thread pool so other tasks will be queued and not started when shutdown

        try (MockedStatic<ThreadUtils> threadUtils = Mockito.mockStatic(ThreadUtils.class, CALLS_REAL_METHODS)) {
            threadUtils.when(ThreadUtils::getExecutorService).thenReturn(testExecutorService);

            Runnable r = this::longRunningTask;

            ThreadUtils.runAsync(r);
            ThreadUtils.runAsync(r);
            ThreadUtils.runAsync(r);
            ThreadUtils.runAsync(r);

            // shutdown executor service
            ThreadUtils.shutdownExecutorService();
            Awaitility.waitAtMost(Duration.ofSeconds(1)).untilAsserted(() -> {
                assertThat(testExecutorService.isShutdown()).isTrue();
                verifyLoggedTimes(1, "Running runnable task in a separate thread"); // assert first task is run
                verifyLogged("Shutting down executor service"); // assert executor is going to be shut down
                verifyLogged("Executor service shut down!"); // assert it is shut down
                verifyLogged("Queued tasks left: 3"); // we scheduled 4 tasks to a 1 thread pool, so 3 tasks still wait
                // since we stopped the task that was executing Sleeper.sleep() to emulate long-running
                // the sleep was interrupted surely, so check for that too
                verifyLoggedTimes(1,"Error sleeping");
            });

        }


    }

    private void longRunningTask() {
        log.info("Running runnable task in a separate thread");
        Sleeper.sleepSeconds(50);
    }
}