package net.wirelabs.etrex.uploader.gui.desktop.stravapanel.activitiestable;

import lombok.Getter;
import net.wirelabs.etrex.uploader.gui.common.components.TwoIconTextLabel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Objects;

/**
 * Created 12/23/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaActivityTitleCellRenderer extends TwoIconTextLabel implements TableCellRenderer {

    @Getter
    private final ImageIcon stravaIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/strava_logo_mini.png")));
    private final ImageIcon detailsIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/search-mini.png")));

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setFirstIcon(stravaIcon);
        setSecondIcon(detailsIcon);
        setText(String.valueOf(value));

        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        return this;

    }
}
