package net.wirelabs.etrex.uploader.gui.strava.account;


import com.strava.model.ActivityStats;
import com.strava.model.SummaryAthlete;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.gui.components.BasePanel;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.DateAndUnitConversionUtil;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.common.utils.ThreadUtils;
import net.wirelabs.etrex.uploader.gui.settings.SettingsDialog;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.URI;

/**
 * Created 9/12/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class UserAccountPanel extends BasePanel {

    final JLabel athleteName = new JLabel();
    final JLabel athletePicture = new JLabel();
    final JLabel ytdRides = new JLabel();
    final JLabel ytdDist = new JLabel();
    final JLabel ytdTime = new JLabel();
    final JLabel totalRides = new JLabel();
    final JLabel totalDist = new JLabel();
    final JLabel totalTime = new JLabel();

    private final StravaClient stravaClient;
    private final JButton btnSettings = new JButton("Settings");
    private final AppConfiguration configuration;
    private final JLabel lblYtdRides = new JLabel("YTD rides:");
    private final JLabel lblYtdDist = new JLabel("YTD distance:");
    private final JLabel lblYtdTime = new JLabel("YTD time:");
    private final JLabel lblTotalRides = new JLabel("Total rides:");
    private final JLabel lblTotalDist = new JLabel("Total distance:");
    private final JLabel lblTotalTime = new JLabel("Total time:");
    private final JLabel lblStatistics = new JLabel("Statistics");
    private final ApiUsagePanel apiUsagePanel;


    public UserAccountPanel(StravaClient stravaClient, AppConfiguration configuration) {
        super("My profile","","[grow][]","[][][][][][][][][][][][grow][grow,bottom]");
        this.stravaClient = stravaClient;
        this.configuration = configuration;
        apiUsagePanel = new ApiUsagePanel(configuration);
        initVisualComponent();
        ThreadUtils.runAsync(() -> {
            getUserAccountData();
            updateAthleteStats();
        });

    }

    private void initVisualComponent() {
        add(athleteName, "cell 0 1 2 1,alignx center");
        add(athletePicture, "cell 0 0 2 1,alignx center");
        add(lblStatistics, "cell 0 2 2 1");
        add(new JSeparator(), "cell 0 3 2 1,growx");
        add(lblYtdRides, "cell 0 4");
        add(ytdRides, "cell 1 4");
        add(lblYtdDist, "cell 0 5");
        add(ytdDist, "cell 1 5");
        add(lblYtdTime, "cell 0 6");
        add(ytdTime, "cell 1 6");
        add(new JSeparator(), "cell 0 7 2 1,growx");
        add(lblTotalRides, "cell 0 8");
        add(totalRides, "cell 1 8");
        add(lblTotalDist, "cell 0 9");
        add(totalDist, "cell 1 9");
        add(lblTotalTime, "cell 0 10");
        add(totalTime, "cell 1 10");

        add(apiUsagePanel, "cell 0 11 2 1,grow");
        add(btnSettings, "cell 0 12 2 1,growx");

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
            athlete = stravaClient.getCurrentAthlete();
            String profilePicFilename = athlete.getProfileMedium();
            if (profilePicFilename != null) {
                img = ImageIO.read(URI.create(profilePicFilename).toURL());
            }
        } catch (StravaException | IOException e) {
            log.error("Error getting user profile data {}",e.getMessage(),e);
        }

        setAthleteFullName(athlete);
        setAthletePicture(img);

    }

    private void updateAthleteStats() {
        try {
            SummaryAthlete athlete = stravaClient.getCurrentAthlete();
            ActivityStats stats = stravaClient.getAthleteStats(athlete.getId());

            ytdDist.setText(Math.round(stats.getYtdRideTotals().getDistance() / 1000F) + " km");
            ytdRides.setText(String.valueOf(stats.getYtdRideTotals().getCount()));
            ytdTime.setText(DateAndUnitConversionUtil.secondsToHoursAsString(stats.getYtdRideTotals().getElapsedTime()) + " hours");

            totalDist.setText(Math.round(stats.getAllRideTotals().getDistance() / 1000F) + " km");
            totalRides.setText(String.valueOf(stats.getAllRideTotals().getCount()));
            totalTime.setText(DateAndUnitConversionUtil.secondsToHoursAsString(stats.getAllRideTotals().getElapsedTime()) + " hours");
        } catch (StravaException e) {
            log.error("Error getting user stats {}", e.getMessage(), e);
        }
    }

    private void setAthletePicture(BufferedImage img) {
        if (img == null) {
            SwingUtilities.invokeLater(() -> athletePicture.setText("Couldn't get athlete picture"));
        } else {
            SwingUtilities.invokeLater(() -> athletePicture.setIcon(new ImageIcon(img)));
        }
    }

    private void setAthleteFullName(SummaryAthlete athlete) {
        if (athlete == null) {
            SwingUtilities.invokeLater(() -> athleteName.setText("Couldn't get athlete name"));
        } else {
            String athleteFullName = athlete.getFirstname() + " " + athlete.getLastname();
            SwingUtilities.invokeLater(() -> athleteName.setText(athleteFullName));
        }
    }
}
