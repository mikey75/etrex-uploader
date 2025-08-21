package net.wirelabs.etrex.uploader.gui.components;

import net.wirelabs.etrex.uploader.common.utils.Sleeper;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Splash extends JFrame {

    private final SplashPanel splashContentPanel = new SplashPanel();

    public Splash() {
        setUndecorated(true);

        if (splashContentPanel.backgroundImage != null) {
            setSize(splashContentPanel.backgroundImage.getWidth(this), splashContentPanel.backgroundImage.getHeight(this));
        } else {
            // since no image present we can't get image size -> set sensible defaults
            setSize(400,300);
        }
        setLocationRelativeTo(null);
        setContentPane(splashContentPanel);
        setVisible(true);

    }


    static class SplashPanel extends BasePanel {

        private final URL imageLocation = getClass().getResource("/images/splash.png");
        // if image is not found (unlikely) we do wish to continue so just get null image
        // so that the dialog still displays, and background image can be final ;)
        private final transient Image backgroundImage = (imageLocation != null) ? new ImageIcon(imageLocation).getImage() : new ImageIcon().getImage();
        private final transient JTextArea textArea = new JTextArea(12, 1);

        public SplashPanel() {

                setLayout(null); // x,y absolute layout

                textArea.setFont(new Font("Arial", Font.BOLD, 11));
                textArea.setForeground(Color.BLACK);
                textArea.setBounds(24, 150, 350, 140);
                textArea.setOpaque(false);
                add(textArea);


        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        void appendLine(String message) {
            textArea.append(message + "\r\n");
            textArea.update(textArea.getGraphics());
        }
    }

    public void update(String message) {
        splashContentPanel.appendLine(message);
        repaint();
        Sleeper.sleepMillis(100);
    }


}