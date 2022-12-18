package net.wirelabs.etrex.uploader.gui;

import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.components.filetree.UploadDialog;
import net.wirelabs.etrex.uploader.strava.service.StravaService;

import java.io.File;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class UploadService {

    private final AppConfiguration configuration;
    private final StravaService stravaService;
    private final FileService fileService;

    public UploadService(AppConfiguration configuration, StravaService stravaService, FileService fileService) {
        this.configuration = configuration;
        this.stravaService = stravaService;
        this.fileService = fileService;

    }
    public void uploadFile(File file) {
        UploadDialog uploadDialog = new UploadDialog(stravaService,fileService);
        uploadDialog.setTrackFile(file, configuration.getDefaultActivityType());
        uploadDialog.setVisible(true);
    }
}
