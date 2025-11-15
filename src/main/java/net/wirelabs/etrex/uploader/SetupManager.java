package net.wirelabs.etrex.uploader;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.utils.LoggingConfigurator;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.utils.SystemUtils;
import net.wirelabs.etrex.uploader.gui.stravaconnector.StravaConnector;

import javax.swing.*;
import java.io.IOException;
@Slf4j
@NoArgsConstructor
public class SetupManager {

    @Getter
    private ApplicationStartupContext appContext;

    public void initialize() throws IllegalStateException {

        try {
            checkSystem();
            configureLogger();
            initializeContext();
            checkStravaIsUp();
            setFontAndLookAndFeel();
            runStravaConnectorIfNecessary();
        } catch (Exception e) {
            throw new IllegalStateException("Setup Manager failed to initialize: " + e.getMessage());
        }
    }

    void checkSystem() {
        SystemUtils.checkGraphicsEnvironmentPresent();
        SystemUtils.checkOsSupport();
    }
    void configureLogger() {
        LoggingConfigurator.configureLogger();
    }

    void initializeContext() throws IOException {
        log.info("Etrex Uploader ver {} starting up....", SystemUtils.getAppVersion());
        appContext = new ApplicationStartupContext();
    }

    void checkStravaIsUp() {
        appContext.getStravaConnectionChecker().checkAndExitIfDown();
    }

    void setFontAndLookAndFeel() throws UnsupportedLookAndFeelException, ReflectiveOperationException {
        SwingUtils.setSystemLookAndFeel(appContext.getAppConfiguration().getLookAndFeelClassName());

        if (!appContext.getAppConfiguration().getLookAndFeelClassName().toLowerCase().contains("flatlaf")) {
            SwingUtils.setGlobalFontSize(appContext.getAppConfiguration().getFontSize());
        }
    }

    void runStravaConnectorIfNecessary() {
        if (!getAppContext().getStravaConfiguration().hasAllTokensAndCredentials()) {
            log.info("Running strava connector...");
            runStravaConnector();
        }
    }

    void runStravaConnector() {
        new StravaConnector(appContext);
    }
}
