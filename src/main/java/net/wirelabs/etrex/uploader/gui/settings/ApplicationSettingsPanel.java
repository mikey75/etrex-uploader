package net.wirelabs.etrex.uploader.gui.settings;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.ListUtils;
import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;

import javax.swing.*;
import java.nio.file.Paths;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class ApplicationSettingsPanel extends BorderedPanel {

    private final AppConfiguration configuration;
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
        this.configuration = configuration;
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

        loadConfiguration();
    }

    private void loadConfiguration() {
        storageRoot.setText(configuration.getStorageRoot().toString());
        userRoots.setText(ListUtils.convertPathListToString(configuration.getUserStorageRoots()));
        discoveryDelay.setText(String.valueOf(configuration.getDeviceDiscoveryDelay()));
        waitDriveTimeout.setText(String.valueOf(configuration.getWaitDriveTimeout()));
        deleteAfterUpl.setSelected(configuration.isDeleteAfterUpload());
        archiveAfterUpload.setSelected(configuration.isArchiveAfterUpload());
    }

    public void updateConfiguration() {
        configuration.setStorageRoot(Paths.get(storageRoot.getText()));
        configuration.setUserStorageRoots(ListUtils.convertStringListToPaths(userRoots.getText()));
        configuration.setDeviceDiscoveryDelay(Long.valueOf(discoveryDelay.getText()));
        configuration.setWaitDriveTimeout(Long.valueOf(waitDriveTimeout.getText()));
        configuration.setDeleteAfterUpload(deleteAfterUpl.isSelected());
        configuration.setArchiveAfterUpload(archiveAfterUpload.isSelected());

    }

}

