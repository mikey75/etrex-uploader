package net.wirelabs.etrex.uploader.strava.api.token;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.strava.api.StravaApiCaller;
import net.wirelabs.etrex.uploader.strava.authorizer.AuthResponse;
import net.wirelabs.etrex.uploader.strava.api.StravaApiException;
import java.time.Duration;

/**
 * Created 11/4/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class TokenManager extends StravaApiCaller {
    @Getter
    private final Configuration configuration;

    public TokenManager(Configuration configuration) {
        this.configuration = configuration;
    }

    public AuthResponse exchangeAuthCodeForToken(String authCode) throws StravaApiException {

        if (!authCode.isEmpty()) {
            log.info("Got auth code, exchanging for token");
            ExchangeAuthForTokenRequest request = new ExchangeAuthForTokenRequest(configuration.getStravaAppId(),configuration.getStravaClientSecret(),authCode);
            String response = execute(request.buildRequest());
            AuthResponse authResponse = jsonParser.fromJson(response,AuthResponse.class);
            log.info("Got token!");
            updateTokenInfo(authResponse.getAccessToken(), authResponse.getRefreshToken(),authResponse.getExpiresAt());
            return authResponse;
        }
        return null;
    }

    public void getNewAccessTokenIfExpired() throws StravaApiException {
        // if a new token is issued, block other threads wanting to get it until it is saved
        // enforcing a new token is available for subsequent calls
        synchronized(this) {
            long currentTime = System.currentTimeMillis() / 1000;
            Long tokenExpiresAt = configuration.getStravaTokenExpires();
            // refresh token 10 minutes before deadline
            Long resultTimeout = currentTime - Duration.ofMinutes(10).toMillis() / 1000;

            if (tokenExpiresAt < resultTimeout) {
                log.info("Token expired, getting new token using refresh token");
                RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(configuration.getStravaAppId(), configuration.getStravaClientSecret(), configuration.getStravaRefreshToken());
                String response = execute(refreshTokenRequest.buildRequest());
                RefreshTokenResponse refreshTokenResponse = jsonParser.fromJson(response, RefreshTokenResponse.class);
                updateTokenInfo(refreshTokenResponse.getAccessToken(), refreshTokenResponse.getRefreshToken(), refreshTokenResponse.getExpiresAt());

            }
        }
    }

    private void updateTokenInfo(String accessToken, String refreshToken, Long expiresAt) {
        log.info("Updating tokens in configuration");
        configuration.setStravaAccessToken(accessToken);
        configuration.setStravaRefreshToken(refreshToken);
        configuration.setStravaTokenExpires(expiresAt);
        configuration.save();
    }


}
