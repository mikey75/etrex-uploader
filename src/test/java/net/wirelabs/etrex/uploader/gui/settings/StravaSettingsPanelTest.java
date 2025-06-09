package net.wirelabs.etrex.uploader.gui.settings;

import com.strava.model.SportType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
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
        AppConfiguration appConfiguration = spy(new AppConfiguration("src/test/resources/config/test.properties"));
        doNothing().when(appConfiguration).save(); // make sure nothing rewrites config file
        StravaSettingsPanel panel = new StravaSettingsPanel(appConfiguration);

        assertThat(panel.activitiesPerPage.getText()).isEqualTo(String.valueOf(appConfiguration.getPerPage()));
        assertThat(panel.warnQuotaPercent.getText()).isEqualTo(String.valueOf(appConfiguration.getApiUsageWarnPercent()));
        assertThat(panel.usePolylines.isSelected()).isEqualTo(appConfiguration.isUsePolyLines());
        assertThat(panel.checkHostBeforeUpload.isSelected()).isEqualTo(appConfiguration.isStravaCheckHostBeforeUpload());
        assertThat(panel.hostTimeout.getText()).isEqualTo(String.valueOf(appConfiguration.getStravaCheckTimeout()));


        ComboBoxModel<SportType> model = panel.activityTypeCombo.getModel();
        List<SportType> items = IntStream.range(0, model.getSize())
                .mapToObj(model::getElementAt).toList();
        // assert combobox has some default sport types
        assertThat(items).contains(SportType.RIDE,SportType.CANOEING,SportType.WALK,SportType.MOUNTAINBIKERIDE);


        panel.activitiesPerPage.setText("100");
        panel.usePolylines.setSelected(false);
        panel.updateConfiguration();
        assertThat(appConfiguration.isUsePolyLines()).isFalse();
        assertThat(appConfiguration.getPerPage()).isEqualTo(100);


    }

}