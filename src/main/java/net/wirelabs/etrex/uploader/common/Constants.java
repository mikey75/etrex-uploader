package net.wirelabs.etrex.uploader.common;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Created 10/27/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String APPLICATION_IDENTIFICATION = "Etrex Uploader ver: 1.0.0 (c) Michał Szwaczko (c) 2022 WireLabs Technologies";

    public static final String STRAVA_DEFAULT_APP_ACCESS_SCOPE = "activity:read,activity:write,read_all";

    public static final String STRAVA_AUTHORIZATION_URL = "https://www.strava.com/oauth/authorize";
    public static final String STRAVA_BASE_URL ="https://www.strava.com/api/v3";

    public static final String GARMIN_DEVICE_XML = "GarminDevice.xml";
    public static final String DEFAULT_LOCAL_STORE = "etrex-uploader-store";
    public static final String UPLOADED_FILES_SUBFOLDER = "archived-uploads";
    public static final String TRACKS_REPO = "tracks-archive";
    public static final String EMPTY_STRING = "";

    public static final String AUTHORIZATION_OK_MSG = "<center><h1>You have allowed the etrex-uploader access to your strava account</h1>" +
            "<h2>You can close your browser now and enjoy etrex-uploader</h2>";

    public static final String AUTHORIZATION_FAIL_MSG = "<center><h1>FAILURE! FAILURE! FAILURE!</h1>" +
            "<h1>Allowing etrex-uploader access to your Strava account failed</h1>" +
            "<h2>You can close your browser now and investigate logs</h2>";
}
