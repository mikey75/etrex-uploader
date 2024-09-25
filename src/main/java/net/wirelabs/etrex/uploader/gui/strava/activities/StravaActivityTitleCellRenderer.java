package net.wirelabs.etrex.uploader.gui.strava.activities;

import lombok.Getter;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Objects;

/**
 * Created 12/23/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaActivityTitleCellRenderer extends JLabel implements TableCellRenderer {

    @Getter
    private final ImageIcon stravaIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/strava_logo_mini.png")));

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        setText((String) value);
        setIcon(stravaIcon);
        setOpaque(true);

        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        return this;

    }
}
