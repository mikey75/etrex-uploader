package net.wirelabs.etrex.uploader.strava.api;

/**
 * Created 11/1/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaApiException extends Exception {
    public StravaApiException(String apiCallStatus) {
        super(apiCallStatus);
    }
}
