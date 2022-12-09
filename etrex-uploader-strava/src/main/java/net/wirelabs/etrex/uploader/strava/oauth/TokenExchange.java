package net.wirelabs.etrex.uploader.strava.oauth;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.strava.client.StravaClientException;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;

@Slf4j
public class TokenExchange {

    private final Configuration configuration;
    private final StravaClient client;

    public TokenExchange(Configuration configuration) {
        this.configuration = configuration;
        this.client = new StravaClient(configuration);

    }

    public AuthResponse exchangeAuthCodeForAccessToken(String authCode) throws StravaClientException {

        if (!authCode.isEmpty()) {
            GetTokenRequest request = new GetTokenRequest(configuration.getStravaAppId(), configuration.getStravaClientSecret(), authCode);
            String response = client.execute(request.buildRequest());
            AuthResponse authResponse = client.jsonParser.fromJson(response, AuthResponse.class);
            log.info("Got token!");
            return authResponse;
        }
        return null;
    }

}
