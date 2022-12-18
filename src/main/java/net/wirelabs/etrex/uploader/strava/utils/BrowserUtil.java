package net.wirelabs.etrex.uploader.strava.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/*
 * Created 12/15/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrowserUtil {

    private static String getOsName() {
        return System.getProperty("os.name");
    }

    private static boolean isLinux() {
        return getOsName().toLowerCase().startsWith("linux");
    }

    private static boolean isWindows() {
        return getOsName().toLowerCase().startsWith("windows");
    }

    public static void browseToUrl(String url) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process browserSubprocess;
        if (isLinux()) {
            browserSubprocess = runtime.exec("xdg-open " + url);
            waitForSubprocess(browserSubprocess);
        } else if (isWindows()) {
            browserSubprocess = runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
            waitForSubprocess(browserSubprocess);
        }

    }

    private static void waitForSubprocess(Process browserSubprocess) {
        try {
            int code = browserSubprocess.waitFor();
            log.info("Browser subprocess launched, exit code:{}", code);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
