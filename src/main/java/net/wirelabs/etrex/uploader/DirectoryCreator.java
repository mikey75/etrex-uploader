package net.wirelabs.etrex.uploader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DirectoryCreator {

    private DirectoryCreator() { }

   
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
}
