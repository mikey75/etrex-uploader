package net.wirelabs.etrex.uploader.gui.strava;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.strava.account.UserAccountPanel;
import net.wirelabs.etrex.uploader.gui.strava.activities.StravaActivitiesPanel;
import net.wirelabs.etrex.uploader.strava.service.StravaService;

import javax.swing.*;

public class StravaPanel extends JPanel {

    public StravaPanel(StravaService stravaService, AppConfiguration appConfiguration) {
        setLayout(new MigLayout("insets 0px", "[89%][11%]", "[grow][grow]"));
        add(new StravaActivitiesPanel(stravaService, appConfiguration), "cell 0 0,grow");
        add(new UserAccountPanel(stravaService,appConfiguration), "cell 1 0,grow");
    }

}
