package net.wirelabs.etrex.uploader.configuration;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.utils.SystemUtils;
import net.wirelabs.eventbus.EventBus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static net.wirelabs.etrex.uploader.common.EventType.ERROR_SAVING_CONFIGURATION;


/**
 * Created 9/13/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public abstract class PropertiesBasedConfiguration implements Serializable {

    protected Properties properties = new SortedProperties();
    private final String configFileName;


    protected PropertiesBasedConfiguration(String configFileName) {
        this.configFileName = configFileName;
        loadConfigFile(configFileName);
    }

    protected boolean configFileExists() {
        return new File(SystemUtils.getWorkDir(), configFileName).exists();
    }

    private void loadConfigFile(String configFileName) {

        log.info("Loading {}", configFileName);
        
        try (InputStream is = Files.newInputStream(Paths.get(SystemUtils.getWorkDir(),configFileName))) {
            properties.load(is);
        } catch (IOException e) {
            log.info("{} file not found or cannot be loaded. Setting default config values.", configFileName);
        }

    }

    protected void storePropertiesToFile() {
        log.info("Saving configuration {}" , configFileName);
        try (OutputStream os = Files.newOutputStream(Paths.get(SystemUtils.getWorkDir(),configFileName))) {
            properties.store(os, "");
        } catch (IOException e) {
            log.error("Can't save configuration: {}",e.getMessage(), e);
            EventBus.publish(ERROR_SAVING_CONFIGURATION, e);
        }
    }
}
