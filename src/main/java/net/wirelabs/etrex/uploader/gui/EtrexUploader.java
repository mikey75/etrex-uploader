package net.wirelabs.etrex.uploader.gui;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.ApplicationStartupContext;
import net.wirelabs.etrex.uploader.common.eventbus.EventBus;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.browsers.GarminDeviceBrowser;
import net.wirelabs.etrex.uploader.gui.browsers.LocalStorageBrowser;
import net.wirelabs.etrex.uploader.gui.components.Splash;
import net.wirelabs.etrex.uploader.gui.map.MapPanel;
import net.wirelabs.etrex.uploader.gui.strava.account.UserAccountPanel;
import net.wirelabs.etrex.uploader.gui.strava.activities.ActivitiesPanel;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static net.wirelabs.etrex.uploader.common.Constants.APPLICATION_IDENTIFICATION;


@Slf4j
public class EtrexUploader extends JFrame {



    private JPanel devicePanel;
    private JPanel storageBrowser;
    private JPanel mapViewer;
    private JPanel athletePanel;
    private JPanel activitiesPanel;

    public EtrexUploader(ApplicationStartupContext ctx) {

        if (ctx.isAuthorized()) {

            Splash splash = new Splash();

            setTitle(APPLICATION_IDENTIFICATION);
            Container container = getContentPane();
            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            container.setLayout(new MigLayout("", "[10%,grow][80%,grow][10%,grow]", "[30%][grow][100px:n,grow]"));
            registerWindowCloseListener(ctx);

            splash.update("Initializing Strava GUI components");
            athletePanel = new UserAccountPanel(ctx.getStravaService(), ctx.getAppConfiguration());
            activitiesPanel = new ActivitiesPanel(ctx.getStravaService());

            splash.update("Initializing browsers");
            UploadService uploadService = new UploadService(ctx.getAppConfiguration(), ctx.getStravaService(), ctx.getFileService());
            devicePanel = new GarminDeviceBrowser(uploadService);
            storageBrowser = new LocalStorageBrowser(ctx.getAppConfiguration());

            splash.update("Initalizing maps");
            mapViewer = new MapPanel(ctx.getAppConfiguration());

            splash.update("Starting Garmin drive observer service");
            ctx.getGarminDeviceService().start();

            splash.update("Laying out main window");
            container.add(devicePanel, "cell 0 0 1 2,grow");
            container.add(activitiesPanel, "cell 1 0,grow");
            container.add(athletePanel, "cell 2 0,grow");
            container.add(mapViewer, "cell 1 1 2 2,grow");
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

    private void registerWindowCloseListener(ApplicationStartupContext ctx) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {

                int answer = SwingUtils.yesNoMsg("Are you sure you want to exit?");
                if (answer == JOptionPane.YES_OPTION) {
                    ctx.getGarminDeviceService().stop();
                    EventBus.stop();
                    System.exit(0);
                }
            }
        });
    }

}
