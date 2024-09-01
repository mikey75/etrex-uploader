package net.wirelabs.etrex.uploader.gui.strava.activities;

import net.wirelabs.etrex.uploader.strava.model.SummaryActivity;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
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
        return super.prepareRenderer(renderer, row, column);
    }

    @Override
    public boolean editCellAt(int row, int column) {
        return false;
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        return false;
    }
}
