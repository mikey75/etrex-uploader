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

    private final AppConfiguration configuration;
    private final JComboBox<MapType> defaultMapCombo = new JComboBox<>();
    private final JLabel lblDefaultMap = new JLabel("Default map:");
    private final JLabel lblApiKeys = new JLabel("Api keys:");
    private final ApiKeyManager apiKeyManager;


    public MapsSettingsPanel(AppConfiguration configuration) {
        super("Maps");
        this.configuration = configuration;
        apiKeyManager = new ApiKeyManager(configuration);

        setLayout(new MigLayout("", "[][grow]", "[][grow]"));
        add(lblDefaultMap, "cell 0 0,alignx trailing");
        add(defaultMapCombo, "cell 1 0,growx");
        add(lblApiKeys, "cell 0 1");
        add(apiKeyManager, "cell 1 1,grow");

        defaultMapCombo.setModel(new DefaultComboBoxModel<>(MapType.values()));
        loadConfiguration();
    }

    private void loadConfiguration() {
        defaultMapCombo.setSelectedItem(configuration.getDefaultMapType());
    }

    public void updateConfiguration() {
        configuration.setDefaultMapType((MapType) defaultMapCombo.getSelectedItem());
        apiKeyManager.saveApiKeys();
    }

}
