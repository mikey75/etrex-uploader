package net.wirelabs.etrex.uploader.gui.desktop.devicepanel;

import com.garmin.xmlschemas.garminDevice.v2.DeviceDocument;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.strava.UploadService;
import net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common.GarminDeviceBrowser;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.eventbus.EventBus;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static net.wirelabs.etrex.uploader.common.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GarminDeviceBrowserTest extends BaseTest {

    private final UploadService uploadService = mock(UploadService.class);

    @Test
    void shouldDisplayGarminInfo() throws XmlException, IOException {

        // given
        File garminXMLFile = new File("src/test/resources/garmin/GarminDevice.xml");
        DeviceDocument deviceDocument = DeviceDocument.Factory.parse(garminXMLFile);
        GarminDeviceBrowser garminBrowser = new GarminDeviceBrowser(uploadService);
        assertDeviceFieldsEmpty(garminBrowser); // assert initialization with empty fields

        // when
        EventBus.publish(EventType.DEVICE_INFO_AVAILABLE, deviceDocument.getDevice()); // simulate new garmin device connection

        // then - assert it has fields correctly filled-in

        waitUntilAsserted(Duration.ofSeconds(1), () -> {
            assertThat(garminBrowser.getPartNumber().getText()).isEqualTo(GARMIN_PART_NUMBER + "006-B3445-00");
            assertThat(garminBrowser.getModel().getText()).isEqualTo(GARMIN_MODEL +"eTrex 32x");
            assertThat(garminBrowser.getSoftwareVersion().getText()).isEqualTo(GARMIN_SOFTWARE_VERSION + "270");
            assertThat(garminBrowser.getSerialNumber().getText()).isEqualTo(GARMIN_SERIAL_NUMBER + "3403532495");
        });

    }

    @Test
    void shouldEmptyLabelsWhenDriveUnregisteredEventIssued() {
        GarminDeviceBrowser gbrowser = new GarminDeviceBrowser(uploadService);
        EventBus.publish(EventType.DEVICE_DRIVE_UNREGISTERED, new File("fakefile")); // this can be any File() in this test
        assertDeviceFieldsEmpty(gbrowser);
    }

    @Test
    void shouldNotFillLabelsWhenIssuingDriveRegisterEventWithNonExistingOrIncorrectGarminDrive() {
        // create file directory structure for fake drive
        GarminDeviceBrowser gbrowser = new GarminDeviceBrowser(uploadService);
        EventBus.publish(EventType.DEVICE_DRIVE_REGISTERED, new File("fakefile")); // this can be any File() in this test
        assertDeviceFieldsEmpty(gbrowser);
    }

    private static void assertDeviceFieldsEmpty(GarminDeviceBrowser garminBrowser) {
        waitUntilAsserted(Duration.ofSeconds(1), () -> {
            assertThat(garminBrowser.getPartNumber().getText()).isEqualTo(GARMIN_PART_NUMBER);
            assertThat(garminBrowser.getModel().getText()).isEqualTo(GARMIN_MODEL);
            assertThat(garminBrowser.getSoftwareVersion().getText()).isEqualTo(GARMIN_SOFTWARE_VERSION);
            assertThat(garminBrowser.getSerialNumber().getText()).isEqualTo(GARMIN_SERIAL_NUMBER);
        });
    }

}