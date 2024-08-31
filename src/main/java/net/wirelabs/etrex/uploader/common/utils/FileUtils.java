package net.wirelabs.etrex.uploader.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static void createDirIfDoesNotExist(File dir) throws IOException {
        if (!dir.exists()) {
            Files.createDirectories(dir.toPath());
        }
    }

    public static List<File> listDirectory(File directory) {
        File[] files = directory.listFiles();
        return (files != null) ? new ArrayList<>(Arrays.asList(files)) : Collections.emptyList();
    }

    public static boolean recursivelyDeleteDirectory(File directoryToBeDeleted) throws IOException {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                recursivelyDeleteDirectory(file);
            }
        }
        return Files.deleteIfExists(directoryToBeDeleted.toPath());
    }

    public static File copyFileToDir(File file, File dir) throws IOException {
        File targetFile = new File(dir, file.getName());
        Files.copy(file.toPath(), new File(dir, targetFile.getName())
                .toPath(), REPLACE_EXISTING);
        return targetFile;
    }

    public static String getFilePart(String filename) throws IOException {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(0, filename.lastIndexOf(".")))
                .orElseThrow(() -> new IOException("No file name"));
    }

    public static String getExtensionPart(String filename) throws IOException {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElseThrow(() -> new IOException("File has no extension"));
    }

    public static boolean isTrackFile(File file) {
        return isFitFile(file) || isGpxFile(file) || isTcxFile(file);
    }

    public static boolean isFitFile(File file) {
        return file.getName().toLowerCase().endsWith(".fit");
    }

    public static boolean isGpxFile(File file) {
        return file.getName().toLowerCase().endsWith(".gpx");
    }

    public static boolean isTcxFile(File file) {
        return file.getName().toLowerCase().endsWith(".tcx");
    }


}
