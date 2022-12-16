package net.wirelabs.etrex.uploader.gui.settings;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;

import javax.swing.*;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class ApplicationSettingsPanel extends BorderedPanel {

    JLabel storageRootLabel = new JLabel("Storage root:");
    JTextField storageRoot = new JTextField();
    JLabel userRootsLabel = new JLabel("User roots:");
    JTextField userRoots = new JTextField();
    JLabel discoveryDelayLabel = new JLabel("Garmin discovery delay:");
    JTextField discoveryDelay = new JTextField();
    JLabel waitDriveTimeoutLabel = new JLabel("Garmin discovery timeout:");
    JTextField waitDriveTimeout = new JTextField();
    JCheckBox deleteAfterUpl = new JCheckBox("Delete after upload");
    JCheckBox archiveAfterUpload = new JCheckBox("Archive");

    public ApplicationSettingsPanel(AppConfiguration configuration) {
        super("Application");
        setLayout(new MigLayout("", "[][grow]", "[][][][][]"));

        add(storageRootLabel, "cell 0 0,alignx trailing");
        add(storageRoot, "cell 1 0,growx");

        add(userRootsLabel, "cell 0 1,alignx trailing");
        add(userRoots, "cell 1 1,growx");

        add(discoveryDelayLabel, "cell 0 2,alignx trailing");
        add(discoveryDelay, "cell 1 2,growx");

        add(waitDriveTimeoutLabel, "cell 0 3,alignx trailing");
        add(waitDriveTimeout, "cell 1 3,growx");

        add(deleteAfterUpl, "cell 0 4");
        add(archiveAfterUpload, "cell 1 4");

        loadConfiguration(configuration);
    }

    private void loadConfiguration(AppConfiguration configuration) {
        storageRoot.setText(configuration.getStorageRoot());
        userRoots.setText(configuration.getUserStorageRoots());
        discoveryDelay.setText(String.valueOf(configuration.getDeviceDiscoveryDelay()));
        waitDriveTimeout.setText(String.valueOf(configuration.getWaitDriveTimeout()));
        deleteAfterUpl.setSelected(configuration.isDeleteAfterUpload());
        archiveAfterUpload.setSelected(configuration.isArchiveAfterUpload());
    }


}

