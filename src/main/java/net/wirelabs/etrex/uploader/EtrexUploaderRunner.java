package net.wirelabs.etrex.uploader;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.etrex.uploader.gui.EtrexUploader;
import net.wirelabs.eventbus.EventBus;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.wirelabs.etrex.uploader.common.utils.SwingUtils.setGlobalFontSize;
import static net.wirelabs.etrex.uploader.common.utils.SystemUtils.checkGraphicsEnvironmentPresent;


@Slf4j
public class EtrexUploaderRunner {

   public static String APP_VERSION;

    public static void main(String[] args) {

        try {
            APP_VERSION = SystemUtils.getAppVersion();
            configureCustomLogbackLogging();
            log.info("Etrex Uploader ver {} starting up....", APP_VERSION);
            checkGraphicsEnvironmentPresent();
            setGlobalFontSize(10);


            ApplicationStartupContext ctx = new ApplicationStartupContext();
            SwingUtils.setSystemLookAndFeel(ctx.getAppConfiguration());
            Frame window = new EtrexUploader(ctx);
            window.setMinimumSize(new Dimension(800, 600));
            window.setVisible(true);
        } catch (Exception e) {
            log.error("Fatal exception, application terminated {}", e.getMessage(), e);
            EventBus.shutdown();
            System.exit(1);
        }

    }

    private static void configureCustomLogbackLogging() {

        Path logbackXmlLocation = Constants.LOGBACK_CONFIG_XML;

        try (InputStream configStream = Files.newInputStream(logbackXmlLocation)) {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.reset();

            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            configurator.doConfigure(configStream); // loads logback file
        } catch (Exception e) {
            String message = String.format("Can't find or load config file (%s)%nDo you want to run the app without logging?", Constants.LOGBACK_CONFIG_XML);
            int result = SwingUtils.yesNoMsg(message);
            if (result != JOptionPane.YES_OPTION) {
                System.exit(1);
            }
        }
    }


}
