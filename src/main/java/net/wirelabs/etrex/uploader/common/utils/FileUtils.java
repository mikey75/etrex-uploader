package net.wirelabs.etrex.uploader.common.utils;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    private static final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy"+File.separator +"MM");
    private static final DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy"+File.separator +"MM"+ File.separator+"dd");
    private static final DateTimeFormatter duplicateFilePrefixFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss-SSS");
    public static void createDirIfDoesNotExist(File dir) throws IOException {
        if (!dir.exists()) {
                Files.createDirectories(dir.toPath());
        }
    }

    public static List<File> listDirectory(File directory) {
        File[] files = directory.listFiles();
        return (files != null) ? new ArrayList<>(Arrays.asList(files)) : Collections.emptyList();
    }
    
    public static boolean deleteDirectory(File directoryToBeDeleted) throws IOException {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return Files.deleteIfExists(directoryToBeDeleted.toPath());
    }
    
    public static String getYearMonthTimestampedDir() {

        LocalDateTime now = LocalDateTime.now();
        return yearMonthFormatter.format(now);
    }

    public static String getFullyTimestampedDir() {

        LocalDateTime now = LocalDateTime.now();
        return yearMonthDayFormatter.format(now);
    }

    public static String createDuplicateFileName(File file) {
        String filename = file.getName();
        StringBuilder sb = new StringBuilder();

        LocalDateTime now = LocalDateTime.now();
        sb.append("duplicate-of-")
                .append(filename.replace(".","-"))
                .append("-")
                .append(duplicateFilePrefixFormatter.format(now));

        return sb.toString();
    }

    public static File copyFileToDir(File file, File dir) throws IOException {
        File targetFile = new File(dir, file.getName());
        Files.copy(file.toPath(), new File(dir, targetFile.getName())
                .toPath(), REPLACE_EXISTING);
        return targetFile;
    }

    public static String  getFilePart(String filename) throws IOException {
        return Optional.ofNullable(filename).filter(f -> f.contains(".")).map(f -> f.substring(0, filename.lastIndexOf(".") ))
                .orElseThrow(()->new IOException("No file name"));
    }

    public static String getExtensionPart(String filename) throws IOException {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElseThrow(()-> new IOException("File has no extension"));
    }
}
