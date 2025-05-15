package net.wirelabs.etrex.uploader.common.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SystemUtilsTest extends BaseTest {

    @Test
    void getJmapsVersionTest() throws IOException {

        String fileContent = getFileContent("src/test/resources/versionfiles/fake-jmaps.version");

        try (MockedStatic<FileUtils> fileUtils = Mockito.mockStatic(FileUtils.class, CALLS_REAL_METHODS)) {
            fileUtils.when(() -> FileUtils.readFileToString(any(File.class), eq(StandardCharsets.UTF_8)))
                    .thenReturn(fileContent);
            // when
            String ver = SystemUtils.getJmapsVersion();
            // then
            assertThat(ver).isEqualTo("3.3.3");
        }
    }

    @Test
    void getAppVersionTest() throws IOException {

        String fileContent = getFileContent("src/test/resources/versionfiles/fake-etrex.version");

        try (MockedStatic<FileUtils> fileUtils = Mockito.mockStatic(FileUtils.class, CALLS_REAL_METHODS)) {
            fileUtils.when(() -> FileUtils.readFileToString(any(File.class), eq(StandardCharsets.UTF_8)))
                    .thenReturn(fileContent);
            // when
            String ver = SystemUtils.getAppVersion();
            // then
            assertThat(ver).contains("2.2.2"); // contains because version file could contain -SNAPSHOT and always contains build date
        }
    }

    @Test
    void shouldSetDefaultWarningVersionWhenVersionFileCannotBeRead() {
        // mock used apache fileutils to throw IOException on reading file (simulating error reading file)
        try (MockedStatic<FileUtils> fileUtils = Mockito.mockStatic(FileUtils.class, CALLS_REAL_METHODS)) {
            fileUtils.when(() -> FileUtils.readFileToString(any(File.class), eq(StandardCharsets.UTF_8)))
                    .thenThrow(new IOException("Forced exception"));
            // when
            String version = SystemUtils.getJmapsVersion();
            // then
            assertThat(version).isEqualTo("1.0U");
            verifyLogged("Can't find or load jmaps.version file");

        }
    }

    @Test
    void testLinuxDetection() {
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class,CALLS_REAL_METHODS)) {
            systemUtils.when(SystemUtils::getOsName).thenReturn("Linux");
            assertThat(SystemUtils.isLinux()).isTrue();
        }
    }

    @Test
    void testOSXDetection() {
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class,CALLS_REAL_METHODS)) {
            systemUtils.when(SystemUtils::getOsName).thenReturn("Mac OS X Sequoia 15.11");
            assertThat(SystemUtils.isOSX()).isTrue();
        }
    }

    @Test
    void testWindowsDetection() {
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class, CALLS_REAL_METHODS)) {
            systemUtils.when(SystemUtils::getOsName).thenReturn("Windows 11 NT x64");
            assertThat(SystemUtils.isWindows()).isTrue();
        }
    }

    @Test
    void testUnknownOSDetection() {
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class,CALLS_REAL_METHODS)) {
            systemUtils.when(SystemUtils::getOsName).thenReturn("Bulbulator 1.0");
            assertThrows(IllegalStateException.class, SystemUtils::checkOsSupport,"Unsupported OS");
        }
    }

    @Test
    void testGraphicsEnvironmentNotPresent() {
        try (MockedStatic<GraphicsEnvironment> env = mockStatic(GraphicsEnvironment.class)) {
            env.when(GraphicsEnvironment::isHeadless).thenReturn(true);
            assertThrows(IllegalStateException.class, SystemUtils::checkGraphicsEnvironmentPresent);
            verifyLogged("This application needs graphics environment - X11 or Windows");
        }
    }

    @Test
    void testGraphicsEnvironmentPresent() {
        try (MockedStatic<GraphicsEnvironment> env = mockStatic(GraphicsEnvironment.class)) {
            env.when(GraphicsEnvironment::isHeadless).thenReturn(false);
            assertDoesNotThrow(SystemUtils::checkGraphicsEnvironmentPresent);
            verifyNeverLogged("This application needs graphics environment - X11 or Windows");
        }
    }

    @Test
    void testLaunchingLinuxSystemBrowser() throws IOException {
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class, CALLS_REAL_METHODS);
             MockedStatic<Runtime> runtime = Mockito.mockStatic(Runtime.class, CALLS_REAL_METHODS)) {

            Runtime mockRuntime = mock(Runtime.class);
            runtime.when(Runtime::getRuntime).thenReturn(mockRuntime);
            when(mockRuntime.exec(anyString())).thenReturn(mock(Process.class));

            systemUtils.when(SystemUtils::getOsName).thenReturn("Linux");
            assertThat(SystemUtils.isLinux()).isTrue();

            SystemUtils.openSystemBrowser("http://kaka.pl");

            systemUtils.verify(() -> SystemUtils.launchProcess("xdg-open http://kaka.pl"));
            assertSuccessfulLaunch(systemUtils);
        }
    }

    @Test
    void testLaunchingOSXSystemBrowser() throws IOException {
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class, CALLS_REAL_METHODS);
             MockedStatic<Runtime> runtime = Mockito.mockStatic(Runtime.class, CALLS_REAL_METHODS)) {

            Runtime mockRuntime = mock(Runtime.class);
            runtime.when(Runtime::getRuntime).thenReturn(mockRuntime);
            when(mockRuntime.exec(anyString())).thenReturn(mock(Process.class));

            systemUtils.when(SystemUtils::getOsName).thenReturn("Mac OS X");
            assertThat(SystemUtils.isOSX()).isTrue();

            SystemUtils.openSystemBrowser("http://kaka.pl");

            systemUtils.verify(() -> SystemUtils.launchProcess("open http://kaka.pl"));
            assertSuccessfulLaunch(systemUtils);
        }
    }

    @Test
    void testLaunchingWindowsSystemBrowser() throws IOException {

        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class, CALLS_REAL_METHODS);
             MockedStatic<Runtime> runtime = Mockito.mockStatic(Runtime.class, CALLS_REAL_METHODS)) {

            Runtime mockRuntime = mock(Runtime.class);
            runtime.when(Runtime::getRuntime).thenReturn(mockRuntime);
            when(mockRuntime.exec(anyString())).thenReturn(mock(Process.class));

            systemUtils.when(SystemUtils::getOsName).thenReturn("Windows NT");
            assertThat(SystemUtils.isWindows()).isTrue();

            SystemUtils.openSystemBrowser("http://kaka.pl");

            systemUtils.verify(() -> SystemUtils.launchProcess("rundll32 url.dll,FileProtocolHandler http://kaka.pl"));
            assertSuccessfulLaunch(systemUtils);

        }

    }

    @Test
    void testProcessLauncher() throws IOException {
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class, CALLS_REAL_METHODS)) {

            // everyone running these tests has java ;)
            String processCmd = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java --version";

            SystemUtils.launchProcess(processCmd);

            assertSuccessfulLaunch(systemUtils);
        }
    }

    @Test
    void shouldCreateNewInstance() throws IOException {
        try (MockedStatic<Runtime> runtime = Mockito.mockStatic(Runtime.class, CALLS_REAL_METHODS)) {
            // given
            Runtime mockRuntime = mock(Runtime.class);
            runtime.when(Runtime::getRuntime).thenReturn(mockRuntime);
            when(mockRuntime.exec(anyString())).thenAnswer(invocation -> null); // do not really exec anything
            // when
            SystemUtils.createNewInstance();
            // then
            verifyLogged("Creating new application instance");
            verifyNeverLogged("Creating new application instance failed!");

        }

    }

    @Test
    void shouldNotCreateNewInstanceOnException() throws IOException {
        try (MockedStatic<Runtime> runtime = Mockito.mockStatic(Runtime.class, CALLS_REAL_METHODS)) {
            // given
            Runtime mockRuntime = mock(Runtime.class);
            runtime.when(Runtime::getRuntime).thenReturn(mockRuntime);
            when(mockRuntime.exec(anyString())).thenThrow(IOException.class);
            // when
            SystemUtils.createNewInstance();
            // then
            verifyLogged("Creating new application instance");
            verifyLogged("Creating new application instance failed!");
        }
    }

    @Test
    void shouldNotCreateNewInstanceOnNonexistentCommandline() {
        // to simulate nonexisting process commandline we fake the OS to unknown, so the
        // getCommandLine(..) returns Optional.empty() - its default behavior on unknown OS
        try (MockedStatic<SystemUtils> sysUtils = Mockito.mockStatic(SystemUtils.class, CALLS_REAL_METHODS)) {
            sysUtils.when(SystemUtils::getOsName).thenReturn("Bulbulator");
            // when
            SystemUtils.createNewInstance();
            // then
            verifyLogged("No new instance could be created");
        }
    }

    private void assertSuccessfulLaunch(MockedStatic<SystemUtils> systemUtils) {
        // when waitForSubprocess() is called, it means the launch succeeded
        systemUtils.verify(() -> SystemUtils.waitForSubprocess(any()));
        // check logs for 100% sure
        verifyLogged("Process finished, exit code:0");
    }


    private static String getFileContent(String pathname) throws IOException {
        File version = new File(pathname);
        assertThat(version).exists().isNotEmpty();
        return FileUtils.readFileToString(version, StandardCharsets.UTF_8);
    }

}