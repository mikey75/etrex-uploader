package net.wirelabs.etrex.uploader.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.ApplicationStartupContext;
import net.wirelabs.etrex.uploader.EtrexUploaderRunner;
import net.wirelabs.eventbus.EventBus;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.List;

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

    public static String getOsName() {
        return System.getProperty("os.name");
    }

    public static void openSystemBrowser(String url) throws IOException {

        if (SystemUtils.isLinux()) {
            launchProcess("xdg-open " + url);
        } else if (SystemUtils.isWindows()) {
            launchProcess("rundll32 url.dll,FileProtocolHandler " + url);
        } else if (SystemUtils.isOSX()) {
            launchProcess("open " + url);
        }

    }

    static void launchProcess(String processCommand) throws IOException {
        Process browserSubprocess = Runtime.getRuntime().exec(processCommand);
        waitForSubprocess(browserSubprocess);
    }

    public static void checkOsSupport() {
        if (!isWindows() && !isLinux() && !isOSX()) {
            // throw exception that will be caught in main app - easier to test and does the same anyway
            throw new IllegalStateException("Unsupported OS");
        }
    }

    static void waitForSubprocess(Process process) {
        try {
            int code = process.waitFor();
            log.info("Process finished, exit code:{}", code);
        } catch (InterruptedException e) {
            log.info("Subprocess interrupted. Exiting current thread!");
            Thread.currentThread().interrupt();
        }
    }

    public static void checkGraphicsEnvironmentPresent() {
        if (GraphicsEnvironment.isHeadless()) {
            log.error("This application needs graphics environment - X11 or Windows");
            throw new IllegalStateException("No graphics environment present");
        }
    }

    public static String getJmapsVersion() {
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
        try {
            String cmd = getCommandLine(ProcessHandle.current()).orElseThrow(() -> new IOException("No command line"));
            Runtime rt = Runtime.getRuntime();
            log.info("Creating new application instance");
            rt.exec(cmd);
        } catch (IOException e) {
            log.error("Creating new application instance failed! {}" , e.getMessage());
        }
    }


    public static void systemExit(int status) {
        ApplicationStartupContext ctx = EtrexUploaderRunner.getAppContext();
        // close garmin service
        if (ctx !=null && ctx.getGarminDeviceService() != null) {
            ctx.getGarminDeviceService().stop();
        }
        // close eventbus
        if (EventBus.getExecutorService() != null && !EventBus.getExecutorService().isShutdown()) {
            EventBus.shutdown();
        }
        // also shutdown any lingering threads from ThreadUtils utility methods
        if (ThreadUtils.getExecutorService() != null && !ThreadUtils.getExecutorService().isShutdown()) {
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

    static Optional<String> getCommandLine(ProcessHandle processHandle) {

        if (isLinux() || isOSX()) {
            return getUnixCommandLine(processHandle);
        }

        if (isWindows()) {
            return getWindowsCommandLine(processHandle);
        }

        return Optional.empty();
    }

    @NotNull
    private static Optional<String> getWindowsCommandLine(ProcessHandle processHandle) {

            // on Windows run:
            // powershell.exe -Command (Get-CimInstance Win32_Process -Filter "ProcessId=$pid").CommandLine
            List<String> command = List.of(
                    "powershell.exe",
                    "-Command",
                    "(Get-CimInstance Win32_Process -Filter \"ProcessId=" + processHandle.pid() + "\").CommandLine"
            );
            return getCommand(command);

    }

    @NotNull
    static Optional<String> getCommand(List<String> command) {
        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();

            try (InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                return (line == null) ? Optional.empty() : Optional.of(line);
            }
        } catch (IOException e) {
            log.error("There was an error getting command line");
            return Optional.empty();
        }
    }

    private static Optional<String> getUnixCommandLine(ProcessHandle processHandle) {

            // run: ps -p $pid -o command=
            // need to make sure ps is /usr/bin/ps on Mac too - maybe run which before
            List<String> command = List.of("/usr/bin/ps", "-p", String.valueOf(processHandle.pid()), "-o", "command=");
            return getCommand(command);

    }

}
