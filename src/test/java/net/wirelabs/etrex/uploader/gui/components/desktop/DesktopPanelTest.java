package net.wirelabs.etrex.uploader.gui.components.desktop;

import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThat;

class DesktopPanelTest {

    static final JComponent LEFT_PANE_COMPONENT = new BorderedPanel("left pane");
    static final JComponent TOP_RIGHT_PANE_COMPONENT = new BorderedPanel("top right pane");
    static final JComponent BOTTOM_RIGHT_PANE_COMPONENT = new BorderedPanel("bottom right pane");

    @Test
    void testDefaultClassicDesktop() {
        // default desktop is slider-less (classic)
        DesktopPanel container = new DesktopPanel();

        // verify
        assertThat(container.getLayout()).isEqualTo(DesktopPanel.LAYOUT_WITHOUT_SLIDERS);
        assertThat(container.getLeftPane()).isNotNull().isInstanceOf(JPanel.class);
        assertThat(container.getBottomRightPane()).isNotNull().isInstanceOf(JPanel.class);
        assertThat(container.getTopRightPane()).isNotNull().isInstanceOf(JPanel.class);
        // classic desktop has no sliders so no further checks
    }

    @Test
    void testDefaultSlidingDesktop() {
        // set sliders enables (classic look = false)
        DesktopPanel container = new DesktopPanel(true);
        assertThat(container.getLayout()).isEqualTo(DesktopPanel.LAYOUT_WITH_SLIDERS);
        assertThat(container.getLeftPane()).isNotNull().isInstanceOf(JPanel.class);
        assertThat(container.getBottomRightPane()).isNotNull().isInstanceOf(JPanel.class);
        assertThat(container.getTopRightPane()).isNotNull().isInstanceOf(JPanel.class);

        // sliding panel has default sliders - check if they're initialized to defaults
        assertThat(container.getHorizontalSliderWidth()).isEqualTo(DesktopPanel.DEFAULT_HORIZONTAL_SLIDER_WIDTH);
        assertThat(container.getVerticalSliderWidth()).isEqualTo(DesktopPanel.DEFAULT_VERTICAL_SLIDER_WIDTH);
        assertThat(container.getHorizontalSliderLocation()).isEqualTo(DesktopPanel.DEFAULT_HORIZONTAL_SLIDER_LOCATION);
        assertThat(container.getVerticalSliderLocation()).isEqualTo(DesktopPanel.DEFAULT_VERTICAL_SLIDER_LOCATION);
    }

    @Test
    void testCustomClassicDesktop() {
        // custom classic desktop with 3 custom panels
        DesktopPanel container = new DesktopPanel(LEFT_PANE_COMPONENT, TOP_RIGHT_PANE_COMPONENT, BOTTOM_RIGHT_PANE_COMPONENT, false);

        // verify
        assertThat(container.getLayout()).isEqualTo(DesktopPanel.LAYOUT_WITHOUT_SLIDERS);
        assertThat(container.getLeftPane()).isNotNull().isEqualTo(LEFT_PANE_COMPONENT);
        assertThat(container.getBottomRightPane()).isNotNull().isEqualTo(BOTTOM_RIGHT_PANE_COMPONENT);
        assertThat(container.getTopRightPane()).isNotNull().isEqualTo(TOP_RIGHT_PANE_COMPONENT);

        // in classic look sliders are not initialized - so they should have default initial (class int default) values
        assertThat(container.getHorizontalSliderWidth()).isZero();
        assertThat(container.getVerticalSliderWidth()).isZero();
        assertThat(container.getHorizontalSliderLocation()).isZero();
        assertThat(container.getVerticalSliderLocation()).isZero();

    }

    @Test
    void testCustomSlidingDesktop() {
        // custom slider desktop with 3 custom panels
        DesktopPanel container = new DesktopPanel(LEFT_PANE_COMPONENT, TOP_RIGHT_PANE_COMPONENT, BOTTOM_RIGHT_PANE_COMPONENT, true);

        // set some attributes so they differ from defaults
        container.setSlidersWidth(20);
        container.setVerticalSliderLocation(300);
        container.setHorizontalSliderLocation(100);

        // verify
        assertThat(container.getLayout()).isEqualTo(DesktopPanel.LAYOUT_WITH_SLIDERS);
        assertThat(container.getLeftPane()).isNotNull().isEqualTo(LEFT_PANE_COMPONENT);
        assertThat(container.getBottomRightPane()).isNotNull().isEqualTo(BOTTOM_RIGHT_PANE_COMPONENT);
        assertThat(container.getTopRightPane()).isNotNull().isEqualTo(TOP_RIGHT_PANE_COMPONENT);
        assertThat(container.getHorizontalSliderWidth()).isEqualTo(20);
        assertThat(container.getVerticalSliderWidth()).isEqualTo(20);
        assertThat(container.getHorizontalSliderLocation()).isEqualTo(100);
        assertThat(container.getVerticalSliderLocation()).isEqualTo(300);

    }

}