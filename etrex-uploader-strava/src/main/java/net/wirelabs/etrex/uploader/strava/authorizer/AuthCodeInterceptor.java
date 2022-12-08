package net.wirelabs.etrex.uploader.strava.authorizer;

import fi.iki.elonen.NanoHTTPD;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;

import java.io.IOException;


/**
 * Micro webserver to intercept authorization redirect
 * and get authorization code from it
 */
@Slf4j
public class AuthCodeInterceptor extends NanoHTTPD {

    @Getter
    private String authCode = Constants.EMPTY_STRING;

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
                return staticResponse(Constants.AUTHORIZATION_OK_MSG);
            }
        }
        return staticResponse(Constants.AUTHORIZATION_FAIL_MSG);
    }

    private Response staticResponse(String authorizationStatusMessage) {
        return NanoHTTPD.newFixedLengthResponse(authorizationStatusMessage);
    }

}
