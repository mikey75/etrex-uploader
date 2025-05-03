package net.wirelabs.etrex.uploader.common.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SystemUtilsTest extends BaseTest {

    @Test
    void getJmapsVersionTest() {

        String ver = SystemUtils.getJmapsVerion();

        assertThat(ver)
                .isNotNull()
                .isEqualTo("1.4.0");
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
}