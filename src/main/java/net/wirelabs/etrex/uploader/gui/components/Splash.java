package net.wirelabs.etrex.uploader.gui.components;

import net.wirelabs.etrex.uploader.common.utils.Sleeper;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Splash extends JFrame {

    private final SplashPanel splashContentPanel = new SplashPanel();

    public Splash() {

        int height = splashContentPanel.backgroundImage.getHeight(this);
        int width = splashContentPanel.backgroundImage.getWidth(this);

        setUndecorated(true);
        setSize(width, height);
        setLocationRelativeTo(null);
        setContentPane(splashContentPanel);
        setVisible(true);

    }


    static class SplashPanel extends JPanel {

        private transient Image backgroundImage;
        private final transient JTextArea textArea = new JTextArea(12, 1);

        public SplashPanel() {
            URL imageLocation = getClass().getResource("/images/splash.png");

            if (imageLocation != null) {
                setLayout(null); // x,y absolute layout
                backgroundImage = new ImageIcon(imageLocation).getImage();
            }

            textArea.setFont(new Font("Arial", Font.BOLD, 11));
            textArea.setBounds(12, 150, 350, 140);
            textArea.setOpaque(false);
            add(textArea);

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public void update(String message) {
        appendMessage(message);
        Sleeper.sleepMillis(100);
    }

    private void appendMessage(String message) {
        splashContentPanel.textArea.append(message + "\r\n");
        splashContentPanel.textArea.update(splashContentPanel.textArea.getGraphics());
    }

    public void close(){
        dispose();
    }
}