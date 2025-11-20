package net.wirelabs.etrex.uploader.gui.desktop.stravapanel.detailsdialog;

import com.strava.model.DetailedActivity;
import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;

import javax.swing.*;

public class DesctiptionPanel extends BasePanel {

    private final JTextPane textPane = new JTextPane();
    private final JScrollPane scrollPane = new JScrollPane();

    public DesctiptionPanel() {
        super("Description","", "[grow]", "[grow]");


        add(scrollPane, "cell 0 0,grow");

        textPane.setEditable(false);
        scrollPane.setViewportView(textPane);

        // set scrolling to top
        textPane.setCaretPosition(0);
        setVisible(true);

    }
    public void setDescription(DetailedActivity activity) {
        textPane.setText(activity.getDescription());
    }
}
