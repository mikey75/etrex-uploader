package net.wirelabs.etrex.uploader.hardware;

import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.awaitility.core.ThrowingRunnable;

public class TestUtils {
    
    public static void waitUntilAsserted(Duration duration, ThrowingRunnable assertion) {
        await().atMost(duration).untilAsserted(assertion);
    }
}
