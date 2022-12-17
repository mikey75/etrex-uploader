package net.wirelabs.etrex.uploader.strava.client;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;

import java.io.Serializable;

@Slf4j
public class TokenManager implements Serializable {

    private static final String STRAVA_TOKEN_URL = "https://www.strava.com/oauth/token";

    private final StravaConfiguration configuration;

    public TokenManager(StravaConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getTokenUrl() {
        return STRAVA_TOKEN_URL;
    }

    public void updateTokenInfo(String accessToken, String refreshToken, Long expiresAt) {
        log.info("Updating strava api tokens");
        configuration.setStravaAccessToken(accessToken);
        configuration.setStravaRefreshToken(refreshToken);
        configuration.setStravaTokenExpires(expiresAt);
        configuration.save();
    }

    public void updateCredentials(String appId, String clientSecret) {
        log.info("Updating strava api credentials");
        configuration.setStravaClientSecret(clientSecret);
        configuration.setStravaAppId(appId);
        configuration.save();
    }

    public Long getTokenExpires() {
        return configuration.getStravaTokenExpires();
    }

    public String getAccessToken() {
        return configuration.getStravaAccessToken();
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

    /**
     * Build refresh token request as per strava docs
     * <p>
     * Returns the HttpRequest object equivalent to:
     * <p>
     * curl -X POST https://www.strava.com/api/v3/oauth/token \
     * -d client_id=ReplaceWithClientID \
     * -d client_secret=ReplaceWithClientSecret \
     * -d grant_type=refresh_token \
     * -d refresh_token=ReplaceWithRefreshToken
     *
     * @return built request
     */
    public Request buildRefreshTokenRequest() {
        RequestBody body = new FormEncodingBuilder()
                .add("client_id", getAppId())
                .add("client_secret", getClientSecret())
                .add("grant_type", "refresh_token")
                .add("refresh_token", getRefreshToken())
                .build();

        return new Request.Builder()
                .url(getTokenUrl())
                .post(body)
                .build();
    }

    /**
     * Build get token request as per strava docs
     * <p>
     * Returns the HttpRequest object equivalent to:
     * <p>
     * curl -X POST https://www.strava.com/oauth/token \
     * -F client_id=YOURCLIENTID \
     * -F client_secret=YOURCLIENTSECRET \
     * -F code=AUTHORIZATIONCODE \
     * -F grant_type=authorization_code
     *
     * @return built request
     */
    public Request buildGetTokenRequest(String appId, String clientSecret, String authCode) {
        RequestBody body = new FormEncodingBuilder()
                .add("client_id", appId)
                .add("client_secret", clientSecret)
                .add("code", authCode)
                .add("grant_type", "authorization_code")
                .build();

        return new Request.Builder()
                .url(getTokenUrl())
                .post(body)
                .build();

    }


    public boolean hasTokens() {
        boolean hasAccessToken = getAccessToken() != null && !getAccessToken().isEmpty();
        boolean hasRefreshToken = getRefreshToken() != null && !getRefreshToken().isEmpty();
        return hasAccessToken && hasRefreshToken;
    }
}
