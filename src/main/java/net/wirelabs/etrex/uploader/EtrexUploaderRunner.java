package net.wirelabs.etrex.uploader;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.utils.LoggingConfigurator;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.etrex.uploader.gui.EtrexUploader;

import java.awt.*;

import static net.wirelabs.etrex.uploader.common.utils.SwingUtils.setGlobalFontSize;
import static net.wirelabs.etrex.uploader.common.utils.SwingUtils.setSystemLookAndFeel;
import static net.wirelabs.etrex.uploader.common.utils.SystemUtils.checkGraphicsEnvironmentPresent;
import static net.wirelabs.etrex.uploader.common.utils.SystemUtils.checkOsSupport;


@Slf4j
public class EtrexUploaderRunner {

    @Getter
    private static ApplicationStartupContext appContext;

    public static void main(String[] args) {

        try {
            LoggingConfigurator.configureLogger();
            log.info("Etrex Uploader ver {} starting up....", SystemUtils.getAppVersion());
            checkGraphicsEnvironmentPresent();
            checkOsSupport();


            appContext = new ApplicationStartupContext();
            setSystemLookAndFeel(appContext.getAppConfiguration().getLookAndFeelClassName());
            setGlobalFontSize(10);
            Frame window = new EtrexUploader(appContext);
            window.setMinimumSize(new Dimension(800, 600));
            window.setVisible(true);
        } catch (Exception e) {
            log.error("Fatal exception, application terminated {}", e.getMessage(), e);
            SystemUtils.systemExit(1);
        }

    }



}
