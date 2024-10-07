package net.wirelabs.etrex.uploader.gui.components.sliding;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * Component to create kind of a desktop with resizable components
 * Creates sliding container like this:
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
public class DesktopContainer extends JSplitPane {

    private final JSplitPane rightContainer = new JSplitPane();
    private final JSplitPane leftContainer = this;

    private static final int DEFAULT_HORIZONTAL_SLIDER_WIDTH = 5;
    private static final int DEFAULT_VERTICAL_SLIDER_WIDTH = 5;
    private static final int DEFAULT_HORIZONTAL_SLIDER_LOCATION = 500;
    private static final int DEFAULT_VERTICAL_SLIDER_LOCATION = 100;
    
    @Getter private Component leftPane;
    @Getter private Component topRightPane;
    @Getter private Component bottomRightPane;

    public DesktopContainer() {
        this(new JPanel(), new JPanel(), new JPanel());
    }

    public DesktopContainer(JComponent leftPane, JComponent topRightPane, JComponent bottomRightPane) {
        this.leftPane = leftPane;
        this.topRightPane = topRightPane;
        this.bottomRightPane = bottomRightPane;

        rightContainer.setOrientation(JSplitPane.VERTICAL_SPLIT);
        rightContainer.setTopComponent(topRightPane);
        rightContainer.setBottomComponent(bottomRightPane);

        leftContainer.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        leftContainer.setLeftComponent(leftPane);
        leftContainer.setRightComponent(rightContainer);

        // defaults - changeable by setters
        setHorizontalSliderWidth(DEFAULT_HORIZONTAL_SLIDER_WIDTH);
        setVerticalSliderWidth(DEFAULT_VERTICAL_SLIDER_WIDTH);
        setHorizontalSliderLocation(DEFAULT_HORIZONTAL_SLIDER_LOCATION);
        setVerticalSliderLocation(DEFAULT_VERTICAL_SLIDER_LOCATION);

    }

    public void setLeftPane(JComponent component) {
        leftContainer.setLeftComponent(component);
        leftPane = component;
    }

    public void setTopRightPane(JComponent component) {
        rightContainer.setTopComponent(component);
        topRightPane = component;
    }

    public void setBottomRightPane(JComponent component) {
        rightContainer.setBottomComponent(component);
        bottomRightPane = component;
    }

    public void setVerticalSliderWidth(int widthpx) {
        rightContainer.setDividerSize(widthpx);
    }

    public int getVerticalSliderWidth() {
        return rightContainer.getDividerSize();
    }

    public void setHorizontalSliderWidth(int widthpx) {
        leftContainer.setDividerSize(widthpx);
    }
    
    public int getHorizontalSliderWidth() {
        return leftContainer.getDividerSize();
    }
    
    public void setVerticalSliderLocation(int location) {
        leftContainer.setDividerLocation(location);
    }
    
    public int getVerticalSliderLocation() {
        return leftContainer.getDividerLocation();
    }

    public void setHorizontalSliderLocation(int location) {
        rightContainer.setDividerLocation(location);
    }
    
    public int getHorizontalSliderLocation() {
        return rightContainer.getDividerLocation();
    }

}
