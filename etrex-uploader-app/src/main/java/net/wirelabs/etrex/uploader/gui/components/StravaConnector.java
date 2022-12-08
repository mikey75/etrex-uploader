package net.wirelabs.etrex.uploader.gui.components;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.strava.authorizer.StravaAuthorizer;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URL;

public class StravaConnector extends JDialog {


    private final JLabel lblApplicationId = new JLabel("Application ID");
    private final JLabel lblClientSecret = new JLabel("Client secret");
    private final JButton connectBtn = new JButton();

    private JTextField appIdInput = new JTextField();
    private JTextField appSecretInput = new JTextField();
    private Configuration configuration;
    private StravaAuthorizer authorizer;


    /**
     * Create the dialog.
     */
    public StravaConnector(Configuration configuration) {
        this.configuration = configuration;
        authorizer = new StravaAuthorizer(configuration);
        appSecretInput.setText(configuration.getStravaClientSecret());
        appIdInput.setText(configuration.getStravaAppId());

        setModal(true);
        getContentPane().setLayout(new MigLayout("", "[grow]", "[][][][][]"));
        setTitle("Connect to strava");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 306, 189);
        setupStravaButton();
        layoutComponent();
        

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

    private void layoutComponent() {
        getContentPane().add(lblApplicationId, "cell 0 0,alignx center,aligny center");
        getContentPane().add(appIdInput, "cell 0 1,grow");
        getContentPane().add(lblClientSecret, "cell 0 2,alignx center,aligny center");
        getContentPane().add(appSecretInput, "cell 0 3,grow");
        getContentPane().add(connectBtn, "cell 0 4,growx,aligny center");
        SwingUtils.centerComponent(this);
    }

    private void authorize(ActionEvent e) {
        configuration.setStravaClientSecret(appSecretInput.getText());
        configuration.setStravaAppId(appIdInput.getText());
        authorizer.authorizeAccess();
        authorizer.shutdown();
        dispose();

    }
}
