package net.wirelabs.etrex.uploader.device;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class GarminUtilsTest extends BaseTest {

    private final File GOOD_DRIVE = new File("src/test/resources/garmin");
    private final File NONEXISTING_DRIVE = new File("src/test/resources/garmin/noexisting");

    @Test
    void testReadingOfGoodFile() {
        // GarminDevice.xml is on the drive - should return path and go on
        Optional<Path> path = GarminUtils.getGarminDeviceXmlFile(GOOD_DRIVE);
        assertThat(path).isPresent();
        verifyNeverLogged("I/O exception looking for GarminDevice.xml file");
    }

    @Test
    void testReadingUnexistingFile() {
        // if we check unexisting drive, GarminDevice.xml is not found, exception thrown and path empty
        Optional<Path> path = GarminUtils.getGarminDeviceXmlFile(NONEXISTING_DRIVE);
        assertThat(path).isEmpty();
        verifyLogged("I/O exception looking for GarminDevice.xml file");
    }

}