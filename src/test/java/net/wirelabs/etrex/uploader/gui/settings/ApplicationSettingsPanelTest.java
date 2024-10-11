package net.wirelabs.etrex.uploader.gui.settings;

import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.eventbus.EventBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;

class ApplicationSettingsPanelTest extends BaseTest {

    private ApplicationSettingsPanel applicationSettingsPanel;
    private MockedStatic<EventBus> evbusMock;

    @BeforeEach
    void beforeEach() {
        AppConfiguration appConfiguration = new AppConfiguration("src/test/resources/config/test.properties");
        applicationSettingsPanel = Mockito.spy(new ApplicationSettingsPanel(appConfiguration));
        evbusMock = Mockito.mockStatic(EventBus.class);
    }

    @AfterEach
    void afterEach() {
        evbusMock.close();
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

}