package net.wirelabs.etrex.uploader.gui.browsers;

import java.io.File;
import java.util.List;
import javax.swing.*;


public class DeviceStatusLabel extends JLabel {
    
    final String[] string = {"."};
    
    public DeviceStatusLabel(List<File> deviceList) {
        
        Timer t = new Timer(400, e -> {
            if (deviceList.isEmpty()) {
                setText("Wait for device " + string[0]);
                string[0] += ".";
                if (string[0].length() > 10) {
                    string[0] = ".";
                }
            }
        });

        t.start();
    }
}
