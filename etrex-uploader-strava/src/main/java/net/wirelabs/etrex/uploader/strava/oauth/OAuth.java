package net.wirelabs.etrex.uploader.strava.oauth;

import fi.iki.elonen.NanoHTTPD;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.strava.client.StravaException;
import net.wirelabs.etrex.uploader.strava.utils.UrlBuilder;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


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
                        authCode.set(incomingCode);
                        authCodeReady.set(true);
                        return staticResponse(Constants.AUTHORIZATION_OK_MSG);
                    }
                }
                authCodeReady.set(false);
                return staticResponse(Constants.AUTHORIZATION_FAIL_MSG);
            }

            private Response staticResponse(String authorizationStatusMessage) {
                Response response = NanoHTTPD.newFixedLengthResponse(authorizationStatusMessage);
                response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.addHeader("Pragma", "no-cache");
                response.addHeader("Expires", "0");
                return response;
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

    /**
     * Build OAuth request URL that will go to Strava app authorization page
     * i.e "Connect with Strava" functionality
     * <p>
     * <a href="https://developers.strava.com/docs/authentication/#requestingaccess">More info</a>
     *
     * @param redirectURL   your OAuth redirect URL
     * @param applicationId registered Application ID
     * @return request that will be issued to Strava
     */
    private String buildAuthRequestUrl(String redirectURL, String applicationId) {

        return UrlBuilder.newBuilder()
                .baseUrl(Constants.STRAVA_AUTHORIZATION_URL)
                .addQueryParam("client_id", applicationId)
                .addQueryParam("redirect_uri", redirectURL)
                .addQueryParam("response_type", "code")
                .addQueryParam("approval_prompt", "force")
                .addQueryParam("scope", Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE)
                .build();
    }

}
