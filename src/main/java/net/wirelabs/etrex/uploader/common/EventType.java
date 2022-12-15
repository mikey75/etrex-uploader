package net.wirelabs.etrex.uploader.common;

/**
 * Created 8/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public enum EventType {
    // map events
    MAP_DISPLAY_GPX_FILE,
    MAP_DISPLAY_FIT_FILE,
    MAP_DISPLAY_TRACK,
   
    // garmin drive events
    DEVICE_DRIVE_REGISTERED,
    DEVICE_DRIVE_UNREGISTERED,
    DEVICE_INFO_AVAILABLE,
    // 
    ACTIVITY_SUCCESSFULLY_UPLOADED,
    AUTH_CODE_RECEIVED, RATELIMIT_INFO_UPDATE;
}
