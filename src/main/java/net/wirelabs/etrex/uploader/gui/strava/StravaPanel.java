package net.wirelabs.etrex.uploader.gui.strava;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.strava.account.UserAccountPanel;
import net.wirelabs.etrex.uploader.gui.strava.activities.StravaActivitiesPanel;
import net.wirelabs.etrex.uploader.strava.service.StravaService;

import javax.swing.*;
import java.awt.*;

public class StravaPanel extends JPanel {

    private final LayoutManager layout = new MigLayout("insets 0px", "[89%][11%]", "[grow][grow]");

    public StravaPanel(StravaService stravaService, AppConfiguration appConfiguration) {
        setLayout(layout);
        add(new StravaActivitiesPanel(stravaService, appConfiguration), "cell 0 0,grow");
        add(new UserAccountPanel(stravaService,appConfiguration), "cell 1 0,grow");
        setSize(new Dimension(800, 350));
    }

}
