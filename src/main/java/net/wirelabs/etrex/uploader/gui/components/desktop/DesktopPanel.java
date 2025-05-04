package net.wirelabs.etrex.uploader.gui.components.desktop;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Component to create application's main desktop panel with optionally resizable components
 * If classicLook is true - the  container does not contain the vertical and horizontal sliders
 * otherwise it allows to resize panels with definable size/location sliders
 * Container looks like this and sits in a panel which you can use in other windows/panels:
 *
 * <pre>
 *     left            right
 *   container       container
 * +-----------+----------------------+
 * |           |                      |
 * | left pane |                      |
 * |           |     top right        |
 * |           |       pane           |
 * |           +----------------------+ <--- horizontal slider
 * |           |                      |
 * |           |                      |
 * |           |     bottom right     |
 * |           |         pane         |
 * +___________+______________________+
 *             ^
 *             +--- vertical slider
 * </pre>
 */
public class DesktopPanel extends JPanel {

    static final LayoutManager LAYOUT_WITH_SLIDERS = new MigLayout("insets 0", "[grow,fill]", "[grow,fill]");
    static final LayoutManager LAYOUT_WITHOUT_SLIDERS = new MigLayout("", "[10%][90%]", "[30%][70%]");

    static final int DEFAULT_HORIZONTAL_SLIDER_WIDTH = 5;
    static final int DEFAULT_VERTICAL_SLIDER_WIDTH = 5;
    static final int DEFAULT_HORIZONTAL_SLIDER_LOCATION = 500;
    static final int DEFAULT_VERTICAL_SLIDER_LOCATION = 100;

    private final JSplitPane rightContainerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private final JSplitPane leftContainerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private static final String ONE_CELL_ONLY = "cell 0 0,grow"; // one cell only - both JSplitPanes put into one and only cell on the panel

    @Getter
    private final boolean slidersEnabled;
    @Getter
    private JComponent leftPane;
    @Getter
    private JComponent bottomRightPane;
    @Getter
    private JComponent topRightPane;
    @Getter
    private int horizontalSliderWidth;
    @Getter
    private int verticalSliderWidth;
    @Getter
    private int horizontalSliderLocation;
    @Getter
    private int verticalSliderLocation;

    // default empty constructor invokes empty classic, slider-less panel
    public DesktopPanel() {
        this(false);
    }

    // default constructor for empty, choosable slider/no-slider look
    public DesktopPanel(boolean slidersEnabled) {
        this(defaultEmptyPanel(), defaultEmptyPanel(), defaultEmptyPanel(), slidersEnabled);
    }

    // custom desktop, set panels and sliders explicitly
    public DesktopPanel(JComponent leftPane, JComponent topRightPane, JComponent bottomRightPane, boolean slidersEnabled) {
        this.slidersEnabled = slidersEnabled;
        if (slidersEnabled) {
            setSlidingDesktop(leftPane, topRightPane, bottomRightPane);
        } else {
            setClassicDesktop(leftPane, topRightPane, bottomRightPane);
        }
    }

    private void setClassicDesktop(JComponent leftPane, JComponent topRightPane, JComponent bottomRightPane) {
        setLayout(LAYOUT_WITHOUT_SLIDERS);
        setPanes(leftPane, topRightPane, bottomRightPane);
    }

    private void setSlidingDesktop(JComponent leftPane, JComponent topRightPane, JComponent bottomRightPane) {
        setLayout(LAYOUT_WITH_SLIDERS);
        // connect two JSplitPanes creating the final component -> left container is now the root of the layout,
        leftContainerPane.setRightComponent(rightContainerPane);
        setPanes(leftPane, topRightPane, bottomRightPane);
        setDefaultSlidersParameters();
        // put the container on the panel
        add(leftContainerPane, ONE_CELL_ONLY);
    }

    private void setDefaultSlidersParameters() {
        // defaults - changeable by setters
        setHorizontalSliderWidth(DEFAULT_HORIZONTAL_SLIDER_WIDTH);
        setVerticalSliderWidth(DEFAULT_VERTICAL_SLIDER_WIDTH);
        setHorizontalSliderLocation(DEFAULT_HORIZONTAL_SLIDER_LOCATION);
        setVerticalSliderLocation(DEFAULT_VERTICAL_SLIDER_LOCATION);
    }

    private void setPanes(JComponent leftPane, JComponent topRightPane, JComponent bottomRightPane) {
        setLeftPane(leftPane);
        setTopRightPane(topRightPane);
        setBottomRightPane(bottomRightPane);
    }

    public void setLeftPane(JComponent component) {
        if (isSlidersEnabled()) {
            leftContainerPane.setLeftComponent(component);
        } else {
            add(component, "cell 0 0 1 3,grow");
        }
        leftPane = component;
    }

    public void setTopRightPane(JComponent component) {
        if (isSlidersEnabled()) {
            rightContainerPane.setTopComponent(component);
        } else {
            add(component, "cell 1 0, grow");
        }
        topRightPane = component;
    }

    public void setBottomRightPane(JComponent component) {
        if (isSlidersEnabled()) {
            rightContainerPane.setBottomComponent(component);
        } else {
            add(component, "cell 1 1 2 2,grow");
        }
        bottomRightPane = component;
    }

    public void setVerticalSliderWidth(int pixels) {
        rightContainerPane.setDividerSize(pixels);
        verticalSliderWidth = pixels;
    }

    public void setHorizontalSliderWidth(int pixels) {
        leftContainerPane.setDividerSize(pixels);
        horizontalSliderWidth = pixels;
    }

    public void setVerticalSliderLocation(int location) {
        leftContainerPane.setDividerLocation(location);
        verticalSliderLocation = location;
    }

    public void setHorizontalSliderLocation(int location) {
        rightContainerPane.setDividerLocation(location);
        horizontalSliderLocation = location;
    }

    public void setSlidersWidth(int pixels) {
        setVerticalSliderWidth(pixels);
        setHorizontalSliderWidth(pixels);
    }

    private static JPanel defaultEmptyPanel() {
        return new JPanel();
    }
}
