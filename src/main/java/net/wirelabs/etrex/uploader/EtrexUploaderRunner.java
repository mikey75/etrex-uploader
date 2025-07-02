package net.wirelabs.etrex.uploader;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.etrex.uploader.gui.EtrexUploader;


@Slf4j
public class EtrexUploaderRunner {

    @Getter
    private static final SetupManager setupManager = new SetupManager();

    public static void main(String[] args) {

        try {
            setupManager.initialize();
            new EtrexUploader(setupManager.getAppContext());
        } catch (Exception e) {
            log.error("Fatal exception, application terminated {}", e.getMessage(), e);
            SystemUtils.systemExit(1);
        }
    }
}
