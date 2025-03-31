package net.wirelabs.etrex.uploader.common.configuration;

import com.strava.model.SportType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.utils.ListUtils;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static net.wirelabs.etrex.uploader.common.configuration.ConfigurationPropertyKeys.*;

/**
 * Created 6/20/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

@Getter
@Setter
@Slf4j
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
    private Double mapHomeLattitude;
    private Double mapHomeLongitude;
    private boolean enableDesktopSliders;
    private int routeLineWidth;

    public AppConfiguration(String configFile) {
        super(configFile);
        storageRoot = Paths.get(properties.getProperty(STORAGE_ROOT, Constants.DEFAULT_LOCAL_STORE));
        userStorageRoots = ListUtils.convertStringListToPaths(properties.getProperty(USER_STORAGE_ROOTS, Constants.EMPTY_STRING));
        deviceDiscoveryDelay = Long.valueOf(properties.getProperty(DRIVE_OBSERVER_DELAY, String.valueOf(Constants.DEFAULT_DRIVE_OBSERVER_DELAY)));
        waitDriveTimeout = Long.valueOf(properties.getProperty(WAIT_DRIVE_TIMEOUT, String.valueOf(Constants.DEFAULT_WAIT_DRIVE_TIMEOUT)));
        deleteAfterUpload = Boolean.parseBoolean(properties.getProperty(DELETE_TRACK_AFTER_UPLOAD, Constants.TRUE));
        archiveAfterUpload = Boolean.parseBoolean(properties.getProperty(BACKUP_TRACK_AFTER_UPLOAD, Constants.TRUE));

        defaultActivityType = SportType.valueOf(properties.getProperty(STRAVA_DEFAULT_ACTIVITY_TYPE, Constants.DEFAULT_SPORT.name()));
        tilerThreads = Integer.parseInt(properties.getProperty(MAP_TILER_THREAD_COUNT, String.valueOf(Constants.DEFAULT_TILER_THREAD_COUNT)));
        perPage = Integer.parseInt(properties.getProperty(STRAVA_ACTIVITIES_PER_PAGE, String.valueOf(Constants.DEFAULT_STRAVA_ACTIVITIES_PER_PAGE)));
        apiUsageWarnPercent = Integer.parseInt(properties.getProperty(STRAVA_API_USAGE_WARN_PERCENT, String.valueOf(Constants.DEFAULT_API_USAGE_WARN_PERCENT)));
        uploadStatusWaitSeconds = Integer.parseInt(properties.getProperty(UPLOAD_STATUS_WAIT_SECONDS, String.valueOf(Constants.DEFAULT_UPLOAD_STATUS_WAIT_SECONDS)));
        mapTrackColor = properties.getProperty(MAP_TRACK_COLOR, Constants.DEFAULT_TRACK_COLOR);
        userMapDefinitonsDir = Paths.get(properties.getProperty(USER_MAP_DEFINITIONS_DIR, Constants.DEFAULT_USER_MAP_DIR));
        mapFile = Paths.get(userMapDefinitonsDir + File.separator + properties.getProperty(MAP_FILE, Constants.DEFAULT_MAP));
        usePolyLines = Boolean.parseBoolean(properties.getProperty(USE_POLYLINES, Constants.TRUE));
        stravaCheckTimeout = Integer.parseInt(properties.getProperty(STRAVA_CHECK_HOST_TIMEOUT, String.valueOf(Constants.DEFAULT_STRAVA_CHECK_TIMEOUT)));
        stravaCheckHostBeforeUpload = Boolean.parseBoolean(properties.getProperty(CHECK_HOSTS_BEFORE_UPLOAD,Constants.TRUE));
        lookAndFeelClassName = String.valueOf(properties.getProperty(LOOK_AND_FEEL_CLASS, UIManager.getCrossPlatformLookAndFeelClassName()));
        mapHomeLattitude = Double.valueOf(properties.getProperty(MAP_HOME_LATTITUDE, String.valueOf(Constants.DEFAULT_MAP_HOME_LOCATION.getLatitude())));
        mapHomeLongitude = Double.valueOf(properties.getProperty(MAP_HOME_LONGITUDE, String.valueOf(Constants.DEFAULT_MAP_HOME_LOCATION.getLongitude())));
        enableDesktopSliders = Boolean.parseBoolean(properties.getProperty(ENABLE_DESKTOP_SLIDERS, String.valueOf(Constants.DEFAULT_USE_SLIDERS)));
        routeLineWidth = Integer.parseInt(properties.getProperty(ROUTE_LINE_WIDTH, String.valueOf(Constants.DEFAULT_ROUTE_LINE_WIDTH)));

        if (!configFileExists()) {
            log.info("Saving new config file with default values");
            save();
        }
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
        properties.setProperty(MAP_HOME_LONGITUDE, String.valueOf(mapHomeLongitude));
        properties.setProperty(MAP_HOME_LATTITUDE, String.valueOf(mapHomeLattitude));
        properties.setProperty(ENABLE_DESKTOP_SLIDERS, String.valueOf(enableDesktopSliders));
        properties.setProperty(ROUTE_LINE_WIDTH, String.valueOf(routeLineWidth));
        try {
            super.store();
        } catch (IllegalStateException e) {
            log.error("Can't save config file: {}", e.getMessage(), e);
        }

    }

}
