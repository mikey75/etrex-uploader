package net.wirelabs.etrex.uploader.common;

/**
 * Created 8/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public enum EventType {
    // map events
    MAP_DISPLAY_GPX_FILE,
    MAP_DISPLAY_FIT_FILE,
    MAP_DISPLAY_TRACK,
    // new files detected
    EVT_NEW_FILES_DETECTED,
    // garmin drive events
    EVT_DRIVE_REGISTERED,
    EVT_DRIVE_UNREGISTERED,
    EVT_HARDWARE_INFO_AVAILABLE,

    EVT_ACTIVITY_UPLOADED;
}
