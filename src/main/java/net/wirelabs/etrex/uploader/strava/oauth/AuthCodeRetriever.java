package net.wirelabs.etrex.uploader.strava.oauth;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.utils.Sleeper;
import net.wirelabs.etrex.uploader.utils.SystemUtils;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.utils.UrlBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;


@Slf4j
public class AuthCodeRetriever implements Serializable {

    private final transient AuthCodeInterceptor interceptorServer;
    @Getter
    private final StravaConfiguration stravaConfiguration;

    @Getter
    private final int port;

    public AuthCodeRetriever(StravaConfiguration stravaConfiguration) throws IOException {
        this.stravaConfiguration = stravaConfiguration;
        interceptorServer = new AuthCodeInterceptor();
        port = interceptorServer.getListeningPort();
        interceptorServer.start();
    }

    public void shutdown() {
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
        openSystemBrowser(requestAccessURL);
    }

    void openSystemBrowser(String requestAccessURL) throws IOException {
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


    int getAuthCodeTimeoutSeconds() {
        return Constants.AUTH_CODE_TIMEOUT_SECONDS;
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
        return UrlBuilder.create().doNotEncodeParams().parse(stravaConfiguration.getAuthUrl())
                .addQueryParam("client_id", applicationId)
                .addQueryParam("redirect_uri", redirectURL)
                .addQueryParam("response_type", "code")
                .addQueryParam("approval_prompt", "force")
                .addQueryParam("scope", Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE)
                .build();
    }


}
