package net.wirelabs.etrex.uploader.common.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

/**
 * Created 9/13/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public abstract class PropertiesBasedConfiguration implements Serializable {

    protected Properties properties = new Properties();
    private final String userDirectory;
    private final String configFileName;


    PropertiesBasedConfiguration(String configFileName) {
        this.configFileName = configFileName;
        this.userDirectory = System.getProperty("user.dir");
        loadConfigFile(configFileName);
    }

    private void loadConfigFile(String configFileName) {

        log.info("Loading {}", configFileName);
        
        try (InputStream is = Files.newInputStream(Paths.get(userDirectory,configFileName))) {
            properties.load(is);
        } catch (IOException e) {
            log.info("{} file not found or cannot be loaded. Setting default config values.", configFileName);
        }

    }

    void store() {
        log.info("Saving configuration {}" , configFileName);
        try (OutputStream os = Files.newOutputStream(Paths.get(userDirectory,configFileName))) {
            properties.store(os, "");
        } catch (IOException e) {
            log.error("Can't save configuration");
        }
    }
}
