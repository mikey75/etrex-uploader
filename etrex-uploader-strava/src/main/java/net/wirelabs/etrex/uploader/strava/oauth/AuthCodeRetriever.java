package net.wirelabs.etrex.uploader.strava.oauth;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.time.Duration;

import static net.wirelabs.etrex.uploader.strava.utils.StravaUtils.buildAuthRequestUrl;


@Slf4j
public class AuthCodeRetriever {

    private AuthCodeInterceptor authCodeInterceptor;
    private Configuration configuration;
    

    public AuthCodeRetriever(Configuration configuration) {
        this.configuration = configuration;
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

            String redirectUri = "http://localhost:" + authCodeInterceptor.getListeningPort();
            String url = buildAuthRequestUrl(redirectUri,configuration.getStravaAppId());
            runDesktopBrowserToAuthorizationUrl(url);

            if (!waitForCode()) {
                log.error("Timeout waiting for auth code");
            }
            return authCodeInterceptor.getAuthCode();

        } catch (IOException e) {
            log.error("Can't run OAuth authorization process: {}", e.getMessage(), e);
            return authCodeInterceptor.getAuthCode();
        }
    }

    private boolean waitForCode() {
        long timeOut = System.currentTimeMillis() + Duration.ofSeconds(configuration.getStravaAuthorizerTimeout()).toMillis();
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

    void runDesktopBrowserToAuthorizationUrl(String requestAccessURL) throws IOException {
        log.info("Redirecting user to strava app authorization page");

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(URI.create(requestAccessURL));
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

    boolean isAlive() {
        return authCodeInterceptor.isAlive();
    }

    int getRandomFreeTcpPort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }
}
