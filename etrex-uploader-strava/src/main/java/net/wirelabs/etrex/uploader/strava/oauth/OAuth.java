package net.wirelabs.etrex.uploader.strava.oauth;

import static net.wirelabs.etrex.uploader.strava.utils.StravaUtils.buildAuthRequestUrl;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import fi.iki.elonen.NanoHTTPD;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.strava.client.StravaException;



@Slf4j
public class OAuth implements Serializable {

    public static final int DEFAULT_AUTH_CODE_TIMEOUT_SECONDS = 60;

    private final AtomicReference<String> authCode = new AtomicReference<>(Constants.EMPTY_STRING);
    @Getter
    private final AtomicBoolean authCodeReady = new AtomicBoolean(false);
    private transient NanoHTTPD server;

    @Getter
    private int port;

    public OAuth start() {
        startAuthCodeInterceptor();
        return this;
    }
    
    public void shutdown() {
        log.info("Shutting down auth interceptor");
        server.closeAllConnections();
        server.stop();
    }

    private void startAuthCodeInterceptor() {
        try {
            server = getServer();
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            log.info("Started authcode interceptor http server on port {}", server.getListeningPort());
        } catch (IOException e) {
            log.error("Could not start interceptor http server");
        }
    }

    private NanoHTTPD getServer() throws IOException {
        port = getRandomFreeTcpPort();
        return new NanoHTTPD(port) {

            @Override
            // this method is called when Strava OAuth application authorization page redirects after allowing access
            // the GET url contains authCode which we'll exchange for access token later
            public Response serve(IHTTPSession session) {
                return parseRequestForAuthCode(session);
            }

            private Response parseRequestForAuthCode(IHTTPSession session) {
                if (session.getParameters().containsKey("code")) {
                    String incomingCode = session.getParameters().get("code").get(0);
                    if (session.getMethod() == Method.GET && incomingCode != null && !incomingCode.isEmpty()) {
                        OAuth.this.authCode.set(incomingCode);
                        authCodeReady.set(true);
                        return staticResponse(Constants.AUTHORIZATION_OK_MSG);
                    }
                }
                authCodeReady.set(false);
                return staticResponse(Constants.AUTHORIZATION_FAIL_MSG);
            }

            private Response staticResponse(String authorizationStatusMessage) {
                return NanoHTTPD.newFixedLengthResponse(authorizationStatusMessage);
            }
        };
    }

    /**
     * Get authorization code from strava OAuth process
     * <p>
     * Redirects user to 'Connect with strava' page where user 
     * authorizes the app to access their account.
     * When user authorizes the app to access their account - the auth code is issued.
     *
     * @param stravaAppId strava application id
     * @return authCode or throws exception
     * @throws StravaException - no code or empty
     * @throws IOException - could not run browser
     */
    public String getAuthCode(String stravaAppId) throws StravaException, IOException {
       
        log.info("Starting Strava OAuth process");

        String redirectUri = "http://localhost:" + server.getListeningPort();
        String url = buildAuthRequestUrl(redirectUri, stravaAppId);
        runDesktopBrowserToAuthorizationUrl(url);

        if (!waitForCode()) {
            log.error("Timeout waiting for auth code");
            throw new StravaException("Timed out waiting for code");
        }
        return authCode.get();
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

    private boolean waitForCode() {
        long timeOut = System.currentTimeMillis() + Duration.ofSeconds(getAuthCodeTimeoutSeconds()).toMillis();
        while (System.currentTimeMillis() < timeOut) {
            if (authCodeReady.get()) {
                log.info("Got auth code");
                return true;
            }
            Sleeper.sleepMillis(200);
        }
        return false;
    }

    int getRandomFreeTcpPort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }

    int getAuthCodeTimeoutSeconds() {
        return DEFAULT_AUTH_CODE_TIMEOUT_SECONDS;
    }
}
