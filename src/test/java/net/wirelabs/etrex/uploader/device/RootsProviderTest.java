
package net.wirelabs.etrex.uploader.device;

import net.wirelabs.etrex.uploader.utils.SystemUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


/**
 * Created 8/11/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

class RootsProviderTest {

    private final RootsProvider rootsProvider = Mockito.spy(new RootsProvider());

    @Test
    void shouldGetLinuxRoots() {

        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class)) {
            systemUtils.when(SystemUtils::isLinux).thenReturn(true);
            systemUtils.when(SystemUtils::isWindows).thenReturn(false);

            // when
            rootsProvider.getRoots();
            // then
            verify(rootsProvider, never()).windowsRoots();
            verify(rootsProvider, times(1)).linuxRoots();
        }
    }

    @Test
    void shouldGetOsxRoots() {

        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class)) {
            systemUtils.when(SystemUtils::isOSX).thenReturn(true);
            systemUtils.when(SystemUtils::isWindows).thenReturn(false);
            systemUtils.when(SystemUtils::isLinux).thenReturn(false);
            // when
            rootsProvider.getRoots();
            // then
            verify(rootsProvider, never()).windowsRoots();
            verify(rootsProvider, never()).linuxRoots();
            verify(rootsProvider, times(1)).osxRoots();
        }
    }

    @Test
    void shouldGetWindowsRoots() {
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class)) {
            systemUtils.when(SystemUtils::isLinux).thenReturn(false);
            systemUtils.when(SystemUtils::isWindows).thenReturn(true);

            // when
            rootsProvider.getRoots();
            // then
            verify(rootsProvider, never()).linuxRoots();
            verify(rootsProvider, times(1)).windowsRoots();
        }
    }

    @Test
    void shouldReturnEmptyRootsCollection() {
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class)) {
            systemUtils.when(SystemUtils::getOsName).thenReturn("Bulbulator 1.0");
            assertThat(rootsProvider.getRoots()).isEmpty();
        }
    }

}

