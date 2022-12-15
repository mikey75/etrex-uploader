package net.wirelabs.etrex.uploader.strava.oauth;

/*
 * Created 12/14/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

import fi.iki.elonen.NanoHTTPD;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class AuthCodeInterceptor extends NanoHTTPD {
    @Getter
    private String authCode = Constants.EMPTY_STRING;
    @Getter
    private String scope = Constants.EMPTY_STRING;
    @Getter
    private final AtomicBoolean authCodeReady = new AtomicBoolean(false);

    public AuthCodeInterceptor(int port) throws IOException {
        super("127.0.0.1",port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        log.info("Started authcode interceptor http server on port {}", getListeningPort());
    }

    @Override
    // this method is called when Strava OAuth application authorization page redirects after allowing access
    // the GET url contains authCode which we'll exchange for access token later
    public Response serve(IHTTPSession session) {
        if (session.getParameters().containsKey("code") && session.getParameters().containsKey("scope")) {
            String incomingCode = session.getParameters().get("code").get(0);
            String incomingScope = session.getParameters().get("scope").get(0);
            if (session.getMethod() == Method.GET && incomingCode != null && !incomingCode.isEmpty()) {
                authCode = incomingCode;
                scope = incomingScope;
                authCodeReady.set(true);
                if (!scopeOK()) {
                    return staticResponse(Constants.AUTHORIZATION_FAIL_MSG);
                }

                return staticResponse(Constants.AUTHORIZATION_OK_MSG);
            }
        }
        return staticResponse(Constants.AUTHORIZATION_FAIL_MSG);
    }

    boolean scopeOK(){
        String[] scopes = Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE.split(",");
        for (String s: scopes) {
            if (!scope.contains(s)) {
                return false;
            }
        }
        return true;
    }

    private Response staticResponse(String authorizationStatusMessage) {
        // make desktop browser not cache the response so it always calls our auth endpoint
        Response response = NanoHTTPD.newFixedLengthResponse(authorizationStatusMessage);
        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "0");
        return response;
    }
}
