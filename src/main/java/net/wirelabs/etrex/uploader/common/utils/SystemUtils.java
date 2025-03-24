package net.wirelabs.etrex.uploader.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.eventbus.EventBus;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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

    public static boolean isOSX() {
        return getOsName().toLowerCase().startsWith("mac os x");
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
        } else if (SystemUtils.isOSX()) {
            browserSubprocess = runtime.exec("open " + url);
            waitForSubprocess(browserSubprocess);
        }

    }

    public static void checkOsSupport() {
        if (!isWindows() && !isLinux() && !isOSX()) {
            SwingUtils.errorMsg("Unsupported operating system, exiting!");
            System.exit(1);
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
            systemExit(1);
        }
    }

    public static String getJmapsVerion() {
        return getVersion("jmaps");
    }

    public static String getAppVersion() {
        return getVersion("etrex-uploader");
    }

    private static String getVersion(String versionSubject) { // subject is the name of the version file without .version part  so 'etrex-uploader' or 'jmaps' for instance
        try {
            return FileUtils.readFileToString(new File(versionSubject +".version"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Can't find or load {}.version file", versionSubject);
            return "1.0U"; // default for not found - looks ok for user and quietly suggests problem for developer
        }
    }


    public static void createNewInstance() {
        Optional<String> cmd = getCommandLine(ProcessHandle.current());
        if (cmd.isPresent()) {
            Runtime rt = Runtime.getRuntime();
            try {
                log.info("Creating new application instance");
                rt.exec(cmd.get());
            } catch (IOException e) {
                log.error("Creating new application instance failed!");
            }
        } else {
            log.error("No new instance could be created");
        }
    }

    public static void shutdownAndExit() {
        EventBus.shutdown();
        System.exit(1);
    }

    public static LocalDateTime getNow() {
        return LocalDateTime.now();
    }

    public static void systemExit(int status) {
        if (!EventBus.getExecutorService().isShutdown()) {
            EventBus.shutdown();
        }
        // also shutdown any lingering threads from ThreadUtils utility methods
        if (!ThreadUtils.getExecutorService().isShutdown()) {
            ThreadUtils.shutdownExecutorService();
        }
        System.exit(status);
    }

    public static String getWorkDir() {
        return System.getProperty("user.dir");
    }

    public static String getHomeDir() {
        return System.getProperty("user.home");
    }

    private static Optional<String> getCommandLine(ProcessHandle processHandle) {

        if (isLinux()) {
            return processHandle.info().commandLine();
        }

        if (isWindows()) {
            long desiredProcessid = processHandle.pid();
            try {
                // run windows command 'wmic process where ProcessID=pidOfTheApp get commandline /format:list'
                // which gets all running processes and filters it by ProcessID of the current app, and gets its commandline
                // so basically get a complete commandline of currently running application
                Process process = new ProcessBuilder("wmic", "process", "where", "ProcessID=" + desiredProcessid, "get",
                        "commandline", "/format:list").
                        redirectErrorStream(true).
                        start();
                try (InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
                     BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            return Optional.empty();
                        }
                        if (!line.startsWith("CommandLine=")) {
                            continue;
                        }
                        return Optional.of(line.substring("CommandLine=".length()));
                    }
                }
            } catch (IOException e) {
                log.error("Exception while getting current process command line: {}", e.getMessage());
                return Optional.empty();
            }
        }

        if (isOSX()) {
            log.warn("Not supported for macos as of now");
            return Optional.empty();
        }

        return Optional.empty();
    }

}
