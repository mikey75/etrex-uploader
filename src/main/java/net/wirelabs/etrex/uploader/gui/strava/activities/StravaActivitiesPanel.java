package net.wirelabs.etrex.uploader.gui.strava.activities;


import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.StravaException;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.common.utils.ThreadUtils;
import net.wirelabs.etrex.uploader.gui.components.EventAwareBorderedPanel;
import net.wirelabs.etrex.uploader.strava.model.LatLng;
import net.wirelabs.etrex.uploader.strava.model.StreamSet;
import net.wirelabs.etrex.uploader.strava.model.SummaryActivity;
import net.wirelabs.etrex.uploader.strava.service.StravaService;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.EventBus;
import net.wirelabs.eventbus.IEventType;
import net.wirelabs.jmaps.map.geo.Coordinate;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.wirelabs.etrex.uploader.common.Constants.DEFAULT_MAP_HOME_LOCATION;
import static net.wirelabs.etrex.uploader.common.EventType.MAP_DISPLAY_TRACK;
import static net.wirelabs.etrex.uploader.common.EventType.MAP_RESET;

@Slf4j
public class StravaActivitiesPanel extends EventAwareBorderedPanel {

    private final JScrollPane scrollPane = new JScrollPane();
    private final ActivitiesTable activitiesTable = new ActivitiesTable();
    private final JLabel pwrdByImageLabel = new JLabel();
    private final JButton btnPrevPage = new JButton("<");
    private final JButton btnNextPage = new JButton(">");
    private final StravaService stravaService;
    private final AppConfiguration configuration;
    private int page = 1;

    public StravaActivitiesPanel(StravaService stravaService, AppConfiguration configuration) {
        super("Strava");
        this.configuration = configuration;
        this.stravaService = stravaService;
        createVisualComponent();
        updateActivities(page);
    }

    private void createVisualComponent() {

        setLayout(new MigLayout("", "[grow][]", "[grow][grow]"));
        add(scrollPane, "cell 0 0 2 1,grow");
        add(btnPrevPage, "flowx,cell 0 1");
        add(btnNextPage, "cell 0 1");


        scrollPane.setViewportView(activitiesTable);
        btnPrevPage.addActionListener(e -> decreasePageAndLoadActivities());
        btnNextPage.addActionListener(e -> increasePageAndLoadActivities());

        activitiesTable.addSelectionListener(this::activitySelected);
        activitiesTable.getTableHeader().setResizingAllowed(false);
        activitiesTable.getTableHeader().setReorderingAllowed(false);

        applyRendererToActivityTitleColumn();
        applyMouseListenerToActivityTitleColumn();
        setPoweredByLogo();

    }

    // this is required by strava api branding guidelines
    private void setPoweredByLogo() {
        ImageIcon poweredBy = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/pwrdBystrava.png")));
        pwrdByImageLabel.setIcon(poweredBy);
        add(pwrdByImageLabel, "cell 1 1,aligny center");
    }

    private void applyMouseListenerToActivityTitleColumn() {
        StravaActivityIconClickListener activityClickListener = new StravaActivityIconClickListener(activitiesTable, 16, 16);
        activitiesTable.addMouseListener(activityClickListener);
        activitiesTable.addMouseMotionListener(activityClickListener);
    }

    private void applyRendererToActivityTitleColumn() {
        TableCellRenderer cellRenderer = new StravaActivityTitleCellRenderer();
        TableColumn titleColumn = activitiesTable.getColumnModel().getColumn(activitiesTable.getModel().findColumn("Title"));
        titleColumn.setCellRenderer(cellRenderer);
        titleColumn.setPreferredWidth(400);
    }

    private void increasePageAndLoadActivities() {
        updateActivities(++page);
    }

    private void decreasePageAndLoadActivities() {
        if (page > 1) {
            updateActivities(--page);
        }
    }

    private void updateActivities(int page) {

        ThreadUtils.runAsync(() -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                List<SummaryActivity> activities = stravaService.getCurrentAthleteActivities(page, 30);
                activitiesTable.setData(activities);

            } catch (StravaException e) {
                SwingUtils.errorMsg(e.getMessage());
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
    }

    /**
     * Activity is selected, emit the event for activity's track painting on map
     * @param event list selection event
     */
    private void activitySelected(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting()) {
            if (activityDoesNotHaveRoute()) {
                warnAndResetMap();
                return;
            }
            if (configuration.isUsePolyLines()) {
                drawTrackFromSummaryPolyLine();
            } else {
                drawTrackFromActivityStream();
            }
        }
    }

    private void warnAndResetMap() {
        SwingUtils.infoMsg("This activity does not contain a track/route\nWill reset map to default!");
        EventBus.publish(MAP_RESET, DEFAULT_MAP_HOME_LOCATION);
    }

    /**
     * check if activity has route, there's always polyline present so if it is empty
     * this means activity has no route/track uploaded
     */
    private boolean activityDoesNotHaveRoute() {

        SummaryActivity summaryActivity = activitiesTable.getActivityAtRow(activitiesTable.getSelectedRow());
        return summaryActivity.getMap().getSummaryPolyline().isEmpty();
    }

    private void drawTrackFromSummaryPolyLine() {

        SummaryActivity selectedActivity = activitiesTable.getActivityAtRow(activitiesTable.getSelectedRow());
        String polyLine = selectedActivity.getMap().getSummaryPolyline();
        if (polyLine != null) {
            EventBus.publish(MAP_DISPLAY_TRACK, polyLine);
        }

    }

    private void drawTrackFromActivityStream() {
        try {
            SummaryActivity selectedActivity = activitiesTable.getActivityAtRow(activitiesTable.getSelectedRow());
            StreamSet streamSet = stravaService.getActivityStreams(selectedActivity.getId(), "latlng,altitude", true);

            List<LatLng> coords = streamSet.getLatlng().getData();

            // convert strava coords to jmaps coordinates
            List<Coordinate> jmapsCoord = coords.stream()
                    .map(sc -> new Coordinate(sc.get(1), sc.get(0))).collect(Collectors.toList());
            if (!jmapsCoord.isEmpty()) {
                EventBus.publish(MAP_DISPLAY_TRACK, jmapsCoord);
            }
        } catch (StravaException e) {
            SwingUtils.errorMsg(e.getMessage());
        }
    }

    @Override
    protected void onEvent(Event evt) {
        if (evt.getEventType() == EventType.ACTIVITY_SUCCESSFULLY_UPLOADED) {
            updateActivities(page);
        }
    }

    @Override
    protected Collection<IEventType> subscribeEvents() {
        return List.of(EventType.ACTIVITY_SUCCESSFULLY_UPLOADED);
    }
}
