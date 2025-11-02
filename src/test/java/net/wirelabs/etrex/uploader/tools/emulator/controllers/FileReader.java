package net.wirelabs.etrex.uploader.tools.emulator.controllers;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class FileReader {

    public String readFileContents(String pathname) {
        try {
            File f = new File(pathname);
            if (f.exists()) {
                return FileUtils.readFileToString(f, StandardCharsets.UTF_8);
            } else {
                return "";
            }
        } catch (IOException e) {
            return "";
        }
    }
}
