package net.wirelabs.etrex.uploader.gui.components.sliding;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * Component to create kind of a desktop with resizable components
 *
 */
public class DesktopContainer extends JSplitPane {

    /* Creates sliding container like this:
     *
     *    left			   right
     *  container	      container
     * ____________________________________
     * |   	       |                      |
     * | left pane |                      |
     * |           |     top right        |
     * |           |       pane           |
     * |           +______________________+ <--- horizontal slider
     * |           |                      |
     * |           |                      |
     * |           |     bottom right     |
     * |           |         pane         |
     * +___________+______________________+
     *
     *             ^
     *             |
     *             +--- vertical slider
     *
     *
     */

    private final JSplitPane rightContainer = new JSplitPane();
    private final JSplitPane leftContainer = this;

    @Getter private int horizontalSliderWidth = 5;
    @Getter private int verticalSliderWidth = 5;
    @Getter private int horizontalSliderLocation = 500;
    @Getter private int verticalSliderLocation = 100;
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
        setHorizontalSliderWidth(horizontalSliderWidth);
        setVerticalSliderWidth(verticalSliderWidth);
        setHorizontalSliderLocation(horizontalSliderLocation);
        setVerticalSliderLocation(verticalSliderLocation);

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
        verticalSliderWidth = widthpx;
    }

    public void setHorizontalSliderWidth(int widthpx) {
        leftContainer.setDividerSize(widthpx);
        horizontalSliderWidth = widthpx;
    }

    public void setVerticalSliderLocation(int location) {
        leftContainer.setDividerLocation(location);
        verticalSliderLocation = location;
    }

    public void setHorizontalSliderLocation(int location) {
        rightContainer.setDividerLocation(location);
        horizontalSliderLocation = location;
    }

}
