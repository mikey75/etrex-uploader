package net.wirelabs.etrex.uploader.common;

import static net.wirelabs.etrex.uploader.common.Constants.TRACKS_REPO;
import static net.wirelabs.etrex.uploader.common.Constants.UPLOADED_FILES_SUBFOLDER;
import static net.wirelabs.etrex.uploader.common.utils.FileUtils.getExtensionPart;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;

/**
 * Created 10/28/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class FileService {

    private final DateTimeFormatter duplicateFilePrefixFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss");
    private final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy"+File.separator +"MM");

    private final AppConfiguration appConfiguration;

    public FileService(AppConfiguration appConfiguration) throws IOException {
        this.appConfiguration = appConfiguration;
        setupWorkDirectories();
    }

    private void setupWorkDirectories() throws IOException {
        log.info("Initializing directories ");
        File defaultStorageRoot = appConfiguration.getStorageRoot().toFile();
        FileUtils.createDirIfDoesNotExist(defaultStorageRoot);

        File defaultUploadedDir = new File(defaultStorageRoot, UPLOADED_FILES_SUBFOLDER);
        FileUtils.createDirIfDoesNotExist(defaultUploadedDir);

        File defaultTracksRepoDir = new File(defaultStorageRoot, TRACKS_REPO);
        FileUtils.createDirIfDoesNotExist(defaultTracksRepoDir);
    }

    public void archiveAndDelete(File trackFile) throws IOException {

        if (appConfiguration.isArchiveAfterUpload()) {
            archive(trackFile);
        }
        if (appConfiguration.isDeleteAfterUpload() && !trackFile.getName().equals("Current.gpx")) {
            delete(trackFile);
        }
    }

    private void delete(File trackFile) throws IOException {
        log.info("Deleting {}", trackFile);
        Files.delete(trackFile.toPath());
    }

    private void archive(File trackFile) throws IOException {
        log.info("Archiving {}", trackFile);
        File uploadedSubdir = new File(appConfiguration.getStorageRoot().toFile(), UPLOADED_FILES_SUBFOLDER);
        File targetDir = new File(uploadedSubdir, getYearMonthTimestampedDir());
        File targetFile = new File(targetDir, trackFile.getName());

        if (targetFile.exists()) {
            log.warn("Target file exists. Changing name by adding current timestamp to filename");
            targetFile = new File(targetDir, createDuplicateFileName(trackFile));
        }

        FileUtils.createDirIfDoesNotExist(targetDir);
        Files.copy(trackFile.toPath(), targetFile.toPath());
    }

    private String createDuplicateFileName(File file) throws IOException {
        String filename = file.getName();
        StringBuilder sb = new StringBuilder();

        String filePart = FileUtils.getFilePart(filename);
        String ext = FileUtils.getExtensionPart(filename);

        LocalDateTime now = getNow();
        sb.append(filePart).append("-")
                .append(duplicateFilePrefixFormatter.format(now))
                .append(".")
                .append(ext);

        return sb.toString();
    }

    private String getYearMonthTimestampedDir() {

        LocalDateTime now = getNow();
        return yearMonthFormatter.format(now);
    }

    LocalDateTime getNow() {
        return LocalDateTime.now();
    }
}
