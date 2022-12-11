package net.wirelabs.etrex.uploader.strava.oauth;

import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.strava.client.StravaException;

/**
 * This class implements OAuth flow for strava
 * - AuthCodeRetriever initiates OAuth flow, runs browser to Authorization page and waits for auth code.
 * - TokenExchange exchanges auth code for access tokens
 * - Stores tokens in persistent configuration
 */
public class OAuthAuthorizer {

    private final AuthCodeRetriever authCodeRetriever;
    private final Configuration configuration;
    private final StravaClient client;

    public OAuthAuthorizer(Configuration configuration) {

        authCodeRetriever = new AuthCodeRetriever(configuration);
        this.client = new StravaClient(configuration);
        this.configuration = configuration;
    }
    
    public void getAndStoreTokens() throws StravaException {
            String code = authCodeRetriever.getAuthCode();
            AuthResponse tokens = client.exchangeAuthCodeForAccessToken(code);
            storeTokens(tokens);
    }

    private void storeTokens(AuthResponse authTokens) {
        configuration.setStravaAccessToken(authTokens.getAccessToken());
        configuration.setStravaRefreshToken(authTokens.getRefreshToken());
        configuration.setStravaTokenExpires(authTokens.getExpiresAt());
        configuration.save();
    }
}
