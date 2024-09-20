package net.wirelabs.etrex.uploader.common.configuration;

import lombok.Getter;
import lombok.Setter;

import net.wirelabs.etrex.uploader.common.utils.ListUtils;
import net.wirelabs.etrex.uploader.strava.model.SportType;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static net.wirelabs.etrex.uploader.common.Constants.*;
import static net.wirelabs.etrex.uploader.common.configuration.ConfigurationPropertyKeys.*;

/**
 * Created 6/20/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

@Getter
@Setter
public class AppConfiguration extends PropertiesBasedConfiguration {

    private transient Path storageRoot;
    private transient List<Path> userStorageRoots;
    private Long deviceDiscoveryDelay;
    private Long waitDriveTimeout;
    private boolean deleteAfterUpload;
    private boolean archiveAfterUpload;

    private SportType defaultActivityType;
    private int tilerThreads;
    private int perPage;
    private int apiUsageWarnPercent;
    private int uploadStatusWaitSeconds;
    private String mapTrackColor;
    private transient Path userMapDefinitonsDir;
    private transient Path mapFile;
    private boolean usePolyLines;
    private int stravaCheckTimeout;
    private boolean stravaCheckHostBeforeUpload;
    private String lookAndFeelClassName;

    public AppConfiguration(String configFile) {
        super(configFile);
        storageRoot = Paths.get(properties.getProperty(STORAGE_ROOT, System.getProperty("user.home") + File.separator + Constants.DEFAULT_LOCAL_STORE));
        userStorageRoots = ListUtils.convertStringListToPaths(properties.getProperty(USER_STORAGE_ROOTS, Constants.EMPTY_STRING));
        deviceDiscoveryDelay = Long.valueOf(properties.getProperty(DRIVE_OBSERVER_DELAY, "500"));
        waitDriveTimeout = Long.valueOf(properties.getProperty(WAIT_DRIVE_TIMEOUT, "15000"));
        deleteAfterUpload = Boolean.parseBoolean(properties.getProperty(DELETE_TRACK_AFTER_UPLOAD, "true"));
        archiveAfterUpload = Boolean.parseBoolean(properties.getProperty(BACKUP_TRACK_AFTER_UPLOAD, "true"));

        defaultActivityType = SportType.valueOf(properties.getProperty(STRAVA_DEFAULT_ACTIVITY_TYPE, SportType.RIDE.name()));
        tilerThreads = Integer.parseInt(properties.getProperty(MAP_TILER_THREAD_COUNT, "8"));
        perPage = Integer.parseInt(properties.getProperty(STRAVA_ACTIVITIES_PER_PAGE, "30"));
        apiUsageWarnPercent = Integer.parseInt(properties.getProperty(STRAVA_API_USAGE_WARN_PERCENT, "85"));
        uploadStatusWaitSeconds = Integer.parseInt(properties.getProperty(UPLOAD_STATUS_WAIT_SECONDS, "60"));
        mapTrackColor = properties.getProperty(MAP_TRACK_COLOR, "#ff0000");
        userMapDefinitonsDir = Paths.get(properties.getProperty(USER_MAP_DEFINITIONS_DIR, System.getProperty("user.home") + File.separator + Constants.DEFAULT_USER_MAP_DIR));
        mapFile = Paths.get(userMapDefinitonsDir + File.separator + properties.getProperty(MAP_FILE, Constants.DEFAULT_MAP));
        usePolyLines = Boolean.parseBoolean(properties.getProperty(USE_POLYLINES, "true"));
        stravaCheckTimeout = Integer.parseInt(properties.getProperty(STRAVA_CHECK_HOST_TIMEOUT, "500"));
        stravaCheckHostBeforeUpload = Boolean.parseBoolean(properties.getProperty(CHECK_HOSTS_BEFORE_UPLOAD,"true"));
        lookAndFeelClassName = String.valueOf(properties.getProperty(LOOK_AND_FEEL_CLASS, UIManager.getCrossPlatformLookAndFeelClassName()));
    }

    public AppConfiguration() {
        this(APPLICATION_CONFIGFILE);
    }

    public void save() {
        properties.setProperty(STORAGE_ROOT, storageRoot.toString());
        properties.setProperty(USER_STORAGE_ROOTS, ListUtils.convertPathListToString(userStorageRoots));
        properties.setProperty(DRIVE_OBSERVER_DELAY, String.valueOf(deviceDiscoveryDelay));
        properties.setProperty(WAIT_DRIVE_TIMEOUT, String.valueOf(waitDriveTimeout));
        properties.setProperty(BACKUP_TRACK_AFTER_UPLOAD, String.valueOf(archiveAfterUpload));
        properties.setProperty(DELETE_TRACK_AFTER_UPLOAD, String.valueOf(deleteAfterUpload));

        properties.setProperty(STRAVA_DEFAULT_ACTIVITY_TYPE, defaultActivityType.name());
        properties.setProperty(MAP_TILER_THREAD_COUNT, String.valueOf(tilerThreads));
        properties.setProperty(STRAVA_ACTIVITIES_PER_PAGE, String.valueOf(perPage));
        properties.setProperty(STRAVA_API_USAGE_WARN_PERCENT, String.valueOf(apiUsageWarnPercent));
        properties.setProperty(UPLOAD_STATUS_WAIT_SECONDS, String.valueOf(uploadStatusWaitSeconds));
        properties.setProperty(MAP_TRACK_COLOR, mapTrackColor);
        properties.setProperty(USER_MAP_DEFINITIONS_DIR, userMapDefinitonsDir.toString());
        properties.setProperty(MAP_FILE, mapFile.getFileName().toString());
        properties.setProperty(USE_POLYLINES, String.valueOf(usePolyLines));
        properties.setProperty(STRAVA_CHECK_HOST_TIMEOUT, String.valueOf(stravaCheckTimeout));
        properties.setProperty(CHECK_HOSTS_BEFORE_UPLOAD, String.valueOf(stravaCheckHostBeforeUpload));
        properties.setProperty(LOOK_AND_FEEL_CLASS, String.valueOf(lookAndFeelClassName));
        super.store();

    }

}
