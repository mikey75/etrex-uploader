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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Cursor;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static net.wirelabs.etrex.uploader.common.EventType.MAP_DISPLAY_TRACK;

@Slf4j
public class ActivitiesPanel extends EventAwarePanel {

    private final JScrollPane scrollPane = new JScrollPane();
    private final ActivitiesTable activitiesTable = new ActivitiesTable();
    private final JLabel pwrdByImageLabel = new JLabel();
    private final JButton btnPrevPage = new JButton("<");
    private final JButton btnNextPage = new JButton(">");
    private final StravaService stravaService;
    private int page = 1;

    public ActivitiesPanel(StravaService stravaService) {

        this.stravaService = stravaService;
        createVisualComponent();
        updateActivities(page);
    }

    private void createVisualComponent() {
        setBorder(new TitledBorder("Strava"));
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
        ImageIcon poweredBy = new ImageIcon(Objects.requireNonNull(getClass().getResource("/pwrdBystrava.png")));
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
            SummaryActivity selectedActivity = activitiesTable.getActivityAtRow(activitiesTable.getSelectedRow());
            String polyLine = selectedActivity.getMap().getSummaryPolyline();
            if (polyLine != null) {
                EventBus.publish(MAP_DISPLAY_TRACK, polyLine);
            }
        }
    }

    @Override
    protected void onEvent(Event evt) {
        if (evt.getEventType() == EventType.ACTIVITY_SUCCESSFULLY_UPLOADED) {
            updateActivities(page);
        }
    }

    @Override
    protected Collection<EventType> subscribeEvents() {
        return List.of(EventType.ACTIVITY_SUCCESSFULLY_UPLOADED);
    }
}
