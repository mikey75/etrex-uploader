package net.wirelabs.etrex.uploader.gui.components;

import net.miginfocom.swing.MigLayout;

public interface LayoutConstraintsSettable {
    default void setConstraints(MigLayout layout, String layoutConstraints, String columnConstraints, String rowConstraints) {
        layout.setLayoutConstraints(layoutConstraints);
        layout.setColumnConstraints(columnConstraints);
        layout.setRowConstraints(rowConstraints);
    }
}
