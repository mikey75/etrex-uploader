package net.wirelabs.etrex.uploader.device;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.utils.GarminUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class GarminUtilsTest extends BaseTest {

    private static final File GOOD_DRIVE = new File("src/test/resources/garmin");
    private static final File GOOD_DRIVE_NESTED = new File("src/test/resources/garmin/garmin-nested");
    private static final File NON_EXISTING_DRIVE = new File("src/test/resources/garmin/nonexisting");

    @Test
    void testReadingOfGoodFile() {
        // GarminDevice.xml is on the drive - should return this file and go on
        Optional<File> path = GarminUtils.getGarminDeviceXmlFile(GOOD_DRIVE);
        assertThat(path).isPresent();
    }

    @Test
    void testReadingOfGoodFileLevels2Deep() {
        // GarminDevice.xml is on the drive at 2nd level - should return this file and go on
        Optional<File> path = GarminUtils.getGarminDeviceXmlFile(GOOD_DRIVE_NESTED);
        assertThat(path).isPresent();
    }

    @Test
    void testReadingNonExistentFile() {
        // if we check nonexistent drive, GarminDevice.xml is not found, exception thrown and path empty
        Optional<File> path = GarminUtils.getGarminDeviceXmlFile(NON_EXISTING_DRIVE);
        assertThat(path).isEmpty();
    }

}