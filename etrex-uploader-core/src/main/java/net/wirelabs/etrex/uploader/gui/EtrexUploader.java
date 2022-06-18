package net.wirelabs.etrex.uploader.gui;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.ApplictationContext;
import net.wirelabs.etrex.uploader.gui.account.AthleteInfo;
import net.wirelabs.etrex.uploader.gui.activitiestable.ActivitiesPanel;
import net.wirelabs.etrex.uploader.gui.browsers.GarminDeviceBrowser;
import net.wirelabs.etrex.uploader.gui.browsers.LocalStorageBrowser;
import net.wirelabs.etrex.uploader.gui.components.Splash;
import net.wirelabs.etrex.uploader.gui.components.filetree.UploadDialog;
import net.wirelabs.etrex.uploader.gui.map.MapPanel;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.eventbus.EventBus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static net.wirelabs.etrex.uploader.common.Constants.APPLICATION_IDENTIFICATION;


@Slf4j
public class EtrexUploader extends JFrame {

    private final Splash splash = new Splash();

    private final JPanel devicePanel;
    private final JPanel storageBrowser;
    private final JPanel mapViewer;
    private final JPanel athletePanel;
    private final JPanel activitiesPanel;

    public EtrexUploader(ApplictationContext ctx) {

        setTitle(APPLICATION_IDENTIFICATION);
        Container container = getContentPane();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        container.setLayout(new MigLayout("", "[10%,grow][80%,grow][10%,grow]", "[30%,grow][grow][100px:n,grow]"));
        registerWindowCloseListener(ctx);

        splash.update("Initializing Strava GUI components");
        athletePanel = new AthleteInfo(ctx.getStravaService());
        activitiesPanel = new ActivitiesPanel(ctx.getStravaService());

        splash.update("Initializing browsers");
        UploadDialog uploadDialog = new UploadDialog(ctx.getStravaService(), ctx.getFileService());
        devicePanel = new GarminDeviceBrowser(uploadDialog);
        storageBrowser = new LocalStorageBrowser(ctx.getConfiguration());

        splash.update("Initalizing maps");
        mapViewer = new MapPanel();

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
    }

    private void registerWindowCloseListener(ApplictationContext ctx) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {

                int answer = SwingUtils.yesNoMsg("Are you sure you want to close this window?");
                if (answer == JOptionPane.YES_OPTION) {
                    ctx.getGarminDeviceService().stop();
                    EventBus.stop();
                    System.exit(0);
                }
            }
        });
    }

}
