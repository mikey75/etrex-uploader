package net.wirelabs.etrex.uploader.gui.components.filetree;

import com.strava.model.SportType;
import com.strava.model.Upload;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.StravaException;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.strava.service.StravaService;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtil;
import net.wirelabs.eventbus.EventBus;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;




@Slf4j
public class UploadDialog extends JDialog {


    private File trackFile;
    @Setter private int hostCheckupTimeout;
    private final JTextField activityTitleTextField;
    private final JComboBox<SportType> activityTypeCombo;
    private final JTextArea activityDesctiptionArea;

    
    private final JLabel lblActivityName;
    private final JLabel lblActivityType;
    private final JLabel lblActivityDescription;
    private final JScrollPane scrollPane;
    private final JButton btnOk;
    private final JButton btnCancel;
    private final JCheckBox commute; // is the activity a commute
    private final JCheckBox virtual; // is the activity a virtual/trainer ride
    private final StravaService stravaService;
    private final transient FileService fileService;

    public UploadDialog(StravaService stravaService, FileService fileService) {
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

        Container container = getContentPane();

        container.setLayout(new MigLayout("", "[grow]", "[][][][][][grow][][]"));
        container.add(lblActivityName, "cell 0 0");
        container.add(activityTitleTextField, "cell 0 1,grow");

        container.add(lblActivityType, "cell 0 2");

        container.add(activityTypeCombo, "cell 0 3,grow");
        container.add(lblActivityDescription, "cell 0 4");
        container.add(scrollPane, "cell 0 5,grow");
        container.add(commute, "cell 0 6");
        container.add(virtual, "cell 0 6");
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

        if (!StravaUtil.isStravaUp(hostCheckupTimeout)) {
            log.info("Some of Strava hosts are down or unavailable");
            SwingUtils.errorMsg("Some of Strava hosts are down or unavailable. Try again!");
            return;
        }
        try {
            log.info("Starting upload of {}", trackFile.getAbsolutePath());
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            Upload upload = stravaService.uploadActivity(trackFile,
                    activityTitleTextField.getText(),
                    activityDesctiptionArea.getText(),
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
        log.error("Upload unsucessful [API response: {}]", upload.getError());
        SwingUtils.errorMsg("Upload unsucessful [API response:" + upload.getError() + "]");
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
