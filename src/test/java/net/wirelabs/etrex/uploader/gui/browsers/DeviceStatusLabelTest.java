package net.wirelabs.etrex.uploader.gui.browsers;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceStatusLabelTest {

    @Test
    void deviceLabelShouldShowWaiting() {

        DeviceStatusLabel l = new DeviceStatusLabel(Collections.emptyList());
        // max 4 dots, wait 3 seconds because every dot is printed in 500 ms intrevals
        Awaitility.await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> assertThat(l.getText()).isEqualTo("waiting...."));
        // check if it resets (after 4 dots already printed it should be max 1 sec
        Awaitility.await().atMost(Duration.ofSeconds(1)).untilAsserted(() -> assertThat(l.getText()).isEqualTo("waiting."));
    }

    @Test
    void deviceLabelShouldNotWaitAndFlashWhenAnyFilesGiven() {
        List<File> list = List.of(new File("dupa"), new File("dupa2"));

        // if given a list, at once it should be empty
        DeviceStatusLabel l = new DeviceStatusLabel(list);
        assertThat(l.getText()).isEmpty();

    }

}