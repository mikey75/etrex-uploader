package net.wirelabs.etrex.uploader.gui.components;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

import java.util.Map;

public class LaFComboRenderer extends BasicComboBoxRenderer {

    private final Map<String, String> names;

    public LaFComboRenderer(Map<String,String> names) {
        this.names = names;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String classname = (String) value;
        setText(names.get(classname));
        return this;
    }
}
