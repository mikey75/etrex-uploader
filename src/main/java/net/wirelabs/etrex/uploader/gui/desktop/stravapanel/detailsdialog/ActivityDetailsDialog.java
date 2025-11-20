package net.wirelabs.etrex.uploader.gui.desktop.stravapanel.detailsdialog;

import com.strava.model.DetailedActivity;
import com.strava.model.SummaryActivity;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.gui.common.base.BaseDialog;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.utils.SwingUtils;

import javax.swing.*;

import java.awt.*;
import java.util.Optional;

import static net.wirelabs.etrex.uploader.gui.desktop.stravapanel.activitiestable.StravaActivitiesPanel.*;


@Slf4j
public class ActivityDetailsDialog extends BaseDialog {


    private final DetailsPanel detailsPanel = new DetailsPanel();
    private final DesctiptionPanel descriptionPanel = new DesctiptionPanel();
    private final PhotosPanel photosPanel  = new PhotosPanel();
    private final SummaryActivity selectedActivity;
    private final StravaClient stravaClient;

    public ActivityDetailsDialog(SummaryActivity selectedActivity, StravaClient stravaClient) {

        super("Activity Details","", "[49%:n:49%][49%:n:49%]", "[30px:n:50px][30%:n:30%,grow][grow]");
        setMinimumSize(new Dimension(1024, 768));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.selectedActivity = selectedActivity;
        this.stravaClient = stravaClient;
        add(descriptionPanel, "cell 0 1,grow");
        add(detailsPanel, "cell 1 1,grow");
        add(photosPanel, "cell 0 2 2 1,grow");
        SwingUtils.centerComponent(this);
        setVisible(true);
    }

    public void populate() {
        Optional<DetailedActivity> detailedActivity = getAndCacheDetailedActivity(selectedActivity, stravaClient);

        if (detailedActivity.isPresent()) {
            descriptionPanel.setDescription(detailedActivity.get());
            detailsPanel.setDetails(detailedActivity.get());
            photosPanel.getPhotos(stravaClient,detailedActivity.get().getId(),200);

            JLabel activityNameLabel = new JLabel(selectedActivity.getName());
            activityNameLabel.setFont(new Font(activityNameLabel.getFont().getName(), Font.BOLD, 16));

            add(activityNameLabel, "cell 0 0 2 1,alignx center");
        } else {
            SwingUtils.errorMsg("Could not load activity details");
            dispose();
        }

    }

    private Optional<DetailedActivity> getAndCacheDetailedActivity(SummaryActivity selectedActivity, StravaClient stravaClient) {
        try {
            DetailedActivity detailedActivity;
            // cache detailed activities to save on strava requests
            // if checking activity details again (after first time)
            if (!getDetailedActivityCache().containsKey(selectedActivity.getId())) {
                detailedActivity = stravaClient.getActivityById(selectedActivity.getId());
                getDetailedActivityCache().put(selectedActivity.getId(), detailedActivity);
            } else {
                // cached activity found - use it
                detailedActivity = getDetailedActivityCache().get(selectedActivity.getId());
            }
            return Optional.of(detailedActivity);
        } catch (StravaException e) {
            log.error("Could not load activity details: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
