package net.wirelabs.etrex.uploader.gui.settingsdialog.appsettings;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;
import net.wirelabs.etrex.uploader.gui.settingsdialog.appsettings.lookandfeelcombo.LookAndFeelComboBox;
import net.wirelabs.eventbus.EventBus;

import javax.swing.*;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static net.wirelabs.etrex.uploader.common.EventType.USER_STORAGE_ROOTS_CHANGED;
import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.*;
import static net.wirelabs.etrex.uploader.utils.SystemUtils.*;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class ApplicationSettingsPanel extends BasePanel {

    private final AppConfiguration configuration;
    private final FileChooserTextField storageRoot = new FileChooserTextField();
    private final FileChooserTextField mapDefinitionsDir = new FileChooserTextField();
    @Getter
    private final FileChooserTextField userRootsFileChooser = new FileChooserTextField(true, true);

    private final JTextField discoveryDelay = new JTextField();
    private final JTextField waitDriveTimeout = new JTextField();
    private final JCheckBox deleteAfterUpl = new JCheckBox("Delete after upload");
    private final JCheckBox archiveAfterUpload = new JCheckBox("Archive");
    @Getter
    private final JCheckBox enableDesktopSlidersCheckBox = new JCheckBox("Enable desktop sliders");
    @Getter
    private final LookAndFeelComboBox lookAndFeelSelector = new LookAndFeelComboBox();


    public ApplicationSettingsPanel(AppConfiguration configuration) {
        super("Application","","[][grow]","[][][][][]");
        this.configuration = configuration;

        JLabel storageRootLabel = new JLabel("Storage root:");
        add(storageRootLabel, cell(0,0).alignX(TRAILING));
        add(storageRoot, cell(1,0).growX());

        JLabel userRootsLabel = new JLabel("User storage roots:");
        add(userRootsLabel, cell(0,1).alignX(TRAILING));
        add(userRootsFileChooser, cell(1,1).growX());

        JLabel mapDefinitionsDirLabel = new JLabel("Map definitions dir:");
        add(mapDefinitionsDirLabel, cell(0,2).alignX(TRAILING));
        add(mapDefinitionsDir, cell(1,2).growX());

        JLabel discoveryDelayLabel = new JLabel("Garmin discovery delay:");
        add(discoveryDelayLabel, cell(0,3).alignX(TRAILING));
        add(discoveryDelay, cell(1,3).growX());


        JLabel waitDriveTimeoutLabel = new JLabel("Garmin discovery timeout:");
        add(waitDriveTimeoutLabel, cell(0,4).alignX(TRAILING));
        add(waitDriveTimeout, cell(1,4).growX());

        JLabel lafLabel = new JLabel("GUI Look and Feel");
        add(lafLabel, cell(0,5).alignX(TRAILING));
        add(lookAndFeelSelector, cell(1,5).growX());

        add(deleteAfterUpl, cell(0,6));
        add(archiveAfterUpload, cell(1,6));
        add(enableDesktopSlidersCheckBox, cell(0,7));
        loadConfiguration();

        enableDesktopSlidersCheckBox.addActionListener(e -> showRebootNeededMsgDialog(configuration));
    }

    private void showRebootNeededMsgDialog(AppConfiguration configuration) {
        int dialogResponse = SwingUtils.yesNoCancelMsg("This change will need restarting the application. Do you want that?");
        // if YES -> update config and reboot app
        if (dialogResponse == JOptionPane.YES_OPTION) {
            updateConfiguration();
            saveConfigAndReboot(configuration);
        } else {
            // if NO -> restore UI status with current config and continue normally
            enableDesktopSlidersCheckBox.setSelected(configuration.isEnableDesktopSliders());
        }
    }

    private void loadConfiguration() {
        storageRoot.setPaths(Collections.singletonList(configuration.getStorageRoot()));
        userRootsFileChooser.setPaths(configuration.getUserStorageRoots());
        discoveryDelay.setText(String.valueOf(configuration.getDeviceDiscoveryDelay()));
        waitDriveTimeout.setText(String.valueOf(configuration.getWaitDriveTimeout()));
        deleteAfterUpl.setSelected(configuration.isDeleteAfterUpload());
        archiveAfterUpload.setSelected(configuration.isArchiveAfterUpload());
        mapDefinitionsDir.setPaths(Collections.singletonList(configuration.getUserMapDefinitionsDir()));
        lookAndFeelSelector.setSelectedItem(configuration.getLookAndFeelClassName());
        enableDesktopSlidersCheckBox.setSelected(configuration.isEnableDesktopSliders());
    }

    public void updateConfiguration() {
        List<Path> origStorageRootsString = configuration.getUserStorageRoots();
        configuration.setStorageRoot(storageRoot.getPaths().get(0));
        configuration.setUserStorageRoots(userRootsFileChooser.getPaths());
        configuration.setDeviceDiscoveryDelay(Long.valueOf(discoveryDelay.getText()));
        configuration.setWaitDriveTimeout(Long.valueOf(waitDriveTimeout.getText()));
        configuration.setDeleteAfterUpload(deleteAfterUpl.isSelected());
        configuration.setArchiveAfterUpload(archiveAfterUpload.isSelected());
        configuration.setUserMapDefinitionsDir(mapDefinitionsDir.getPaths().get(0));
        configuration.setLookAndFeelClassName((String) lookAndFeelSelector.getSelectedItem());
        configuration.setEnableDesktopSliders(enableDesktopSlidersCheckBox.isSelected());
        // publish change if it really changed ;)
        if (!origStorageRootsString.equals(configuration.getUserStorageRoots())) {
            log.info("Storage roots changed");
            EventBus.publish(USER_STORAGE_ROOTS_CHANGED, configuration.getUserStorageRoots());
        }
    }

}

