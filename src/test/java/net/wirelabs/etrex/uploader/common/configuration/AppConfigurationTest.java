package net.wirelabs.etrex.uploader.common.configuration;

import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


/**
 * Created 10/25/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class AppConfigurationTest extends BaseTest {
    // config files
    private static final File USER_CONFIG = new File("config.properties"); // possible user config that we dont wanna touch!
    private static final List<String> origUserConfigContent = new ArrayList<>();
    private static final List<String> currentUserConfigContent = new ArrayList<>();

    private final File TEST_CONFIG_FILE = new File("src/test/resources/config/test.properties");
    private final File NONEXISTENT = new File("target/nonexisting.properties");


    @BeforeAll
    static void beforeAll() throws IOException {
        // store user's file (if it exists) content for later comparison
        if (USER_CONFIG.exists()) {
            origUserConfigContent.addAll(Files.readAllLines(USER_CONFIG.toPath()));
        }
    }

    @AfterAll
    static void afterAll() throws IOException {
        // check if user's file (if it exists) was not touched - just to be sure
        if (USER_CONFIG.exists()) {
            currentUserConfigContent.addAll(Files.readAllLines(USER_CONFIG.toPath()));
            assertThat(currentUserConfigContent).hasSameElementsAs(origUserConfigContent);
        }
    }

    @Test
    void shouldThrowAndLogWhenCannotSaveConfig() throws IOException {
        Files.deleteIfExists(NONEXISTENT.toPath());
        AppConfiguration configuration = new AppConfiguration(NONEXISTENT.getPath());
        configuration.properties = Mockito.spy(configuration.properties);

        doThrow(new IOException("I/O exception"))
                .when(configuration.properties)
                .store(any(OutputStream.class), anyString());

        configuration.save();

        verifyLogged("Saving configuration " + NONEXISTENT);
        verifyLogged("Can't save config file: I/O exception");

    }

    @Test
    void shouldAssertDefaultValuesWhenConfigFileNonExistentAndCreateConfigFile() throws IOException {
        Files.deleteIfExists(NONEXISTENT.toPath());
        AppConfiguration c = new AppConfiguration(NONEXISTENT.getPath());
        assertDefaultValues(c);
        verifyLogged(NONEXISTENT.getPath() + " file not found or cannot be loaded. Setting default config values.");
        // verify new config file is created
        verifyLogged("Saving new config file with default values");
        assertThat(NONEXISTENT).exists();

    }


    @Test
    void shouldReadAndParseCorrectConfig() {
        AppConfiguration c = new AppConfiguration(TEST_CONFIG_FILE.getPath());
        assertThat(c.getStorageRoot()).isEqualTo(Paths.get("/test/root"));
        assertThat(c.getUserStorageRoots()).containsExactly(Paths.get("/test/1"), Paths.get("test/2"));
        assertThat(c.isArchiveAfterUpload()).isTrue();
        assertThat(c.isDeleteAfterUpload()).isFalse();
        assertThat(c.getWaitDriveTimeout()).isEqualTo(100L);
        assertThat(c.getDeviceDiscoveryDelay()).isEqualTo(500L);
        verifyLogged("Loading " + TEST_CONFIG_FILE.getPath());

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
        File configFile = new File(TEST_CONFIG_FILE.getPath());
        File newConfigFile = new File("target", TEST_CONFIG_FILE.getName());

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
}