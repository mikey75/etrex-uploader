package net.wirelabs.etrex.uploader.gui.settings;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;
import net.wirelabs.etrex.uploader.gui.components.FileChooserTextField;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.util.Collections;

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

        add(deleteAfterUpl, "cell 0 5");
        add(archiveAfterUpload, "cell 1 5");

        loadConfiguration();
    }

    private void loadConfiguration() {
        storageRoot.setPaths(Collections.singletonList(configuration.getStorageRoot()));
        userRoots.setPaths(configuration.getUserStorageRoots());
        discoveryDelay.setText(String.valueOf(configuration.getDeviceDiscoveryDelay()));
        waitDriveTimeout.setText(String.valueOf(configuration.getWaitDriveTimeout()));
        deleteAfterUpl.setSelected(configuration.isDeleteAfterUpload());
        archiveAfterUpload.setSelected(configuration.isArchiveAfterUpload());
        mapDefinitionsDir.setPaths(Collections.singletonList(configuration.getMapDefinitonsDir()));
    }

    public void updateConfiguration() {
        configuration.setStorageRoot(storageRoot.getPaths().get(0));
        configuration.setUserStorageRoots(userRoots.getPaths());
        configuration.setDeviceDiscoveryDelay(Long.valueOf(discoveryDelay.getText()));
        configuration.setWaitDriveTimeout(Long.valueOf(waitDriveTimeout.getText()));
        configuration.setDeleteAfterUpload(deleteAfterUpl.isSelected());
        configuration.setArchiveAfterUpload(archiveAfterUpload.isSelected());
        configuration.setMapDefinitonsDir(mapDefinitionsDir.getPaths().get(0));

    }

}

