package net.wirelabs.etrex.uploader.gui.settings;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.SwingUtils;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.etrex.uploader.gui.components.BasePanel;
import net.wirelabs.etrex.uploader.gui.components.ColorChooserTextField;
import net.wirelabs.etrex.uploader.gui.components.choosemapcombo.ChooseMapComboBox;
import net.wirelabs.eventbus.EventBus;
import net.wirelabs.jmaps.map.geo.Coordinate;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static net.wirelabs.etrex.uploader.common.utils.SystemUtils.*;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class MapsSettingsPanel extends BasePanel {

    public static final String INFO_MSG = "<html>You can write new home position here,<br>but you can also use the map,<br> and select it with double click</html>";

    final AppConfiguration configuration;
    final ChooseMapComboBox newMaps = new ChooseMapComboBox();
    private final JComboBox<String> cacheCombo = new JComboBox<>(new String[]{"Files","Database"});
    private final JLabel lblDefaultMap = new JLabel("Default map:");
    private final JLabel lblTilerThreads = new JLabel("Threads:");
    private final JLabel lblColor = new JLabel("Track color:");
    private final JLabel lblMapHomeLat = new JLabel("Map home latitude:");
    private final JLabel lblMapHomeLon = new JLabel("Map home longitude:");
    private final JLabel lblTileCacheType = new JLabel("Tile cache type:");

    private final JTextField threads = new JTextField();
    @Getter
    private final JTextField mapHomeLat = new JTextField();
    @Getter
    private final JTextField mapHomeLon = new JTextField();
    @Getter
    private final ColorChooserTextField colorChooserTextField;
    final JTextField routeLineWidth = new JTextField();
    private final JLabel lblTrkWidth = new JLabel("Track width:");


    public MapsSettingsPanel(AppConfiguration configuration) {
        super("Maps","","[][][][grow]","[][][grow]");
        this.configuration = configuration;
        this.colorChooserTextField = new ColorChooserTextField(configuration.getMapTrackColor());

        add(lblDefaultMap, "cell 0 0,alignx trailing");
        add(newMaps, "cell 1 0, growx");
        add(lblTilerThreads, "cell 2 0,alignx trailing");
        add(threads, "cell 3 0,growx");

        add(lblColor, "cell 0 1, alignx trailing");
        add(colorChooserTextField, "cell 1 1, growx");

        add(lblTrkWidth, "cell 0 2, alignx trailing");
        add(routeLineWidth, "cell 1 2, growx");

        add(lblMapHomeLon, "cell 0 3, alignx trailing");
        add(mapHomeLon, "cell 1 3, growx");

        add(lblMapHomeLat, "cell 0 4, alignx trailing");
        add(mapHomeLat, "cell 1 4, growx");

        add(lblTileCacheType, "cell 0 5, alignx trailing");
        add(cacheCombo, "cell 1 5, growx");
        mapHomeLon.setToolTipText(INFO_MSG);
        mapHomeLat.setToolTipText(INFO_MSG);
        loadConfiguration();
        cacheCombo.addActionListener(e -> showRebootNeededMsgDialog(configuration));
    }

    private void showRebootNeededMsgDialog(AppConfiguration configuration) {
        int dialogResponse = SwingUtils.yesNoCancelMsg("This change will need restarting the application. Do you want that?");
        // if YES -> update config and reboot app
        if (dialogResponse == JOptionPane.YES_OPTION) {
            updateConfiguration();
            saveConfigAndReboot(configuration);
        } else {
            // if NO -> restore UI status with current config and continue normally
            cacheCombo.setSelectedItem(configuration.getCacheType());
        }
    }


    private void loadConfiguration() {


        // set values from gui
        newMaps.setSelectedItem(configuration.getMapFile().toFile());
        threads.setText(String.valueOf(configuration.getTilerThreads()));
        mapHomeLon.setText(String.valueOf(configuration.getMapHomeLongitude()));
        mapHomeLat.setText(String.valueOf(configuration.getMapHomeLatitude()));
        routeLineWidth.setText(String.valueOf(configuration.getRouteLineWidth()));
        cacheCombo.setSelectedItem(String.valueOf(configuration.getCacheType()));
    }

    public void updateConfiguration() {
        configuration.setTilerThreads(Integer.parseInt(threads.getText()));
        configuration.setCacheType(String.valueOf(cacheCombo.getSelectedItem()));
        // emit events if track color/width, map home, and default map changed
        updateTrackColor(); // emit events for updated track color
        updateTrackWidth(); // emit events for updated track width
        updateMapHome();    // emit events for changed map home
        updateDefaultMap(); // emit events for changed map

    }

    private void updateDefaultMap() {

        if (newMaps.getSelectedItem() != null && !configuration.getMapFile().toString().equals(newMaps.getSelectedItem().toString())) {
            configuration.setMapFile(((File) newMaps.getSelectedItem()).toPath());
            EventBus.publish(EventType.MAP_CHANGED,newMaps.getSelectedItem());
        }
    }

    private void updateMapHome() {
        // copy original config values for event bus change detection
        Double origLat = configuration.getMapHomeLatitude();
        Double origLon = configuration.getMapHomeLongitude();
        // set new values
        configuration.setMapHomeLongitude(Double.parseDouble(mapHomeLon.getText()));
        configuration.setMapHomeLatitude(Double.parseDouble(mapHomeLat.getText()));
        // publish the map home change event only if it actually really changed
        if (!origLat.equals(configuration.getMapHomeLatitude()) || !origLon.equals(configuration.getMapHomeLongitude())) {
            EventBus.publish(EventType.MAP_HOME_CHANGED, new Coordinate(configuration.getMapHomeLongitude(), configuration.getMapHomeLatitude()));
        }
    }

    private void updateTrackColor() {
        // copy original config values for event bus change detection
        String origColor = configuration.getMapTrackColor();
        // set new values
        configuration.setMapTrackColor(colorChooserTextField.getText());
        // publish color change event if it actually really changed
        if (!configuration.getMapTrackColor().equals(origColor)) {
            EventBus.publish(EventType.TRACK_COLOR_CHANGED, Color.decode(colorChooserTextField.getText()));
        }
    }

    private void updateTrackWidth() {
        // publish line width change if it actually really changed
        int origLineWidth = configuration.getRouteLineWidth();
        int newLineWidth = Integer.parseInt(routeLineWidth.getText());
        // usable width is about 3-10 pixels, setting more is ugly and unnecessary so
        // take care for that by setting default (3) if out of allowed range
        if (newLineWidth >= 3 && newLineWidth <= 10) {
            configuration.setRouteLineWidth(newLineWidth);
        } else {
            int dialogResult = SwingUtils.yesNoMsg("Route line width is best in 3-10 pixels range\nDo you want to set default (3px)\n\nPressing 'No' will ignore the change");
            if (dialogResult == JOptionPane.YES_OPTION) {
                configuration.setRouteLineWidth(Constants.DEFAULT_ROUTE_LINE_WIDTH);
            }
        }
        // publish event if width changed
        if (configuration.getRouteLineWidth() != origLineWidth) {
            EventBus.publish(EventType.ROUTE_LINE_WIDTH_CHANGED, configuration.getRouteLineWidth());
        }
    }

}
