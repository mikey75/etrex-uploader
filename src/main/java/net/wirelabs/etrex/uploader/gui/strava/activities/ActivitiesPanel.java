package net.wirelabs.etrex.uploader.gui.strava.activities;


import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.StravaException;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.eventbus.Event;
import net.wirelabs.etrex.uploader.common.eventbus.EventBus;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.common.utils.ThreadUtils;
import net.wirelabs.etrex.uploader.gui.components.EventAwarePanel;
import net.wirelabs.etrex.uploader.strava.model.SummaryActivity;
import net.wirelabs.etrex.uploader.strava.service.StravaService;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.Cursor;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static net.wirelabs.etrex.uploader.common.EventType.MAP_DISPLAY_TRACK;

@Slf4j
public class ActivitiesPanel extends EventAwarePanel {

    private final JScrollPane scrollPane = new JScrollPane();
    private final ActivitiesTable activitiesTable = new ActivitiesTable();
    private final JButton btnPrevPage = new JButton("<");
    private final JButton btnNextPage = new JButton(">");
    private final StravaService stravaService;
    private int page = 1;

    public ActivitiesPanel(StravaService stravaService) {
        this.stravaService = stravaService;
        createVisualComponent();
        updateActivities();
    }

    @Override
    protected void onEvent(Event evt) {
        if (evt.getEventType() == EventType.ACTIVITY_SUCCESSFULLY_UPLOADED) {
            updateActivities();
        }
    }

    @Override
    protected Collection<EventType> subscribeEvents() {
        return Arrays.asList(EventType.ACTIVITY_SUCCESSFULLY_UPLOADED);
    }

    private void createVisualComponent() {
        setBorder(new TitledBorder("Strava"));
        setLayout(new MigLayout("", "[grow]", "[grow][]"));
        add(scrollPane, "cell 0 0,grow");
        add(btnPrevPage, "flowx,cell 0 1");
        add(btnNextPage, "cell 0 1");

        scrollPane.setViewportView(activitiesTable);

        btnPrevPage.addActionListener(e -> decreasePageAndLoadActivities());

        btnNextPage.addActionListener(e -> increasePageAndLoadActivities());

        activitiesTable.addSelectionListener(this::drawTrack);
    }

    private void increasePageAndLoadActivities() {
        page++;
        updateActivities();
    }

    private void decreasePageAndLoadActivities() {
        if (page > 1) {
            page--;
            updateActivities();
        }
    }

    private void updateActivities() {
        ThreadUtils.runAsync(() -> {
            try {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                List<SummaryActivity> activities = stravaService.getCurrentAthleteActivities(page, 30);
                activitiesTable.setData(activities);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (StravaException e) {
                SwingUtils.errorMsg(e.getMessage());
            }
        });
    }

    private void drawTrack(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting()) {
            SummaryActivity selectedActivity = activitiesTable.getActivityAtRow(activitiesTable.getSelectedRow());
            String polyLine = selectedActivity.getMap().getSummaryPolyline();
            if (selectedActivity.getMap().getSummaryPolyline() != null) {
                EventBus.publish(MAP_DISPLAY_TRACK, polyLine);
            }
        }
    }


}
