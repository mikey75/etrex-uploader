package net.wirelabs.etrex.uploader.gui.browsers;

import com.garmin.xmlschemas.garminDevice.v2.DeviceT;
import com.garmin.xmlschemas.garminDevice.v2.ModelT;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.device.GarminUtils;
import net.wirelabs.etrex.uploader.gui.UploadService;
import net.wirelabs.etrex.uploader.gui.components.BaseEventAwarePanel;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileNode;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileTree;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.IEventType;

import javax.swing.*;
import javax.swing.tree.*;
import java.io.File;
import java.util.*;


/**
 * Created 9/8/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class GarminDeviceBrowser extends BaseEventAwarePanel {

    private final List<File> garminDrives = new ArrayList<>();
    @Getter(value = AccessLevel.PACKAGE)
    private final JLabel lblModelDescriptionValue = new JLabel();
    @Getter(value = AccessLevel.PACKAGE)
    private final JLabel lblSerialNoValue = new JLabel();
    @Getter(value = AccessLevel.PACKAGE)
    private final JLabel lblPartNoValue = new JLabel();
    @Getter(value = AccessLevel.PACKAGE)
    private final JLabel lblSoftwareVerValue = new JLabel();
    private final JLabel lblStatusValue = new DeviceStatusLabel(garminDrives);

    private final JLabel lblModelDescription = new JLabel("Model:");
    private final JLabel lblSerialNo = new JLabel("Serial Number:");
    private final JLabel lblPartNo = new JLabel("Part Number:");
    private final JLabel lblSoftwareVersion = new JLabel("Software version:");
    private final JLabel lblStatus = new JLabel("Status:");
    private final JScrollPane scrollPane = new JScrollPane();
    private final FileTree tree;


    GarminDeviceBrowser(UploadService uploadService) {
        super("Garmin device","","[grow]","[][][][][][grow]");
        add(lblModelDescription, "flowx,cell 0 0,alignx left");
        add(lblModelDescriptionValue, "cell 0 0,alignx left");
        add(lblSerialNo, "flowx,cell 0 1,alignx left");
        add(lblSerialNoValue, "cell 0 1,alignx left");
        add(lblPartNo, "flowx,cell 0 2,alignx left");
        add(lblPartNoValue, "cell 0 2,alignx left");
        add(lblSoftwareVersion, "flowx,cell 0 3,alignx left");
        add(lblSoftwareVerValue, "cell 0 3,alignx left");
        add(lblStatus, "flowx,cell 0 4");
        add(lblStatusValue, "cell 0 4");

        add(scrollPane, "cell 0 5,grow");

        tree = new FileTree(); 
        
        tree.setCellRenderer(new FileTreeCellRenderer());
        tree.addTreeSelectionListener(new TrackSelectedListener());
        tree.addPopupMenu(new FileOperationsPopupMenu(tree, uploadService));
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

        if (driveHasDeviceXml(driveOnEvent)){
            findAndMarkFileNodeAsSystemDrive(driveOnEvent);
        }

    }

    private void unregisterDriveFromBrowser(Event evt) {
        File driveOnEvent = (File) evt.getPayload();
        garminDrives.remove(driveOnEvent);
        tree.removeDrive(driveOnEvent);
    }

    private void clearGarminInfo() {
        lblModelDescriptionValue.setText("");
        lblSoftwareVerValue.setText("");
        lblPartNoValue.setText("");
        lblSerialNoValue.setText("");
        
    }

    private void updateGarminInfo(Event evt) {
        DeviceT deviceInfo = (DeviceT)  evt.getPayload();
        ModelT modelInfo = deviceInfo.getModel();
        
        lblModelDescriptionValue.setText(modelInfo.getDescription());
        lblSoftwareVerValue.setText(String.valueOf(modelInfo.getSoftwareVersion()));
        lblPartNoValue.setText(modelInfo.getPartNumber());
        lblSerialNoValue.setText(String.valueOf(deviceInfo.getId()));
        lblStatusValue.setText("Connected");
    }

    @Override
    protected Collection<IEventType> subscribeEvents() {
        return List.of(
                EventType.DEVICE_DRIVE_REGISTERED,
                EventType.DEVICE_DRIVE_UNREGISTERED,
                EventType.DEVICE_INFO_AVAILABLE);
    }

    public boolean driveHasDeviceXml(File drive) {
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
