package net.wirelabs.etrex.uploader.common.configuration;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static net.wirelabs.etrex.uploader.common.Constants.CURRENT_WORK_DIR;

/**
 * Created 9/13/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public abstract class PropertiesBasedConfiguration implements Serializable {

    protected Properties properties = new Properties();
    private final String configFileName;


    PropertiesBasedConfiguration(String configFileName) {
        this.configFileName = configFileName;
        loadConfigFile(configFileName);
    }

    private void loadConfigFile(String configFileName) {

        log.info("Loading {}", configFileName);
        
        try (InputStream is = Files.newInputStream(Paths.get(CURRENT_WORK_DIR,configFileName))) {
            properties.load(is);
        } catch (IOException e) {
            log.info("{} file not found or cannot be loaded. Setting default config values.", configFileName);
        }

    }

    void store() {
        log.info("Saving configuration {}" , configFileName);
        try (OutputStream os = Files.newOutputStream(Paths.get(CURRENT_WORK_DIR,configFileName))) {
            properties.store(os, "");
        } catch (IOException e) {
            log.error("Can't save configuration");
        }
    }
}
