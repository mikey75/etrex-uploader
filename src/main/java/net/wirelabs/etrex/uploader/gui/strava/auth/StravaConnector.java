package net.wirelabs.etrex.uploader.gui.strava.auth;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.StravaException;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.strava.oauth.AuthService;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created 12/8/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaConnector extends JDialog {

    private final JLabel lblApplicationId = new JLabel("Client ID");
    private final JLabel lblClientSecret = new JLabel("Client secret");
    private final JTextField appIdInput = new JTextField();
    private final JTextField appSecretInput = new JTextField();
    private final JButton connectBtn = new StravaButton();
    private final AuthService authService;
    @Getter
    private final AtomicBoolean oauthStatus = new AtomicBoolean(false);
    @Getter
    private String oauthMessage;

    public StravaConnector(StravaClient client)  {

        authService = new AuthService(client);

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
        dispose();

        try {
            if (!appId.isBlank() && !clientSecret.isBlank()) {
                authService.getToken(appId,clientSecret);
                oauthStatus.set(true);
            } else {
                oauthStatus.set(false);
                oauthMessage = "client id/client secret cannot be blank";
            }
        } catch (StravaException | IOException e) {

            oauthStatus.set(false);
            oauthMessage = e.getMessage();
        }
    }

   
}
