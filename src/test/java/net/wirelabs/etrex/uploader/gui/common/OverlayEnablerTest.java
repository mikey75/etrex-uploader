package net.wirelabs.etrex.uploader.gui.common;

import net.wirelabs.etrex.uploader.gui.desktop.mappanel.common.OverlayEnabler;
import net.wirelabs.jmaps.map.MapViewer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class OverlayEnablerTest {

    private MapViewer mapViewer;
    private TestPainter1 testPainter1;
    private TestPainter2 testPainter2;
    private OverlayEnabler overlayEnabler;

    @BeforeEach
    void shouldDisableAndEnableOverlays() {
        // init
        mapViewer = Mockito.spy(new MapViewer());
        testPainter1 = Mockito.spy(new TestPainter1());
        testPainter2 = Mockito.spy(new TestPainter2());
        overlayEnabler = Mockito.spy(new OverlayEnabler(mapViewer));
    }

    @Test
    void shouldCheckInitialState() {
        assertThat(mapViewer.getUserOverlays()).isEmpty();
        assertThat(overlayEnabler.getPainters()).isEmpty();
    }

    @Test
    void shouldAddPainter() {
        overlayEnabler.addPainter(testPainter1, "testpainter1", true, true);
        assertThat(mapViewer.getUserOverlays()).isNotEmpty().contains(testPainter1);
        assertThat(overlayEnabler.getPainters().get(testPainter1).isSelected()).isTrue();
        assertThat(overlayEnabler.getPainters().get(testPainter1).isEnabled()).isTrue();
    }

    @Test
    void shouldAddTwoPainters() {

        overlayEnabler.addPainter(testPainter1, "testpainter1", true, true);
        overlayEnabler.addPainter(testPainter2, "testpainter2", false, true);

        assertThat(overlayEnabler.getPainters().get(testPainter1).isSelected()).isTrue();
        assertThat(overlayEnabler.getPainters().get(testPainter1).isEnabled()).isTrue();
        assertThat(overlayEnabler.getPainters().get(testPainter2).isSelected()).isFalse();
        assertThat(overlayEnabler.getPainters().get(testPainter2).isEnabled()).isTrue();

        assertThat(mapViewer.getUserOverlays()).contains(testPainter1);
        assertThat(mapViewer.getUserOverlays()).doesNotContain(testPainter2); // since the selection was false, remove from user overlays
    }

    @Test
    void testDisableByClick() {
        overlayEnabler.addPainter(testPainter1, "test1", true, true);
        assertThat(overlayEnabler.getPainters().get(testPainter1).isSelected()).isTrue();
        assertThat(mapViewer.getUserOverlays()).contains(testPainter1);

        overlayEnabler.getPainters().get(testPainter1).doClick();

        assertThat(overlayEnabler.getPainters().get(testPainter1).isSelected()).isFalse();
        assertThat(mapViewer.getUserOverlays()).doesNotContain(testPainter1);
    }

    @Test
    void testEnableByClick() {
        overlayEnabler.addPainter(testPainter1, "test1", false, true);
        assertThat(overlayEnabler.getPainters().get(testPainter1).isSelected()).isFalse();
        assertThat(mapViewer.getUserOverlays()).doesNotContain(testPainter1);

        overlayEnabler.getPainters().get(testPainter1).doClick();

        assertThat(overlayEnabler.getPainters().get(testPainter1).isSelected()).isTrue();
        assertThat(mapViewer.getUserOverlays()).contains(testPainter1);

    }
}
