package net.wirelabs.etrex.uploader.gui.browsers;

import com.garmin.xmlschemas.garminDevice.v2.DeviceDocument;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.gui.UploadService;
import net.wirelabs.eventbus.EventBus;
import org.apache.xmlbeans.XmlException;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GarminDeviceBrowserTest {

    private final UploadService uploadService = mock(UploadService.class);

    @Test
    void shouldDisplayGarminInfo() throws XmlException, IOException {

        // given
        File garminXMLFile = new File("src/test/resources/garmin/GarminDevice.xml");
        DeviceDocument d = DeviceDocument.Factory.parse(garminXMLFile);
        GarminDeviceBrowser gbrowser = new GarminDeviceBrowser(uploadService);
        assertDeviceFieldsEmpty(gbrowser); // assert initialization with empty fields

        // when
        EventBus.publish(EventType.DEVICE_INFO_AVAILABLE, d.getDevice()); // simulate new garmin device connection

        // then - assert it has fields correctly filled-in

        Awaitility.await().atMost(Duration.ofSeconds(1)).untilAsserted( () -> {
            assertThat(gbrowser.getLblPartNoValue().getText()).isEqualTo("006-B3445-00");
            assertThat(gbrowser.getLblModelDescriptionValue().getText()).isEqualTo("eTrex 32x");
            assertThat(gbrowser.getLblSoftwareVerValue().getText()).isEqualTo("270");
            assertThat(gbrowser.getLblSerialNoValue().getText()).isEqualTo("3403532495");
        });

    }

    @Test
    void shouldEmptyLabelsWhenDriveUnregisteredEventIssued() {
        GarminDeviceBrowser gbrowser = new GarminDeviceBrowser(uploadService);
        EventBus.publish(EventType.DEVICE_DRIVE_UNREGISTERED, new File("kaka")); // this can be any File() in this test
        assertDeviceFieldsEmpty(gbrowser);
    }

    @Test
    void shouldNotFillLabelsWhenIssuingDriveRegisterEventWithNonExistingOrIncorrectGarminDrive() {
        // create file directory structure for fake drive
        GarminDeviceBrowser gbrowser = new GarminDeviceBrowser(uploadService);
        EventBus.publish(EventType.DEVICE_DRIVE_REGISTERED, new File("kaka")); // this can be any File() in this test
        assertDeviceFieldsEmpty(gbrowser);
    }

    private static void assertDeviceFieldsEmpty(GarminDeviceBrowser gbrowser) {
        Awaitility.waitAtMost(Duration.ofSeconds(1)).untilAsserted(() -> {
            assertThat(gbrowser.getLblPartNoValue().getText()).isEmpty();
            assertThat(gbrowser.getLblModelDescriptionValue().getText()).isEmpty();
            assertThat(gbrowser.getLblSoftwareVerValue().getText()).isEmpty();
            assertThat(gbrowser.getLblSerialNoValue().getText()).isEmpty();
        });
    }

}