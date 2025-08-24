package net.wirelabs.etrex.uploader.strava.oauth;

/*
 * Created 12/14/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

import com.sun.net.httpserver.HttpExchange;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.utils.HttpUtils;
import net.wirelabs.etrex.uploader.utils.HttpUtils.ContentTypes;
import net.wirelabs.etrex.uploader.strava.utils.LocalWebServer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static net.wirelabs.etrex.uploader.common.Constants.STRAVA_AUTHORIZATION_FAIL_MSG;
import static net.wirelabs.etrex.uploader.common.Constants.STRAVA_AUTHORIZATION_OK_MSG;

/**
 * A tiny webserver for interception of auth code in Strava OAuth process
 * Listens on random port, when it gets code it's done!
 */
@Slf4j
class AuthCodeInterceptor extends LocalWebServer {

    private final AtomicReference<String> authCode = new AtomicReference<>(Constants.EMPTY_STRING);
    private final AtomicReference<String> scope = new AtomicReference<>(Constants.EMPTY_STRING);
    private final AtomicBoolean authCodeReady = new AtomicBoolean(false);

    public AuthCodeInterceptor() throws IOException {
        log.info("Started auth code interceptor http server on port {}", getListeningPort());
    }

    /**
     * this method is called when Strava OAuth application authorization page redirects user
     * after allowing access to user strava account. it extracts auth code and scopes
     * of the user authorization
     */
    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        Map<String,String> params = HttpUtils.parseQueryParams(exchange.getRequestURI().getQuery());

        if (containsAuthCodeAndScopes(params)) {
            String incomingCode = params.get("code");
            String incomingScope = params.get("scope");
            if (isNotNullOrEmpty(incomingCode) && isNotNullOrEmpty(incomingScope)) {
                authCode.set(incomingCode);
                scope.set(incomingScope);
                authCodeReady.set(true);
            }
        }
        sendResponse(exchange);
    }

    private static boolean isNotNullOrEmpty(String incomingCode) {
        return incomingCode != null && !incomingCode.isEmpty();
    }

    private static boolean containsAuthCodeAndScopes(Map<String,String> params) {
        return params.containsKey("code") && params.containsKey("scope");
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

    private void sendResponse(HttpExchange exchange) throws IOException {
        String response = (isAuthCodeReady() && scopeOK()) ? STRAVA_AUTHORIZATION_OK_MSG : STRAVA_AUTHORIZATION_FAIL_MSG;

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", ContentTypes.HTML);
        exchange.getResponseHeaders().add("Cache-Control", "no-cache, no-store, must-revalidate");
        exchange.getResponseHeaders().add("Pragma", "no-cache");
        exchange.getResponseHeaders().add("Expires", "0");
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
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
