package net.wirelabs.etrex.uploader.gui.desktop.stravapanel.account;

import lombok.Getter;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.common.base.BaseEventAwarePanel;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtil;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.IEventType;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.*;

/**
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class ApiUsagePanel extends BaseEventAwarePanel {
    private final AppConfiguration configuration;
    @Getter
    private final JLabel dailyLimits;
    @Getter
    private final JLabel quarterLimits;
    private Integer currentDaily;
    private Integer current15min;
    private Integer allowedDaily;
    private Integer allowed15min;


    public ApiUsagePanel(AppConfiguration configuration) {
        super("insets 0","[grow][grow]","[][center][][]");
        this.configuration = configuration;

        JLabel header = new JLabel("API usage:");
        add(header, cell(0,1,2,1).growX().alignY(CENTER));

        JSeparator separator = new JSeparator();
        add(separator, cell(0,0,2,1).grow());

        JLabel dailyLabel = new JLabel("Daily:");
        add(dailyLabel, cell(0,2).alignX(LEFT));

        dailyLimits = new JLabel("$daily");
        add(dailyLimits, cell(1,2).alignX(RIGHT));

        JLabel quarterLabel = new JLabel("15min:");
        add(quarterLabel, cell(0,3).alignX(LEFT));

        quarterLimits = new JLabel("$15min");
        add(quarterLimits, cell(1,3).alignX(RIGHT));
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
        double percentDaily = calculatePercentage(currentDaily.doubleValue(), allowedDaily.doubleValue());
        double percent15min = calculatePercentage(current15min.doubleValue(), allowed15min.doubleValue());

        if (percentDaily >= configuration.getApiUsageWarnPercent()) {
            dailyLimits.setForeground(Color.RED);
        } else {
            dailyLimits.setForeground(getForeground());
        }
        if (percent15min >= configuration.getApiUsageWarnPercent()) {
            quarterLimits.setForeground(Color.RED);
        } else {
            quarterLimits.setForeground(getForeground());
        }
    }

    private void updateApiUsageLabels(Map<String, Integer> apiInfo) {

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
    protected Collection<IEventType> subscribeEvents() {
        return List.of(EventType.RATELIMIT_INFO_UPDATE);
    }

    private double calculatePercentage(double value, double total) {
        return value * 100 / total;
    }
}
