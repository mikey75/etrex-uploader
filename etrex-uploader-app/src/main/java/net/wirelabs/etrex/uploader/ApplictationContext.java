package net.wirelabs.etrex.uploader;

import ch.qos.logback.core.subst.Token;
import lombok.Getter;
import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
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

    private final Configuration configuration;
    private final FileService fileService;
    private final IStravaService stravaService;
    private final GarminDeviceService garminDeviceService;
    private final TokenManager tokenManager;
    private final StravaClient client;

    public ApplictationContext() {
        this.configuration = new Configuration();
        this.fileService = new FileService(configuration);
        this.garminDeviceService = new GarminDeviceService(configuration);
        
        this.tokenManager = new TokenManager(configuration);
        this.client = new StravaClient(tokenManager);
        this.stravaService = new StravaService(client);
    }

    

}
