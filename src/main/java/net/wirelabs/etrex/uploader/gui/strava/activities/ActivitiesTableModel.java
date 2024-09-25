package net.wirelabs.etrex.uploader.gui.strava.activities;

import net.wirelabs.etrex.uploader.common.utils.DateAndUnitConversionUtil;
import com.strava.model.SummaryActivity;


import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created 9/9/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class ActivitiesTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Title", "Date", "Sport","Time","Distance","Elevation","Speed avg", "Achievements"};

    private transient Object[][] data = new Object[][]{};
    private transient List<SummaryActivity> activitiesList = new ArrayList<>();

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public SummaryActivity getActivityAtRow(int rowIndex) {
        if (rowIndex<0) rowIndex=0;
        return activitiesList.get(rowIndex);
    }

    public void populate(List<SummaryActivity> activities) {
        // sort by date
        activities.sort(Comparator.comparing(SummaryActivity::getStartDateLocal).reversed());

        int rowIdx = 0;
        this.activitiesList = activities;
        data = new Object[activities.size()][columnNames.length];
        for (SummaryActivity act : activities) {
            data[rowIdx][0] = act.getName();
            data[rowIdx][1] = DateAndUnitConversionUtil.offsetDateTimeToLocalAsString(act.getStartDateLocal());
            data[rowIdx][2] = act.getSportType();
            data[rowIdx][3] = DateAndUnitConversionUtil.secondsToTimeAsString(act.getElapsedTime());
            data[rowIdx][4] = act.getDistance() / 1000;
            data[rowIdx][5] = act.getTotalElevationGain();
            data[rowIdx][6] = DateAndUnitConversionUtil.metersPerSecToKilometersPerHourAsString(act.getAverageSpeed());
            data[rowIdx][7] = act.getAchievementCount();
            rowIdx++;
        }
    }
}
