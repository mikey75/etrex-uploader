package net.wirelabs.etrex.uploader.gui.desktop;

import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;

import java.awt.EventQueue;

import javax.swing.*;



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
		
		JPanel leftPanel = new BasePanel("Browser");
		JPanel stravaPanel = new BasePanel("Strava");
		JPanel mapPanel = new BasePanel("Map!");

		DesktopPanel desktopPanel = new DesktopPanel(leftPanel,stravaPanel, mapPanel,true);
		desktopPanel.setSlidersWidth(5);
		desktopPanel.setHorizontalSliderLocation(100);
		desktopPanel.setVerticalSliderLocation(200);
		add(desktopPanel);
		 
	}

}
