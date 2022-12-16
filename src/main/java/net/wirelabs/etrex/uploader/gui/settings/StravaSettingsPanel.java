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

    JLabel activityTypeLabel = new JLabel("Default activty type:");
    JComboBox<SportType> activityTypeCombo = new JComboBox<>();
    JLabel lblActivitiesPerPage = new JLabel("Activities per page:");
    JTextField activitiesPerPage = new JTextField();
    JLabel lblWarnQuotaPercent = new JLabel("Warn quota percent:");
    JTextField warnQuotaPercent = new JTextField();

    public StravaSettingsPanel(AppConfiguration configuration) {
        super("Strava");
        setLayout(new MigLayout("", "[][grow]", "[][][]"));

        add(activityTypeLabel, "cell 0 0,alignx trailing");
        activityTypeCombo.setModel(new DefaultComboBoxModel<>(SportType.values()));
        add(activityTypeCombo, "cell 1 0,growx");

        add(lblActivitiesPerPage, "cell 0 1,alignx trailing");
        add(activitiesPerPage, "cell 1 1,growx");

        add(lblWarnQuotaPercent, "cell 0 2,alignx trailing");
        add(warnQuotaPercent, "cell 1 2,growx");
    }

}
