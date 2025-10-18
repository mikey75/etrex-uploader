package net.wirelabs.etrex.uploader.gui.stravaconnector;

import lombok.Getter;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.utils.SystemUtils;
import net.wirelabs.etrex.uploader.gui.common.base.BaseDialog;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.strava.oauth.AuthService;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.cc;


/**
 * Created 12/8/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaConnector extends BaseDialog {

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
        super("Connect to strava","","[grow]","[][][][][]");
        authService = new AuthService(client);

        registerExitOnCloseListener();
        registerActionOnClickConnect();
        createVisualComponent();

        if (!getOauthStatus().get()) {
            SwingUtils.errorMsg(getOauthMessage());
            SystemUtils.systemExit(1);
        }
    }
    

    
    private void registerExitOnCloseListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                dispose();
                SystemUtils.systemExit(0);
            }
        });
    }

    private void registerActionOnClickConnect() {
        connectBtn.addActionListener(this::connectWithStrava);
    }

    private void createVisualComponent() {

        add(lblApplicationId, cc().cell(0,0).alignX("center").alignY("center"));
        add(appIdInput, cc().cell(0,1).grow());
        add(lblClientSecret, cc().cell(0,2).alignX("center").alignY("center"));
        add(appSecretInput, cc().cell(0,3).grow());
        add(connectBtn, cc().cell(0,4).growX().alignY("center"));

        setBounds(100, 100, 320, 210);
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        SwingUtils.centerComponent(this);
        setAlwaysOnTop(true);
        setVisible(true);
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
