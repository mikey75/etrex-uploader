package net.wirelabs.etrex.uploader.strava.oauth;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import com.squareup.okhttp.HttpUrl;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;


@Slf4j
public class AuthCodeRetriever {
    
    private AuthCodeInterceptor authCodeInterceptor;
    private final String applicationId;
    private final Long authCodeTimeout;
    
    public AuthCodeRetriever(Configuration configuration) {
        this.applicationId = configuration.getStravaAppId();
        this.authCodeTimeout = configuration.getStravaAuthorizerTimeout();
    }

    /**
     * Runs standard strava OAuth process
     * i.e opens webpage with authorization request in system www browser where
     * user allows actions to strava account, then redirects to
     * configured redirect url where AuthCallbackListenerServer intercepts authorization code
     * and exchanges it for token which is then stored in configuration.
     * <p>
     * After running the process, the method waits here until token is received.
     */

    public String getAuthCode() {

        try {
            log.info("Starting Strava OAuth process");
            runAuthCodeInterceptor();
            runDesktopBrowserToAuthorizationUrl(getAuthorizationUrl());
            
            if (!waitForCode()) {
                log.error("Timeout waiting for auth code");
            }
            return authCodeInterceptor.getAuthCode();

        } catch (IOException | URISyntaxException e) {
            log.error("Can't run OAuth authorization process: {}", e.getMessage(), e);
            return authCodeInterceptor.getAuthCode();
        }
    }

    private boolean waitForCode() {
        long timeOut = System.currentTimeMillis() + Duration.ofSeconds(authCodeTimeout).toMillis();
        while (System.currentTimeMillis() < timeOut) {
            if (authCodeInterceptor.getAuthCodeReady().get()) {
                log.info("Got auth code");
                return true;
            }
            Sleeper.sleepMillis(200);
        }
        return false;
    }
 
    private void runAuthCodeInterceptor() throws IOException {
        int port = getRandomFreeTcpPort();
        authCodeInterceptor = new AuthCodeInterceptor(port);
    }

    void runDesktopBrowserToAuthorizationUrl(String requestAccessURL) throws IOException, URISyntaxException {
        log.info("Redirecting user to strava app authorization page");
        
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(requestAccessURL));
        } else {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("xdg-open " + requestAccessURL);
        }
    }

    public void shutdown() {
        if (authCodeInterceptor != null) {
            log.info("Closing auth code interceptor");
            authCodeInterceptor.closeAllConnections();
            authCodeInterceptor.stop();
        }
    }

    private String getAuthorizationUrl() {

        HttpUrl url = HttpUrl.parse(Constants.STRAVA_AUTHORIZATION_URL);
        String redirectUri = "http://localhost:" + authCodeInterceptor.getListeningPort();
        
        return url.newBuilder()
                .addQueryParameter("client_id", applicationId)
                .addQueryParameter("redirect_uri", redirectUri)
                .addQueryParameter("response_type", "code")
                .addQueryParameter("approval_prompt", "force")
                .addQueryParameter("scope", Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE)
                .build().toString();

    }
    boolean isAlive() {
        return authCodeInterceptor.isAlive();
    }
    int getRandomFreeTcpPort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }
}
