package net.wirelabs.etrex.uploader.gui.settingsdialog.mapsettings;

import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.utils.SystemUtils;
import net.wirelabs.eventbus.EventBus;
import net.wirelabs.jmaps.map.geo.Coordinate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MapsSettingsPanelTest {

    private MapsSettingsPanel mapsSettingsPanel;
    private MockedStatic<EventBus> eventBusMock;

    @BeforeEach
    void beforeEach() {
        AppConfiguration appConfiguration = new AppConfiguration("src/test/resources/config/test.properties");
        mapsSettingsPanel = Mockito.spy(new MapsSettingsPanel(appConfiguration));
        eventBusMock = Mockito.mockStatic(EventBus.class);
    }

    @AfterEach
    void afterEach() {
        eventBusMock.close();
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
        eventBusMock.verify(() -> EventBus.publish(eq(EventType.TRACK_COLOR_CHANGED), any(Color.class)));
        eventBusMock.verify(() -> EventBus.publish(eq(EventType.MAP_HOME_CHANGED), any(Coordinate.class)));

    }

    @Test
    void shouldNotPublishEventsIfMapHomeOrTrackColorNotChanged() {
        // given -> none, no change on the panel

        // when
        mapsSettingsPanel.updateConfiguration();

        // verify changes will NOT be published since nothing changed
        eventBusMock.verify(() -> EventBus.publish(eq(EventType.TRACK_COLOR_CHANGED), any(Color.class)), never());
        eventBusMock.verify(() -> EventBus.publish(eq(EventType.MAP_HOME_CHANGED), any(Coordinate.class)), never());
    }

    @Test
    void shouldUpdateWidth() {

        mapsSettingsPanel.routeLineWidth.setText("5");
        mapsSettingsPanel.updateConfiguration();

        assertThat(mapsSettingsPanel.getConfiguration().getRouteLineWidth()).isEqualTo(5);
        eventBusMock.verify(() -> EventBus.publish(eq(EventType.ROUTE_LINE_WIDTH_CHANGED), eq(5)));
    }

    @Test
    void shouldDisplayIfRouteWidthUncomfortable() {
        try (MockedStatic<SwingUtils> swingUtils = mockStatic(SwingUtils.class)) {

            // when route width > 10 you'll get option to set default 3px (YES)
            // or ignore and set what was before (NO)
            mapsSettingsPanel.routeLineWidth.setText(String.valueOf(11));
            swingUtils.when(() -> SwingUtils.yesNoMsg(anyString())).thenReturn(JOptionPane.YES_OPTION);
            //when(SwingUtils.yesNoMsg(anyString())).thenReturn(JOptionPane.YES_OPTION);

            mapsSettingsPanel.updateConfiguration();
            assertThat(mapsSettingsPanel.getConfiguration().getRouteLineWidth()).isEqualTo(Constants.DEFAULT_ROUTE_LINE_WIDTH);

        }
    }

    @Test
    void shouldChangeMap() {
        File newMap = new File("src/test/resources/config/maps/goodMapDef.xml");

        mapsSettingsPanel.getMapsCombo().getChooseMapComboBoxModel().addElement(newMap);
        mapsSettingsPanel.getMapsCombo().setSelectedItem(newMap);

        mapsSettingsPanel.updateConfiguration();

        eventBusMock.verify(() -> EventBus.publish(eq(EventType.MAP_CHANGED), eq(newMap)));

    }

    @Test
    void shouldPresentCacheTypeComboWithConfiguredCache() {
        assertThat(mapsSettingsPanel.getCacheCombo().getItemCount()).isEqualTo(3);
        assertThat(mapsSettingsPanel.getCacheCombo().getModel().getElementAt(0)).isEqualTo(Constants.DIR_BASED_CACHE_TYPE);
        assertThat(mapsSettingsPanel.getCacheCombo().getModel().getElementAt(1)).isEqualTo(Constants.DB_BASED_CACHE_TYPE);
        assertThat(mapsSettingsPanel.getCacheCombo().getModel().getElementAt(2)).isEqualTo(Constants.REDIS_CACHE);
    }

    @Test
    void checkSettingCacheFromCombo() {
        try (MockedStatic<SwingUtils> swingUtils = mockStatic(SwingUtils.class);
            MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class)) {

            // do nothing on saveandreboot
            systemUtils.when(() -> SystemUtils.saveConfigAndReboot(any())).thenAnswer(inv -> null);

            // make sure dir based is selected
            assertThat(mapsSettingsPanel.getCacheCombo().getSelectedItem()).isEqualTo(Constants.DIR_BASED_CACHE_TYPE);

            // no on confirmation dialog - cache should stay at DIR_BASED
            swingUtils.when(() -> SwingUtils.yesNoCancelMsg(anyString())).thenReturn(JOptionPane.NO_OPTION);
            mapsSettingsPanel.getCacheCombo().setSelectedItem(Constants.DB_BASED_CACHE_TYPE);
            assertThat(mapsSettingsPanel.getConfiguration().getCacheType()).isEqualTo(Constants.DIR_BASED_CACHE_TYPE);

            // yes on confirmation dialog - cache should be set to selected item (DB_BASED)
            swingUtils.when(() -> SwingUtils.yesNoCancelMsg(anyString())).thenReturn(JOptionPane.YES_OPTION);
            mapsSettingsPanel.getCacheCombo().setSelectedItem(Constants.DB_BASED_CACHE_TYPE);
            assertThat(mapsSettingsPanel.getConfiguration().getCacheType()).isEqualTo(Constants.DB_BASED_CACHE_TYPE);


        }

    }


}