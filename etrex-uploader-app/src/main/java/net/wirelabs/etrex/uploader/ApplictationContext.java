package net.wirelabs.etrex.uploader;

import lombok.Getter;
import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.device.GarminDeviceService;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.strava.client.TokenManager;
import net.wirelabs.etrex.uploader.strava.service.IStravaService;
import net.wirelabs.etrex.uploader.strava.service.StravaService;



/**
 * Created 11/2/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Getter
public class ApplictationContext {

    private final AppConfiguration appConfiguration;
    private final StravaConfiguration stravaConfiguration;
    private final FileService fileService;
    private final IStravaService stravaService;
    private final GarminDeviceService garminDeviceService;
    private final TokenManager tokenManager;
    private final StravaClient client;

    public ApplictationContext() {
        this.appConfiguration = new AppConfiguration();
        this.fileService = new FileService(appConfiguration);
        this.garminDeviceService = new GarminDeviceService(appConfiguration);

        this.stravaConfiguration = new StravaConfiguration();
        this.tokenManager = new TokenManager(stravaConfiguration);
        this.client = new StravaClient(tokenManager);
        this.stravaService = new StravaService(client);
    }

    

}
