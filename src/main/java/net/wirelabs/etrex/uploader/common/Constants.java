package net.wirelabs.etrex.uploader.common;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wirelabs.etrex.uploader.EtrexUploaderRunner;
import net.wirelabs.jmaps.map.geo.Coordinate;

import java.io.File;

/**
 * Created 10/27/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String CURRENT_WORK_DIR = System.getProperty("user.dir");
    public static final String HOME_DIR = System.getProperty("user.home");

    public static final Coordinate DEFAULT_MAP_START_LOCATION = new Coordinate(22.565628, 51.247717); // Lublin,PL!
    public static final int DEFAULT_MAP_START_ZOOM = 12;
    public static final String COPYRIGHT_SIGN = Character.toString( 169 );
    public static final String APPLICATION_IDENTIFICATION = String.format("Etrex Uploader ver: %s (%s) 2022 Michał Szwaczko, WireLabs Technologies", EtrexUploaderRunner.APP_VERSION, COPYRIGHT_SIGN);
    public static final String STRAVA_DEFAULT_APP_ACCESS_SCOPE = "activity:read,activity:write,read_all";
    public static final String STRAVA_AUTHORIZATION_URL = "https://www.strava.com/oauth/authorize";
    public static final String STRAVA_TOKEN_URL = "https://www.strava.com/oauth/token";
    public static final String STRAVA_BASE_URL ="https://www.strava.com/api/v3";
    public static final String STRAVA_ACTIVITY_URL = "https://www.strava.com/activities";
    public static final String DEFAULT_MAP = "defaultMap.xml";
    public static final String GARMIN_DEVICE_XML = "GarminDevice.xml";
    // these are home dir based
    public static final String DEFAULT_LOCAL_STORE = HOME_DIR + File.separator +"etrex-uploader-store";
    public static final String DEFAULT_USER_MAP_DIR = HOME_DIR + File.separator +"etrex-uploader-maps";

    public static final String EMPTY_STRING = "";
    public static final String LINUX_USB_MOUNTDIR = "/media";

    public static final int DEFAULT_AUTH_CODE_TIMEOUT_SECONDS = 60;


}
