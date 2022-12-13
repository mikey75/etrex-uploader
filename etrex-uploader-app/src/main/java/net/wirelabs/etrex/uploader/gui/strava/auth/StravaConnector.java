package net.wirelabs.etrex.uploader.gui.strava.auth;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.strava.client.StravaException;
import net.wirelabs.etrex.uploader.strava.client.TokenManager;
import net.wirelabs.etrex.uploader.strava.oauth.AuthResponse;
import net.wirelabs.etrex.uploader.strava.oauth.OAuth;
import net.wirelabs.etrex.uploader.strava.service.IStravaService;
import net.wirelabs.etrex.uploader.strava.service.StravaService;


/**
 * Created 12/8/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaConnector extends JDialog {


    private final OAuth authCodeInterceptor;
    private final IStravaService strava;

    private final JLabel lblApplicationId = new JLabel("Application ID");
    private final JLabel lblClientSecret = new JLabel("Client secret");
    private final JTextField appIdInput = new JTextField();
    private final JTextField appSecretInput = new JTextField();
    private final JButton connectBtn = new ConnectWithStravaButton();
    private final TokenManager tokenManager;

    public StravaConnector(IStravaService strava, TokenManager tokenManager, OAuth authCodeInterceptor) {

        this.authCodeInterceptor = authCodeInterceptor;
        this.strava = strava;
        this.tokenManager = tokenManager;
        createVisualComponent();
        registerExitOnCloseListener();
        connectBtn.addActionListener(this::connectWithStrava);

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
                String authCode = authCodeInterceptor.getAuthCode(appId);
                AuthResponse authResponse = strava.exchangeAuthCodeForAccessToken(appId, clientSecret, authCode);
                tokenManager.updateTokenInfo(authResponse.getAccessToken(), authResponse.getRefreshToken(), authResponse.getExpiresAt());
                tokenManager.updateCredentials(appId,clientSecret);
                authCodeInterceptor.shutdown();
                dispose();
            }
        } catch (StravaException | IOException e) {
            SwingUtils.errorMsg("Could not connect with strava:" + e.getMessage());
        }
    }

}
