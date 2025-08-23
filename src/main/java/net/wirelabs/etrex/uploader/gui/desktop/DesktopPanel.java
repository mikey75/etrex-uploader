package net.wirelabs.etrex.uploader.gui.desktop;

import lombok.Getter;
import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;

import javax.swing.*;

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
public class DesktopPanel extends BasePanel {

    static final int DEFAULT_HORIZONTAL_SLIDER_WIDTH = 5;
    static final int DEFAULT_VERTICAL_SLIDER_WIDTH = 5;

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
        super(getLayoutConstraints(slidersEnabled), getColumnConstraints(slidersEnabled), getRowConstraints(slidersEnabled)); //
        this.slidersEnabled = slidersEnabled;
        if (slidersEnabled) {
            setSlidingDesktop(leftPane, topRightPane, bottomRightPane);
        } else {
            setClassicDesktop(leftPane, topRightPane, bottomRightPane);
        }
    }

    private void setClassicDesktop(JComponent leftPane, JComponent topRightPane, JComponent bottomRightPane) {
        setPanes(leftPane, topRightPane, bottomRightPane);
    }

    private void setSlidingDesktop(JComponent leftPane, JComponent topRightPane, JComponent bottomRightPane) {
        // connect two JSplitPanes creating the final component -> left container is now the root of the layout,
        leftContainerPane.setRightComponent(rightContainerPane);
        setPanes(leftPane, topRightPane, bottomRightPane);
        // set positions and widths
        setVerticalSliderWidth(DEFAULT_VERTICAL_SLIDER_WIDTH);
        setHorizontalSliderWidth(DEFAULT_HORIZONTAL_SLIDER_WIDTH);
        setVerticalSliderLocation(leftPane.getWidth());
        setHorizontalSliderLocation(topRightPane.getHeight());
        // put the container on the panel
        add(leftContainerPane, ONE_CELL_ONLY);
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

    private static BasePanel defaultEmptyPanel() {
        return new BasePanel();
    }

    private static String getLayoutConstraints(boolean slidersEnabled) {
        return slidersEnabled ? "insets 0" : "";
    }

    private static String getColumnConstraints(boolean slidersEnabled) {
        return slidersEnabled ? "[grow,fill]" : "[10%][90%]";
    }

    private static String getRowConstraints(boolean slidersEnabled) {
        return slidersEnabled ? "[grow,fill]" : "[30%][70%]";
    }

}
