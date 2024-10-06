package net.wirelabs.etrex.uploader.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.eventbus.EventBus;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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

    public static void checkGraphicsEnvironmentPresent() {
        if (GraphicsEnvironment.isHeadless()) {
            log.error("This application needs graphics environment - X11 or Windows");
            System.exit(1);
        }
    }

    public static String getAppVersion() {
        try {
            return FileUtils.readFileToString(new File("etrex-uploader.version"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Can't find or load etrex-uploader.version file");
            return "UNKNOWN";
        }
    }

    public static void createNewInstance() {
        Optional<String> cmd = ProcessHandle.current().info().commandLine();
        if (cmd.isPresent()) {
            Runtime rt = Runtime.getRuntime();
            try {
                log.info("Restarting application");
                rt.exec(cmd.get());
            } catch (IOException e) {
                log.error("Restarting application failed. Nothing to do. Exiting!");
            }
        }
    }

    public static void shutdownAndExit() {
        EventBus.shutdown();
        System.exit(1);
    }
}
