package net.wirelabs.etrex.uploader.gui.desktop.stravapanel.detailsdialog;

import com.strava.model.DetailedActivity;
import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;

import javax.swing.*;

import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.cell;

public class DescriptionPanel extends BasePanel {

    private final JTextPane textPane = new JTextPane();
    private final JScrollPane scrollPane = new JScrollPane();

    public DescriptionPanel() {
        super("Description","", "[grow]", "[grow]");

        add(scrollPane, cell(0,0).grow());
        textPane.setEditable(false);
        scrollPane.setViewportView(textPane);
        setVisible(true);

    }
    public void setDescription(DetailedActivity activity) {
        textPane.setText(activity.getDescription());
        // set scrolling to top
        textPane.setCaretPosition(0);
    }
}
