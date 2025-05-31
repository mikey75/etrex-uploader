package net.wirelabs.etrex.uploader;

import lombok.Getter;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.device.GarminDeviceService;
import net.wirelabs.etrex.uploader.gui.UploadService;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.strava.service.StravaService;
import net.wirelabs.etrex.uploader.strava.service.StravaServiceImpl;

import java.io.IOException;


/**
 * Created 11/2/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Getter
public class ApplicationStartupContext {

    private final AppConfiguration appConfiguration;
    private final StravaConfiguration stravaConfiguration;
    private final UploadService uploadService;
    private final FileService fileService;
    private final GarminDeviceService garminDeviceService;
    private final StravaService stravaService;
    private final StravaClient stravaClient;

    public ApplicationStartupContext() throws IOException {
        // load config files
        this.appConfiguration = new AppConfiguration(Constants.DEFAULT_APPLICATION_CONFIG_FILE);
        this.stravaConfiguration = new StravaConfiguration(Constants.DEFAULT_STRAVA_CONFIG_FILE);
        // create necessary services
        this.fileService = new FileService(appConfiguration);
        this.garminDeviceService = new GarminDeviceService(appConfiguration);
        this.stravaClient = new StravaClient(stravaConfiguration);
        this.stravaService = new StravaServiceImpl(appConfiguration,stravaClient);
        this.uploadService = new UploadService(appConfiguration, stravaService, fileService);
    }



}
