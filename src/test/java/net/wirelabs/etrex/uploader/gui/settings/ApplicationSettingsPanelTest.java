package net.wirelabs.etrex.uploader.gui.settings;

import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.eventbus.EventBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ApplicationSettingsPanelTest extends BaseTest {

    private ApplicationSettingsPanel applicationSettingsPanel;
    private MockedStatic<EventBus> evbusMock;
    private MockedStatic<SwingUtils> swingUtilsMock;
    private MockedStatic<SystemUtils> systemUtilsMock;
    private AppConfiguration appConfiguration;

    @BeforeEach
    void beforeEach() {
        appConfiguration = Mockito.spy(new AppConfiguration("src/test/resources/config/test.properties"));
        applicationSettingsPanel = Mockito.spy(new ApplicationSettingsPanel(appConfiguration));
        evbusMock = Mockito.mockStatic(EventBus.class);
        swingUtilsMock = Mockito.mockStatic(SwingUtils.class);
        systemUtilsMock = Mockito.mockStatic(SystemUtils.class);
    }

    @AfterEach
    void afterEach() {
        evbusMock.close();
        swingUtilsMock.close();
        systemUtilsMock.close();
    }

    @Test
    void shouldPublishEventsIfUserRootsChanged() {

        // given -> change values on the panel and update config
        // get current list
        List<Path> currentListOfRoots = applicationSettingsPanel.getUserRootsFileChooser().getPaths();
        // add new element
        currentListOfRoots.add(Paths.get("fakefile"));
        applicationSettingsPanel.getUserRootsFileChooser().setPaths(currentListOfRoots);

        // when
        applicationSettingsPanel.updateConfiguration();

        // verify changes are made and the event published
        assertThat(applicationSettingsPanel.getUserRootsFileChooser().getPaths()).hasSize(3);
        evbusMock.verify(() -> EventBus.publish(eq(EventType.USER_STORAGE_ROOTS_CHANGED), any(List.class)));
        verifyLogged("Storage roots changed");
    }

    @Test
    void shouldNotPublishEventsIfUserRootsChanged() {
        // given -> none, no change on the panel

        // when
        applicationSettingsPanel.updateConfiguration();

        // verify changes will NOT be published since nothing changed
        evbusMock.verify(() -> EventBus.publish(eq(EventType.USER_STORAGE_ROOTS_CHANGED), any(List.class)), never());
        verifyNeverLogged("Storage roots changed");
    }

    @Test
    void shouldRebootIfDesktopChanged() {

        // set yes answer on dialog
        swingUtilsMock.when(() -> SwingUtils.yesNoCancelMsg(anyString())).thenReturn(JOptionPane.YES_OPTION);
        // dont do anything on system exit and create new instance
        systemUtilsMock.when(() -> SystemUtils.systemExit(anyInt())).thenAnswer((Answer<Void>) invocation -> null);
        systemUtilsMock.when(SystemUtils::createNewInstance).thenAnswer((Answer<Void>) invocation -> null);
        // dont do anything on updateConfiguration and save
        doNothing().when(applicationSettingsPanel).updateConfiguration();
        doNothing().when(appConfiguration).save();

        // this invokes dialog  -  yes option chosen
        applicationSettingsPanel.getUseSliders().doClick();
        // verify reboot dialog was shown and accepted
        swingUtilsMock.verify(() -> SwingUtils.yesNoCancelMsg("This change will need restarting the application. Do you want that?"));
        verifyLogged("Restarting application");


    }

    @Test
    void shouldNotRebooIfDesktopNotChanged() {

        // set yes answer on dialog
        swingUtilsMock.when(() -> SwingUtils.yesNoCancelMsg(anyString())).thenReturn(JOptionPane.NO_OPTION);
        // dont do anything on system exit and create new instance
        systemUtilsMock.when(() -> SystemUtils.systemExit(anyInt())).thenAnswer((Answer<Void>) invocation -> null);
        systemUtilsMock.when(SystemUtils::createNewInstance).thenAnswer((Answer<Void>) invocation -> null);
        // dont do anything on updateConfiguration and save
        doNothing().when(applicationSettingsPanel).updateConfiguration();
        doNothing().when(appConfiguration).save();

        // this invokes dialog -> no option chosen
        applicationSettingsPanel.getUseSliders().doClick();
        // verify reboot dialog was shown but denied
        swingUtilsMock.verify(() -> SwingUtils.yesNoCancelMsg("This change will need restarting the application. Do you want that?"));
        verifyNeverLogged("Restarting application");

    }
}