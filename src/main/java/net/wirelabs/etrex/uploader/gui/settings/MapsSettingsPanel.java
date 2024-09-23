package net.wirelabs.etrex.uploader.gui.settings;


import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.eventbus.EventBus;
import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;
import net.wirelabs.etrex.uploader.gui.components.choosemapcombo.ChooseMapComboBox;
import net.wirelabs.etrex.uploader.gui.components.ColorChooserTextField;
import net.wirelabs.jmaps.map.geo.Coordinate;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class MapsSettingsPanel extends BorderedPanel {

    public static final String INFO_MSG = "<html>You can write new home position here,<br>but you can also use the map,<br> and select it with double click</html>";
    private final AppConfiguration configuration;
    private final ChooseMapComboBox newMaps = new ChooseMapComboBox();

    private final JLabel lblDefaultMap = new JLabel("Default map:");
    private final JLabel lblTilerThreads = new JLabel("Threads:");
    private final JLabel lblColor = new JLabel("Track color:");
    private final JLabel lblMapHomeLat = new JLabel("Map home lattitude:");
    private final JLabel lblMapHomeLon = new JLabel("Map home longitude:");

    private final JTextField threads = new JTextField();
    private final JTextField mapHomeLat = new JTextField();
    private final JTextField mapHomeLon = new JTextField();
    private final ColorChooserTextField colorChooserTextField = new ColorChooserTextField();

    public MapsSettingsPanel(AppConfiguration configuration) {
        super("Maps");
        this.configuration = configuration;
        setLayout(new MigLayout("", "[][][][grow]", "[][][grow]"));
        add(lblDefaultMap, "cell 0 0,alignx trailing");
        add(newMaps, "cell 1 0, growx");
        add(lblTilerThreads, "cell 2 0,alignx trailing");
        add(threads, "cell 3 0,growx");

        add(lblColor, "cell 0 1, alignx trailing");
        add(colorChooserTextField, "cell 1 1, growx");

        add(lblMapHomeLon,"cell 0 2, alignx trailing");
        add(mapHomeLon, "cell 1 2, growx");

        add(lblMapHomeLat,"cell 0 3, alignx trailing");
        add(mapHomeLat, "cell 1 3, growx");

        mapHomeLon.setToolTipText(INFO_MSG);
        mapHomeLat.setToolTipText(INFO_MSG);
        loadConfiguration();
    }

    private void loadConfiguration() {
        newMaps.setSelectedItem(configuration.getMapFile().toFile());
        threads.setText(String.valueOf(configuration.getTilerThreads()));
        colorChooserTextField.setText(configuration.getMapTrackColor());
        mapHomeLon.setText(String.valueOf(configuration.getMapHomeLongitude()));
        mapHomeLat.setText(String.valueOf(configuration.getMapHomeLattitude()));
    }

    public void updateConfiguration() {
        configuration.setTilerThreads(Integer.parseInt(threads.getText()));

        if (newMaps.getSelectedItem() != null) {
            configuration.setMapFile(((File) newMaps.getSelectedItem()).toPath());
        }
        updateTrackColor();

        configuration.setMapHomeLongitude(Double.parseDouble(mapHomeLon.getText()));
        configuration.setMapHomeLattitude(Double.parseDouble(mapHomeLat.getText()));
        EventBus.publish(EventType.MAP_HOME_CHANGED,new Coordinate(configuration.getMapHomeLongitude(), configuration.getMapHomeLattitude()));
    }

    private void updateTrackColor() {
        configuration.setMapTrackColor(colorChooserTextField.getText());
        EventBus.publish(EventType.TRACK_COLOR_CHANGED, Color.decode(colorChooserTextField.getText()));
    }

}
