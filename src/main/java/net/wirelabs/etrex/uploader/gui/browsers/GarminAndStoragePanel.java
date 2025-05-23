package net.wirelabs.etrex.uploader.gui.browsers;

import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.UploadService;
import net.wirelabs.etrex.uploader.gui.components.BasePanel;

import java.awt.*;

public class GarminAndStoragePanel extends BasePanel {

    public GarminAndStoragePanel(UploadService uploadService, AppConfiguration appConfiguration) {
        super("insets 0","[grow]","[grow][grow]");
        add(new GarminDeviceBrowser(uploadService), "cell 0 0,grow");
        add(new LocalStorageBrowser(appConfiguration), "cell 0 1,grow");
        setSize(new Dimension(180, 200));
    }

}
