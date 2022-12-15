package net.wirelabs.etrex.uploader.common.configuration;

import static net.wirelabs.etrex.uploader.common.configuration.ConfigurationPropertyKeys.*;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import net.wirelabs.etrex.uploader.common.Constants;

/**
 * Created 6/20/22 by Michał Szwaczko (mikey@wirelabs.net)
 */


@Getter
@Setter
public class AppConfiguration extends PropertiesBasedConfiguration {

    private String storageRoot;
    private String userStorageRoots;
    private Long deviceDiscoveryDelay;
    private Long waitDriveTimeout;
    private boolean deleteAfterUpload;
    private boolean archiveAfterUpload;
    

    public AppConfiguration(String configFile) {
        super(configFile);
        storageRoot = properties.getProperty(STORAGE_ROOT, System.getProperty("user.home") + File.separator + Constants.DEFAULT_LOCAL_STORE);
        userStorageRoots = properties.getProperty(USER_STORAGE_ROOTS, Constants.EMPTY_STRING);
        deviceDiscoveryDelay = Long.valueOf(properties.getProperty(DRIVE_OBSERVER_DELAY, "500"));
        waitDriveTimeout = Long.valueOf(properties.getProperty(WAIT_DRIVE_TIMEOUT, "15000"));
        deleteAfterUpload = Boolean.parseBoolean(properties.getProperty(DELETE_TRACK_AFTER_UPLOAD, "true"));
        archiveAfterUpload = Boolean.parseBoolean(properties.getProperty(BACKUP_TRACK_AFTER_UPLOAD, "true"));
    }

    public AppConfiguration() {
        this(APPLICATION_CONFIGFILE);
    }

    public void save() {
        properties.setProperty(STORAGE_ROOT, storageRoot);
        properties.setProperty(USER_STORAGE_ROOTS, userStorageRoots);
        properties.setProperty(DRIVE_OBSERVER_DELAY, String.valueOf(deviceDiscoveryDelay));
        properties.setProperty(WAIT_DRIVE_TIMEOUT, String.valueOf(waitDriveTimeout));
        properties.setProperty(BACKUP_TRACK_AFTER_UPLOAD, String.valueOf(archiveAfterUpload));
        properties.setProperty(DELETE_TRACK_AFTER_UPLOAD, String.valueOf(deleteAfterUpload));
        super.store();

    }


}
