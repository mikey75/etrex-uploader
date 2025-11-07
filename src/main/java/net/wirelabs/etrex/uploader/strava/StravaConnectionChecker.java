package net.wirelabs.etrex.uploader.strava;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtil;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.utils.SystemUtils;

@Slf4j

public class StravaConnectionChecker {

    private final StravaConfiguration configuration;

    public StravaConnectionChecker(StravaConfiguration configuration) {
        this.configuration = configuration;
    }

    public void checkStravaIsUp() {
        log.info("Starting Strava status check");
        if (StravaUtil.isStravaUp(configuration.getStravaCheckTimeout())) {
            log.info("Strava is up and running!");
        } else {
            SwingUtils.errorMsg("Strava seems to be down! Exiting!");
            log.warn("Strava seems to be down. Exiting!");
            SystemUtils.systemExit(1);
        }
    }
}
