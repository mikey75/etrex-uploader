package net.wirelabs.etrex.uploader;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import lombok.extern.slf4j.Slf4j;

import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.EtrexUploader;
import net.wirelabs.eventbus.EventBus;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.wirelabs.etrex.uploader.common.utils.SwingUtils.setGlobalFontSize;


@Slf4j
public class EtrexUploaderRunner {

    public static void main(String[] args) {

        configureCustomLogbackLogging();
        setGlobalFontSize(10);

        try {
            ApplicationStartupContext ctx = new ApplicationStartupContext();
            SwingUtils.setSystemLookAndFeel(ctx.getAppConfiguration());
            Frame window = new EtrexUploader(ctx);
            window.setMinimumSize(new Dimension(800,600));
            window.setVisible(true);
        } catch (Exception e) {
            log.error("Fatal exception, application terminated {}", e.getMessage(), e);
            EventBus.shutdown();
            System.exit(1);
        }

    }

    private static void configureCustomLogbackLogging() {

        String currentDir = System.getProperty("user.dir");
        String logbackXml = "logback.xml";
        String path = currentDir + File.separator + logbackXml;
        Path logbackXmlLocation = Paths.get(path);

        try (InputStream configStream = Files.newInputStream(logbackXmlLocation)) {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.reset();

            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            configurator.doConfigure(configStream); // loads logback file
        } catch (Exception e) {
            log.warn("Exception while configuring logging");
        }
    }


}
