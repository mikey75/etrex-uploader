package net.wirelabs.etrex.uploader.strava.oauth;

/*
 * Created 12/14/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

import fi.iki.elonen.NanoHTTPD;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A tiny webserver for interception of auth code in Strava OAuth process
 * Listens on random port, when it gets code it's done!
 */
@Slf4j
class AuthCodeInterceptor extends NanoHTTPD {

    private final AtomicReference<String> authCode = new AtomicReference<>(Constants.EMPTY_STRING);
    private final AtomicReference<String> scope = new AtomicReference<>(Constants.EMPTY_STRING);
    private final AtomicBoolean authCodeReady = new AtomicBoolean(false);

    public AuthCodeInterceptor(int port) throws IOException {
        super("127.0.0.1", port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        log.info("Started authcode interceptor http server on port {}", getListeningPort());
    }

    /**
     * this method is called when Strava OAuth application authorization page redirects user
     * after allowing access to user strava account. it extracts auth code and scopes
     * of the user authorization
     */
    @Override
    public Response serve(IHTTPSession session) {
        if (containsAuthCodeAndScopes(session)) {
            String incomingCode = session.getParameters().get("code").get(0);
            String incomingScope = session.getParameters().get("scope").get(0);
            if (isNotNullOrEmpty(incomingCode) && isNotNullOrEmpty(incomingScope)) {
                authCode.set(incomingCode);
                scope.set(incomingScope);
                authCodeReady.set(true);
            }
        }
        return statusResponse();
    }

    private static boolean isNotNullOrEmpty(String incomingCode) {
        return incomingCode != null && !incomingCode.isEmpty();
    }

    private static boolean containsAuthCodeAndScopes(IHTTPSession session) {
        return session.getParameters().containsKey("code") && session.getParameters().containsKey("scope");
    }

    boolean scopeOK() {
        String[] scopes = Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE.split(",");
        for (String s : scopes) {
            if (!getScope().contains(s)) {
                return false;
            }
        }
        return true;
    }

    private Response statusResponse() {
        Response response = newFixedLengthResponse((isAuthCodeReady() && scopeOK()) ? Constants.AUTHORIZATION_OK_MSG : Constants.AUTHORIZATION_FAIL_MSG);
        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "0");
        return response;
    }

    public String getAuthCode() {
        return authCode.get();
    }

    public String getScope() {
        return scope.get();
    }

    public boolean isAuthCodeReady() {
        return authCodeReady.get();
    }
}
