package net.wirelabs.etrex.uploader.common;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wirelabs.jmaps.map.geo.Coordinate;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;


/**
 * Created 10/27/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String CURRENT_WORK_DIR = System.getProperty("user.dir");
    public static final String HOME_DIR = System.getProperty("user.home");

    public static final String APPLICATION_IDENTIFICATION = "Etrex Uploader ver: 1.0.0 (c) Michał Szwaczko (c) 2022 WireLabs Technologies";
    public static final Coordinate DEFAULT_MAP_START_LOCATION = new Coordinate(22.565628, 51.247717); // Lublin,PL!
    public static final int DEFAULT_MAP_START_ZOOM = 12;
    public static final String COPYRIGHT_SIGN = Character.toString( 169 );
    public static final String APPLICATION_IDENTIFICATION = "Etrex Uploader ver: "
            + SystemUtils.getAppVersion()
            + " " + COPYRIGHT_SIGN + " "
            + "2022 Michał Szwaczko, WireLabs Technologies";

    public static final String STRAVA_DEFAULT_APP_ACCESS_SCOPE = "activity:read,activity:write,read_all";
    public static final String STRAVA_AUTHORIZATION_URL = "https://www.strava.com/oauth/authorize";
    public static final String STRAVA_TOKEN_URL = "https://www.strava.com/oauth/token";
    public static final String STRAVA_BASE_URL ="https://www.strava.com/api/v3";
    public static final String STRAVA_ACTIVITY_URL = "https://www.strava.com/activities";
    public static final String DEFAULT_MAP = "defaultMap.xml";
    public static final String GARMIN_DEVICE_XML = "GarminDevice.xml";
    public static final String DEFAULT_LOCAL_STORE = "etrex-uploader-store";
    public static final String DEFAULT_USER_MAP_DIR = "etrex-uploader-maps";
    public static final String UPLOADED_FILES_SUBFOLDER = "archived-uploads";
    public static final String TRACKS_REPO = "tracks-archive";
    public static final String EMPTY_STRING = "";
    public static final String LINUX_USB_MOUNTDIR = "/media";

    public static final int DEFAULT_AUTH_CODE_TIMEOUT_SECONDS = 60;

    public static final String AUTHORIZATION_OK_MSG = "<center><h1>You have allowed the etrex-uploader access to your strava account</h1>" +
            "<h2>You can close your browser now and enjoy etrex-uploader</h2>";
    public static final String AUTHORIZATION_FAIL_MSG = "<center><h1>FAILURE! FAILURE! FAILURE!</h1>" +
            "<h1>Allowing etrex-uploader access to your Strava account failed</h1>" +
            "<h2>You can close your browser now and investigate logs</h2>";
}
