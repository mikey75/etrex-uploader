package net.wirelabs.etrex.uploader.gui.strava.account;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.eventbus.Event;
import net.wirelabs.etrex.uploader.gui.components.EventAwarePanel;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtil;


import javax.swing.*;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class ApiUsagePanel extends EventAwarePanel {

    @Getter
    private final JLabel dailyLimits;
    @Getter
    private final JLabel quarterLimits;
    private final AppConfiguration configuration;
    private Integer currentDaily;
    private Integer current15min;
    private Integer allowedDaily;
    private Integer allowed15min;

    public ApiUsagePanel(AppConfiguration configuration) {
        this.configuration = configuration;
        setLayout(new MigLayout("insets 0 0 0 0", "[][]", "[][][]"));

        JLabel header = new JLabel("API usage:");
        JLabel dailyLabel = new JLabel("Daily:");
        JLabel quarterLabel = new JLabel("15min:");

        dailyLimits = new JLabel("");
        quarterLimits = new JLabel("");

        add(header, "cell 0 0 2 1,alignx center");
        add(dailyLabel, "cell 0 1,alignx right");
        add(quarterLabel, "cell 0 2,alignx right");
        add(dailyLimits, "cell 1 1");
        add(quarterLimits, "cell 1 2");
    }

    @Override
    protected void onEvent(Event evt) {
        if (evt.getEventType() == EventType.RATELIMIT_INFO_UPDATE) {
            Map<String, Integer> apiInfo = (Map<String, Integer>) evt.getPayload();
            updateApiUsageLabels(apiInfo);
            warnIfPercentageReached();
        }
    }

    private void warnIfPercentageReached() {
        double percDaily = calculatePercentage(currentDaily.doubleValue(), allowedDaily.doubleValue());
        double perc15min = calculatePercentage(current15min.doubleValue(), allowed15min.doubleValue());

        if (percDaily >= configuration.getApiUsageWarnPercent()) {
            dailyLimits.setForeground(Color.RED);
        } else {
            dailyLimits.setForeground(getForeground());
        }
        if (perc15min >= configuration.getApiUsageWarnPercent()) {
            quarterLimits.setForeground(Color.RED);
        } else {
            quarterLimits.setForeground(getForeground());
        }
    }

    private void updateApiUsageLabels(Map<String,Integer> apiInfo) {

        currentDaily = apiInfo.get(StravaUtil.CURRENT_DAILY);
        current15min = apiInfo.get(StravaUtil.CURRENT_15MINS);
        allowedDaily = apiInfo.get(StravaUtil.ALLOWED_DAILY);
        allowed15min = apiInfo.get(StravaUtil.ALLOWED_15MINS);

        SwingUtilities.invokeLater(() -> {
            dailyLimits.setText("(" + currentDaily + "/" + allowedDaily + ")");
            quarterLimits.setText("(" + current15min + "/" + allowed15min + ")");
        });
    }

    @Override
    protected Collection<EventType> subscribeEvents() {
        return List.of(EventType.RATELIMIT_INFO_UPDATE);
    }

    private double calculatePercentage(double value, double total) {
        return value * 100 / total;
    }
}
