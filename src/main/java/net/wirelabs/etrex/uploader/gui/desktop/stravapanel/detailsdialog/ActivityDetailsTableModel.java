package net.wirelabs.etrex.uploader.gui.desktop.stravapanel.detailsdialog;

import com.strava.model.DetailedActivity;

import javax.swing.table.*;

import static net.wirelabs.etrex.uploader.utils.DateAndUnitConversionUtil.*;

public class ActivityDetailsTableModel extends AbstractTableModel {

    private static final String NOT_RECORDED = "not recorded";
    private final DetailedActivity activity;
    private static final int COLUMN_COUNT = 4;
    private static final int ROW_COUNT = 10;
    private final transient Object[][] data = new Object[ROW_COUNT][COLUMN_COUNT];

    public ActivityDetailsTableModel(DetailedActivity detailedActivity) {
        this.activity = detailedActivity;
    }

    @Override
    public int getRowCount() {
        return ROW_COUNT;
    }

    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    public void populate() {


        data[0][0] = "Date:";
        data[0][1] = offsetDateTimeToLocalAsString(activity.getStartDateLocal(), true);

        data[0][2] = "Distance:";
        data[0][3] = String.format("%.2f", activity.getDistance() / 1000) + " km";

        data[1][0] = "Elapsed time:";
        data[1][1] = secondsToTimeAsString(activity.getElapsedTime());

        data[1][2] = "Moving time:";
        data[1][3] = secondsToTimeAsString(activity.getMovingTime());

        data[2][0] = "Avg speed:";
        data[2][1] = metersPerSecToKilometersPerHourAsString(activity.getAverageSpeed()) + " km/h";

        data[2][2] = "Max speed:";
        data[2][3] = metersPerSecToKilometersPerHourAsString(activity.getMaxSpeed()) + " km/h";

        data[3][0] = "Elevation gain:";
        data[3][1] = activity.getTotalElevationGain() + " meters";

        data[3][2] = "Gear:";
        data[3][3] = (activity.getGear() != null) ? activity.getGear().getName() : NOT_RECORDED;

        data[4][0] = "Kudoers:";
        data[4][1] = activity.getKudosCount();

        data[4][2] = "Achievements:";
        data[4][3] = activity.getAchievementCount();

        data[5][0] = "Max power:";
        data[5][1] = (activity.getMaxWatts()!=null) ? activity.getMaxWatts() + " Watts" : NOT_RECORDED;

        data[5][2] = "Avg power:";
        data[5][3] = (activity.getWeightedAverageWatts()!=null) ? activity.getAverageWatts() + " Watts" : NOT_RECORDED;

        data[6][0] = "Device:";
        data[6][1] = (activity.getDeviceName()!=null) ? activity.getDeviceName() : NOT_RECORDED;




    }
}
