package net.wirelabs.etrex.uploader.gui.desktop.devicepanel;

import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.strava.UploadService;
import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;
import net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common.GarminDeviceBrowser;
import net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common.LocalStorageBrowser;

import java.awt.*;

public class GarminAndStoragePanel extends BasePanel {

    public GarminAndStoragePanel(UploadService uploadService, AppConfiguration appConfiguration) {
        super("insets 0","[grow]","[grow][grow]");
        add(new GarminDeviceBrowser(uploadService), "cell 0 0,grow");
        add(new LocalStorageBrowser(appConfiguration), "cell 0 1,grow");
        setSize(new Dimension(180, 200));
    }

}
