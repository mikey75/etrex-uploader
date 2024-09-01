package net.wirelabs.etrex.uploader.gui.browsers;

import com.garmin.xmlschemas.garminDevice.v2.DeviceT;
import com.garmin.xmlschemas.garminDevice.v2.ModelT;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.eventbus.Event;
import net.wirelabs.etrex.uploader.device.GarminUtils;
import net.wirelabs.etrex.uploader.gui.UploadService;
import net.wirelabs.etrex.uploader.gui.components.EventAwarePanel;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileNode;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileTree;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreeNode;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Created 9/8/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class GarminDeviceBrowser extends EventAwarePanel {

    private final List<File> garminDrives = new ArrayList<>();
    private final JLabel device = new JLabel();
    private final JLabel serialNo = new JLabel();
    private final JLabel partNo = new JLabel();
    private final JLabel softwareVer = new JLabel();
    private final JLabel status = new DeviceStatusLabel(garminDrives);

    private final JLabel lblDevice = new JLabel("Model:");
    private final JLabel lblSerialNo = new JLabel("Serial Number:");
    private final JLabel lblPartNo = new JLabel("Part Number:");
    private final JLabel lblSoftwareVersion = new JLabel("Software version:");
    private final JLabel lblStatus = new JLabel("Status:");
    private final JScrollPane scrollPane = new JScrollPane();
    private final FileTree tree;

    public GarminDeviceBrowser(UploadService uploadService) {

        setBorder(new TitledBorder(null, "Garmin device", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setLayout(new MigLayout("", "[grow]", "[][][][][][grow]"));

        add(lblDevice, "flowx,cell 0 0,alignx left");
        add(lblSerialNo, "flowx,cell 0 1,alignx left");
        add(lblPartNo, "flowx,cell 0 2,alignx left");
        add(lblSoftwareVersion, "flowx,cell 0 3,alignx left");
        add(lblStatus, "flowx,cell 0 4");
        add(device, "cell 0 0,alignx left");
        add(serialNo, "cell 0 1,alignx left");
        add(partNo, "cell 0 2,alignx left");
        add(softwareVer, "cell 0 3,alignx left");
        add(status, "cell 0 4");
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
        device.setText("");
        softwareVer.setText("");
        partNo.setText("");
        serialNo.setText("");
        
    }

    private void updateGarminInfo(Event evt) {
        DeviceT deviceInfo = (DeviceT)  evt.getPayload();
        ModelT modelInfo = deviceInfo.getModel();
        
        device.setText(modelInfo.getDescription());
        softwareVer.setText(String.valueOf(modelInfo.getSoftwareVersion()));
        partNo.setText(modelInfo.getPartNumber());
        serialNo.setText(String.valueOf(deviceInfo.getId()));
        status.setText("Connected");
    }

    @Override
    protected Collection<EventType> subscribeEvents() {
        return Arrays.asList(
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
