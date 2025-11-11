package net.wirelabs.etrex.uploader.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created 11/1/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class SleeperTest extends BaseTest {

    @Test
    void shouldSleep() {

        long s1 = System.currentTimeMillis();
        Sleeper.sleepMillis(500);
        long s2 = System.currentTimeMillis();
        assertThat(s2-s1).isGreaterThanOrEqualTo(500);

        s1 = System.currentTimeMillis();
        Sleeper.sleepSeconds(1);
        s2 = System.currentTimeMillis();
        assertThat(s2-s1).isGreaterThanOrEqualTo(1000);
    }

    @Test
    void testSleepInterruptionSignaling() {
        // test sleep interruption exception
        Thread thread = new Thread(() -> Sleeper.sleepSeconds(2));
        thread.start();

        // wait for start
        waitUntilAsserted(Duration.ofSeconds(1), thread::isAlive);

        // interrupt
        thread.interrupt();

        // wait for interruption
        waitUntilAsserted(Duration.ofSeconds(1), thread::isInterrupted);

        // assert log
        verifyLogged("Error sleeping");
    }
}