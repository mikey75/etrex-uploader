package net.wirelabs.etrex.uploader;

import lombok.Getter;
import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.device.GarminDeviceService;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.strava.service.StravaService;
import net.wirelabs.etrex.uploader.strava.service.StravaServiceImpl;


/**
 * Created 11/2/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Getter
public class ApplictationContext {

    private final AppConfiguration appConfiguration;
    private final StravaConfiguration stravaConfiguration;

    private final FileService fileService;
    private final GarminDeviceService garminDeviceService;
    private final StravaService stravaService;
    private final StravaClient stravaClient;

    public ApplictationContext() {
        this.appConfiguration = new AppConfiguration();
        this.stravaConfiguration = new StravaConfiguration();
        this.fileService = new FileService(appConfiguration);
        this.garminDeviceService = new GarminDeviceService(appConfiguration);

        this.stravaClient = new StravaClient(stravaConfiguration);
        this.stravaService = new StravaServiceImpl(appConfiguration,stravaClient);

    }

    

}
