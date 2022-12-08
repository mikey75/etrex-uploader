package net.wirelabs.etrex.uploader.common.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ConfigurationPropertyKeys {
    // system
    static final String WAIT_DRIVE_TIMEOUT              = "system.wait.drive.timeout";
    static final String DRIVE_OBSERVER_DELAY            = "system.drive.observer.delay";
    static final String STORAGE_ROOT                    = "system.storage.root";
    static final String DELETE_TRACK_AFTER_UPLOAD       = "system.delete.after.upload";
    static final String BACKUP_TRACK_AFTER_UPLOAD       = "system.backup.after.upload";
    static final String USER_STORAGE_ROOTS              = "system.user.storage.roots";
    // strava
    static final String STRAVA_APP_ID                   = "strava.app.id";
    static final String STRAVA_AUTH_CALLBACK_PORT       = "strava.auth.callback.port";

    static final String STRAVA_CLIENT_SECRET            = "strava.client.secret";
    static final String STRAVA_ACCESS_TOKEN             = "strava.token.access";
    static final String STRAVA_ACCESS_TOKEN_EXPIRES_AT  = "strava.token.expires.at";
    static final String STRAVA_REFRESH_TOKEN            = "strava.token.refresh";
    static final String STRAVA_BASE_URL                 = "strava.api.base.url";
    // config files constants
    static final String APPLICATION_CONFIGFILE          = "config.properties";
}
