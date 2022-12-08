package net.wirelabs.etrex.uploader.gui.components;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.eventbus.Event;
import net.wirelabs.etrex.uploader.strava.authorizer.StravaAuthorizer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created 12/8/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaConnectorPanel extends EventAwarePanel {

    private Configuration configuration;
    private StravaAuthorizer authorizer;

    private final JLabel lblApplicationId = new JLabel("Application ID");
    private final JLabel lblClientSecret = new JLabel("Client secret");
    private final JTextField appIdInput = new JTextField();
    private final JTextField appSecretInput = new JTextField();
    private final JButton connectBtn = new JButton();
    private final JDialog parent;

    public StravaConnectorPanel(Configuration configuration, JDialog parent) {
        setLayout(new MigLayout("", "[grow]", "[][][][][]"));
        this.parent = parent;
        add(lblApplicationId, "cell 0 0,alignx center,aligny center");
        add(appIdInput, "cell 0 1,grow");
        add(lblClientSecret, "cell 0 2,alignx center,aligny center");
        add(appSecretInput, "cell 0 3,grow");
        add(connectBtn, "cell 0 4,growx,aligny center");

        authorizer = new StravaAuthorizer(configuration);
        appSecretInput.setText(configuration.getStravaClientSecret());
        appIdInput.setText(configuration.getStravaAppId());
        setupStravaButton();
    }

    private void setupStravaButton() {
        URL iconLocation = getClass().getResource("/btn_strava_connectwith_orange.png");
        if (iconLocation != null) {
            ImageIcon icon = new ImageIcon(iconLocation);
            connectBtn.setIcon(icon);
        } else {
            connectBtn.setText("Connect to strava");
        }
        connectBtn.addActionListener(this::authorize);
    }

    private void authorize(ActionEvent e) {
        configuration.setStravaClientSecret(appSecretInput.getText());
        configuration.setStravaAppId(appIdInput.getText());
        authorizer.authorizeAccess();
        authorizer.shutdown();
        parent.dispose();

    }

    @Override
    protected void onEvent(Event evt) {

    }

    @Override
    protected Collection<EventType> subscribeEvents() {
        return new ArrayList<>();
    }
}
