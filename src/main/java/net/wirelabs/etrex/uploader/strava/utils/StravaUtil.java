package net.wirelabs.etrex.uploader.strava.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URLConnection;
import java.util.*;

import static net.wirelabs.etrex.uploader.strava.utils.NetworkingUtils.getAllIpsForHost;
import static net.wirelabs.etrex.uploader.strava.utils.NetworkingUtils.isHostTcpPortReachable;

/*
 * Created 12/10/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StravaUtil {

    public static final String ALLOWED_DAILY = "allowedDaily";
    public static final String ALLOWED_15MINS = "allowed15mins";
    public static final String CURRENT_DAILY = "currentDaily";
    public static final String CURRENT_15MINS = "current15mins";

    static final String STRAVA_HOST_NAME = "www.strava.com";
    static final int STRAVA_HTTP_PORT = 80;

    public static String guessUploadFileFormat(File file) throws StravaException {

        String[] allowedFileFormats = {"gpx", "gpx.gz", "fit", "fit.gz", "tcx", "tcx.gz"};
        String fileName = file.getName().toLowerCase();

        for (String extension : allowedFileFormats) {
            if (fileName.endsWith("." + extension)) return extension;
        }

        throw new StravaException("The file you're uploading is in unsupported format");
    }


    public static String guessContentTypeFromFileName(File file) {
        String contentType = URLConnection.guessContentTypeFromName(file.getName());
        return Objects.requireNonNullElse(contentType, "application/octet-stream");
    }

    public static void sendRateLimitInfo(Map<String, List<String>> headers) {

        if (headers.containsKey("x-ratelimit-limit") && headers.containsKey("x-ratelimit-usage")) {

            HashMap<String, Integer> info = new HashMap<>();

            String rateLimitAllowed = headers.get("x-ratelimit-limit").get(0);
            String rateLimitCurrent = headers.get("x-ratelimit-usage").get(0);

            StringTokenizer tokenizer = new StringTokenizer(rateLimitAllowed, ",");
            info.put(ALLOWED_15MINS, Integer.parseInt(tokenizer.nextToken()));
            info.put(ALLOWED_DAILY, Integer.parseInt(tokenizer.nextToken()));

            tokenizer = new StringTokenizer(rateLimitCurrent, ",");
            info.put(CURRENT_15MINS, Integer.parseInt(tokenizer.nextToken()));
            info.put(CURRENT_DAILY, Integer.parseInt(tokenizer.nextToken()));

            EventBus.publish(EventType.RATELIMIT_INFO_UPDATE, info);
        }
    }

    /**
     * Checks if ipv4 strava hosts are available for http connection
     *
     * @return yes or no
     */
    public static boolean isStravaUp(int hostTimeout) {

        List<InetAddress> allStravaIpv4Hosts;

        try {
            allStravaIpv4Hosts = getAllIpsForHost(getStravaHostName());

            for (InetAddress stravaHost : allStravaIpv4Hosts) {
                String host = stravaHost.getHostAddress();
                // if one of the hosts is unreachable - false
                if (!isHostTcpPortReachable(host, getStravaPort(), hostTimeout)) {
                    log.warn("{}:{} inaccessible, assume uploads might fail", host, getStravaPort());
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

    static int getStravaPort() {
        return STRAVA_HTTP_PORT;
    }

    static String getStravaHostName() {
        return STRAVA_HOST_NAME;
    }
}

