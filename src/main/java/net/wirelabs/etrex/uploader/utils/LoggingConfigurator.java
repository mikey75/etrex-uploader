package net.wirelabs.etrex.uploader.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggingConfigurator {

    private static final Path LOGBACK_CONFIG_XML = Paths.get(SystemUtils.getWorkDir(), "logback.xml");

    public static void configureLogger() {

        if (!LOGBACK_CONFIG_XML.toFile().exists()) {
            issueConfirmationDialog("Can't find logging config file:");
        } else {

            try (InputStream configStream = Files.newInputStream(LOGBACK_CONFIG_XML)) {
                LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                loggerContext.reset();

                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(loggerContext);
                configurator.doConfigure(configStream); // loads logback file
            } catch (Exception e) {
                issueConfirmationDialog("Can't load logging config file:" + e.getMessage());
            }
        }
    }

    public static void issueConfirmationDialog(String cause) {
        String message = String.format("%s (%s) %nDo you want to run the app without logging?", cause, LOGBACK_CONFIG_XML);
        int result = SwingUtils.yesNoMsg(message);
        if (result != JOptionPane.YES_OPTION) {
            SystemUtils.systemExit(1);
        }
    }

}
