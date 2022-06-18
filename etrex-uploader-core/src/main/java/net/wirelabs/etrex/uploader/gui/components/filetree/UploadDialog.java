package net.wirelabs.etrex.uploader.gui.components.filetree;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;


import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.model.strava.Upload;
import net.wirelabs.etrex.uploader.strava.IStravaService;
import net.wirelabs.etrex.uploader.strava.api.StravaApiException;
import net.wirelabs.etrex.uploader.system.FileService;

import net.wirelabs.eventbus.EventBus;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;




@Slf4j
public class UploadDialog extends JDialog {


    private File trackFile;
    private final JTextField activityTitleTextField;
    private final JComboBox<String> activityTypeCombo;
    private final JTextArea activityDesctiptionArea;

    private final String[] stravaActivityTypes = {"Ride", "MountainBikeRide", "Walk", "Hike", "Run", "EBikeRide",
            "EMountainBikeRide", "Canoeing", "Kayaking", "AlpineSki", "BackcountrySki", "Crossfit", "Elliptical",
            "Golf", "Handcycle", "IceSkate", "InlineSkate", "Kitesurf", "NordicSki", "RockClimbing", "RollerSki",
            "Rowing", "Sail", "Skateboard", "Snowboard", "Snowshoe", "Soccer", "StairStepper", "StandUpPaddling",
            "Surfing", "Swim", "TrailRun", "Velomobile", "VirtualRide", "VirtualRun", "WeightTraining", "Wheelchair",
            "Windsurf", "Workout", "Yoga"};
    private final JLabel lblActivityName;
    private final JLabel lblActivityType;
    private final JLabel lblActivityDescription;
    private final JScrollPane scrollPane;
    private final JButton btnOk;
    private final JButton btnCancel;
    private final IStravaService stravaService;
    private final FileService fileService;

    public UploadDialog(IStravaService stravaService, FileService fileService) {
        this.stravaService = stravaService;
        this.fileService = fileService;
        lblActivityName = new JLabel("Name");
        lblActivityDescription = new JLabel("Description");
        lblActivityType = new JLabel("Activity Type");
        scrollPane = new JScrollPane();
        activityTitleTextField = new JTextField();
        activityDesctiptionArea = new JTextArea();
        btnOk = new JButton("Upload");
        btnCancel = new JButton("Cancel");
        activityTypeCombo = new JComboBox<>(stravaActivityTypes);

        setModal(true);
        setSize(600, 300);
        SwingUtils.centerComponent(this);

        Container container = getContentPane();

        container.setLayout(new MigLayout("", "[grow]", "[][][][][][grow][][]"));
        container.add(lblActivityName, "cell 0 0");
        container.add(activityTitleTextField, "cell 0 1,grow");

        container.add(lblActivityType, "cell 0 2");

        container.add(activityTypeCombo, "cell 0 3,grow");
        container.add(lblActivityDescription, "cell 0 4");
        container.add(scrollPane, "cell 0 5,grow");

        //  container.add(statusLabel, "cell 0 6"); <-- tu zrobic mozna progressbar teraz
        container.add(btnOk, "flowx,cell 0 7");
        container.add(btnCancel, "cell 0 7");

        activityTitleTextField.setColumns(10);
        scrollPane.setViewportView(activityDesctiptionArea);

        btnCancel.addActionListener(e -> dispose());

        btnOk.addActionListener(e -> uploadFile(trackFile));

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    private void uploadFile(File trackFile) {

        try {

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Upload upload = stravaService.uploadActivity(trackFile, activityTitleTextField.getText(),
                    activityDesctiptionArea.getText(), (String) activityTypeCombo.getSelectedItem());
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            if (upload.getActivityId() != null) {
                handleSuccessfulUpload(upload);
                archiveAndDelete();
            } else {
                handleUnsuccessfulUpload(upload);
            }
        } catch (StravaApiException e) {
            SwingUtils.errorMsg(e.getMessage());
        }
    }

    private void handleUnsuccessfulUpload(Upload upload) {
        log.error("Upload failed [Api response: {}]", upload.getError());
        SwingUtils.errorMsg("Upload unsucessful [API response:" + upload.getError() + "]");
        dispose();
    }

    private void handleSuccessfulUpload(Upload upload) {
        EventBus.publish(EventType.EVT_ACTIVITY_UPLOADED, trackFile);
        log.info("Upload successful [API response: {}]",  upload.getStatus());
        SwingUtils.infoMsg("Upload successful [API response: " + upload.getStatus() + "]");
        dispose();
    }

    private void archiveAndDelete() {
        try {
            fileService.archivAndDelete(trackFile);
        } catch (IOException e) {
            SwingUtils.errorMsg("Archive file operation failed\n" + e + "\n" + e.getMessage());
            log.error("IO exception: {}", e.getMessage(), e);

        }
    }

    public void clearInputAndStatus() {
        activityDesctiptionArea.setText("");
        activityTitleTextField.setText("");
        activityTypeCombo.setSelectedIndex(0);

    }

    public void setTrackFile(File trackFile) {
        this.trackFile = trackFile;
        setTitle("Upload " + trackFile.getName() + " to Strava?");
    }
}
