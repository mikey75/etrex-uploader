package net.wirelabs.etrex.uploader.common.utils;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    
    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
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

    public static File copyFileToDir(File file, File dir) throws IOException {
        File targetFile = new File(dir, file.getName());
        Files.copy(file.toPath(), new File(dir, targetFile.getName())
                .toPath(), REPLACE_EXISTING);
        return targetFile;
    }
}
