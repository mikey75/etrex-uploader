package net.wirelabs.etrex.uploader.common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static File createDirIfDoesNotExist(File dir) {
        if (!dir.exists()) {
            try {
                Files.createDirectories(dir.toPath());
            } catch (IOException ex) {
                throw new IllegalStateException(
                        "Critical error while creating directory " + dir.toPath(), ex);
            }
        }
        return dir;
    }

    public static List<File> listDirectory(File directory) {
        File[] files = directory.listFiles();
        return (files != null) ? new ArrayList<>(Arrays.asList(files)) : Collections.emptyList();
    }

    public static String readFileToString(File file) {

        StringBuilder content = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(file.getPath()), StandardCharsets.UTF_8))
        {
            //Read the content with Stream
            stream.forEach(s -> content.append(s).append("\n"));
            return content.toString();
        }
        catch (IOException e)
        {
            log.error("Can't read file: ", e);
            return "";
        }

    }

    public static void copyFile(File f, File destinationFile) {
        try {
            log.info("Copying {}", f.getName());
            Files.copy(f.toPath(), destinationFile.toPath());
        } catch (IOException e) {
            log.error("File cannot be copied {}", e.getMessage(), e);
        }
    }
    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static void clearDir(File source) {
        File[] files = source.listFiles();

        if (files != null && files.length > 0) {
            List<File> fileList = Arrays.asList(files);
            for (File f: fileList) {
                f.delete();
            }
        }

    }

    public static Optional<File> findSubdir(File root, String name) {
        return listDirectory(root).stream()
                .filter(f -> f.isDirectory() && f.getName().equalsIgnoreCase(name))
                .findFirst();

    }

    public static String getYearMonthTimestampedDir() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy"+File.separator +"MM");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static String getFullyTimestampedDir() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy"+File.separator +"MM"+ File.separator+"dd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
