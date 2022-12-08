package net.wirelabs.etrex.uploader.strava.authorizer;

import com.squareup.okhttp.HttpUrl;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.strava.api.StravaApiCaller;
import net.wirelabs.etrex.uploader.strava.tokenmanager.TokenManager;
import net.wirelabs.etrex.uploader.strava.api.StravaApiException;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;


@Getter
@Setter
@Slf4j
public class StravaAuthorizer extends StravaApiCaller {

    private final Configuration configuration;
    private final TokenManager tokenManager;
    private AuthCodeInterceptor authCodeInterceptor;

    public StravaAuthorizer(Configuration configuration) {
        this.configuration = configuration;
        this.tokenManager = new TokenManager(configuration);
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
    public void authorizeAccess() {

        try {
                log.info("Starting Strava OAuth process");
                // odpal interceptor requesta
                int port = getRandomFreeTcpPort();
                authCodeInterceptor = new AuthCodeInterceptor(port);
                // odpal auth url w przegladarce
                String requestAccessURL = buildAuthorizationUrl("http://localhost:"+ port);
                runDesktopBrowserToAutorizationUrl(requestAccessURL);
                // czekaj na authCode i zamien go na token
                long timeOut = System.currentTimeMillis() + Duration.ofSeconds(60).toMillis();

                log.info("Waiting for auth code");
                // try to get token for 60 seconds
                while (System.currentTimeMillis() < timeOut) {
                    AuthResponse authResponse = tokenManager.exchangeAuthCodeForToken(authCodeInterceptor.getAuthCode());
                    if (authResponse != null) {
                        return;
                    }
                    Sleeper.sleepMillis(200);
                }
                log.error("Could not get token in 60 seconds");

        } catch (IOException | URISyntaxException | StravaApiException e) {
            log.error("Can't run OAuth authorization process: {}", e.getMessage(), e);
        }
    }

    private void runDesktopBrowserToAutorizationUrl(String requestAccessURL) throws IOException, URISyntaxException {
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

    private String buildAuthorizationUrl(String redirectUri) {

        HttpUrl url = HttpUrl.parse(Constants.STRAVA_AUTHORIZATION_URL);

        return url.newBuilder()
                .addQueryParameter("client_id", configuration.getStravaAppId())
                .addQueryParameter("redirect_uri", redirectUri)
                .addQueryParameter("response_type", "code")
                .addQueryParameter("approval_prompt", "force")
                .addQueryParameter("scope", Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE)
                .build().toString();

    }

    private int getRandomFreeTcpPort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }
}
