package net.wirelabs.etrex.uploader.gui.settings;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;
import net.wirelabs.etrex.uploader.gui.components.FileChooserTextField;
import net.wirelabs.eventbus.EventBus;

import javax.swing.*;
import java.util.Collections;

import static net.wirelabs.etrex.uploader.common.EventType.USER_STORAGE_ROOTS_CHANGED;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class ApplicationSettingsPanel extends BorderedPanel {

    private final AppConfiguration configuration;
    private final FileChooserTextField storageRoot = new FileChooserTextField(true, false);
    private final FileChooserTextField userRoots = new FileChooserTextField(true, true);
    private final FileChooserTextField mapDefinitionsDir = new FileChooserTextField(true,false);

    private final JTextField discoveryDelay = new JTextField();
    private final JTextField waitDriveTimeout = new JTextField();
    private final JCheckBox deleteAfterUpl = new JCheckBox("Delete after upload");
    private final JCheckBox archiveAfterUpload = new JCheckBox("Archive");
    private final JCheckBox useSliders = new JCheckBox("Desktop look with sliders");
    private final LookAndFeelComboBox lookAndFeelSelector = new LookAndFeelComboBox();

    public ApplicationSettingsPanel(AppConfiguration configuration) {
        super("Application");
        this.configuration = configuration;
        setLayout(new MigLayout("", "[][grow]", "[][][][][]"));

        JLabel storageRootLabel = new JLabel("Storage root:");
        add(storageRootLabel, "cell 0 0,alignx trailing");
        add(storageRoot, "cell 1 0,growx");

        JLabel userRootsLabel = new JLabel("User storage roots:");
        add(userRootsLabel, "cell 0 1,alignx trailing");
        add(userRoots, "cell 1 1,growx");

        JLabel mapDefinitionsDirLabel = new JLabel("Map definitons dir:");
        add(mapDefinitionsDirLabel, "cell 0 2,alignx trailing");
        add(mapDefinitionsDir, "cell 1 2,growx");

        JLabel discoveryDelayLabel = new JLabel("Garmin discovery delay:");
        add(discoveryDelayLabel, "cell 0 3,alignx trailing");
        add(discoveryDelay, "cell 1 3,growx");


        JLabel waitDriveTimeoutLabel = new JLabel("Garmin discovery timeout:");
        add(waitDriveTimeoutLabel, "cell 0 4,alignx trailing");
        add(waitDriveTimeout, "cell 1 4,growx");

        JLabel lafLabel = new JLabel("GUI Look and Feel");
        add(lafLabel, "cell 0 5, alignx trailing");
        add(lookAndFeelSelector, "cell 1 5, growx");

        add(deleteAfterUpl, "cell 0 6");
        add(archiveAfterUpload, "cell 1 6");
        add(useSliders, "cell 0 7");
        loadConfiguration();
    }

    private void loadConfiguration() {
        storageRoot.setPaths(Collections.singletonList(configuration.getStorageRoot()));
        userRoots.setPaths(configuration.getUserStorageRoots());
        discoveryDelay.setText(String.valueOf(configuration.getDeviceDiscoveryDelay()));
        waitDriveTimeout.setText(String.valueOf(configuration.getWaitDriveTimeout()));
        deleteAfterUpl.setSelected(configuration.isDeleteAfterUpload());
        archiveAfterUpload.setSelected(configuration.isArchiveAfterUpload());
        mapDefinitionsDir.setPaths(Collections.singletonList(configuration.getUserMapDefinitonsDir()));
        lookAndFeelSelector.setSelectedItem(configuration.getLookAndFeelClassName());
        useSliders.setSelected(configuration.isUseSliders());
    }

    public void updateConfiguration() {
        configuration.setStorageRoot(storageRoot.getPaths().get(0));
        configuration.setUserStorageRoots(userRoots.getPaths());
        configuration.setDeviceDiscoveryDelay(Long.valueOf(discoveryDelay.getText()));
        configuration.setWaitDriveTimeout(Long.valueOf(waitDriveTimeout.getText()));
        configuration.setDeleteAfterUpload(deleteAfterUpl.isSelected());
        configuration.setArchiveAfterUpload(archiveAfterUpload.isSelected());
        configuration.setUserMapDefinitonsDir(mapDefinitionsDir.getPaths().get(0));
        configuration.setLookAndFeelClassName((String) lookAndFeelSelector.getSelectedItem());
        configuration.setUseSliders(useSliders.isSelected());
        EventBus.publish(USER_STORAGE_ROOTS_CHANGED, configuration.getUserStorageRoots());
    }

}

