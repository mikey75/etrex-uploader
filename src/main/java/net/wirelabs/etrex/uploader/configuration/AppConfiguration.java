package net.wirelabs.etrex.uploader.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.utils.ListUtils;
import net.wirelabs.etrex.uploader.utils.SwingUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static net.wirelabs.etrex.uploader.configuration.ConfigurationPropertyKeys.*;

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
    private int tilerThreads;
    private String mapTrackColor;
    private transient Path userMapDefinitionsDir;
    private transient Path mapFile;
    private String lookAndFeelClassName;
    private int fontSize;
    private Double mapHomeLatitude;
    private Double mapHomeLongitude;
    private boolean enableDesktopSliders;
    private int routeLineWidth;
    private String cacheType;
    private String redisHost;
    private int redisPort;
    private int redisPoolSize;

    public AppConfiguration(String configFile) {
        super(configFile);
        storageRoot = Paths.get(properties.getProperty(STORAGE_ROOT, Constants.DEFAULT_LOCAL_STORE));
        userStorageRoots = ListUtils.convertStringListToPaths(properties.getProperty(USER_STORAGE_ROOTS, Constants.EMPTY_STRING));
        deviceDiscoveryDelay = Long.valueOf(properties.getProperty(DRIVE_OBSERVER_DELAY, String.valueOf(Constants.DEFAULT_DRIVE_OBSERVER_DELAY)));
        waitDriveTimeout = Long.valueOf(properties.getProperty(WAIT_DRIVE_TIMEOUT, String.valueOf(Constants.DEFAULT_WAIT_DRIVE_TIMEOUT)));
        deleteAfterUpload = Boolean.parseBoolean(properties.getProperty(DELETE_TRACK_AFTER_UPLOAD, Constants.TRUE));
        archiveAfterUpload = Boolean.parseBoolean(properties.getProperty(BACKUP_TRACK_AFTER_UPLOAD, Constants.TRUE));
        tilerThreads = Integer.parseInt(properties.getProperty(MAP_TILER_THREAD_COUNT, String.valueOf(Constants.DEFAULT_TILER_THREAD_COUNT)));
        mapTrackColor = properties.getProperty(MAP_TRACK_COLOR, Constants.DEFAULT_TRACK_COLOR);
        userMapDefinitionsDir = Paths.get(properties.getProperty(USER_MAP_DEFINITIONS_DIR, Constants.DEFAULT_USER_MAP_DIR));
        mapFile = Paths.get(userMapDefinitionsDir + File.separator + properties.getProperty(MAP_FILE, Constants.DEFAULT_MAP));
        lookAndFeelClassName = properties.getProperty(LOOK_AND_FEEL_CLASS, SwingUtils.getCrossPlatformLookAndFeelClassName());
        mapHomeLatitude = Double.valueOf(properties.getProperty(MAP_HOME_LATITUDE, String.valueOf(Constants.DEFAULT_MAP_HOME_LOCATION.getLatitude())));
        mapHomeLongitude = Double.valueOf(properties.getProperty(MAP_HOME_LONGITUDE, String.valueOf(Constants.DEFAULT_MAP_HOME_LOCATION.getLongitude())));
        enableDesktopSliders = Boolean.parseBoolean(properties.getProperty(ENABLE_DESKTOP_SLIDERS, String.valueOf(Constants.DEFAULT_USE_SLIDERS)));
        routeLineWidth = Integer.parseInt(properties.getProperty(ROUTE_LINE_WIDTH, String.valueOf(Constants.DEFAULT_ROUTE_LINE_WIDTH)));
        fontSize = Integer.parseInt(properties.getProperty(FONT_SIZE, String.valueOf(Constants.DEFAULT_FONT_SIZE)));
        cacheType = properties.getProperty(TILE_CACHE_TYPE, Constants.DEFAULT_TILE_CACHE_TYPE);
        redisHost = properties.getProperty(REDIS_HOST, Constants.DEFAULT_REDIS_HOST);
        redisPort = Integer.parseInt(properties.getProperty(REDIS_PORT, String.valueOf(Constants.DEFAULT_REDIS_PORT)));
        redisPoolSize = Integer.parseInt(properties.getProperty(REDIS_POOL_SIZE, String.valueOf(Constants.DEFAULT_REDIS_POOLSIZE)));

        if (!configFileExists()) {
            log.info("Saving new config file with default values");
            save();
        }
    }

    public void save() {
        properties.setProperty(STORAGE_ROOT, storageRoot.toString());
        properties.setProperty(USER_STORAGE_ROOTS, ListUtils.convertPathListToString(userStorageRoots));
        properties.setProperty(DRIVE_OBSERVER_DELAY, String.valueOf(deviceDiscoveryDelay));
        properties.setProperty(WAIT_DRIVE_TIMEOUT, String.valueOf(waitDriveTimeout));
        properties.setProperty(BACKUP_TRACK_AFTER_UPLOAD, String.valueOf(archiveAfterUpload));
        properties.setProperty(DELETE_TRACK_AFTER_UPLOAD, String.valueOf(deleteAfterUpload));
        properties.setProperty(MAP_TILER_THREAD_COUNT, String.valueOf(tilerThreads));
        properties.setProperty(MAP_TRACK_COLOR, mapTrackColor);
        properties.setProperty(USER_MAP_DEFINITIONS_DIR, userMapDefinitionsDir.toString());
        properties.setProperty(MAP_FILE, mapFile.getFileName().toString());
        properties.setProperty(LOOK_AND_FEEL_CLASS, lookAndFeelClassName);
        properties.setProperty(MAP_HOME_LONGITUDE, String.valueOf(mapHomeLongitude));
        properties.setProperty(MAP_HOME_LATITUDE, String.valueOf(mapHomeLatitude));
        properties.setProperty(ENABLE_DESKTOP_SLIDERS, String.valueOf(enableDesktopSliders));
        properties.setProperty(ROUTE_LINE_WIDTH, String.valueOf(routeLineWidth));
        properties.setProperty(FONT_SIZE, String.valueOf(fontSize));
        properties.setProperty(TILE_CACHE_TYPE, cacheType);
        properties.setProperty(REDIS_HOST, redisHost);
        properties.setProperty(REDIS_PORT, String.valueOf(redisPort));
        properties.setProperty(REDIS_POOL_SIZE, String.valueOf(redisPoolSize));
        storePropertiesToFile();

    }

}
