package net.wirelabs.etrex.uploader.gui.browsers;

import com.garmin.xmlschemas.garminDevice.v2.DeviceDocument;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.gui.UploadService;
import net.wirelabs.eventbus.EventBus;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GarminDeviceBrowserTest {

    UploadService uploadService = mock(UploadService.class);

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
        Sleeper.sleepMillis(500); // give it time to process event
        assertThat(gbrowser.getPartNo().getText()).isEqualTo("006-B3445-00");
        assertThat(gbrowser.getDevice().getText()).isEqualTo("eTrex 32x");
        assertThat(gbrowser.getSoftwareVer().getText()).isEqualTo("270");
        assertThat(gbrowser.getSerialNo().getText()).isEqualTo("3403532495");

    }

    @Test
    void shouldUnregisterAndClearInfo() {
        GarminDeviceBrowser gbrowser = new GarminDeviceBrowser(uploadService);
        EventBus.publish(EventType.DEVICE_DRIVE_UNREGISTERED, new File("kaka"));
        assertDeviceFieldsEmpty(gbrowser);
    }

    @Test
    void registerFakeGarmin() {
        // create file directory structure for fake drive
        GarminDeviceBrowser gbrowser = new GarminDeviceBrowser(uploadService);
        EventBus.publish(EventType.DEVICE_DRIVE_REGISTERED, new File("kaka"));
        assertDeviceFieldsEmpty(gbrowser);
    }

    private static void assertDeviceFieldsEmpty(GarminDeviceBrowser gbrowser) {
        Sleeper.sleepMillis(500); // give it time to process event
        assertThat(gbrowser.getPartNo().getText()).isEmpty();
        assertThat(gbrowser.getDevice().getText()).isEmpty();
        assertThat(gbrowser.getSoftwareVer().getText()).isEmpty();
        assertThat(gbrowser.getSerialNo().getText()).isEmpty();
    }

}