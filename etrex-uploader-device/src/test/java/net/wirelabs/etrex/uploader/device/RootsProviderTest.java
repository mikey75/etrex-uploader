
package net.wirelabs.etrex.uploader.device;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


/**
 * Created 8/11/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

public class RootsProviderTest {

    private RootsProvider rootsProvider = Mockito.spy(new RootsProvider());

    @Test
    void shouldGetLinuxRoots() {
        doReturn("Linux").when(rootsProvider).getOperatingSystem();
        // when
        rootsProvider.getRoots();
        // then
        verify(rootsProvider, never()).windowsRoots();
        verify(rootsProvider, times(1)).linuxRoots();

    }

    @Test
    void shouldGetWindowsRoots() {
        doReturn("Windows").when(rootsProvider).getOperatingSystem();
        // when
        rootsProvider.getRoots();
        // then
        verify(rootsProvider, never()).linuxRoots();
        verify(rootsProvider, times(1)).windowsRoots();
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenUnknownOS() {
        doReturn("UNKNOWN").when(rootsProvider).getOperatingSystem();
        assertThrows(IllegalStateException.class, rootsProvider::getRoots,"Unsupported operating system");
    }

}

