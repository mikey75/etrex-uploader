package net.wirelabs.etrex.uploader.strava.oauth;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.StravaException;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;

import java.io.IOException;
import java.io.Serializable;

/*
 * Created 12/17/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class AuthService implements Serializable {

    private final transient StravaClient client;

    public AuthService(StravaClient stravaClient) {
        this.client = stravaClient;
    }

    public void getToken(String appId, String clientSecret) throws IOException, StravaException {
        AuthCodeRetriever authCodeRetriever = new AuthCodeRetriever();
        String code = authCodeRetriever.getAuthCode(appId);
        client.exchangeAuthCodeForAccessToken(appId, clientSecret, code);
        authCodeRetriever.shutdown();
    }

}
