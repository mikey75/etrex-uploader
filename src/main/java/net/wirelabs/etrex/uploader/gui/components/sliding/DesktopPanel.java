package net.wirelabs.etrex.uploader.gui.components.sliding;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Component to create kind of a desktop panel with resizable components
 * Creates sliding container like this and puts it into this panel which
 * you can use in other windows/panels:
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

    private static final String ONE_CELL_ONLY = "cell 0 0,grow"; // one cell only - both jsplitpanes put into one and only cell on the panel
    private static final LayoutManager DEFAULT_LAYOUT = new MigLayout("insets 0", "[grow,fill]", "[grow,fill]");

    private static final int DEFAULT_HORIZONTAL_SLIDER_WIDTH = 5;
    private static final int DEFAULT_VERTICAL_SLIDER_WIDTH = 5;
    private static final int DEFAULT_HORIZONTAL_SLIDER_LOCATION = 500;
    private static final int DEFAULT_VERTICAL_SLIDER_LOCATION = 100;

    private final JSplitPane rightContainerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private final JSplitPane leftContainerPane =  new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    public DesktopPanel() {
        this(new JPanel(), new JPanel(), new JPanel());
    }

    public DesktopPanel(JComponent leftPane, JComponent topRightPane, JComponent bottomRightPane) {

        setLayout(DEFAULT_LAYOUT);
        // connect two splitpanes creating the final component,
        // leftcontainer is now the root of the layout,
        leftContainerPane.setRightComponent(rightContainerPane);

        setLeftPane(leftPane);
        setTopRightPane(topRightPane);
        setBottomRightPane(bottomRightPane);

        // defaults - changeable by setters
        setHorizontalSliderWidth(DEFAULT_HORIZONTAL_SLIDER_WIDTH);
        setVerticalSliderWidth(DEFAULT_VERTICAL_SLIDER_WIDTH);
        setHorizontalSliderLocation(DEFAULT_HORIZONTAL_SLIDER_LOCATION);
        setVerticalSliderLocation(DEFAULT_VERTICAL_SLIDER_LOCATION);

        // put the container on the panel
        add(leftContainerPane, ONE_CELL_ONLY);
    }

    public void setLeftPane(JComponent component) {
        leftContainerPane.setLeftComponent(component);
    }

    public Component getLeftPane(){
        return leftContainerPane.getLeftComponent();
    }

    public void setTopRightPane(JComponent component) {
        rightContainerPane.setTopComponent(component);
    }

    public Component getTopRightPane() {
        return rightContainerPane.getTopComponent();
    }

    public void setBottomRightPane(JComponent component) {
        rightContainerPane.setBottomComponent(component);
    }

    public Component getBottomRightPane() {
        return rightContainerPane.getBottomComponent();
    }

    public void setVerticalSliderWidth(int widthpx) {
        rightContainerPane.setDividerSize(widthpx);
    }

    public int getVerticalSliderWidth() {
        return rightContainerPane.getDividerSize();
    }

    public void setHorizontalSliderWidth(int widthpx) {
        leftContainerPane.setDividerSize(widthpx);
    }

    public int getHorizontalSliderWidth() {
        return leftContainerPane.getDividerSize();
    }

    public void setVerticalSliderLocation(int location) {
        leftContainerPane.setDividerLocation(location);
    }

    public int getVerticalSliderLocation() {
        return leftContainerPane.getDividerLocation();
    }

    public void setHorizontalSliderLocation(int location) {
        rightContainerPane.setDividerLocation(location);
    }

    public int getHorizontalSliderLocation() {
        return rightContainerPane.getDividerLocation();
    }

    public void setSlidersWidth(int widthpx) {
        setVerticalSliderWidth(widthpx);
        setHorizontalSliderWidth(widthpx);
    }
}
