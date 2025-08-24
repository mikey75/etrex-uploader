package net.wirelabs.etrex.uploader.strava.client;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.strava.client.token.RefreshTokenResponse;
import net.wirelabs.etrex.uploader.strava.client.token.TokenResponse;

@Slf4j
public class StravaConfigUpdater {

    private final StravaConfiguration stravaConfiguration;

    public StravaConfigUpdater(StravaConfiguration stravaConfiguration) {
        this.stravaConfiguration = stravaConfiguration;
    }

    void refreshExpired(RefreshTokenResponse refreshTokenResponse) {
        stravaConfiguration.setStravaAccessToken(refreshTokenResponse.getAccessToken());
        stravaConfiguration.setStravaRefreshToken(refreshTokenResponse.getRefreshToken());
        stravaConfiguration.setStravaTokenExpires(refreshTokenResponse.getExpiresAt());
        stravaConfiguration.save();
        log.info("[Refresh token] - updated strava config");
    }

    void updateToken(TokenResponse tokenResponse) {
        stravaConfiguration.setStravaAccessToken(tokenResponse.getAccessToken());
        stravaConfiguration.setStravaRefreshToken(tokenResponse.getRefreshToken());
        stravaConfiguration.setStravaTokenExpires(tokenResponse.getExpiresAt());
        stravaConfiguration.save();
        log.info("[Update token] - updated strava config");
    }

    void updateCredentials(String appId, String clientSecret) {
        stravaConfiguration.setStravaAppId(appId);
        stravaConfiguration.setStravaClientSecret(clientSecret);
        stravaConfiguration.save();
        log.info("[Update credentials] - updated strava config");
    }
}
