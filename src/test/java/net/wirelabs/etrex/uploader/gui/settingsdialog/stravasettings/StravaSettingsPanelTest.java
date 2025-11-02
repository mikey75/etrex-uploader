package net.wirelabs.etrex.uploader.gui.settingsdialog.stravasettings;

import com.strava.model.SportType;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;
import java.util.stream.IntStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;


class StravaSettingsPanelTest {

    @Test
    void l() {
        StravaConfiguration stravaConfiguration = spy(new StravaConfiguration("src/test/resources/config/test.properties"));
        doNothing().when(stravaConfiguration).save(); // make sure nothing rewrites config file
        StravaSettingsPanel panel = new StravaSettingsPanel(stravaConfiguration);

        assertThat(panel.activitiesPerPage.getText()).isEqualTo(String.valueOf(stravaConfiguration.getPerPage()));
        assertThat(panel.warnQuotaPercent.getText()).isEqualTo(String.valueOf(stravaConfiguration.getApiUsageWarnPercent()));
        assertThat(panel.usePolylines.isSelected()).isEqualTo(stravaConfiguration.isUsePolyLines());
        assertThat(panel.checkHostBeforeUpload.isSelected()).isEqualTo(stravaConfiguration.isStravaCheckHostBeforeUpload());
        assertThat(panel.hostTimeout.getText()).isEqualTo(String.valueOf(stravaConfiguration.getStravaCheckTimeout()));


        ComboBoxModel<SportType> model = panel.activityTypeCombo.getModel();
        List<SportType> items = IntStream.range(0, model.getSize())
                .mapToObj(model::getElementAt).toList();
        // assert combobox has some default sport types
        assertThat(items).contains(SportType.RIDE,SportType.CANOEING,SportType.WALK,SportType.MOUNTAINBIKERIDE);


        panel.activitiesPerPage.setText("100");
        panel.usePolylines.setSelected(false);
        panel.updateConfiguration();
        assertThat(stravaConfiguration.isUsePolyLines()).isFalse();
        assertThat(stravaConfiguration.getPerPage()).isEqualTo(100);


    }

}