package net.wirelabs.etrex.uploader.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;

import java.io.File;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class GarminUtils {
    /**
     * Find garmin device info xml file
     * Checks two levels deep from root dir
     *
     * @param root - root dir to start from
     * @return Optional.of(file) or empty
     */
    public static Optional<File> getGarminDeviceXmlFile(File root) {
        File[] level1 = root.listFiles();
        if (level1 == null) return Optional.empty();

        for (File file : level1) {
            if (file.isFile() && file.getName().equals(Constants.GARMIN_DEVICE_XML)) {
                return Optional.of(file);
            }
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children == null) continue;
                for (File child : children) {
                    if (child.isFile() && child.getName().equals(Constants.GARMIN_DEVICE_XML)) {
                        return Optional.of(child);
                    }
                }
            }
        }
        return Optional.empty();
    }
}
