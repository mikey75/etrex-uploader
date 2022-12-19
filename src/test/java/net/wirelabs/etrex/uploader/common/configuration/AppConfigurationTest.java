package net.wirelabs.etrex.uploader.common.configuration;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created 10/25/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class AppConfigurationTest {

    @Test
    void shouldAssertDefaultValuesWhenNoConfigFile() {
        AppConfiguration c = new AppConfiguration("nonexistent.file");
        assertThat(c.getStorageRoot()).isEqualTo(Paths.get(System.getProperty("user.home") + File.separator + "etrex-uploader-store"));
        assertThat(c.getUserStorageRoots()).isEmpty();
        assertThat(c.isArchiveAfterUpload()).isTrue();
        assertThat(c.isDeleteAfterUpload()).isTrue();
        assertThat(c.getWaitDriveTimeout()).isEqualTo(15000L);
        assertThat(c.getDeviceDiscoveryDelay()).isEqualTo(500L);
    }

    @Test
    void shouldReadAndParseCorrectConfig() {
        AppConfiguration c = new AppConfiguration("src/test/resources/test.properties");
        assertThat(c.getStorageRoot()).isEqualTo(Paths.get("/test/root"));
        assertThat(c.getUserStorageRoots()).containsExactly(Paths.get("/test/1"),Paths.get("test/2"));
        assertThat(c.isArchiveAfterUpload()).isTrue();
        assertThat(c.isDeleteAfterUpload()).isFalse();
        assertThat(c.getWaitDriveTimeout()).isEqualTo(100L);
        assertThat(c.getDeviceDiscoveryDelay()).isEqualTo(500L);

    }

    @Test
    void shouldStoreConfig() throws IOException {

        String[] expectedChange = {
                "system.wait.drive.timeout=10",
                "system.drive.observer.delay=100",
                "system.backup.after.upload=false"
        };

        // because configuration save() overwrites src file, we need to operate on copy
        File configFile = new File("src/test/resources/test.properties");
        File configCopy = new File("target/test.properties");
        Files.copy(configFile.toPath(), configCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);


        AppConfiguration c = new AppConfiguration(configCopy.getPath());
        c.setArchiveAfterUpload(false);
        c.setDeviceDiscoveryDelay(100L);
        c.setWaitDriveTimeout(10L);
        c.save();
        // now reload changed file and check
        assertThat(Files.readAllLines(configCopy.toPath())).containsAll(Arrays.asList(expectedChange));
    }

}