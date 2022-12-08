package net.wirelabs.etrex.uploader.gui.components;

import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;

import javax.swing.*;

public class StravaConnectorDialog extends JDialog {

    /**
     * Create the dialog.
     */
    public StravaConnectorDialog(Configuration configuration) {
        JPanel contentPanel = new StravaConnectorPanel(configuration, this);
        setModal(true);
        setTitle("Connect to strava");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 306, 189);
        SwingUtils.centerComponent(this);
        add(contentPanel);
    }





}
