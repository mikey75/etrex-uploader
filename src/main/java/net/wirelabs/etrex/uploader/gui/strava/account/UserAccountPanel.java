package net.wirelabs.etrex.uploader.gui.strava.account;


import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.common.utils.ThreadUtils;
import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;
import net.wirelabs.etrex.uploader.gui.settings.SettingsDialog;
import net.wirelabs.etrex.uploader.StravaException;
import net.wirelabs.etrex.uploader.strava.model.SummaryAthlete;
import net.wirelabs.etrex.uploader.strava.service.StravaService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

/**
 * Created 9/12/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class UserAccountPanel extends BorderedPanel {

    final JLabel athleteName = new JLabel();
    final JLabel athletePicture = new JLabel();
    final ApiUsagePanel apiUsagePanel;
    
    private final StravaService stravaService;
    private final JButton btnSettings = new JButton("Settings");
    private final AppConfiguration configuration;

    public UserAccountPanel(StravaService stravaService, AppConfiguration configuration) {
        this.apiUsagePanel = new ApiUsagePanel(configuration);
        this.stravaService = stravaService;
        this.configuration = configuration;
        initVisualComponent();
        ThreadUtils.runAsync(this::getUserAccountData);
    }

    private void initVisualComponent() {
        setBorderTitle("My profile");

        setLayout(new MigLayout("", "[grow]", "[][][][][][][][grow,bottom]"));
        add(athleteName, "cell 0 1,alignx center");
        add(athletePicture, "cell 0 0,alignx center");
        add(apiUsagePanel,"cell 0 4,growx");
        add(btnSettings, "cell 0 7,growx");

        btnSettings.addActionListener(e -> {
            SettingsDialog d = new SettingsDialog(configuration);
            SwingUtils.centerComponent(d);
            d.setVisible(true);
        });
    }

    private void getUserAccountData() {
        SummaryAthlete athlete = null;
        BufferedImage img = null;
        
        try {
            athlete = stravaService.getCurrentAthlete();
            String profilePicFilename = athlete.getProfile();
            if (profilePicFilename != null) {
                img = ImageIO.read(URI.create(profilePicFilename).toURL());
            }
        } catch (StravaException | IOException e ) {
            log.error("Error getting user profile data {}",e.getMessage(),e);
        }
        
        setAthleteFullName(athlete);
        setAthletePicture(img);
        
    }

    private void setAthletePicture(BufferedImage img) {
        if (img == null ) {
            SwingUtilities.invokeLater(() -> athletePicture.setText("Couldn't get athlete picture"));
        } else {
            SwingUtilities.invokeLater(() -> athletePicture.setIcon(new ImageIcon(img)));
        }
    }

    private void setAthleteFullName(SummaryAthlete athlete) {
        if (athlete == null) {
            SwingUtilities.invokeLater(() -> athleteName.setText("Couldn't get athlete name"));
        }else {
            String athleteFullName = athlete.getFirstname() + " " + athlete.getLastname();
            SwingUtilities.invokeLater(() -> athleteName.setText(athleteFullName));
        }
    }
}
