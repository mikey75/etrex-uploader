
package net.wirelabs.etrex.uploader.hardware;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Created 8/11/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

public class RootsProviderTest {

    private RootsProvider rootsProvider = Mockito.spy(new RootsProvider());

    @Test
    void shouldGetLinuxRootsAndWindowsRoots() {

        // when
        doReturn("Linux")
                .doReturn("Windows")
                .when(rootsProvider).getOperatingSystem();
       
        rootsProvider.getRoots();
        verify(rootsProvider, times(1)).linuxRoots();
        
        rootsProvider.getRoots();
        verify(rootsProvider, times(1)).windowsRoots();
        
        
    }
    @Test
    void shouldCallOperatingSystem() {
        rootsProvider.getRoots();
        verify(rootsProvider, times(1)).getOperatingSystem();
    }

    @Test
    void shouldThrowExceptionWhenUnknownOS() {

        doReturn("UNKNOWN").when(rootsProvider).getOperatingSystem();

        Throwable thrown = Assertions.assertThrows(IllegalStateException.class, rootsProvider::getRoots);

        Assertions.assertEquals("Unsupported operating system", thrown.getMessage());
    }


}

