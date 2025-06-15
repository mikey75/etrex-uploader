package net.wirelabs.etrex.uploader.gui.strava;

import net.wirelabs.etrex.uploader.gui.components.BasePanel;
import net.wirelabs.etrex.uploader.gui.strava.account.UserAccountPanel;
import net.wirelabs.etrex.uploader.gui.strava.activities.StravaActivitiesPanel;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;

import java.awt.*;

public class StravaPanel extends BasePanel {

    public StravaPanel(StravaClient stravaClient) {
        super("insets 0px","[89%][11%]","[grow][grow]");
        add(new StravaActivitiesPanel(stravaClient), "cell 0 0,grow");
        add(new UserAccountPanel(stravaClient), "cell 1 0,grow");
        setSize(new Dimension(800, 350));
    }

}
