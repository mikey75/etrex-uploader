package net.wirelabs.etrex.uploader.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TrackFileUtils {

    public static boolean isTrackFile(File file) {
        return isFitFile(file) || isGpxFile(file) || isTcxFile(file);
    }

    public static boolean isFitFile(File file) {
        return doesFileNameEndWith(file, ".fit");
    }

    private static boolean isGpxFile(File file) {
        return doesFileNameEndWith(file, ".gpx");
    }

    public static boolean isTcxFile(File file) {
        return doesFileNameEndWith(file, ".tcx");
    }

    public static boolean isGpx10File(File file) {
        return isGpxFileContaining(file, "xmlns=\"http://www.topografix.com/GPX/1/0\"");
    }

    public static boolean isGpx11File(File file) {
        return isGpxFileContaining(file, "xmlns=\"http://www.topografix.com/GPX/1/1\"");
    }

    private static boolean isGpxFileContaining(File fileToCheck, String stringToBeContained) {
        try {
            String content = FileUtils.readFileToString(fileToCheck, StandardCharsets.UTF_8);
            return isGpxFile(fileToCheck) && content.contains(stringToBeContained);
        } catch (IOException e) {
            log.error("Could not read file {}", fileToCheck, e);
            return false;
        }
    }

    private static boolean doesFileNameEndWith(File file, String end) {
        return file.getName().toLowerCase().endsWith(end);
    }
}
