package net.wirelabs.etrex.uploader.gui.settingsdialog.stravasettings;

import com.strava.model.SportType;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;

import javax.swing.*;

import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.cell;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class StravaSettingsPanel extends BasePanel {

    final AppConfiguration configuration;
    final JComboBox<SportType> activityTypeCombo = new JComboBox<>();
    final JTextField activitiesPerPage = new JTextField();
    final JTextField warnQuotaPercent = new JTextField();
    final JCheckBox usePolylines = new JCheckBox("Use activity polyline to draw tracks (faster)");
    final JCheckBox checkHostBeforeUpload = new JCheckBox("Check if strava is up on startup and before upload");
    final JTextField hostTimeout = new JTextField();


    public StravaSettingsPanel(AppConfiguration configuration) {

        super("Strava","","[][grow]","[][][]");

        JLabel activityTypeLabel = new JLabel("Default activity type:");
        JLabel lblActivitiesPerPage = new JLabel("Activities per page:");
        JLabel lblWarnQuotaPercent = new JLabel("Warn quota percent:");
        JLabel lblHostTimeout = new JLabel("Strava hosts timeout milliseconds:");

        this.configuration = configuration;

        add(activityTypeLabel, cell(0,0).alignX("trailing"));
        activityTypeCombo.setModel(new DefaultComboBoxModel<>(SportType.values()));
        add(activityTypeCombo, cell(1,0).growX());

        add(lblActivitiesPerPage, cell(0,1).alignX("trailing"));
        add(activitiesPerPage, cell(1,1).growX());

        add(lblWarnQuotaPercent, cell(0,2).alignX("trailing"));
        add(warnQuotaPercent, cell(1,2).growX());
        add(usePolylines, cell(1,3));

        add(checkHostBeforeUpload, cell(1,4).alignX("trailing"));
        add(lblHostTimeout, cell(1,5).alignX("trailing"));
        add(hostTimeout, cell(1,5).growX());

        loadConfiguration();
    }

    private void loadConfiguration() {
        activityTypeCombo.setSelectedItem(configuration.getDefaultActivityType());
        activitiesPerPage.setText(String.valueOf(configuration.getPerPage()));
        warnQuotaPercent.setText(String.valueOf(configuration.getApiUsageWarnPercent()));
        usePolylines.setSelected(configuration.isUsePolyLines());
        hostTimeout.setText(String.valueOf(configuration.getStravaCheckTimeout()));
        checkHostBeforeUpload.setSelected(configuration.isStravaCheckHostBeforeUpload());
    }

    public void updateConfiguration() {
        configuration.setDefaultActivityType((SportType) activityTypeCombo.getSelectedItem());
        configuration.setPerPage(Integer.parseInt(activitiesPerPage.getText()));
        configuration.setApiUsageWarnPercent(Integer.parseInt(warnQuotaPercent.getText()));
        configuration.setUsePolyLines(usePolylines.isSelected());
        configuration.setStravaCheckTimeout(Integer.parseInt(hostTimeout.getText()));
        configuration.setStravaCheckHostBeforeUpload(checkHostBeforeUpload.isSelected());
    }


}
