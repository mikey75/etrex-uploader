package net.wirelabs.etrex.uploader;

/*
 * Created 11/1/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaException extends Exception {
    public StravaException(String apiCallStatus) {
        super(apiCallStatus);
    }

}
