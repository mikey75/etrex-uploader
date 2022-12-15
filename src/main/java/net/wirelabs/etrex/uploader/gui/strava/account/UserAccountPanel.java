package net.wirelabs.etrex.uploader.gui.strava.account;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;


import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.eventbus.Event;
import net.wirelabs.etrex.uploader.common.utils.ThreadUtils;
import net.wirelabs.etrex.uploader.gui.components.EventAwarePanel;
import net.wirelabs.etrex.uploader.strava.client.RateLimitInfo;
import net.wirelabs.etrex.uploader.strava.client.StravaException;
import net.wirelabs.etrex.uploader.strava.model.SummaryAthlete;
import net.wirelabs.etrex.uploader.strava.service.IStravaService;

/**
 * Created 9/12/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class UserAccountPanel extends EventAwarePanel {

    final JLabel athleteName = new JLabel();
    final JLabel athletePicture = new JLabel();
    final JLabel apiUsageHourly = new JLabel();
    final JLabel apiUsageDaily = new JLabel();
    private final IStravaService stravaService;
    
    public UserAccountPanel(IStravaService stravaService) {
        this.stravaService = stravaService;
        initVisualComponent();
        ThreadUtils.runAsync(this::getUserAccountData);
    }

    private void initVisualComponent() {
        setBorder(new TitledBorder("My profile"));
        //setBorderTitle("My profile");
        setLayout(new MigLayout("", "[]", "[][][][]"));
        add(athleteName, "cell 0 0,growx");
        add(athletePicture, "cell 0 1,alignx center");
        add(apiUsageDaily,"cell 0 2");
        add(apiUsageHourly, "cell 0 3");
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

    @Override
    protected void onEvent(Event evt) {
        if (evt.getEventType() == EventType.RATELIMIT_INFO_UPDATE) {
            RateLimitInfo rinfo = (RateLimitInfo) evt.getPayload();
            SwingUtilities.invokeLater(() -> {
                apiUsageDaily.setText("API daily (" + rinfo.getCurrentDaily() + "/" + rinfo.getAllowedDaily() + ")");
                apiUsageHourly.setText("API 15min (" + rinfo.getCurrentHourly() + "/" + rinfo.getAllowedHourly() + ")");
            });
        }
    }

    @Override
    protected Collection<EventType> subscribeEvents() {
        return List.of(EventType.RATELIMIT_INFO_UPDATE);
    }
}
