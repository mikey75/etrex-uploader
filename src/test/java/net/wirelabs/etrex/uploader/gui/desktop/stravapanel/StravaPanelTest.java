package net.wirelabs.etrex.uploader.gui.desktop.stravapanel;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.gui.desktop.stravapanel.account.UserAccountPanel;
import net.wirelabs.etrex.uploader.gui.desktop.stravapanel.activitiestable.StravaActivitiesPanel;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StravaPanelTest extends BaseTest {

    private final StravaClient client = mock(StravaClient.class);
    private final File configFile = new File("target/nonexistent");
    private final AppConfiguration appConfiguration = new AppConfiguration(configFile.getPath());
    private final StravaConfiguration stravaConfiguration = new StravaConfiguration(configFile.getPath());

    @Test
    void shouldInitializeStravaPanel() {
        // return default configs (nonexistent file will produce default configurations)
        when(client.getAppConfiguration()).thenReturn(appConfiguration);
        when(client.getStravaConfiguration()).thenReturn(stravaConfiguration);

        // when
        StravaPanel panel = new StravaPanel(client);

        // then
        assertThat(panel.getActivitiesPanel()).isNotNull().isInstanceOf(StravaActivitiesPanel.class);
        assertThat(panel.getUserAccountPanel()).isNotNull().isInstanceOf(UserAccountPanel.class);
        assertThat(panel.getHeight()).isEqualTo(350);
        assertThat(panel.getWidth()).isEqualTo(800);
        assertThat(panel.getLayout()).isInstanceOf(MigLayout.class);

        // clean up
        FileUtils.deleteQuietly(configFile);

    }
}