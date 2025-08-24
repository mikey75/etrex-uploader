package net.wirelabs.etrex.uploader.gui.common.components;

import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;

import javax.swing.*;

public class TwoIconTextLabel extends BasePanel {

    // label consisting of - in this order - [icon1][icon2][Text..........]

    private final JLabel iconOne = new JLabel();
    private final JLabel iconTwo = new JLabel();
    private final JLabel text = new JLabel();

    public TwoIconTextLabel() {

        super("gapx 1, gapy 0, ins 0", "[][][]", "[]");
        add(iconOne, "cell 0 0");
        add(iconTwo, "cell 1 0");
        add(text, "cell 2 0");
        setOpaque(true);

    }

    public void setFirstIcon(Icon icon) {
        iconOne.setIcon(icon);
    }

    public void setSecondIcon(Icon icon) {
        iconTwo.setIcon(icon);
    }

    public void setText(String text) {
        this.text.setText(String.valueOf(text));
    }

}
