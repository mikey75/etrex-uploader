package net.wirelabs.etrex.uploader;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.gui.EtrexUploader;
import net.wirelabs.etrex.uploader.utils.SystemUtils;


@Slf4j
public class EtrexUploaderRunner {

    public static void main(String[] args) {

        try {
            new EtrexUploader();
        } catch (Exception e) {
            log.error("Fatal exception, application terminated {}", e.getMessage(), e);
            SystemUtils.systemExit(1);
        }
    }
}
