package net.wirelabs.etrex.uploader.common.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
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
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class)) {
            when(SystemUtils.isLinux()).thenCallRealMethod();
            systemUtils.when(SystemUtils::getOsName).thenReturn("Linux");
            assertThat(SystemUtils.isLinux()).isTrue();
        }
    }

    @Test
    void testOSXDetection() {
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class)) {
            when(SystemUtils.isOSX()).thenCallRealMethod();
            systemUtils.when(SystemUtils::getOsName).thenReturn("Mac OS X Sequoia 15.11");
            assertThat(SystemUtils.isOSX()).isTrue();
        }
    }

    @Test
    void testWindowsDetection() {
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class)) {
            when(SystemUtils.isWindows()).thenCallRealMethod();
            systemUtils.when(SystemUtils::getOsName).thenReturn("Windows 11 NT x64");
            assertThat(SystemUtils.isWindows()).isTrue();
        }
    }

    @Test
    void testUnknownOSDetection() {
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class)) {
            systemUtils.when(SystemUtils::getOsName).thenReturn("Bulbulator 1.0");
            systemUtils.when(SystemUtils::checkOsSupport).thenCallRealMethod();
            assertThrows(IllegalStateException.class, SystemUtils::checkOsSupport,"Unsupported OS");
        }
    }

}