package net.wirelabs.etrex.uploader.common.configuration;

import lombok.Getter;
import lombok.Setter;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.utils.ListUtils;
import net.wirelabs.etrex.uploader.gui.map.MapType;
import net.wirelabs.etrex.uploader.strava.model.SportType;

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
public class AppConfiguration extends PropertiesBasedConfiguration {

    private transient Path storageRoot;
    private transient List<Path> userStorageRoots;
    private Long deviceDiscoveryDelay;
    private Long waitDriveTimeout;
    private boolean deleteAfterUpload;
    private boolean archiveAfterUpload;

    private int perPage;
    private SportType defaultActivityType;
    private MapType defaultMapType;
    private int apiUsageWarnPercent;
    private String thunderforestApiKey; // api key needed for thunderforest maps (OUTDOOR, CYCLE)

    public AppConfiguration(String configFile) {
        super(configFile);
        storageRoot = Paths.get(properties.getProperty(STORAGE_ROOT, System.getProperty("user.home") + File.separator + Constants.DEFAULT_LOCAL_STORE));
        userStorageRoots = ListUtils.convertStringListToPaths(properties.getProperty(USER_STORAGE_ROOTS, Constants.EMPTY_STRING));
        deviceDiscoveryDelay = Long.valueOf(properties.getProperty(DRIVE_OBSERVER_DELAY, "500"));
        waitDriveTimeout = Long.valueOf(properties.getProperty(WAIT_DRIVE_TIMEOUT, "15000"));
        deleteAfterUpload = Boolean.parseBoolean(properties.getProperty(DELETE_TRACK_AFTER_UPLOAD, "true"));
        archiveAfterUpload = Boolean.parseBoolean(properties.getProperty(BACKUP_TRACK_AFTER_UPLOAD, "true"));

        defaultActivityType = SportType.valueOf(properties.getProperty(STRAVA_DEFAULT_ACTIVITY_TYPE, SportType.RIDE.name()));
        defaultMapType = MapType.valueOf(properties.getProperty(MAP_TYPE, MapType.OPENSTREETMAP.name()));
        perPage = Integer.parseInt(properties.getProperty(STRAVA_ACTIVITIES_PER_PAGE, "30"));
        apiUsageWarnPercent = Integer.parseInt(properties.getProperty(STRAVA_API_USAGE_WARN_PERCENT, "85"));
        thunderforestApiKey = properties.getProperty(MAP_THUNDERFOREST_API_KEY, Constants.EMPTY_STRING);
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
        properties.setProperty(MAP_TYPE, defaultMapType.name());
        properties.setProperty(STRAVA_ACTIVITIES_PER_PAGE, String.valueOf(perPage));
        properties.setProperty(STRAVA_API_USAGE_WARN_PERCENT, String.valueOf(apiUsageWarnPercent));
        properties.setProperty(MAP_THUNDERFOREST_API_KEY, thunderforestApiKey);
        super.store();

    }


}
