package net.wirelabs.etrex.uploader.strava;

import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common.filetree.UploadDialog;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;

import java.io.File;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class UploadService {

    private final StravaClient stravaClient;
    private final FileService fileService;

    public UploadService(StravaClient stravaClient, FileService fileService) {
        this.stravaClient = stravaClient;
        this.fileService = fileService;
    }

    public void uploadFile(File file) {
        UploadDialog uploadDialog = createUploadDialog();
        uploadDialog.setTrackFile(file, stravaClient.getStravaConfiguration().getDefaultActivityType());
        uploadDialog.setVisible(true);
    }

    UploadDialog createUploadDialog() {
        return new UploadDialog(stravaClient, fileService);
    }
}
