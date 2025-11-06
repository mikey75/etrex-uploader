package net.wirelabs.etrex.uploader.configuration;

import com.strava.model.SportType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;


import static net.wirelabs.etrex.uploader.configuration.ConfigurationPropertyKeys.*;

/**
 * Created 12/14/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

@Getter
@Setter
@Slf4j
public class StravaConfiguration extends PropertiesBasedConfiguration {

    private int stravaCheckTimeout;
    private String stravaAppId;
    private String stravaClientSecret;
    private String stravaAccessToken;
    private String stravaRefreshToken;
    private Long stravaTokenExpires;

    // urls
    private String baseTokenUrl;
    private String baseUrl;
    private String authUrl;

    // other
    private SportType defaultActivityType;
    private boolean usePolyLines;
    private int perPage;
    private int apiUsageWarnPercent;
    private boolean stravaCheckHostBeforeUpload;
    private int uploadStatusWaitSeconds;

    public StravaConfiguration(String configFileName) {
        super(configFileName);
        stravaAppId = properties.getProperty(STRAVA_APP_ID, Constants.EMPTY_STRING);
        stravaClientSecret = properties.getProperty(STRAVA_CLIENT_SECRET, Constants.EMPTY_STRING);
        stravaAccessToken = properties.getProperty(STRAVA_ACCESS_TOKEN, Constants.EMPTY_STRING);
        stravaRefreshToken = properties.getProperty(STRAVA_REFRESH_TOKEN, Constants.EMPTY_STRING);
        stravaTokenExpires = Long.valueOf(properties.getProperty(STRAVA_ACCESS_TOKEN_EXPIRES_AT, "0"));
        baseTokenUrl = properties.getProperty(STRAVA_TOKEN_URL, Constants.STRAVA_TOKEN_URL);
        baseUrl = properties.getProperty(STRAVA_API_BASE_URL, Constants.STRAVA_API_BASE_URL);
        authUrl = properties.getProperty(STRAVA_AUTH_URL, Constants.STRAVA_AUTH_URL);
        defaultActivityType = SportType.valueOf(properties.getProperty(STRAVA_DEFAULT_ACTIVITY_TYPE, Constants.DEFAULT_SPORT.name()));
        stravaCheckTimeout = Integer.parseInt(properties.getProperty(STRAVA_CHECK_HOST_TIMEOUT, String.valueOf(Constants.STRAVA_CHECK_TIMEOUT)));
        usePolyLines = Boolean.parseBoolean(properties.getProperty(STRAVA_USE_POLYLINES, Constants.TRUE));
        perPage = Integer.parseInt(properties.getProperty(STRAVA_ACTIVITIES_PER_PAGE, String.valueOf(Constants.STRAVA_ACTIVITIES_PER_PAGE)));
        apiUsageWarnPercent = Integer.parseInt(properties.getProperty(STRAVA_API_USAGE_WARN_PERCENT, String.valueOf(Constants.STRAVA_API_USAGE_WARN_PERCENT)));
        stravaCheckHostBeforeUpload = Boolean.parseBoolean(properties.getProperty(STRAVA_CHECK_HOST_BEFORE_UPLOAD,Constants.TRUE));
        uploadStatusWaitSeconds = Integer.parseInt(properties.getProperty(STRAVA_UPLOAD_STATUS_WAIT_SECONDS, String.valueOf(Constants.UPLOAD_STATUS_WAIT_SECONDS)));

        if (!configFileExists()) {
            log.info("Saving new strava config file with default values");
            save();
        }

    }

    public void save() {
        properties.setProperty(STRAVA_APP_ID, stravaAppId);
        properties.setProperty(STRAVA_CLIENT_SECRET, stravaClientSecret);
        properties.setProperty(STRAVA_ACCESS_TOKEN, stravaAccessToken);
        properties.setProperty(STRAVA_REFRESH_TOKEN, stravaRefreshToken);
        properties.setProperty(STRAVA_ACCESS_TOKEN_EXPIRES_AT, String.valueOf(stravaTokenExpires));
        properties.setProperty(STRAVA_TOKEN_URL, baseTokenUrl);
        properties.setProperty(STRAVA_API_BASE_URL, baseUrl);
        properties.setProperty(STRAVA_AUTH_URL, authUrl);
        properties.setProperty(STRAVA_DEFAULT_ACTIVITY_TYPE, defaultActivityType.name());
        properties.setProperty(STRAVA_CHECK_HOST_TIMEOUT, String.valueOf(stravaCheckTimeout));
        properties.setProperty(STRAVA_USE_POLYLINES, String.valueOf(usePolyLines));
        properties.setProperty(STRAVA_ACTIVITIES_PER_PAGE, String.valueOf(perPage));
        properties.setProperty(STRAVA_API_USAGE_WARN_PERCENT, String.valueOf(apiUsageWarnPercent));
        properties.setProperty(STRAVA_CHECK_HOST_BEFORE_UPLOAD, String.valueOf(stravaCheckHostBeforeUpload));
        properties.setProperty(STRAVA_UPLOAD_STATUS_WAIT_SECONDS, String.valueOf(uploadStatusWaitSeconds));
        storePropertiesToFile();
    }

    boolean hasAccessToken() {
        return stravaAccessToken != null && !stravaAccessToken.isBlank();
    }

    boolean hasRefreshToken() {
        return stravaRefreshToken != null && !stravaRefreshToken.isBlank();
    }

    boolean hasAppId() {
        return stravaAppId != null && !stravaAppId.isBlank();
    }

    boolean hasClientSecret() {
        return stravaClientSecret != null && !stravaClientSecret.isBlank();
    }

    public boolean hasAllTokensAndCredentials() {
        return hasAccessToken() && hasRefreshToken() && hasAppId() && hasClientSecret();
    }
}
