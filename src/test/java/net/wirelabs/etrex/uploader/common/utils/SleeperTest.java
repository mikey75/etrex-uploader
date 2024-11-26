package net.wirelabs.etrex.uploader.common.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.awaitility.Awaitility;
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
        Thread t = new Thread(() -> Sleeper.sleepSeconds(2));
        t.start();

        // wait for start
        Awaitility.waitAtMost(Duration.ofSeconds(1)).until(t::isAlive);

        // interrupt
        t.interrupt();

        // wait for interruption
        Awaitility.waitAtMost(Duration.ofSeconds(1)).until(t::isInterrupted);

        // assert log
        verifyLogged("Error sleeping");
    }
}