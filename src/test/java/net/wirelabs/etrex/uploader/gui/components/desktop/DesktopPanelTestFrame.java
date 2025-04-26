package net.wirelabs.etrex.uploader.gui.components.desktop;

import java.awt.EventQueue;

import javax.swing.*;

import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;

/**
 * Test application just for local use to test the DesktopPanel component
 */

public class DesktopPanelTestFrame extends JFrame {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {

				DesktopPanelTestFrame frame = new DesktopPanelTestFrame();
				frame.setResizable(true);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				frame.setExtendedState(MAXIMIZED_BOTH);
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DesktopPanelTestFrame() {

		setBounds(100, 100, 450, 300);
		
		JPanel leftPanel = new BorderedPanel("Browser");
		JPanel stravaPanel = new BorderedPanel("Strava");
		JPanel mapPanel = new BorderedPanel("Map!");

		DesktopPanel desktopPanel = new DesktopPanel(leftPanel,stravaPanel, mapPanel,true);
		desktopPanel.setSlidersWidth(5);
		desktopPanel.setHorizontalSliderLocation(100);
		desktopPanel.setVerticalSliderLocation(200);
		getContentPane().add(desktopPanel);
		 
	}

}
