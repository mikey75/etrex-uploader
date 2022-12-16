package net.wirelabs.etrex.uploader.gui.settings;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;
import net.wirelabs.etrex.uploader.gui.map.MapType;

import javax.swing.*;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class MapsSettingsPanel extends BorderedPanel {

    JLabel lblDefaultMap = new JLabel("Default map:");
    JComboBox<MapType> defaultMapCombo = new JComboBox<>();
    JLabel lblThunderstormApiKey = new JLabel("ThunderForest API key:");
    JTextField thunderforestApiKey = new JTextField();


    public MapsSettingsPanel(AppConfiguration configuration) {
        super("Maps");
        setLayout(new MigLayout("", "[][grow]", "[][]"));
        add(lblDefaultMap, "cell 0 0,alignx trailing");
        add(defaultMapCombo, "cell 1 0,growx");

        defaultMapCombo.setModel(new DefaultComboBoxModel<>(MapType.values()));
        add(lblThunderstormApiKey, "cell 0 1,alignx trailing");

        add(thunderforestApiKey, "cell 1 1,growx");

    }
}
