package net.wirelabs.etrex.uploader;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.utils.LoggingConfigurator;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.etrex.uploader.gui.EtrexUploader;


import java.awt.*;

import static net.wirelabs.etrex.uploader.common.utils.SwingUtils.setGlobalFontSize;
import static net.wirelabs.etrex.uploader.common.utils.SystemUtils.checkGraphicsEnvironmentPresent;


@Slf4j
public class EtrexUploaderRunner {


    public static void main(String[] args) {

        try {
            LoggingConfigurator.configureLogger();
            log.info("Etrex Uploader ver {} starting up....", SystemUtils.getAppVersion());
            checkGraphicsEnvironmentPresent();
            setGlobalFontSize(10);


            ApplicationStartupContext ctx = new ApplicationStartupContext();
            SwingUtils.setSystemLookAndFeel(ctx.getAppConfiguration());
            Frame window = new EtrexUploader(ctx);
            window.setMinimumSize(new Dimension(800, 600));
            window.setVisible(true);
        } catch (Exception e) {
            log.error("Fatal exception, application terminated {}", e.getMessage(), e);
            SystemUtils.shutdownAndExit();
        }

    }



}
