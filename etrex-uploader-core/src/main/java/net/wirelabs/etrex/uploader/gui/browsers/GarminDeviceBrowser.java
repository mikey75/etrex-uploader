package net.wirelabs.etrex.uploader.gui.browsers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreeNode;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.eventbus.Event;
import net.wirelabs.etrex.uploader.gui.components.EventAwarePanel;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileNode;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileTree;
import net.wirelabs.etrex.uploader.gui.components.filetree.UploadDialog;
import net.wirelabs.etrex.uploader.hardware.GarminHardwareInfo;


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

    public GarminDeviceBrowser(UploadDialog uploadDialog) {

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
        
        tree.addPopupMenu(new FileOperationsPopupMenu(tree, uploadDialog));
        scrollPane.setViewportView(tree);
        
    }
    
    @Override
    protected void onEvent(Event evt) {

        if (evt.getEventType() == EventType.EVT_HARDWARE_INFO_AVAILABLE) {
            updateGarminInfo(evt);
            findAndMarkFileNodeAsSystemDrive(evt);
        }

        if (evt.getEventType() == EventType.EVT_DRIVE_UNREGISTERED) {
            clearGarminInfo();
            unregisterDriveFromBrowser(evt);
        }

        if (evt.getEventType() == EventType.EVT_DRIVE_REGISTERED) {
            registerDriveInBrowser(evt);
        }

    }

    private void findAndMarkFileNodeAsSystemDrive(Event evt) {

        GarminHardwareInfo hwInfo = (GarminHardwareInfo) evt.getPayload();

        for (TreeNode t:  tree.getRootNodes()) {
            FileNode fn = (FileNode) t;
            if (fn.getFile().getPath().equals(hwInfo.getDrive().getPath())) {
                fn.setGarminSystemDrive(true);
            }
        }
    }

    private void registerDriveInBrowser(Event evt) {
        File driveOnEvent = (File) evt.getPayload();
        garminDrives.add(driveOnEvent);
        tree.addDrive(driveOnEvent);
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
        GarminHardwareInfo hardwareInfo = (GarminHardwareInfo) evt.getPayload();
        device.setText(hardwareInfo.getDescription());
        softwareVer.setText(hardwareInfo.getSoftwareVersion());
        partNo.setText(hardwareInfo.getPartNumber());
        serialNo.setText(hardwareInfo.getSerialNumber());
        status.setText("Connected");
    }

    @Override
    protected Collection<EventType> subscribeEvents() {
        return Arrays.asList(
                EventType.EVT_DRIVE_REGISTERED,
                EventType.EVT_DRIVE_UNREGISTERED,
                EventType.EVT_HARDWARE_INFO_AVAILABLE);
    }
}
