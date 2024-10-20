package net.wirelabs.etrex.uploader.gui.browsers;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.UploadService;

import javax.swing.*;
import java.awt.*;

public class GarminAndStoragePanel extends JPanel {

    private final LayoutManager layout = new MigLayout("insets 0px", "[grow]", "[grow][grow]");

    public GarminAndStoragePanel(UploadService uploadService, AppConfiguration appConfiguration) {
        setLayout(layout);
        add(new GarminDeviceBrowser(uploadService), "cell 0 0,grow");
        add(new LocalStorageBrowser(appConfiguration), "cell 0 1,grow");
        setSize(new Dimension(180, 200));
    }
}
