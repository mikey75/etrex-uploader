package utils;

import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created 11/1/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class SleeperTest {

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

}