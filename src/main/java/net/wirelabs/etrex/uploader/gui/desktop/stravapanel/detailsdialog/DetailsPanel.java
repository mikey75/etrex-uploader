package net.wirelabs.etrex.uploader.gui.desktop.stravapanel.detailsdialog;

import com.strava.model.DetailedActivity;
import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;

import javax.swing.*;
import javax.swing.table.*;

public class DetailsPanel extends BasePanel {

    private final JTable table = new JTable();

    public DetailsPanel() {
        super("Activity details","", "[grow]", "[grow]");
        JScrollPane scrollPane = new JScrollPane();

        setLayout(layout);
        scrollPane.setViewportView(table);
        add(scrollPane, "cell 0 0,grow");

        table.setTableHeader(null);
        table.setEnabled(false);
        setVisible(true);


    }

    public void setDetails(DetailedActivity activity) {
        ActivityDetailsTableModel model = new ActivityDetailsTableModel(activity);
        table.setModel(model);
        model.populate();
        applyRendererToTable();
        repaint();
    }

    private void applyRendererToTable() {

        TableCellRenderer cellRenderer = new BoldingRenderer();

        TableColumn col0 = table.getColumnModel().getColumn(0);
        TableColumn col2 = table.getColumnModel().getColumn(2);
        col0.setCellRenderer(cellRenderer);
        col2.setCellRenderer(cellRenderer);

    }
}
