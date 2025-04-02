package net.wirelabs.etrex.uploader.common;

import net.wirelabs.eventbus.IEventType;

/**
 * Created 8/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public enum EventType implements IEventType {
    // map events
    MAP_DISPLAY_TRACK,
    MAP_RESET,
   
    // garmin drive events
    DEVICE_DRIVE_REGISTERED,
    DEVICE_DRIVE_UNREGISTERED,
    DEVICE_INFO_AVAILABLE,

    // misc
    ACTIVITY_SUCCESSFULLY_UPLOADED,
    TRACK_COLOR_CHANGED,
    RATELIMIT_INFO_UPDATE,
    USER_STORAGE_ROOTS_CHANGED,
    ROUTE_LINE_WIDTH_CHANGED,
    MAP_HOME_CHANGED,
    ERROR_SAVING_CONFIGURATION
}
