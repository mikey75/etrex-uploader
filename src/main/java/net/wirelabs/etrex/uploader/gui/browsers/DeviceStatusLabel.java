package net.wirelabs.etrex.uploader.gui.browsers;

import javax.swing.JLabel;
import javax.swing.Timer;
import java.io.File;
import java.util.List;

/**
 * Silly label that shows waiting for device
 * as a mini dot progressbar
 */
public class DeviceStatusLabel extends JLabel {

    private final StringBuilder dotProgress = new StringBuilder();

    public DeviceStatusLabel(List<File> deviceList) {

        new Timer(500, e -> printBar(deviceList))
                .start();
    }

    private void printBar(List<File> deviceList) {
        if (deviceList.isEmpty()) {
            dotProgress.append(".");
            setText("waiting" + dotProgress);
            if (dotProgress.length() > 3) {
                dotProgress.setLength(0);
            }
        }
    }
}
