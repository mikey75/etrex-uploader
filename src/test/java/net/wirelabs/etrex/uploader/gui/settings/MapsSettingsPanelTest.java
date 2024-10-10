package net.wirelabs.etrex.uploader.gui.settings;

import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.eventbus.EventBus;
import net.wirelabs.jmaps.map.geo.Coordinate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.awt.*;

import static org.mockito.Mockito.*;

class MapsSettingsPanelTest {

    private MapsSettingsPanel mapsSettingsPanel;
    private MockedStatic<EventBus> evbusMock;

    @BeforeEach
    void beforeEach() {
        AppConfiguration appConfiguration = new AppConfiguration("src/test/resources/config/test.properties");
        mapsSettingsPanel = Mockito.spy(new MapsSettingsPanel(appConfiguration));
        evbusMock = Mockito.mockStatic(EventBus.class);
    }

    @AfterEach
    void afterEach() {
        evbusMock.close();
    }

    @Test
    void shouldPublishEventsIfMapHomeOrTrackColorChanged() {

            // given -> change values on the panel and update config
            mapsSettingsPanel.getMapHomeLat().setText("102903910");
            mapsSettingsPanel.getMapHomeLon().setText("019237981");
            mapsSettingsPanel.getColorChooserTextField().setText("#ff0001");

            // when
            mapsSettingsPanel.updateConfiguration();

            // verify changes will be published in the order from updateConfiguration
            evbusMock.verify(() -> EventBus.publish(eq(EventType.TRACK_COLOR_CHANGED), any(Color.class)));
            evbusMock.verify(() -> EventBus.publish(eq(EventType.MAP_HOME_CHANGED), any(Coordinate.class)));

    }

    @Test
    void shouldNotPublishEventsIfMapHomeOrTrackColorNotChanged() {
            // given -> none, no change on the panel

            // when
            mapsSettingsPanel.updateConfiguration();

            // verify changes will NOT be published since nothing changed
            evbusMock.verify(() -> EventBus.publish(eq(EventType.TRACK_COLOR_CHANGED), any(Color.class)), never());
            evbusMock.verify(() -> EventBus.publish(eq(EventType.MAP_HOME_CHANGED), any(Coordinate.class)), never());
    }

}