package net.wirelabs.etrex.uploader;

import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SetupManagerTest extends BaseTest {

    private SetupManager manager;

    @BeforeEach
    void before() {
        manager = spy(new SetupManager());
        doNothing().when(manager).runStravaConnector(); // don't display - it's a modal dialog and will halt the test
    }

    @Test
    void shouldCallAllInitMethods() throws IOException, UnsupportedLookAndFeelException, ReflectiveOperationException {

        manager.initialize();

        verify(manager).checkSystem();
        verify(manager).configureLogger();
        verify(manager).initializeContext();
        verify(manager).setFontAndLookAndFeel();
        verify(manager).runStravaConnectorIfNecessary();

        assertNotNullAppContext();

    }

    @Test
    void shouldInvokeStravaConnector() {

        mockNonexistentStravaConfig();

        manager.initialize();

        verify(manager).runStravaConnectorIfNecessary();
        verify(manager).runStravaConnector();

    }

    @Test
    void shouldServiceException() {
        // emulate unknown OS so that the checkSystem() throws
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class)) {

            mockUnknownOS(systemUtils);

            doCallRealMethod().when(manager).checkSystem();

            assertThatThrownBy(() -> manager.initialize())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Setup Manager failed to initialize: Unsupported OS");
        }

    }

    private void mockUnknownOS(MockedStatic<SystemUtils> systemUtils) {
        systemUtils.when(() -> SystemUtils.systemExit(anyInt())).thenAnswer(inv -> null);
        systemUtils.when(SystemUtils::getWorkDir).thenCallRealMethod();
        systemUtils.when(SystemUtils::getHomeDir).thenCallRealMethod();
        systemUtils.when(SystemUtils::checkOsSupport).thenCallRealMethod();
        systemUtils.when(SystemUtils::getOsName).thenReturn("Bulbulator 1.0");
    }

    private void mockNonexistentStravaConfig() {
        // mock some basic config to get nonexistent strava configuration - this triggers the strava connector
        ApplicationStartupContext mockAppContext = mock(ApplicationStartupContext.class);
        when(mockAppContext.getStravaConfiguration()).thenReturn(new StravaConfiguration("/target/nonexistent-strava-config-file"));
        when(manager.getAppContext()).thenReturn(mockAppContext);
    }

    private void assertNotNullAppContext() {
        assertThat(manager.getAppContext()).isNotNull();
        assertThat(manager.getAppContext().getAppConfiguration()).isNotNull();
        assertThat(manager.getAppContext().getStravaConfiguration()).isNotNull();
        assertThat(manager.getAppContext().getUploadService()).isNotNull();
        assertThat(manager.getAppContext().getFileService()).isNotNull();
        assertThat(manager.getAppContext().getGarminDeviceService()).isNotNull();
    }
}