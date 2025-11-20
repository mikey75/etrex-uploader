package net.wirelabs.etrex.uploader.gui.desktop.stravapanel.detailsdialog;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class BoldingRenderer extends JLabel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((String) value);
        setFont(new Font(getFont().getName(),Font.BOLD,getFont().getSize()));
        return this;
    }
}
