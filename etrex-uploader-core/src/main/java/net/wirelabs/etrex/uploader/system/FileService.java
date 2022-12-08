package net.wirelabs.etrex.uploader.system;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


import static net.wirelabs.etrex.uploader.common.Constants.TRACKS_REPO;
import static net.wirelabs.etrex.uploader.common.Constants.UPLOADED_FILES_SUBFOLDER;

/**
 * Created 10/28/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class FileService {

    private final Configuration configuration;

    public FileService(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setupWorkDirectories() throws IOException {
        log.info("Initializing directories ");
        File defaultStorageRoot = new File(configuration.getStorageRoot());
        FileUtils.createDirIfDoesNotExist(defaultStorageRoot);

        File defaultUploadedDir = new File(defaultStorageRoot, UPLOADED_FILES_SUBFOLDER);
        FileUtils.createDirIfDoesNotExist(defaultUploadedDir);

        File defaultTracksRepoDir = new File(defaultStorageRoot, TRACKS_REPO);
        FileUtils.createDirIfDoesNotExist(defaultTracksRepoDir);
    }

    public void archiveAndDelete(File trackFile) throws IOException {


        if (configuration.isArchiveAfterUpload()) {
            log.info("Archiving {}", trackFile);
            File uploadedSubdir = new File(configuration.getStorageRoot(), UPLOADED_FILES_SUBFOLDER);
            File targetDir = new File(uploadedSubdir, FileUtils.getYearMonthTimestampedDir());
            File targetFile = new File(targetDir, trackFile.getName());

            if (targetFile.exists()) {
                log.warn("Target file exists. Changing name by adding current timestamp to filename");
                targetFile = new  File(targetDir, System.currentTimeMillis() +"-"+trackFile.getName());
            }

            FileUtils.createDirIfDoesNotExist(targetDir);
            Files.copy(trackFile.toPath(),targetFile.toPath());
        }
        if (configuration.isDeleteAfterUpload()) {
            log.info("Deleting {}", trackFile);
            Files.delete(trackFile.toPath());
        }
    }
}
