package net.wirelabs.etrex.uploader.gui.components.choosemapcombo;

import net.wirelabs.jmaps.map.readers.MapReader;
import net.wirelabs.jmaps.model.map.MapDocument;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.io.File;

public class ChooseMapComboRenderer extends BasicComboBoxRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        // there is always at least one item so no need to check emptiness
        File item = (File) value;
        MapDocument m = MapReader.loadMapDefinitionFile(item);
        setText(m.getMap().getName());
        return this;
    }
}
