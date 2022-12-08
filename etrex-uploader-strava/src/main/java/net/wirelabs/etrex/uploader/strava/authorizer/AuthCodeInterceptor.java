package net.wirelabs.etrex.uploader.strava.authorizer;

import fi.iki.elonen.NanoHTTPD;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Micro webserver to intercept authorization redirect
 * and get authorization code from it
 */
@Slf4j
@Getter
public class AuthCodeInterceptor extends NanoHTTPD {


    private String authCode = Constants.EMPTY_STRING;
    private final AtomicBoolean authCodeReady = new AtomicBoolean(false);

    public AuthCodeInterceptor(int port) {
        super(port);
        startSever();
    }

    private void startSever() {
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            log.info("Started authcode interceptor http server on port {}", getListeningPort());
        } catch (IOException e) {
            log.error("Could not start interceptor http server");
        }
    }

    @Override
    // this method is called when Strava OAuth application authorization page redirects after allowing access
    // the GET url contains authCode which we'll exchange for access token
    public Response serve(IHTTPSession session) {
        if (session.getParameters().containsKey("code")) {
            String incomingCode = session.getParameters().get("code").get(0);
            if (session.getMethod() == Method.GET && incomingCode != null && !incomingCode.isEmpty()) {
                authCode = incomingCode;
                authCodeReady.set(true);
                EventBus.publish(EventType.AUTH_CODE_RECEIVED, authCode);
                return staticResponse(Constants.AUTHORIZATION_OK_MSG);
            }
        }
        authCodeReady.set(false);
        return staticResponse(Constants.AUTHORIZATION_FAIL_MSG);
    }

    private Response staticResponse(String authorizationStatusMessage) {
        return NanoHTTPD.newFixedLengthResponse(authorizationStatusMessage);
    }

}
