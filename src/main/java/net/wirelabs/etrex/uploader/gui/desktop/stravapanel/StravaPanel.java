package net.wirelabs.etrex.uploader.gui.desktop.stravapanel;

import lombok.Getter;
import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;
import net.wirelabs.etrex.uploader.gui.desktop.stravapanel.account.UserAccountPanel;
import net.wirelabs.etrex.uploader.gui.desktop.stravapanel.activitiestable.StravaActivitiesPanel;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;

import java.awt.*;

@Getter
public class StravaPanel extends BasePanel {

    private final StravaActivitiesPanel activitiesPanel;
    private final UserAccountPanel userAccountPanel;

    public StravaPanel(StravaClient stravaClient) {
        super("insets 0px","[89%][11%]","[grow][grow]");

        activitiesPanel = new StravaActivitiesPanel(stravaClient);
        userAccountPanel = new UserAccountPanel(stravaClient);

        add(activitiesPanel, "cell 0 0,grow");
        add(userAccountPanel, "cell 1 0,grow");

        setSize(new Dimension(800, 350));
    }

}
