package net.wirelabs.etrex.uploader.gui.components.sliding;

import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThat;

class DesktopPanelTest {

    public static final JComponent LEFT_PANE_COMPONENT_1 = new JLabel("l1");
    public static final JComponent TOP_RIGHT_PANE_COMPONENT_1 = new JLabel("l2");
    public static final JComponent BOTTOM_RIGHT_PANE_COMPONENT_1 = new JLabel("l3");

    public static final JComponent LEFT_PANE_COMPONENT_2 = new BorderedPanel("left pane");
    public static final JComponent TOP_RIGHT_PANE_COMPONENT_2 = new BorderedPanel("top right pane");
    public static final JComponent BOTTOM_RIGHT_PANE_COMPONENT_2 = new BorderedPanel("bottom right pane");

    @Test
    void testDefaultConstructor() {

        DesktopPanel container = new DesktopPanel();

        assertThat(container.getLeftPane()).isNotNull().isInstanceOf(JPanel.class);
        assertThat(container.getBottomRightPane()).isNotNull().isInstanceOf(JPanel.class);
        assertThat(container.getTopRightPane()).isNotNull().isInstanceOf(JPanel.class);
        assertThat(container.getHorizontalSliderWidth()).isEqualTo(5);
        assertThat(container.getVerticalSliderWidth()).isEqualTo(5);
        assertThat(container.getHorizontalSliderLocation()).isEqualTo(500);
        assertThat(container.getVerticalSliderLocation()).isEqualTo(100);

    }

    @Test
    void testCustomConstructor() {

        DesktopPanel container = new DesktopPanel(LEFT_PANE_COMPONENT_1, TOP_RIGHT_PANE_COMPONENT_1, BOTTOM_RIGHT_PANE_COMPONENT_1);
        container.setSlidersWidth(20);
        container.setVerticalSliderLocation(300);
        container.setHorizontalSliderLocation(100);

        assertThat(container.getLeftPane()).isNotNull().isEqualTo(LEFT_PANE_COMPONENT_1);
        assertThat(container.getBottomRightPane()).isNotNull().isEqualTo(BOTTOM_RIGHT_PANE_COMPONENT_1);
        assertThat(container.getTopRightPane()).isNotNull().isEqualTo(TOP_RIGHT_PANE_COMPONENT_1);
        assertThat(container.getHorizontalSliderWidth()).isEqualTo(20);
        assertThat(container.getVerticalSliderWidth()).isEqualTo(20);
        assertThat(container.getHorizontalSliderLocation()).isEqualTo(100);
        assertThat(container.getVerticalSliderLocation()).isEqualTo(300);

    }

    @Test
    void testCustomInitialization() {

        DesktopPanel container = new DesktopPanel();

        container.setLeftPane(LEFT_PANE_COMPONENT_2);
        container.setTopRightPane(TOP_RIGHT_PANE_COMPONENT_2);
        container.setBottomRightPane(BOTTOM_RIGHT_PANE_COMPONENT_2);

        assertThat(container.getLeftPane()).isNotNull().isEqualTo(LEFT_PANE_COMPONENT_2);
        assertThat(container.getBottomRightPane()).isNotNull().isEqualTo(BOTTOM_RIGHT_PANE_COMPONENT_2);
        assertThat(container.getTopRightPane()).isNotNull().isEqualTo(TOP_RIGHT_PANE_COMPONENT_2);

        assertThat(container.getHorizontalSliderWidth()).isEqualTo(5);
        assertThat(container.getVerticalSliderWidth()).isEqualTo(5);

        assertThat(container.getHorizontalSliderLocation()).isEqualTo(500);
        assertThat(container.getVerticalSliderLocation()).isEqualTo(100);
    }

}