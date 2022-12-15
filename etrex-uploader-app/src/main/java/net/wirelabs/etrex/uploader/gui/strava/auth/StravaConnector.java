package net.wirelabs.etrex.uploader.gui.strava.auth;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.strava.client.StravaException;
import net.wirelabs.etrex.uploader.strava.oauth.AuthCodeRetriever;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;


/**
 * Created 12/8/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaConnector extends JDialog {

    private final AuthCodeRetriever authCodeRetriever;
    private final StravaClient client;

    private final JLabel lblApplicationId = new JLabel("Application ID");
    private final JLabel lblClientSecret = new JLabel("Client secret");
    private final JTextField appIdInput = new JTextField();
    private final JTextField appSecretInput = new JTextField();
    private final JButton connectBtn = new ConnectWithStravaButton();


    public StravaConnector(StravaClient client, AuthCodeRetriever authCodeRetriever)  {

        this.authCodeRetriever = authCodeRetriever;
        this.client = client;
  
        createVisualComponent();
        registerExitOnCloseListener();
        connectBtn.addActionListener(this::connectWithStrava);
        setVisible(true);
    }
    

    
    private void registerExitOnCloseListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    private void createVisualComponent() {

        setTitle("Connect to strava");
        setLayout(new MigLayout("", "[grow]", "[][][][][]"));
        add(lblApplicationId, "cell 0 0,alignx center,aligny center");
        add(appIdInput, "cell 0 1,grow");
        add(lblClientSecret, "cell 0 2,alignx center,aligny center");
        add(appSecretInput, "cell 0 3,grow");
        add(connectBtn, "cell 0 4,growx,aligny center");

        setBounds(100, 100, 320, 210);
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        SwingUtils.centerComponent(this);
    }


    private void connectWithStrava(ActionEvent ev) {
        String appId = appIdInput.getText();
        String clientSecret = appSecretInput.getText();
        
        try {
            if (!appId.isBlank() && !clientSecret.isBlank()) {
                String authCode = authCodeRetriever.getAuthCode(appId);
                client.exchangeAuthCodeForAccessToken(appId, clientSecret, authCode);
                authCodeRetriever.shutdown();
                dispose();
            }
        } catch (StravaException | IOException e) {
            SwingUtils.errorMsg("Could not connect with strava:" + e.getMessage());
            System.exit(1);
        }
    }

   
}
