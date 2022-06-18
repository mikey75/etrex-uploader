package net.wirelabs.etrex.uploader.gui.account;


import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;
import net.wirelabs.etrex.uploader.model.strava.SummaryAthlete;
import net.wirelabs.etrex.uploader.strava.IStravaService;
import net.wirelabs.etrex.uploader.strava.api.StravaApiException;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Created 9/12/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class AthleteInfo extends BorderedPanel {

    private final JLabel label = new JLabel("Getting data");
    private final JLabel imageLabel = new JLabel("");
    private final IStravaService stravaService;

    public AthleteInfo(IStravaService stravaService) {
        super("My profile");
        this.stravaService = stravaService;
        setLayout(new MigLayout("", "[]", "[][]"));
        add(label, "cell 0 0,growx");
        add(imageLabel, "cell 0 1,alignx center");
        updateAthleteInfo();

    }

    private void updateAthleteInfo()  {
        SwingUtilities.invokeLater(() -> {
            try {
                SummaryAthlete athlete = stravaService.getCurrentAthlete();
                updateProfileData(athlete);
            } catch (StravaApiException e) {
                SwingUtils.errorMsg(e.getMessage());
            }
        });
    }

    private void updateProfileData(SummaryAthlete athlete) {
        label.setText(athlete.getFirstname() + " " + athlete.getLastname());
        try {
            String profilePicFilename = athlete.getProfileMedium();
            BufferedImage img = ImageIO.read(new URL(profilePicFilename));
            imageLabel.setIcon(new ImageIcon(img));
        } catch (IOException e) {
            imageLabel.setText("Couldn't get profile picture");
        }
    }

}
