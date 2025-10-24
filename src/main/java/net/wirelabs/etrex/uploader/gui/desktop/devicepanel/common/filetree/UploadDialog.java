package net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common.filetree;

import com.strava.model.SportType;
import com.strava.model.Upload;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.common.base.BaseDialog;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtil;
import net.wirelabs.eventbus.EventBus;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.cell;


@Slf4j
public class UploadDialog extends BaseDialog {


    private File trackFile;
    @Setter private int hostCheckupTimeout;
    private final JTextField activityTitleTextField;
    private final JComboBox<SportType> activityTypeCombo;
    private final JTextArea activityDescriptionArea;

    
    private final JLabel lblActivityName;
    private final JLabel lblActivityType;
    private final JLabel lblActivityDescription;
    private final JScrollPane scrollPane;
    private final JButton btnOk;
    private final JButton btnCancel;
    private final JCheckBox commute; // is the activity a commute
    private final JCheckBox virtual; // is the activity a virtual/trainer ride
    private final StravaClient stravaClient;
    private final transient FileService fileService;


    public UploadDialog(StravaClient stravaClient, FileService fileService) {
        super("Upload","","[grow]","[][][][][][grow][][]");
        this.stravaClient = stravaClient;
        this.fileService = fileService;
        lblActivityName = new JLabel("Name");
        lblActivityDescription = new JLabel("Description");
        lblActivityType = new JLabel("Activity Type");
        scrollPane = new JScrollPane();
        activityTitleTextField = new JTextField();
        activityDescriptionArea = new JTextArea();
        btnOk = new JButton("Upload");
        btnCancel = new JButton("Cancel");
        activityTypeCombo = new JComboBox<>(SportType.values());
        commute = new JCheckBox("Commuting ride");
        virtual = new JCheckBox("Virtual/Trainer ride");
        commute.addActionListener(e -> {
            if (commute.isSelected()) virtual.setSelected(false);
        });
        virtual.addActionListener(e -> {
            if (virtual.isSelected()) commute.setSelected(false);
        });
        setModal(true);
        setSize(600, 300);
        SwingUtils.centerComponent(this);

        add(lblActivityName, cell(0,0));
        add(activityTitleTextField, cell(0,1).grow());

        add(lblActivityType, cell(0,2));

        add(activityTypeCombo, cell(0,3).grow());
        add(lblActivityDescription, cell(0,4));
        add(scrollPane, cell(0,5).grow());
        add(commute, cell(0,6));
        add(virtual, cell(0,6));
        //  container.add(statusLabel, "cell 0 6"); <-- here, it is now possible to add a progressbar
        add(btnOk, cell(0,7).flowX());
        add(btnCancel, cell(0,7));

        activityTitleTextField.setColumns(10);
        scrollPane.setViewportView(activityDescriptionArea);

        btnCancel.addActionListener(e -> dispose());

        btnOk.addActionListener(e -> uploadFile(trackFile));

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    private void uploadFile(File trackFile) {

        if (!StravaUtil.isStravaUp(hostCheckupTimeout)) {
            log.info("Some of Strava hosts are down or unavailable");
            SwingUtils.errorMsg("Some of Strava hosts are down or unavailable. Try again!");
            return;
        }
        try {
            log.info("Starting upload of {}", trackFile.getAbsolutePath());
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            Upload upload = stravaClient.uploadActivity(trackFile,
                    activityTitleTextField.getText(),
                    activityDescriptionArea.getText(),
                    (SportType) activityTypeCombo.getSelectedItem(),
                    virtual.isSelected(), commute.isSelected());
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            log.info("Upload request finished, getting the upload");
            if (upload.getActivityId() != null) {
                handleSuccessfulUpload(upload);
                archiveAndDelete();
            } else {
                handleUnsuccessfulUpload(upload);
            }
        } catch (StravaException e) {
            SwingUtils.errorMsg(e.getMessage());
        }
    }

    private void handleUnsuccessfulUpload(Upload upload) {
        log.error("Upload unsuccessful [API response: {}]", upload.getError());
        SwingUtils.errorMsg("Upload unsuccessful [API response:" + upload.getError() + "]");
        dispose();
    }

    private void handleSuccessfulUpload(Upload upload) {
        log.info("Upload successful [API response: {}]", upload.getStatus());
        EventBus.publish(EventType.ACTIVITY_SUCCESSFULLY_UPLOADED, trackFile);
        SwingUtils.infoMsg("Upload successful [API response: " + upload.getStatus() + "]");
        dispose();
    }

    private void archiveAndDelete() {
        try {
            fileService.archiveAndDelete(trackFile);
        } catch (IOException e) {
            SwingUtils.errorMsg("Archive file operation failed\n" + e + "\n" + e.getMessage());
            log.error("IO exception: {}", e.getMessage(), e);

        }
    }

    public void setTrackFile(File trackFile, SportType type) {
        this.trackFile = trackFile;
        this.activityTypeCombo.setSelectedItem(type);
        setTitle("Upload " + trackFile.getName() + " to Strava?");
    }
}
