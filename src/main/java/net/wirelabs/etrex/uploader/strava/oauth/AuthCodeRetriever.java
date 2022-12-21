package net.wirelabs.etrex.uploader.strava.oauth;

import com.squareup.okhttp.HttpUrl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.StravaException;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.time.Duration;


@Slf4j
class AuthCodeRetriever implements Serializable {

    private final transient AuthCodeInterceptor interceptorServer;

    @Getter
    private final int port;

    public AuthCodeRetriever() throws IOException {
        port = getRandomFreeTcpPort();
        interceptorServer = new AuthCodeInterceptor(port);
    }

    public void shutdown() {
        interceptorServer.closeAllConnections();
        interceptorServer.stop();
        log.info("auth code interceptor shut down");
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
     * @throws IOException     - could not run browser
     */
    public String getAuthCode(String stravaAppId) throws StravaException, IOException {

        log.info("Starting Strava OAuth process");

        String redirectUri = "http://127.0.0.1:" + interceptorServer.getListeningPort();
        String url = buildAuthRequestUrl(redirectUri, stravaAppId);
        runDesktopBrowserToAuthorizationUrl(url);
        checkForTimeoutAndCorrectScopes();
        return interceptorServer.getAuthCode();
    }

    private void checkForTimeoutAndCorrectScopes() throws StravaException {
        if (!waitForCode()) {
            log.error("Timeout waiting for auth code");
            throw new StravaException("Timed out waiting for code");
        }
        if (!interceptorServer.scopeOK()) {
            log.error("You must approve all requested authorization scopes");
            throw new StravaException("You must approve all requested authorization scopes");
        }
    }

    void runDesktopBrowserToAuthorizationUrl(String requestAccessURL) throws IOException {
        log.info("Redirecting user to strava app authorization page");
        SystemUtils.openSystemBrowser(requestAccessURL);
    }

    private boolean waitForCode() {
        long timeOut = System.currentTimeMillis() + Duration.ofSeconds(getAuthCodeTimeoutSeconds()).toMillis();
        while (System.currentTimeMillis() < timeOut) {
            if (interceptorServer.isAuthCodeReady()) {
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
        return Constants.DEFAULT_AUTH_CODE_TIMEOUT_SECONDS;
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
        HttpUrl baseUrl = HttpUrl.parse(Constants.STRAVA_AUTHORIZATION_URL);
        return baseUrl.newBuilder()
                .addQueryParameter("client_id", applicationId)
                .addQueryParameter("redirect_uri", redirectURL)
                .addQueryParameter("response_type", "code")
                .addQueryParameter("approval_prompt", "force")
                .addQueryParameter("scope", Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE)
                .build()
                .toString();
    }

}
