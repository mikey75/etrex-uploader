package net.wirelabs.etrex.uploader.common;


import com.strava.model.SportType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.jmaps.map.geo.Coordinate;

import java.nio.file.Paths;

/**
 * Created 10/27/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final Coordinate DEFAULT_MAP_HOME_LOCATION = new Coordinate(22.565628, 51.247717); // Lublin,PL!
    public static final int DEFAULT_MAP_START_ZOOM = 12;

    public static final String APPLICATION_IDENTIFICATION = String.format(
            "Etrex Uploader ver: %s (Jmaps ver: %s) © 2022 Michał Szwaczko, WireLabs Technologies" ,
            SystemUtils.getAppVersion(),
            SystemUtils.getJmapsVerion()
    );
    public static final String STRAVA_DEFAULT_APP_ACCESS_SCOPE = "activity:read,activity:write,read_all";
    public static final String STRAVA_AUTHORIZATION_URL = "https://www.strava.com/oauth/authorize";
    public static final String STRAVA_TOKEN_URL = "https://www.strava.com/oauth/token";
    public static final String STRAVA_BASE_URL = "https://www.strava.com/api/v3";
    public static final String STRAVA_ACTIVITY_URL = "https://www.strava.com/activities";
    public static final String DEFAULT_MAP = "defaultMap.xml";
    public static final String GARMIN_DEVICE_XML = "GarminDevice.xml";
    // these are home dir based
    public static final String DEFAULT_LOCAL_STORE = String.valueOf(Paths.get(SystemUtils.getHomeDir(), "etrex-uploader-store"));
    public static final String DEFAULT_USER_MAP_DIR = String.valueOf(Paths.get(SystemUtils.getHomeDir(), "etrex-uploader-maps"));

    public static final String EMPTY_STRING = "";
    public static final String TRUE = Boolean.TRUE.toString();
    public static final String LINUX_USB_MOUNTDIR = "/media";
    public static final String OSX_USB_MOUNTDIR = "/Volumes";
    public static final int DEFAULT_AUTH_CODE_TIMEOUT_SECONDS = 60;
    public static final String DEFAULT_TRACK_COLOR = "#ff0000";
    public static final SportType DEFAULT_SPORT = SportType.RIDE;

    public static final long DEFAULT_DRIVE_OBSERVER_DELAY = 500;
    public static final long DEFAULT_WAIT_DRIVE_TIMEOUT = 15000;
    public static final int DEFAULT_TILER_THREAD_COUNT = 8;
    public static final int DEFAULT_STRAVA_CHECK_TIMEOUT = 500;
    public static final int DEFAULT_STRAVA_ACTIVITIES_PER_PAGE = 30;
    public static final int DEFAULT_API_USAGE_WARN_PERCENT = 85;
    public static final int DEFAULT_UPLOAD_STATUS_WAIT_SECONDS = 60;

    public static final boolean DEFAULT_USE_SLIDERS = false;

    public static final String STRAVA_AUTHORIZATION_OK_MSG =
            "<center>" +
                "<h1>You have allowed the etrex-uploader access to your strava account</h1>" +
                "<h2>You can close your browser now and enjoy etrex-uploader</h2>" +
            "</center>";

    public static final String STRAVA_AUTHORIZATION_FAIL_MSG =
            "<center>" +
                "<h1>FAILURE!</h1>" +
                "<h1>Allowing etrex-uploader access to your Strava account failed</h1>" +
                "<h2>You can close your browser now and investigate logs</h2>" +
            "</center>";
}
