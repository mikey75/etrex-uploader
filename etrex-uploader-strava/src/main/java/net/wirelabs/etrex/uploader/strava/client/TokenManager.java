package net.wirelabs.etrex.uploader.strava.client;

import java.io.Serializable;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
@Slf4j
public class TokenManager implements Serializable {

    private final Configuration configuration;

    public TokenManager(Configuration configuration) {
        this.configuration = configuration;
    }

    public boolean hasAccessToken() {
        return !configuration.getStravaAccessToken().isBlank();
    }

    public boolean hasRefreshToken() {
        return !configuration.getStravaRefreshToken().isBlank();
    }

    public Long getTokenExpires() {
        return configuration.getStravaTokenExpires();
    }
    public void updateTokenInfo(String accessToken, String refreshToken, Long expiresAt) {
        log.info("Updating tokens in configuration");
        configuration.setStravaAccessToken(accessToken);
        configuration.setStravaRefreshToken(refreshToken);
        configuration.setStravaTokenExpires(expiresAt);
        configuration.save();
    }

    public String getAccessToken() {
        return configuration.getStravaAccessToken();
    }
    
    public void updateCredentials(String appId, String clientSecret) {
        configuration.setStravaClientSecret(clientSecret);
        configuration.setStravaAppId(appId);
        configuration.save();
    }

    public String getAppId() {
        return configuration.getStravaAppId();
    }

    public String getClientSecret() {
        return configuration.getStravaClientSecret();
    }

    public String getRefreshToken() {
        return configuration.getStravaRefreshToken();
    }
}
