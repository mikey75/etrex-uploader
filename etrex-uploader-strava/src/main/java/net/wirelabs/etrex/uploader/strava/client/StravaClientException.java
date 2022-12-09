package net.wirelabs.etrex.uploader.strava.client;

/**
 * Created 11/1/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaClientException extends Exception {
    public StravaClientException(String apiCallStatus) {
        super(apiCallStatus);
    }
}
