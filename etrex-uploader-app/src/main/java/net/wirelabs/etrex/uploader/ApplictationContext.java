package net.wirelabs.etrex.uploader;

import lombok.Getter;
import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.device.GarminDeviceService;
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


    public ApplictationContext() {
        this.configuration = new Configuration();
        this.fileService = new FileService(configuration);
        this.stravaService = new StravaService(configuration);
        this.garminDeviceService = new GarminDeviceService(configuration);
    }

    

}
