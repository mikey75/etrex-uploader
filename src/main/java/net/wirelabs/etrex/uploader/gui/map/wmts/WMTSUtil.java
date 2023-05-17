package net.wirelabs.etrex.uploader.gui.map.wmts;

import com.squareup.okhttp.HttpUrl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created 5/14/23 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WMTSUtil {

    public static Capabilities getCapabilities(String url) {
        String urlPath = url.replace("http://", "").replace("https://", "");
        Path base = Paths.get(System.getProperty("user.home"), ".jmap-cache", "WMTS");
        Path subdir = Paths.get(base.toString(), urlPath);
        Path finalPath = Paths.get(base.toString(), urlPath, "capabilities.xml");

        try {
            if (!Files.exists(finalPath)) {
                String getCapabilitiesUrl = HttpUrl.parse(url).newBuilder()
                        .addQueryParameter("service", "wmts")
                        .addQueryParameter("request", "GetCapabilities")
                        .build().toString();

                URL myURL = new URL(getCapabilitiesUrl);

                try (InputStream is = myURL.openStream()) {
                    Files.createDirectories(subdir);
                    Files.write(finalPath, is.readAllBytes());
                }

            }
            return parse(finalPath.toFile());
        } catch (IOException e) {
            throw new IllegalStateException("Could not get WMTS capabilities descriptor");
        }
    }

    private static Capabilities parse(File f) {
        try {
            JAXBContext context = JAXBContext.newInstance(Capabilities.class);
            Unmarshaller jaxb = context.createUnmarshaller();
            return (Capabilities) jaxb.unmarshal(f);
        } catch (JAXBException ex) {
            throw new IllegalArgumentException("Failed to parse Capabilities XML", ex);
        }
    }
}