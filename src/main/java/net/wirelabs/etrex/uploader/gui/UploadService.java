package net.wirelabs.etrex.uploader.gui;

import lombok.Getter;
import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.components.filetree.UploadDialog;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;

import java.io.File;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class UploadService {

    private final AppConfiguration configuration;
    @Getter private final UploadDialog uploadDialog;

    public UploadService(StravaClient stravaClient, FileService fileService) {
        this.configuration = stravaClient.getAppConfiguration();
        this.uploadDialog = new UploadDialog(stravaClient,fileService);
    }
    public void uploadFile(File file) {
        uploadDialog.setTrackFile(file, configuration.getDefaultActivityType());
        uploadDialog.setHostCheckupTimeout(configuration.getStravaCheckTimeout());
        uploadDialog.setVisible(true);
    }
}
