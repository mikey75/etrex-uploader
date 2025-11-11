package net.wirelabs.etrex.uploader.tools;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.eventbus.EventBus;
import org.apache.commons.io.FileUtils;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseTest {

    // Base test for all tests with some common test features
    private final LogVerifier logVerifier = new LogVerifier();
    @AfterEach
    void after() {
        if (!GraphicsEnvironment.isHeadless()) {
            // close any lingering windows
            for (Window w : Window.getWindows()) {
                w.dispose();
            }
        }
    }
    @BeforeEach
    void beforeEach() {
       logVerifier.clearLogs();
    }

    protected void verifyNeverLogged(String message) {
        logVerifier.verifyNeverLogged(message);
    }

    protected void verifyLogged(String message) {
        logVerifier.verifyLogged(message);
    }

    protected void verifyLoggedTimes(int times, String message) {
        logVerifier.verifyLoggedTimes(times, message);
    }

    // must be static, since it may be used in @BeforeAll (which must be static)
    protected static void waitUntilAsserted(Duration duration, ThrowingRunnable assertion) {
        Awaitility.await().atMost(duration).untilAsserted(assertion);
    }

    protected static void preserveFiles(File... files) throws IOException {
        for (File f: files) {
            if (f.exists()) {
                FileUtils.copyFile(f, new File(f.getPath() + ".orig"), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    protected static void restoreFiles(File... files) throws IOException {
        for (File f: files) {
            File copy = new File(f.getPath()+".orig");
            if (copy.exists()) {
                FileUtils.copyFile(copy, f, StandardCopyOption.REPLACE_EXISTING);
                FileUtils.deleteQuietly(copy);
            }
        }

    }

    protected static void eventBusReset() {
        EventBus.getUniqueClients().clear();
        EventBus.getDeadEvents().clear();
        EventBus.getSubscribersByEventType().clear();
        waitUntilAsserted(Duration.ofSeconds(2),() -> {
            assertThat(EventBus.getDeadEvents()).isEmpty();
            assertThat(EventBus.getUniqueClients()).isEmpty();
            assertThat(EventBus.getSubscribersByEventType()).isEmpty();
        });
    }
}
