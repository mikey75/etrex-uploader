package net.wirelabs.etrex.uploader.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggingConfigurator {

    private static final Path LOGBACK_CONFIG_XML = Paths.get(SystemUtils.getWorkDir(), "logback.xml");
    private static final String MESSAGE = "Can't find logging config file: " + LOGBACK_CONFIG_XML;
    private static final String EXCEPTION = "Exception while loading config file: " + LOGBACK_CONFIG_XML;
    private static final String QUESTION = "Do you want to run the app without logging?";

    public static void configureLogger() {

        if (!LOGBACK_CONFIG_XML.toFile().exists()) {
            SwingUtils.issueConfirmationWithExitDialog(MESSAGE + "\n" + QUESTION);
        } else {

            try (InputStream configStream = Files.newInputStream(LOGBACK_CONFIG_XML)) {
                LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                loggerContext.reset();

                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(loggerContext);
                configurator.doConfigure(configStream); // loads logback file
            } catch (Exception e) {
                SwingUtils.issueConfirmationWithExitDialog(EXCEPTION + "\n" + "Exception message: " + e.getMessage() + "\n" + QUESTION);
            }
        }
    }

}
