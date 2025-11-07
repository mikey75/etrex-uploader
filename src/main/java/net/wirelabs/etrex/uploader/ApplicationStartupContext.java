package net.wirelabs.etrex.uploader;

import lombok.Getter;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.device.GarminDeviceService;
import net.wirelabs.etrex.uploader.strava.StravaConnectionChecker;
import net.wirelabs.etrex.uploader.strava.UploadService;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;

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
    private final StravaClient stravaClient;
    private final StravaConnectionChecker stravaConnectionChecker;

    public ApplicationStartupContext() throws IOException {
        // load config files
        this.appConfiguration = new AppConfiguration(Constants.DEFAULT_APPLICATION_CONFIG_FILE);
        this.stravaConfiguration = new StravaConfiguration(Constants.DEFAULT_STRAVA_CONFIG_FILE);
        // create necessary services
        this.fileService = new FileService(appConfiguration);
        this.garminDeviceService = new GarminDeviceService(appConfiguration);
        this.stravaClient = new StravaClient(stravaConfiguration, appConfiguration);
        this.uploadService = new UploadService(stravaClient, fileService);
        this.stravaConnectionChecker = new StravaConnectionChecker(stravaConfiguration);
    }



}
