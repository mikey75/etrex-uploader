package net.wirelabs.etrex.uploader.gui.stravaconnector;

import lombok.Getter;
import net.wirelabs.etrex.uploader.ApplicationStartupContext;
import net.wirelabs.etrex.uploader.strava.StravaConnectionChecker;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.utils.SystemUtils;
import net.wirelabs.etrex.uploader.gui.common.base.BaseDialog;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.*;


/**
 * Created 12/8/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaConnector extends BaseDialog {

    private final JLabel lblApplicationId = new JLabel("Client ID");
    private final JLabel lblClientSecret = new JLabel("Client secret");
    private final JTextField appIdInput = new JTextField();
    private final JTextField appSecretInput = new JTextField();
    private final JButton connectBtn = new StravaButton();
    @Getter
    private final AtomicBoolean oauthStatus = new AtomicBoolean(false);
    private final StravaClient client;
    private final transient StravaConnectionChecker stravaConnectionChecker;
    @Getter
    private String oauthMessage;

    public StravaConnector(ApplicationStartupContext ctx)  {
        super("Connect to strava","","[grow]","[][][][][]");
        this.client = ctx.getStravaClient();
        this.stravaConnectionChecker = ctx.getStravaConnectionChecker();
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

        add(lblApplicationId, cell(0,0).alignX(CENTER).alignY(CENTER));
        add(appIdInput, cell(0,1).grow());
        add(lblClientSecret, cell(0,2).alignX(CENTER).alignY(CENTER));
        add(appSecretInput, cell(0,3).grow());
        add(connectBtn, cell(0,4).growX().alignY(CENTER));

        setBounds(100, 100, 320, 210);
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        SwingUtils.centerComponent(this);
        setAlwaysOnTop(true);
        setVisible(true);
    }


    private void connectWithStrava(ActionEvent ev) {
        setAlwaysOnTop(false);
        stravaConnectionChecker.checkAndExitIfDown();

        String appId = appIdInput.getText();
        String clientSecret = appSecretInput.getText();
        dispose();

        try {
            if (!appId.isBlank() && !clientSecret.isBlank()) {
                client.authorizeToStrava(appId,clientSecret);
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
