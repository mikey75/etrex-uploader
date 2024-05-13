package net.wirelabs.etrex.uploader.gui.settings;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;
import net.wirelabs.etrex.uploader.strava.model.SportType;

import javax.swing.*;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaSettingsPanel extends BorderedPanel {

    private final AppConfiguration configuration;
    private final JComboBox<SportType> activityTypeCombo = new JComboBox<>();
    private final JTextField activitiesPerPage = new JTextField();
    private final JTextField warnQuotaPercent = new JTextField();
    private final JCheckBox usePolylines = new JCheckBox("Use activity polyline to draw tracks (faster)");

    public StravaSettingsPanel(AppConfiguration configuration) {

        super("Strava");

        JLabel activityTypeLabel = new JLabel("Default activty type:");
        JLabel lblActivitiesPerPage = new JLabel("Activities per page:");
        JLabel lblWarnQuotaPercent = new JLabel("Warn quota percent:");

        this.configuration = configuration;
        setLayout(new MigLayout("", "[][grow]", "[][][]"));

        add(activityTypeLabel, "cell 0 0,alignx trailing");
        activityTypeCombo.setModel(new DefaultComboBoxModel<>(SportType.values()));
        add(activityTypeCombo, "cell 1 0,growx");

        add(lblActivitiesPerPage, "cell 0 1,alignx trailing");
        add(activitiesPerPage, "cell 1 1,growx");

        add(lblWarnQuotaPercent, "cell 0 2,alignx trailing");
        add(warnQuotaPercent, "cell 1 2,growx");
        add(usePolylines, "cell 1 3");
        loadConfiguration();
    }

    private void loadConfiguration() {
        activityTypeCombo.setSelectedItem(configuration.getDefaultActivityType());
        activitiesPerPage.setText(String.valueOf(configuration.getPerPage()));
        warnQuotaPercent.setText(String.valueOf(configuration.getApiUsageWarnPercent()));
        usePolylines.setSelected(configuration.isUsePolyLines());
    }

    public void updateConfiguration() {
        configuration.setDefaultActivityType((SportType) activityTypeCombo.getSelectedItem());
        configuration.setPerPage(Integer.parseInt(activitiesPerPage.getText()));
        configuration.setApiUsageWarnPercent(Integer.parseInt(warnQuotaPercent.getText()));
        configuration.setUsePolyLines(usePolylines.isSelected());
    }


}
