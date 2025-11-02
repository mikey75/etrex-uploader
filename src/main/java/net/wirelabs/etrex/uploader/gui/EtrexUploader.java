package net.wirelabs.etrex.uploader.gui;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.ApplicationStartupContext;
import net.wirelabs.etrex.uploader.SetupManager;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.utils.FileUtils;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.utils.SystemUtils;
import net.wirelabs.etrex.uploader.gui.desktop.devicepanel.GarminAndStoragePanel;
import net.wirelabs.etrex.uploader.gui.desktop.DesktopPanel;
import net.wirelabs.etrex.uploader.gui.desktop.mappanel.MapPanel;
import net.wirelabs.etrex.uploader.gui.desktop.stravapanel.StravaPanel;
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
    @Getter
    private static final SetupManager setupManager = new SetupManager();
    private final Splash splash = new Splash();

    public EtrexUploader() {
        super(APPLICATION_IDENTIFICATION);

        setupManager.initialize();
        splash.invoke();

        ApplicationStartupContext ctx = setupManager.getAppContext();

        splash.update("Application initialization started ....");

        splash.update("Checking strava status");
        checkStravaIsUp(ctx.getStravaConfiguration());

        splash.update("Configuring maps");
        getMapDefinitionFiles(ctx.getAppConfiguration());

        if (ctx.getStravaConfiguration().hasAllTokensAndCredentials()) {

            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            registerWindowCloseListener(ctx);
            createApplicationDesktop(ctx);

            log.info("Application initialization finished.");
        } else {
            SwingUtils.errorMsg("You are not authorized");
            SystemUtils.systemExit(1);
        }
    }

    private void createApplicationDesktop(ApplicationStartupContext ctx) {

        splash.update("Initializing browsers");
        GarminAndStoragePanel garminAndStoragePanel = new GarminAndStoragePanel(ctx.getUploadService(), ctx.getAppConfiguration());

        splash.update("Initializing Strava component");
        StravaPanel stravaPanel = new StravaPanel(ctx.getStravaClient());

        splash.update("Initializing maps component");
        MapPanel mapPanel = new MapPanel(ctx.getAppConfiguration());

        splash.update("Starting Garmin drive observer service");
        ctx.getGarminDeviceService().start();

        splash.update("Laying out main window");
        DesktopPanel desktopPanel = new DesktopPanel(garminAndStoragePanel, stravaPanel, mapPanel, ctx.getAppConfiguration().isEnableDesktopSliders());
        add(desktopPanel);

        splash.update("Done");
        setExtendedState(Frame.MAXIMIZED_BOTH);
        splash.dispose();
        setMinimumSize(new Dimension(800, 600));
        setVisible(true);
    }

    private void checkStravaIsUp(StravaConfiguration cfg) {
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
