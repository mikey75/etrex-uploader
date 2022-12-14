package net.wirelabs.etrex.uploader.strava.oauth;

/*
 * Created 12/14/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

import fi.iki.elonen.NanoHTTPD;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class AuthCodeInterceptor extends NanoHTTPD {
    @Getter
    private final AtomicReference<String> authCode = new AtomicReference<>(Constants.EMPTY_STRING);
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
        return newFixedLengthResponse(authorizationStatusMessage);
    }
}
