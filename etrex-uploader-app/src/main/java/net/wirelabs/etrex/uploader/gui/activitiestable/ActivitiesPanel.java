package net.wirelabs.etrex.uploader.gui.activitiestable;


import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.eventbus.Event;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.common.utils.ThreadUtils;
import net.wirelabs.etrex.uploader.gui.components.EventAwarePanel;

import net.wirelabs.etrex.uploader.strava.model.SummaryActivity;
import net.wirelabs.etrex.uploader.strava.service.IStravaService;
import net.wirelabs.etrex.uploader.strava.client.StravaException;
import net.wirelabs.etrex.uploader.gui.map.MapUtil;




@Slf4j
public class ActivitiesPanel extends EventAwarePanel {

    private final JScrollPane scrollPane = new JScrollPane();
    private final ActivitiesTable activitiesTable = new ActivitiesTable();
    private final JButton btnPrevPage = new JButton("<");
    private final JButton btnNextPage = new JButton(">");
    private final IStravaService stravaService;
    private int page = 1;

    public ActivitiesPanel(IStravaService stravaService) {
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

        activitiesTable.addSelectionListener(this::drawPolyLineTrackFromActivity);
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
                SwingUtilities.invokeLater(()-> activitiesTable.setData(activities));
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (StravaException e) {
                SwingUtils.errorMsg(e.getMessage());
            }
        });
    }

    private void drawPolyLineTrackFromActivity(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting()) {
            MapUtil.drawTrackFromActivity(activitiesTable.getActivityAtRow(activitiesTable.getSelectedRow()));
        }
    }


}
