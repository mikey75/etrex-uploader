package net.wirelabs.etrex.uploader.configuration;

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
    static final String LOOK_AND_FEEL_CLASS             = "system.look.and.feel.classname";
    static final String ENABLE_DESKTOP_SLIDERS          = "system.look.sliders";
    static final String FONT_SIZE                       = "system.font.size";
    // strava
    static final String STRAVA_APP_ID                   = "strava.app.id";
    static final String STRAVA_CLIENT_SECRET            = "strava.client.secret";
    static final String STRAVA_ACCESS_TOKEN             = "strava.token.access";
    static final String STRAVA_ACCESS_TOKEN_EXPIRES_AT  = "strava.token.expires.at";
    static final String STRAVA_REFRESH_TOKEN            = "strava.token.refresh";
    static final String STRAVA_ACTIVITIES_PER_PAGE      = "strava.activities.per.page";
    static final String STRAVA_DEFAULT_ACTIVITY_TYPE    = "strava.default.activity.type";
    static final String STRAVA_API_USAGE_WARN_PERCENT   = "strava.api.usage.warn.percent";
    static final String UPLOAD_STATUS_WAIT_SECONDS      = "strava.upload.status.wait.seconds";
    static final String USE_POLYLINES                   = "strava.use.polylines"; // use polylines instead of real track data
    static final String CHECK_HOSTS_BEFORE_UPLOAD       = "strava.check.host.before.upload"; // whether to check strava status before update
    static final String STRAVA_CHECK_HOST_TIMEOUT       = "strava.check.host.timeout";       // time to wait for strava host to be http available
    // map
    static final String MAP_FILE                        = "map.file";
    static final String MAP_TILER_THREAD_COUNT          = "map.tiler.threads";
    static final String MAP_TRACK_COLOR                 = "map.track.color";
    static final String USER_MAP_DEFINITIONS_DIR        = "map.definitions.dir";
    static final String MAP_HOME_LATITUDE               = "map.home.latitude";
    static final String MAP_HOME_LONGITUDE              = "map.home.longitude";
    static final String ROUTE_LINE_WIDTH                = "map.route.line.width";
    static final String TILE_CACHE_TYPE                 = "map.tile.cache.type";

}
