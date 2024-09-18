package net.wirelabs.etrex.uploader.common.configuration;

import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.strava.model.SportType;
import net.wirelabs.etrex.uploader.tools.BaseTest;
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

import static net.wirelabs.etrex.uploader.TestConstants.CONFIG_FILE;
import static net.wirelabs.etrex.uploader.TestConstants.NONEXISTENT_FILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


/**
 * Created 10/25/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class AppConfigurationTest extends BaseTest {


    @Test
    void shouldLoadDefaultConfigIfNoneGiven() {

        AppConfiguration c = new AppConfiguration();
        // these are default values that are never modified in any code
        assertThat(c.getDeviceDiscoveryDelay()).isEqualTo(500L);
        assertThat(c.getWaitDriveTimeout()).isEqualTo(15000L);
        assertThat(c.isDeleteAfterUpload()).isTrue();
        assertThat(c.isArchiveAfterUpload()).isTrue();

    }

    @Test
    void shouldThrowAndLogWhenCannotSaveConfig() throws IOException {

        AppConfiguration configuration = new AppConfiguration();
        configuration.properties = Mockito.spy(configuration.properties);

        doThrow(new IOException())
                .when(configuration.properties)
                .store(any(OutputStream.class), anyString());

        configuration.store();

        verifyLogged("Saving configuration "+ ConfigurationPropertyKeys.APPLICATION_CONFIGFILE);
        verifyLogged("Can't save configuration");

    }

    @Test
    void shouldAssertDefaultValuesWhenNoConfigFile() {
        AppConfiguration c = new AppConfiguration(NONEXISTENT_FILE.getPath());
        assertThat(c.getStorageRoot()).isEqualTo(Paths.get(System.getProperty("user.home") + File.separator + "etrex-uploader-store"));
        assertThat(c.getUserStorageRoots()).isEmpty();
        assertThat(c.getDeviceDiscoveryDelay()).isEqualTo(500L);
        assertThat(c.getWaitDriveTimeout()).isEqualTo(15000L);
        assertThat(c.isDeleteAfterUpload()).isTrue();
        assertThat(c.isArchiveAfterUpload()).isTrue();
        assertThat(c.getDefaultActivityType()).isEqualTo(SportType.RIDE);
        assertThat(c.getTilerThreads()).isEqualTo(8);
        assertThat(c.getPerPage()).isEqualTo(30);
        assertThat(c.getApiUsageWarnPercent()).isEqualTo(85);
        assertThat(c.getUploadStatusWaitSeconds()).isEqualTo(60);
        assertThat(c.getMapTrackColor()).isEqualTo("#ff0000");
        assertThat(c.getUserMapDefinitonsDir()).hasToString(System.getProperty("user.home") + File.separator + Constants.DEFAULT_USER_MAP_DIR);
        assertThat(c.getMapFile()).hasToString(c.getUserMapDefinitonsDir().toString() + File.separator + "null");
        assertThat(c.isUsePolyLines()).isTrue();
        assertThat(c.getLookAndFeelClassName()).isEqualTo(UIManager.getCrossPlatformLookAndFeelClassName());
        assertThat(c.getStravaCheckTimeout()).isEqualTo(500);
        assertThat(c.isStravaCheckHostBeforeUpload()).isTrue();
        verifyLogged(NONEXISTENT_FILE.getPath() +" file not found or cannot be loaded");
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
        verifyLogged("Loading " +CONFIG_FILE.getPath());

    }

    @Test
    void shouldStoreChangedConfig() throws IOException {

        String[] expectedChange = {
                "system.wait.drive.timeout=10",
                "system.drive.observer.delay=100",
                "system.backup.after.upload=false"
        };

        // because configuration save() overwrites src file, we need to operate on copy (newConfigFile)
        File configFile = new File(CONFIG_FILE.getPath());
        File newConfigFile = new File("target", CONFIG_FILE.getName());

        Files.copy(configFile.toPath(), newConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        AppConfiguration c = new AppConfiguration(newConfigFile.getPath());
        c.setArchiveAfterUpload(false);
        c.setDeviceDiscoveryDelay(100L);
        c.setWaitDriveTimeout(10L);
        c.save();

        verifyLogged("Loading " + newConfigFile.getPath());
        verifyLogged("Saving configuration " + newConfigFile.getPath());
        // now reload changed file and check
        assertThat(Files.readAllLines(newConfigFile.toPath())).containsAll(Arrays.asList(expectedChange));

    }

}