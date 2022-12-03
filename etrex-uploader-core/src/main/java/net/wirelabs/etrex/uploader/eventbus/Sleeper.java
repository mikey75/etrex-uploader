package net.wirelabs.etrex.uploader.eventbus;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Sleeper {
    
    public static void sleepSeconds(long amount) {
        sleep(TimeUnit.SECONDS, amount);
    }
    
    public static void sleepMillis(long amount) {
        sleep(TimeUnit.MILLISECONDS, amount);
    }

    public static void sleepMinutes(long amount) {
        sleep(TimeUnit.MINUTES, amount);
    }

    private static void sleep(TimeUnit timeUnit, long amount) {
        try {
            timeUnit.sleep(amount);
        } catch (InterruptedException ex) {
            log.error("Error sleeping", ex);
            Thread.currentThread().interrupt();
        }
    }
}