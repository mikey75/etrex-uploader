package net.wirelabs.etrex.uploader.gui.components;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

@Slf4j
public class Splash extends JFrame {

    private final JTextArea textArea = new JTextArea();
    private final JLabel iconLabel = new JLabel();

    private final LayoutManager layout = new MigLayout("", "[][grow]", "[][grow]");

    public Splash() {

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(textArea);

        JLabel appName = new JLabel("Etrex Uploader");

        setUndecorated(true);
        setBounds(0, 0, 418, 137);
        getContentPane().setLayout(layout);
        getContentPane().add(appName, "cell 0 0 2 1,alignx center");
        getContentPane().add(iconLabel, "cell 0 1,aligny top");
        getContentPane().add(scrollPane, "cell 1 1,grow");

        setApplicationLogoIcon();
        setLocationRelativeTo(null);
        setVisible(true);
        
    }

    private void setApplicationLogoIcon() {
        URL iconLocation = getClass().getResource("/images/garmin.png");
        if (iconLocation != null) {
            ImageIcon icon = new ImageIcon(iconLocation);
            iconLabel.setIcon(icon);
            iconLabel.update(iconLabel.getGraphics());
        }
    }

    public void update(String message) {
        appendMessage(message);
    }

    private void appendMessage(String message) {
        textArea.append(message + "\r\n");
        textArea.update(textArea.getGraphics());
    }

    public void close(){
        dispose();
    }
    
}