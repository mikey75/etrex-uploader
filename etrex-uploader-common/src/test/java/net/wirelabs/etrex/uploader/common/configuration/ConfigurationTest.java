package net.wirelabs.etrex.uploader.common.configuration;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created 10/25/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class ConfigurationTest {

    @Test
    void shouldAssertDefaultValuesWhenNoConfigFile() {
        Configuration c = new Configuration("nonexistent.file");
        assertThat(c.getStorageRoot()).isEqualTo(System.getProperty("user.home") + File.separator + "etrex-uploader-store");
        assertThat(c.getUserStorageRoots()).isEmpty();
        assertThat(c.isArchiveAfterUpload()).isTrue();
        assertThat(c.isDeleteAfterUpload()).isTrue();
        assertThat(c.getWaitDriveTimeout()).isEqualTo(15000L);
        assertThat(c.getDeviceDiscoveryDelay()).isEqualTo(500L);
        assertThat(c.getStravaAuthorizerTimeout()).isEqualTo(60L);
    }

    @Test
    void shouldReadAndParseCorrectConfig() {
        Configuration c = new Configuration("src/test/resources/test.properties");
        assertThat(c.getStorageRoot()).isEqualTo("/test/root");
        assertThat(c.getUserStorageRoots()).isEqualTo("/test/1,test/2");
        assertThat(c.isArchiveAfterUpload()).isTrue();
        assertThat(c.isDeleteAfterUpload()).isFalse();
        assertThat(c.getWaitDriveTimeout()).isEqualTo(100L);
        assertThat(c.getDeviceDiscoveryDelay()).isEqualTo(500L);
        assertThat(c.getStravaAuthorizerTimeout()).isEqualTo(120L);
    }

    @Test
    void shouldStoreConfig() throws IOException {

        String[] expectedChange = {
                "system.wait.drive.timeout=10",
                "system.drive.observer.delay=100",
                "system.backup.after.upload=false",
                "strava.auth.timeout.seconds=800"
        };

        // because configuration save() overwrites src file, we need to operate on copy
        File configFile = new File("src/test/resources/test.properties");
        File configCopy = new File("target/test.properties");
        Files.copy(configFile.toPath(), configCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);


        Configuration c = new Configuration(configCopy.getPath());
        c.setArchiveAfterUpload(false);
        c.setDeviceDiscoveryDelay(100L);
        c.setWaitDriveTimeout(10L);
        c.setStravaAuthorizerTimeout(800L);
        c.save();
        // now reload changed file and check
        assertThat(Files.readAllLines(configCopy.toPath())).containsAll(Arrays.asList(expectedChange));
    }

}