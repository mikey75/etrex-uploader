package net.wirelabs.etrex.uploader.gui.strava;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.gui.strava.account.UserAccountPanel;
import net.wirelabs.etrex.uploader.gui.strava.activities.StravaActivitiesPanel;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StravaPanelTest extends BaseTest {

    @Test
    void shouldInitializeStravaPanel() {
        StravaClient client = mock(StravaClient.class);
        StravaPanel panel = new StravaPanel(client);

        assertThat(panel.getActivitiesPanel()).isNotNull().isInstanceOf(StravaActivitiesPanel.class);
        assertThat(panel.getUserAccountPanel()).isNotNull().isInstanceOf(UserAccountPanel.class);

        assertThat(panel.getHeight()).isEqualTo(350);
        assertThat(panel.getWidth()).isEqualTo(800);
        assertThat(panel.getLayout()).isInstanceOf(MigLayout.class);


    }


}