package net.wirelabs.etrex.uploader.strava;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.utils.SystemUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import static net.wirelabs.etrex.uploader.utils.NetworkingUtils.getAllIpsForHost;
import static net.wirelabs.etrex.uploader.utils.NetworkingUtils.isHostTcpPortReachable;

@Slf4j

public class StravaConnectionChecker {

    @Getter @Setter private String stravaHostName = "www.strava.com";
    @Getter @Setter private int stravaPort = 80;

    private final StravaConfiguration configuration;

    public StravaConnectionChecker(StravaConfiguration configuration) {
        this.configuration = configuration;
    }

    // emit warning end exit the app
    public void checkAndExitIfDown() {
        doCheck(true);
    }

    // just emit the warning but don't exit the app
    // throw exception so that you can react to this case upstream
    public void checkAndContinueIfDown() {
        doCheck(false);
    }

    private void doCheck(boolean exit) {
        log.info("Starting Strava status check");
        if (isStravaUp(configuration.getStravaCheckTimeout())) {
            log.info("Strava is up and running!");
        } else {
            String main = "Strava seems to be down. ";
            String result = (exit) ? "Exiting!" : "Try again!";
            SwingUtils.errorMsg(main + result);
            log.warn(main + result);
            if (exit) {
                SystemUtils.systemExit(1);
            } else {
                throw new IllegalStateException("Strava is down, but continuing");
            }
        }
    }

    /**
     * Checks if ipv4 strava hosts are available for http connection
     *
     * @return yes or no
     */
    private boolean isStravaUp(int hostTimeout) {

        List<InetAddress> allStravaIpv4Hosts;

        try {
            allStravaIpv4Hosts = getAllIpsForHost(stravaHostName);

            for (InetAddress stravaHost : allStravaIpv4Hosts) {
                String host = stravaHost.getHostAddress();
                // if one of the hosts is unreachable - false
                if (!isHostTcpPortReachable(host, stravaPort, hostTimeout)) {
                    log.warn("{}:{} inaccessible, assume uploads might fail", host, stravaPort);
                    return false;
                }
            }
            // all hosts reachable
            return true;

        } catch (IOException e) {
            log.error("Strava or network is down!");
            return false;
        }
    }
}
