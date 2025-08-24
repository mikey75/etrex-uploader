package net.wirelabs.etrex.uploader.gui.desktop.stravapanel.activitiestable;

import com.strava.model.SummaryActivity;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * Created 9/12/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class ActivitiesTable extends JTable {

    private final ActivitiesTableModel model;

    public ActivitiesTable() {
        this(new ArrayList<>());
    }

    public ActivitiesTable(List<SummaryActivity> activities) {
        this(new ActivitiesTableModel());
        setData(activities);
    }

    public ActivitiesTable(ActivitiesTableModel model) {
        this.model = model;
        setModel(model);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void setData(List<SummaryActivity> list) {
        model.populate(list);
        model.fireTableDataChanged();
    }

    public SummaryActivity getActivityAtRow(int rowIndex) {
        return model.getActivityAtRow(rowIndex);
    }

    public void addSelectionListener(ListSelectionListener listSelectionListener) {
        getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    @Override
    public ActivitiesTableModel getModel() {
        return model;
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {

        final Color shadeColor = getShadeColor();

        Component comp = super.prepareRenderer(renderer, row, column);

        if (!isCellSelected(row, column)) {
            comp.setBackground(row % 2 == 0 ? getBackground() : shadeColor);
        }
        return comp;
    }

    @Override
    public boolean editCellAt(int row, int column) {
        return false;
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        return false;
    }

        private Color getShadeColor() {
        float[] hsb = Color.RGBtoHSB(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), null);
        float reducedBrightness = hsb[2] * 0.88f;
        reducedBrightness = Math.max(0f, Math.min(1f, reducedBrightness));
        return Color.getHSBColor(hsb[0], hsb[1], reducedBrightness);
    }
}
