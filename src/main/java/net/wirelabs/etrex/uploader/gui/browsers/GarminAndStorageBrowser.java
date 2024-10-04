package net.wirelabs.etrex.uploader.gui.browsers;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.UploadService;

import javax.swing.*;

public class GarminAndStorageBrowser extends JPanel {

    public GarminAndStorageBrowser(UploadService uploadService, AppConfiguration appConfiguration) {
        setLayout(new MigLayout("insets 0px", "[grow]", "[grow][grow]"));
        add(new GarminDeviceBrowser(uploadService), "cell 0 0,grow");
        add(new LocalStorageBrowser(appConfiguration), "cell 0 1,grow");
    }
}
