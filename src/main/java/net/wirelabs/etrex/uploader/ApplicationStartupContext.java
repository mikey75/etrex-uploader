package net.wirelabs.etrex.uploader;

import lombok.Getter;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.etrex.uploader.device.GarminDeviceService;
import net.wirelabs.etrex.uploader.gui.strava.auth.StravaConnector;
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
        // run OAuth if not already authorized
        runStravaOAuthIfNecessary(stravaClient);
    }

    private void runStravaOAuthIfNecessary(StravaClient client) {

        if (!isAuthorized()) {
            StravaConnector connector = new StravaConnector(client);
            if (!connector.getOauthStatus().get()) {
                SwingUtils.errorMsg(connector.getOauthMessage());
                SystemUtils.systemExit(1);
            }
        }
    }

    // You are authorized if you have all tokens (access/refresh)
    // and app credentials (client id/client secret)
    public boolean isAuthorized() {
        return stravaConfiguration.hasAllTokensAndCredentials();
    }


}
