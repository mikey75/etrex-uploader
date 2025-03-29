package net.wirelabs.etrex.uploader.common.configuration;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

@Slf4j
class SortedProperties extends Properties {

    @Override
    public void store(OutputStream outputStream, String comments) throws IOException {

        this.keySet().stream().map(k -> (String) k).sorted().forEach(k -> {
            try {
                outputStream.write(String.format("%s=%s%n", k, get(k)).getBytes());
            } catch (IOException e) {
                log.error("Writing properties file failed: {}", e.getMessage(), e);
            }
        });
    }
}
