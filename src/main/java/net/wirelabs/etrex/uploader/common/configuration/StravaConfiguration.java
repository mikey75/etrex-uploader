package net.wirelabs.etrex.uploader.common.configuration;

import lombok.Getter;
import lombok.Setter;
import net.wirelabs.etrex.uploader.common.Constants;

import static net.wirelabs.etrex.uploader.common.configuration.ConfigurationPropertyKeys.*;

/**
 * Created 12/14/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

@Getter
@Setter
public class StravaConfiguration extends PropertiesBasedConfiguration {

    private String stravaAppId;
    private String stravaClientSecret;
    private String stravaAccessToken;
    private String stravaRefreshToken;
    private Long stravaTokenExpires;


    public StravaConfiguration(String configFileName) {
        super(configFileName);
        stravaAppId = properties.getProperty(STRAVA_APP_ID, Constants.EMPTY_STRING);
        stravaClientSecret = properties.getProperty(STRAVA_CLIENT_SECRET, Constants.EMPTY_STRING);
        stravaAccessToken = properties.getProperty(STRAVA_ACCESS_TOKEN, Constants.EMPTY_STRING);
        stravaRefreshToken = properties.getProperty(STRAVA_REFRESH_TOKEN, Constants.EMPTY_STRING);
        stravaTokenExpires = Long.valueOf(properties.getProperty(STRAVA_ACCESS_TOKEN_EXPIRES_AT, "0"));

    }

    public StravaConfiguration() {
        this(STRAVA_CONFIGFILE);
    }

    public void save() {
        properties.setProperty(STRAVA_APP_ID, stravaAppId);
        properties.setProperty(STRAVA_CLIENT_SECRET, stravaClientSecret);
        properties.setProperty(STRAVA_ACCESS_TOKEN, stravaAccessToken);
        properties.setProperty(STRAVA_REFRESH_TOKEN, stravaRefreshToken);
        properties.setProperty(STRAVA_ACCESS_TOKEN_EXPIRES_AT, String.valueOf(stravaTokenExpires));
        super.store();
    }

    private boolean hasAccessToken() {
        return stravaAccessToken != null && !stravaAccessToken.isBlank();
    }

    private boolean hasRefreshToken() {
        return stravaRefreshToken != null && !stravaRefreshToken.isBlank();
    }

    private boolean hasAppId() {
        return stravaAppId != null && !stravaAppId.isBlank();
    }

    private boolean hasClientSecret() {
        return stravaClientSecret != null && !stravaClientSecret.isBlank();
    }

    public boolean hasAllTokensAndCredentials() {
        return hasAccessToken() && hasRefreshToken() && hasAppId() && hasClientSecret();
    }
}
