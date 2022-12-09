package net.wirelabs.etrex.uploader.gui.account;


import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;
import net.wirelabs.etrex.uploader.strava.model.SummaryAthlete;
import net.wirelabs.etrex.uploader.strava.service.IStravaService;
import net.wirelabs.etrex.uploader.strava.client.StravaClientException;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Created 9/12/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class UserAccountPanel extends BorderedPanel {

    private final JLabel label = new JLabel("Getting data");
    private final JLabel imageLabel = new JLabel("");
    private final IStravaService stravaService;
    
    public UserAccountPanel(IStravaService stravaService) {
        super("My profile");
        this.stravaService = stravaService;
        setLayout(new MigLayout("", "[]", "[][]"));
        add(label, "cell 0 0,growx");
        add(imageLabel, "cell 0 1,alignx center");
        
        SwingUtilities.invokeLater(this::getUserAccountData);

    }
    
    private void getUserAccountData() {
        try {
            SummaryAthlete athlete = stravaService.getCurrentAthlete();
            label.setText(athlete.getFirstname() + " " + athlete.getLastname());
            String profilePicFilename = athlete.getProfile();
            BufferedImage img = ImageIO.read(new URL(profilePicFilename));
            imageLabel.setIcon(new ImageIcon(img));
        } catch (IOException e) {
            imageLabel.setText("Couldn't get profile picture");
        } catch (StravaClientException e) {
            SwingUtils.errorMsg(e.getMessage());
        }
    }

}
