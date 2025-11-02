package net.wirelabs.etrex.uploader.gui.desktop.stravapanel.activitiestable;


import com.strava.model.LatLng;
import com.strava.model.StreamSet;
import com.strava.model.SummaryActivity;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.gui.common.base.BaseEventAwarePanel;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.EventBus;
import net.wirelabs.eventbus.IEventType;
import net.wirelabs.jmaps.map.geo.Coordinate;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static net.wirelabs.etrex.uploader.common.Constants.DEFAULT_MAP_HOME_LOCATION;
import static net.wirelabs.etrex.uploader.common.EventType.MAP_DISPLAY_TRACK;
import static net.wirelabs.etrex.uploader.common.EventType.MAP_RESET;
import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.CENTER;
import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.cell;

@Slf4j
public class StravaActivitiesPanel extends BaseEventAwarePanel {

    private final JScrollPane scrollPane = new JScrollPane();
    private final ActivitiesTable activitiesTable = new ActivitiesTable();
    private final JLabel poweredByImageLabel = new JLabel();
    private final JButton btnPrevPage = new JButton("<");
    private final JButton btnNextPage = new JButton(">");
    private final StravaClient stravaClient;
    private final AppConfiguration configuration;
    private int page = 1;


    public StravaActivitiesPanel(StravaClient stravaClient) {
        super("Strava", "","[grow][]","[grow][grow]");
        this.configuration = stravaClient.getAppConfiguration();
        this.stravaClient = stravaClient;
        createVisualComponent();
        updateActivities(page);
    }

    private void createVisualComponent() {
        add(scrollPane, cell(0,0,2,1).grow());
        add(btnPrevPage, cell(0,1).flowX());
        add(btnNextPage, cell(0,1));


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
        poweredByImageLabel.setIcon(poweredBy);
        add(poweredByImageLabel, cell(1,1).alignY(CENTER));
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

        SwingUtilities.invokeLater(() -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                List<SummaryActivity> activities = stravaClient.getCurrentAthleteActivities(page, configuration.getPerPage());
                activitiesTable.setData(activities);
                // always select first activity on a page
                selectFirstActivityOnPage();
            } catch (StravaException e) {
                SwingUtils.errorMsg(e.getMessage());
            } catch (ArrayIndexOutOfBoundsException e) {
                SwingUtils.infoMsg("No more activities, use [<] button to return");
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            // ensure the panel is finally redrawn (some os gfx drivers make it weirdly flicker or mess up)
            repaint();
        });
    }

    private void selectFirstActivityOnPage() {
        activitiesTable.requestFocus();
        if (activitiesTable.getModel().getValueAt(0,0) != null) {
            activitiesTable.changeSelection(0, 0, false, false);
        }
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
            StreamSet streamSet = stravaClient.getActivityStreams(selectedActivity.getId(), "latlng,altitude", true);

            List<LatLng> coords = streamSet.getLatlng().getData();

            // convert strava coords to jmaps coordinates
            List<Coordinate> jmapsCoords = coords.stream()
                    .map(sc -> new Coordinate(sc.get(1), sc.get(0)))
                    .toList();

            if (!jmapsCoords.isEmpty()) {
                EventBus.publish(MAP_DISPLAY_TRACK, jmapsCoords);
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
