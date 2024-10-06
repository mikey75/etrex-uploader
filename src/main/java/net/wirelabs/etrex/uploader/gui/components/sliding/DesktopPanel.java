package net.wirelabs.etrex.uploader.gui.components.sliding;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class DesktopPanel extends JPanel {

    private final DesktopContainer desktopContainer;
    private final LayoutManager layout = new MigLayout("insets 0", "[grow,fill]", "[grow,fill]");
    private static final String ONE_CELL_ONLY = "cell 0 0,grow"; // one cell only

    public DesktopPanel() {
        desktopContainer = new DesktopContainer();
        setLayout(layout);
        add(desktopContainer, ONE_CELL_ONLY);

    }

    public DesktopPanel(JComponent leftPane, JComponent topRightPane, JComponent bottomRightPane) {
        desktopContainer = new DesktopContainer(leftPane,topRightPane,bottomRightPane);
        setLayout(layout);
        add(desktopContainer, ONE_CELL_ONLY);
    }


    public void setSlidersWidth(int width) {
        // in this panel horiz and vert sliders are always the same size
        desktopContainer.setHorizontalSliderWidth(width);
        desktopContainer.setVerticalSliderWidth(width);
    }

    public void setVerticalSliderLocation(int location) {
        desktopContainer.setVerticalSliderLocation(location);
    }

    public void setHorizontalSliderLocation(int location) {
        desktopContainer.setHorizontalSliderLocation(location);
    }

    public void setLeftPane(JComponent component) {
        desktopContainer.setLeftPane(component);
    }

    public void setTopRightPane(JComponent component) {
        desktopContainer.setTopRightPane(component);
    }

    public void setBottomRightPane(JComponent component) {
        desktopContainer.setBottomRightPane(component);
    }

    public Component getLeftPane() {
        return desktopContainer.getLeftPane();
    }

    public Component getBottomRightPane() {
        return desktopContainer.getBottomRightPane();
    }

    public Component getTopRightPane() {
        return desktopContainer.getTopRightPane();
    }

    public int getHorizontalSliderWidth() {
        return desktopContainer.getHorizontalSliderWidth();
    }

    public int getVerticalSliderWidth() {
        return desktopContainer.getVerticalSliderWidth();
    }

    public int getVerticalSliderLocation() {
        return desktopContainer.getVerticalSliderLocation();
    }

    public int getHorizontalSliderLocation() {
        return desktopContainer.getHorizontalSliderLocation();
    }
}
