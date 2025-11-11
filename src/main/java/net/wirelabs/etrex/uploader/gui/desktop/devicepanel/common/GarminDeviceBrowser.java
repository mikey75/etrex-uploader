package net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common;

import com.garmin.xmlschemas.garminDevice.v2.DeviceT;
import com.garmin.xmlschemas.garminDevice.v2.ModelT;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.gui.desktop.GarminLogo;
import net.wirelabs.etrex.uploader.utils.GarminUtils;
import net.wirelabs.etrex.uploader.strava.UploadService;
import net.wirelabs.etrex.uploader.gui.common.base.BaseEventAwarePanel;
import net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common.filetree.FileNode;
import net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common.filetree.FileTree;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.IEventType;

import javax.swing.*;
import javax.swing.tree.*;
import java.io.File;
import java.util.*;
import java.util.List;

import static net.wirelabs.etrex.uploader.common.Constants.*;
import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.*;


/**
 * Created 9/8/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class GarminDeviceBrowser extends BaseEventAwarePanel {

    private final List<File> garminDrives = new ArrayList<>();
    private final FileTree tree = new FileTree();
    @Getter
    private final JLabel model = new JLabel(GARMIN_MODEL);
    @Getter
    private final JLabel serialNumber = new JLabel(GARMIN_SERIAL_NUMBER);
    @Getter
    private final JLabel partNumber = new JLabel(GARMIN_PART_NUMBER);
    @Getter
    private final JLabel softwareVersion = new JLabel(GARMIN_SOFTWARE_VERSION);
    @Getter
    private final JLabel deviceStatus = new JLabel(GARMIN_STATUS);

    public GarminDeviceBrowser(UploadService uploadService) {
        super("Garmin device","","[grow]","[][][][][][][grow]");

        GarminLogo garminLogo = new GarminLogo();
        TrackSelectedListener trackSelectedListener = new TrackSelectedListener();
        FileOperationsPopupMenu fileOperationsPopupMenu = new FileOperationsPopupMenu(tree, uploadService);
        JScrollPane scrollPane = new JScrollPane();

        add(garminLogo, cell(0,0).alignX(CENTER));
        add(model, cell(0,1).alignX(LEFT).flowX());
        add(serialNumber, cell(0,2).alignX(LEFT).flowX());
        add(partNumber, cell(0,3).alignX(LEFT).flowX());
        add(softwareVersion, cell(0,4).alignX(LEFT).flowX());
        add(deviceStatus, cell(0,5).flowX());
        add(scrollPane, cell(0,6).grow());

        tree.addTreeSelectionListener(trackSelectedListener);
        tree.addPopupMenu(fileOperationsPopupMenu);
        scrollPane.setViewportView(tree);
        
    }

    @Override
    protected void onEvent(Event evt) {

        if (evt.getEventType() == EventType.DEVICE_INFO_AVAILABLE) {
            updateGarminInfo(evt);
        }

        if (evt.getEventType() == EventType.DEVICE_DRIVE_UNREGISTERED) {
            clearGarminInfo();
            unregisterDriveFromBrowser(evt);
        }

        if (evt.getEventType() == EventType.DEVICE_DRIVE_REGISTERED) {
            registerDriveInBrowser(evt);
        }

    }

    private void registerDriveInBrowser(Event evt) {
        File driveOnEvent = (File) evt.getPayload();
        garminDrives.add(driveOnEvent);
        tree.addDrive(driveOnEvent);

        if (isGarminSystemDrive(driveOnEvent)){
            findAndMarkFileNodeAsSystemDrive(driveOnEvent);
        }

    }

    private void unregisterDriveFromBrowser(Event evt) {
        File driveOnEvent = (File) evt.getPayload();
        garminDrives.remove(driveOnEvent);
        tree.removeDrive(driveOnEvent);
    }

    private void clearGarminInfo() {
        model.setText(GARMIN_MODEL);
        softwareVersion.setText(GARMIN_SOFTWARE_VERSION);
        partNumber.setText(GARMIN_PART_NUMBER);
        serialNumber.setText(GARMIN_SERIAL_NUMBER);
        
    }

    private void updateGarminInfo(Event evt) {
        DeviceT deviceInfo = (DeviceT)  evt.getPayload();
        ModelT modelInfo = deviceInfo.getModel();
        
        model.setText(GARMIN_MODEL + modelInfo.getDescription());
        softwareVersion.setText(GARMIN_SOFTWARE_VERSION + modelInfo.getSoftwareVersion());
        partNumber.setText(GARMIN_PART_NUMBER + modelInfo.getPartNumber());
        serialNumber.setText(GARMIN_SERIAL_NUMBER + deviceInfo.getId());
        deviceStatus.setText("Connected");
    }

    @Override
    protected Collection<IEventType> subscribeEvents() {
        return List.of(
                EventType.DEVICE_DRIVE_REGISTERED,
                EventType.DEVICE_DRIVE_UNREGISTERED,
                EventType.DEVICE_INFO_AVAILABLE);
    }

    /**
     * Checks whether the given drive contains a Garmin device descriptor (device.xml),
     * which indicates it is a Garmin system drive.
     */
    private boolean isGarminSystemDrive(File drive) {
        return GarminUtils.getGarminDeviceXmlFile(drive).isPresent();
    }

    private void findAndMarkFileNodeAsSystemDrive(File drive) {

        for (TreeNode t:  tree.getRootNodes()) {
            FileNode fn = (FileNode) t;
            if (fn.getFile().getPath().equals(drive.getPath())) {
                fn.setGarminSystemDrive(true);
            }
        }
    }
}
