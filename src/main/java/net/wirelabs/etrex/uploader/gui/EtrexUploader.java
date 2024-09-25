package net.wirelabs.etrex.uploader.gui;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.ApplicationStartupContext;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.browsers.GarminDeviceBrowser;
import net.wirelabs.etrex.uploader.gui.browsers.LocalStorageBrowser;
import net.wirelabs.etrex.uploader.gui.components.Splash;
import net.wirelabs.etrex.uploader.gui.map.MapPanel;
import net.wirelabs.etrex.uploader.gui.strava.account.UserAccountPanel;
import net.wirelabs.etrex.uploader.gui.strava.activities.StravaActivitiesPanel;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtil;
import net.wirelabs.eventbus.EventBus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static net.wirelabs.etrex.uploader.common.Constants.APPLICATION_IDENTIFICATION;
import static net.wirelabs.etrex.uploader.common.Constants.CURRENT_WORK_DIR;


@Slf4j
public class EtrexUploader extends JFrame {

    @Getter
    private static final List<File> configuredMaps = new ArrayList<>();

    private GarminDeviceBrowser devicePanel;
    private LocalStorageBrowser storageBrowser;
    private MapPanel mapPanel;
    private UserAccountPanel athletePanel;
    private StravaActivitiesPanel activitiesPanel;


    public EtrexUploader(ApplicationStartupContext ctx) {
        checkStravaIsUp(ctx.getAppConfiguration());
        getMapDefinitionFiles(ctx.getAppConfiguration());

        if (ctx.isAuthorized()) {

            Splash splash = new Splash();

            setTitle(APPLICATION_IDENTIFICATION);
            Container container = getContentPane();
            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            container.setLayout(new MigLayout("", "[10%,grow][80%,grow][10%,grow]", "[30%][grow][100px:n,grow]"));
            registerWindowCloseListener(ctx);

            splash.update("Initializing Strava GUI components");
            athletePanel = new UserAccountPanel(ctx.getStravaService(), ctx.getAppConfiguration());
            activitiesPanel = new StravaActivitiesPanel(ctx.getStravaService(), ctx.getAppConfiguration());

            splash.update("Initializing browsers");
            UploadService uploadService = new UploadService(ctx.getAppConfiguration(), ctx.getStravaService(), ctx.getFileService());
            devicePanel = new GarminDeviceBrowser(uploadService);
            storageBrowser = new LocalStorageBrowser(ctx.getAppConfiguration());

            splash.update("Initalizing maps");
            mapPanel = new MapPanel(ctx.getAppConfiguration());

            splash.update("Starting Garmin drive observer service");
            ctx.getGarminDeviceService().start();

            splash.update("Laying out main window");
            container.add(devicePanel, "cell 0 0 1 2,grow");
            container.add(activitiesPanel, "cell 1 0,grow");
            container.add(athletePanel, "cell 2 0,grow");
            container.add(mapPanel, "cell 1 1 2 2,grow");
            container.add(storageBrowser, "cell 0 2,grow");

            splash.update("Done");
            setExtendedState(Frame.MAXIMIZED_BOTH);
            splash.close();
            setVisible(true);
            log.info("Application initalization finished.");
        } else {
            SwingUtils.errorMsg("You are not authorized");
            System.exit(1);
        }
    }

    private  void checkStravaIsUp(AppConfiguration cfg) {
        if (!StravaUtil.isStravaUp(cfg.getStravaCheckTimeout())) {
            SwingUtils.errorMsg("Strava seems to be down! Exiting!");
            log.info("Exiting due to strava being down!");
            System.exit(1);
        }
    }

    public void getMapDefinitionFiles(AppConfiguration configuration) {

        // get and sort maps from app's default location
        List<File> defaultMaps = FileUtils.listDirectory(new File(CURRENT_WORK_DIR + File.separator + "maps")).stream()
                .sorted(Comparator.comparing(File::getName))
                .collect(Collectors.toList());

        // get and sort usermaps
        File mapsDefinitionsDir = configuration.getUserMapDefinitonsDir().toFile();
        List<File> sortedUserMaps = FileUtils.listDirectory(mapsDefinitionsDir).stream()
                .sorted(Comparator.comparing(File::getName))
                .collect(Collectors.toList());

        // add default map(s)
        configuredMaps.addAll(defaultMaps);
        // add sorted usermaps
        configuredMaps.addAll(sortedUserMaps);

        if (configuredMaps.isEmpty()) {
            SwingUtils.errorMsg("Fatal error: No maps defined, check configuration. Exiting!");
            System.exit(1);
        }
    }

    private void registerWindowCloseListener(ApplicationStartupContext ctx) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {

                int answer = SwingUtils.yesNoMsg("Are you sure you want to exit?");
                if (answer == JOptionPane.YES_OPTION) {
                    ctx.getGarminDeviceService().stop();
                    EventBus.shutdown();
                    System.exit(0);
                }
            }
        });
    }

}
