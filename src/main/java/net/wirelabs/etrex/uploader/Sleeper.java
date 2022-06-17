package net.wirelabs.etrex.uploader;

import java.util.concurrent.TimeUnit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)

public class Sleeper {
    public static void sleep(TimeUnit timeUnit, long amount) {
        try {
            timeUnit.sleep(amount);
        } catch (InterruptedException ex) {
            log.error("Error sleeping", ex);
            Thread.currentThread().interrupt();
        }
    }
}
