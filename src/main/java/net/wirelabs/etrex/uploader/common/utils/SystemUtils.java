package net.wirelabs.etrex.uploader.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Created 12/21/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SystemUtils {

    public static boolean isLinux() {
        return getOsName().toLowerCase().startsWith("linux");
    }

    public static boolean isWindows() {
        return getOsName().toLowerCase().startsWith("windows");
    }

    private static String getOsName() {
        return System.getProperty("os.name");
    }

    public static void openSystemBrowser(String url) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process browserSubprocess;
        if (SystemUtils.isLinux()) {
            browserSubprocess = runtime.exec("xdg-open " + url);
            waitForSubprocess(browserSubprocess);
        } else if (SystemUtils.isWindows()) {
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
