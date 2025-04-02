package net.wirelabs.etrex.uploader.common.configuration;

import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.EventBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


/**
 * Created 10/25/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class AppConfigurationTest extends BaseTest {

    private final File CONFIG_FILE = new File("src/test/resources/config/test.properties");
    public static final File APP_CONFIG_FILE = new File("src/test/resources/dupa");
    private final File currentConfig = new File(SystemUtils.getWorkDir(), Constants.DEFAULT_APPLICATION_CONFIGFILE);
    private final File currentConfigCopy = new File(SystemUtils.getWorkDir(), Constants.DEFAULT_APPLICATION_CONFIGFILE + "-copy");

    @BeforeEach
    void makeACopyOfCurrentConfigFile() throws IOException {
        /*
            since the test works on config file that might be present on local computer during development
            lets first preserve it, and then delete it (so basically move it) so that the test has always
            the default no-config configuration
         */
        if (currentConfig.exists()) {
            Files.move(currentConfig.toPath(), currentConfigCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        // delete previously nonexistent file - but now created by config
        if (APP_CONFIG_FILE.exists()) {
            Files.delete(APP_CONFIG_FILE.toPath());
        }
    }

    @AfterEach
    void restoreCopiedConfig() throws IOException {
        // restore copied config, and remove the copy
        if (currentConfigCopy.exists()) {
            Files.move(currentConfigCopy.toPath(), currentConfig.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Test
    void shouldLoadDefaultConfigValuesIfNoneConfigFileGiven() {

        AppConfiguration c = new AppConfiguration();
        assertDefaultValues(c);
    }

    @Test
    void shouldThrowAndLogWhenCannotSaveConfig() throws IOException {

        AppConfiguration configuration = new AppConfiguration();
        configuration.properties = Mockito.spy(configuration.properties);

        doThrow(new IOException())
                .when(configuration.properties)
                .store(any(OutputStream.class), anyString());

        configuration.save();

        // check event thrown -> since in test no evbus client is subscribed to this event
        // it will be found in dead events
        List<Event> events = EventBus.getDeadEvents();
        assertThat(events.stream()
                .filter(e -> e.getEventType().equals(EventType.ERROR_SAVING_CONFIGURATION))
                .toList()).isNotEmpty();


        verifyLogged("Saving configuration " + Constants.DEFAULT_APPLICATION_CONFIGFILE);
        verifyLogged("Can't save configuration");

    }

    @Test
    void shouldAssertDefaultValuesWhenConfigFileNonExistentAndCreateConfigFile() {
        assertThat(APP_CONFIG_FILE).doesNotExist();
        AppConfiguration c = new AppConfiguration(APP_CONFIG_FILE.getPath());
        assertDefaultValues(c);
        verifyLogged(APP_CONFIG_FILE.getPath() + " file not found or cannot be loaded. Setting default config values.");
        // verify new config file is created
        verifyLogged("Saving new config file with default values");
        assertThat(APP_CONFIG_FILE).exists();

    }

    private void assertDefaultValues(AppConfiguration c) {
        assertThat(c.getStorageRoot()).isEqualTo(Paths.get(System.getProperty("user.home") + File.separator + "etrex-uploader-store"));
        assertThat(c.getUserStorageRoots()).isEmpty();
        assertThat(c.getDeviceDiscoveryDelay()).isEqualTo(Constants.DEFAULT_DRIVE_OBSERVER_DELAY);
        assertThat(c.getWaitDriveTimeout()).isEqualTo(Constants.DEFAULT_WAIT_DRIVE_TIMEOUT);
        assertThat(c.isDeleteAfterUpload()).isTrue();
        assertThat(c.isArchiveAfterUpload()).isTrue();
        assertThat(c.getDefaultActivityType()).isEqualTo(Constants.DEFAULT_SPORT);
        assertThat(c.getTilerThreads()).isEqualTo(Constants.DEFAULT_TILER_THREAD_COUNT);
        assertThat(c.getPerPage()).isEqualTo(Constants.DEFAULT_STRAVA_ACTIVITIES_PER_PAGE);
        assertThat(c.getApiUsageWarnPercent()).isEqualTo(Constants.DEFAULT_API_USAGE_WARN_PERCENT);
        assertThat(c.getUploadStatusWaitSeconds()).isEqualTo(Constants.DEFAULT_UPLOAD_STATUS_WAIT_SECONDS);
        assertThat(c.getMapTrackColor()).isEqualTo(Constants.DEFAULT_TRACK_COLOR);
        assertThat(c.getUserMapDefinitonsDir()).hasToString(Constants.DEFAULT_USER_MAP_DIR);
        assertThat(c.getMapFile()).hasToString(c.getUserMapDefinitonsDir().toString() + File.separator + Constants.DEFAULT_MAP);
        assertThat(c.isUsePolyLines()).isTrue();
        assertThat(c.getLookAndFeelClassName()).isEqualTo(UIManager.getCrossPlatformLookAndFeelClassName());
        assertThat(c.getStravaCheckTimeout()).isEqualTo(Constants.DEFAULT_STRAVA_CHECK_TIMEOUT);
        assertThat(c.isStravaCheckHostBeforeUpload()).isTrue();
        assertThat(c.getMapHomeLattitude()).isEqualTo(Constants.DEFAULT_MAP_HOME_LOCATION.getLatitude());
        assertThat(c.getMapHomeLongitude()).isEqualTo(Constants.DEFAULT_MAP_HOME_LOCATION.getLongitude());
        assertThat(c.isEnableDesktopSliders()).isEqualTo(Constants.DEFAULT_USE_SLIDERS);
    }

    @Test
    void shouldReadAndParseCorrectConfig() {
        AppConfiguration c = new AppConfiguration(CONFIG_FILE.getPath());
        assertThat(c.getStorageRoot()).isEqualTo(Paths.get("/test/root"));
        assertThat(c.getUserStorageRoots()).containsExactly(Paths.get("/test/1"), Paths.get("test/2"));
        assertThat(c.isArchiveAfterUpload()).isTrue();
        assertThat(c.isDeleteAfterUpload()).isFalse();
        assertThat(c.getWaitDriveTimeout()).isEqualTo(100L);
        assertThat(c.getDeviceDiscoveryDelay()).isEqualTo(500L);
        verifyLogged("Loading " + CONFIG_FILE.getPath());

    }

    @Test
    void shouldStoreChangedConfig() throws IOException {

        String[] expectedChange = {
                "system.wait.drive.timeout=10",
                "system.drive.observer.delay=100",
                "system.backup.after.upload=false",
                "map.home.lattitude=10.111",
                "map.home.longitude=30.111",
                "system.look.sliders=true"
        };

        // because configuration save() overwrites src file, we need to operate on copy (newConfigFile)
        File configFile = new File(CONFIG_FILE.getPath());
        File newConfigFile = new File("target", CONFIG_FILE.getName());

        Files.copy(configFile.toPath(), newConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        AppConfiguration c = new AppConfiguration(newConfigFile.getPath());
        c.setArchiveAfterUpload(false);
        c.setDeviceDiscoveryDelay(100L);
        c.setWaitDriveTimeout(10L);
        c.setMapHomeLattitude(10.111);
        c.setMapHomeLongitude(30.111);
        c.setEnableDesktopSliders(true);
        c.save();

        verifyLogged("Loading " + newConfigFile.getPath());
        verifyLogged("Saving configuration " + newConfigFile.getPath());
        // now reload changed file and check
        assertThat(Files.readAllLines(newConfigFile.toPath())).containsAll(Arrays.asList(expectedChange));

    }

}