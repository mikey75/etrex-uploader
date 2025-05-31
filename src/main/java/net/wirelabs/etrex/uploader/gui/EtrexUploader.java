package net.wirelabs.etrex.uploader.gui;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.ApplicationStartupContext;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.etrex.uploader.gui.browsers.GarminAndStoragePanel;
import net.wirelabs.etrex.uploader.gui.components.Splash;
import net.wirelabs.etrex.uploader.gui.components.desktop.DesktopPanel;
import net.wirelabs.etrex.uploader.gui.map.MapPanel;
import net.wirelabs.etrex.uploader.gui.strava.StravaPanel;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static net.wirelabs.etrex.uploader.common.Constants.APPLICATION_IDENTIFICATION;


@Slf4j
public class EtrexUploader extends JFrame {

    @Getter
    private static final List<File> configuredMaps = new ArrayList<>();
    private DesktopPanel desktopPanel;
    private GarminAndStoragePanel garminAndStoragePanel;
    private StravaPanel stravaPanel;
    private MapPanel mapPanel;


    public EtrexUploader(ApplicationStartupContext ctx) {

        Splash splash = new Splash();
        splash.update("Checking strava status");
        checkStravaIsUp(ctx.getAppConfiguration());
        splash.update("Configuring maps");
        getMapDefinitionFiles(ctx.getAppConfiguration());

        if (ctx.getStravaConfiguration().hasAllTokensAndCredentials()) {

            setTitle(APPLICATION_IDENTIFICATION);

            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            registerWindowCloseListener(ctx);

            splash.update("Initializing Strava GUI components");

            splash.update("Initializing browsers");
            garminAndStoragePanel = new GarminAndStoragePanel(ctx.getUploadService(), ctx.getAppConfiguration());

            splash.update("Initializing Strava component");
            stravaPanel = new StravaPanel(ctx.getStravaService(), ctx.getAppConfiguration());

            splash.update("Initializing maps component");
            mapPanel = new MapPanel(ctx.getAppConfiguration());

            splash.update("Starting Garmin drive observer service");
            ctx.getGarminDeviceService().start();

            splash.update("Laying out main window");

            final boolean slidersEnabled = ctx.getAppConfiguration().isEnableDesktopSliders();
            desktopPanel = new DesktopPanel(garminAndStoragePanel, stravaPanel, mapPanel, slidersEnabled);

            // if sliders enabled - set default slider sizes and locations
            if (slidersEnabled) {
                desktopPanel.setSlidersWidth(5);
                desktopPanel.setVerticalSliderLocation(garminAndStoragePanel.getSize().width);
                desktopPanel.setHorizontalSliderLocation(stravaPanel.getSize().height);
            }

            add(desktopPanel);

            splash.update("Done");
            setExtendedState(Frame.MAXIMIZED_BOTH);
            splash.close();
            setVisible(true);
            log.info("Application initialization finished.");
        } else {
            SwingUtils.errorMsg("You are not authorized");
            SystemUtils.systemExit(1);
        }
    }

    private void checkStravaIsUp(AppConfiguration cfg) {
        log.info("Starting Strava status check");
        if (StravaUtil.isStravaUp(cfg.getStravaCheckTimeout())) {
            log.info("Strava is up and running!");
        } else {
            SwingUtils.errorMsg("Strava seems to be down! Exiting!");
            log.warn("Strava seems to be down. Exiting!");
            SystemUtils.systemExit(1);
        }
    }

    private void getMapDefinitionFiles(AppConfiguration configuration) {

        File defaultMapDefinitionsDir = new File(SystemUtils.getWorkDir(), "maps");
        File userMapDefinitionsDir = configuration.getUserMapDefinitionsDir().toFile();

        // get and sort maps from app's default location
        List<File> sortedDefaultMaps = FileUtils.listDirectorySorted(defaultMapDefinitionsDir);
        // get and sort user maps
        List<File> sortedUserMaps = FileUtils.listDirectorySorted(userMapDefinitionsDir);
        // add them to application's map source
        configuredMaps.addAll(sortedDefaultMaps);
        configuredMaps.addAll(sortedUserMaps);

        if (configuredMaps.isEmpty()) {
            SwingUtils.errorMsg("Fatal error: No maps defined, check configuration. Exiting!");
            SystemUtils.systemExit(1);
        }
    }

    private void registerWindowCloseListener(ApplicationStartupContext ctx) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {

                int answer = SwingUtils.yesNoMsg("Are you sure you want to exit?");
                if (answer == JOptionPane.YES_OPTION) {
                    ctx.getGarminDeviceService().stop();
                    SystemUtils.systemExit(0);
                }
            }
        });
    }

}
