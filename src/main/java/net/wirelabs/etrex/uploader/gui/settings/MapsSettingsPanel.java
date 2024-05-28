package net.wirelabs.etrex.uploader.gui.settings;


import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.eventbus.EventBus;
import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;
import net.wirelabs.etrex.uploader.gui.components.choosemapcombo.ChooseMapComboBox;
import net.wirelabs.etrex.uploader.gui.components.ColorChooserTextField;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class MapsSettingsPanel extends BorderedPanel {

    private final AppConfiguration configuration;
    private final ChooseMapComboBox newMaps;

    private final JLabel lblDefaultMap = new JLabel("Default map:");
    private final JLabel lblTilerThreads = new JLabel("Threads:");
    private final JLabel lblColor = new JLabel("Track color:");


    private final JTextField threads = new JTextField();
    private final ColorChooserTextField colorChooserTextField = new ColorChooserTextField();

    public MapsSettingsPanel(AppConfiguration configuration) {
        super("Maps");
        this.configuration = configuration;
        newMaps = new ChooseMapComboBox();

        setLayout(new MigLayout("", "[][][][grow]", "[][][grow]"));
        add(lblDefaultMap, "cell 0 0,alignx trailing");
        add(newMaps, "cell 1 0, growx");
        add(lblTilerThreads, "cell 2 0,alignx trailing");
        add(threads, "cell 3 0,growx");

        add(lblColor, "cell 0 1, alignx trailing");
        add(colorChooserTextField, "cell 1 1, growx");
        loadConfiguration();
    }

    private void loadConfiguration() {
        newMaps.setSelectedItem(configuration.getMapFile().toFile());
        threads.setText(String.valueOf(configuration.getTilerThreads()));
        colorChooserTextField.setText(configuration.getMapTrackColor());
    }

    public void updateConfiguration() {
        configuration.setTilerThreads(Integer.parseInt(threads.getText()));

        if (newMaps.getSelectedItem() != null) {
            configuration.setMapFile(((File) newMaps.getSelectedItem()).toPath());
        }
        updateTrackColor();
    }

    private void updateTrackColor() {
        configuration.setMapTrackColor(colorChooserTextField.getText());
        EventBus.publish(EventType.TRACK_COLOR_CHANGED, Color.decode(colorChooserTextField.getText()));
    }

}
