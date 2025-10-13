package net.wirelabs.etrex.uploader.gui.desktop;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GarminLogo extends JLabel {

    public GarminLogo() {
        // original logo is some thousands px wide so shrink it without touching original garmin file.
        ImageIcon garminLogoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/garmin-logo.png")));
        int scaledWidth = garminLogoIcon.getIconWidth()/12;
        int scaledHeight = garminLogoIcon.getIconHeight()/12;
        Image scaledImage = garminLogoIcon.getImage().getScaledInstance(scaledWidth, scaledHeight,Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        setIcon(scaledIcon);
    }
}
