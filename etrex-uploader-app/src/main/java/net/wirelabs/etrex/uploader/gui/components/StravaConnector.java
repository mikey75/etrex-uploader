package net.wirelabs.etrex.uploader.gui.components;

import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.strava.client.StravaClientException;
import net.wirelabs.etrex.uploader.strava.oauth.OAuthAuthorizer;

/**
 * Created 12/8/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaConnector extends JDialog {

    private final Configuration configuration;
    private final OAuthAuthorizer authorizer;

    private final JLabel lblApplicationId = new JLabel("Application ID");
    private final JLabel lblClientSecret = new JLabel("Client secret");
    private final JTextField appIdInput = new JTextField();
    private final JTextField appSecretInput = new JTextField();
    private final JButton connectBtn = new JButton();


    public StravaConnector(Configuration configuration) {
        this.configuration = configuration;
        getContentPane().setLayout(new MigLayout("", "[grow]", "[][][][][]"));
        getContentPane().add(lblApplicationId, "cell 0 0,alignx center,aligny center");
        getContentPane().add(appIdInput, "cell 0 1,grow");
        getContentPane().add(lblClientSecret, "cell 0 2,alignx center,aligny center");
        getContentPane().add(appSecretInput, "cell 0 3,grow");
        getContentPane().add(connectBtn, "cell 0 4,growx,aligny center");

        setModal(true);
        setTitle("Connect to strava");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 306, 189);
        SwingUtils.centerComponent(this);

        authorizer = new OAuthAuthorizer(configuration);
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
        if (appIdInput.getText().isBlank() || appSecretInput.getText().isBlank()) {
            SwingUtils.errorMsg("clientSecret and application id must not be blank");
        }
        configuration.setStravaClientSecret(appSecretInput.getText());
        configuration.setStravaAppId(appIdInput.getText());
        try {
            authorizer.getAndStoreTokens();
            dispose();
        } catch (StravaClientException ex) {
            SwingUtils.errorMsg("OAuth process failed to get authorization code");
        }
    }




}
