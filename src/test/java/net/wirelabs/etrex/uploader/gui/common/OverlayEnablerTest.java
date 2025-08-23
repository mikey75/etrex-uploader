package net.wirelabs.etrex.uploader.gui.common;

import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.desktop.mappanel.common.OverlayEnabler;
import net.wirelabs.etrex.uploader.gui.desktop.mappanel.common.RoutePainter;
import net.wirelabs.jmaps.map.MapViewer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class OverlayEnablerTest {

    @Test
    void shouldDisableAndEnableOverlays() {
        // init
        AppConfiguration appConfiguration = Mockito.spy(new AppConfiguration("target/kaka"));
        MapViewer mapViewer = Mockito.spy(new MapViewer());
        RoutePainter routePainter = Mockito.spy(new RoutePainter(appConfiguration));
        OverlayEnabler overlayEnabler = Mockito.spy(new OverlayEnabler(mapViewer,routePainter));


        // check that component instantiated the checkbox ;)
        assertThat(overlayEnabler.getShowOverlaysCheckbox()).isNotNull();

        // set some user overlay
        mapViewer.addUserOverlay(routePainter);
        assertThat(mapViewer.getUserOverlays()).isNotEmpty().hasSize(1).contains(routePainter);

        // emulate checkbox is false (default is true, so clicking will set it to false)
        overlayEnabler.getShowOverlaysCheckbox().doClick();
        assertThat(overlayEnabler.getShowOverlaysCheckbox().isSelected()).isFalse();

        // so the user overlays list should be cleared
        assertThat(mapViewer.getUserOverlays()).isEmpty();

        // now click again - should be true
        overlayEnabler.getShowOverlaysCheckbox().doClick();
        assertThat(overlayEnabler.getShowOverlaysCheckbox().isSelected()).isTrue();

        // so now the list should not be empty and contain the previous overlay
        assertThat(mapViewer.getUserOverlays()).isNotEmpty().hasSize(1).contains(routePainter);
    }

}