package net.wirelabs.etrex.uploader.gui.stravaconnector;

import javax.swing.*;
import java.net.URL;

public class StravaButton extends JButton {
    
    public StravaButton() {
        
        URL iconLocation = getClass().getResource("/images/btn_strava_connectwith_orange.png");
    
        if (iconLocation!= null) {
            ImageIcon icon = new ImageIcon(iconLocation);
            setIcon(icon);    
        } else {
            setText("Connect to strava");    
        }
        
    }

}
