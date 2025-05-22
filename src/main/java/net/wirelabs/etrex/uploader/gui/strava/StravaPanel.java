package net.wirelabs.etrex.uploader.gui.strava;

import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.components.BasePanel;
import net.wirelabs.etrex.uploader.gui.strava.account.UserAccountPanel;
import net.wirelabs.etrex.uploader.gui.strava.activities.StravaActivitiesPanel;
import net.wirelabs.etrex.uploader.strava.service.StravaService;

import java.awt.*;

public class StravaPanel extends BasePanel {


    public StravaPanel(StravaService stravaService, AppConfiguration appConfiguration) {
        layout.setLayoutConstraints("insets 0px");
        layout.setColumnConstraints("[89%][11%]");
        layout.setRowConstraints("[grow][grow]");
        setLayout(layout);

        add(new StravaActivitiesPanel(stravaService, appConfiguration), "cell 0 0,grow");
        add(new UserAccountPanel(stravaService,appConfiguration), "cell 1 0,grow");
        setSize(new Dimension(800, 350));
    }

}
