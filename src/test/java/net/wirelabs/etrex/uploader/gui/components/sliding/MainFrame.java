package net.wirelabs.etrex.uploader.gui.components.sliding;

import java.awt.EventQueue;

import javax.swing.*;

import net.wirelabs.etrex.uploader.gui.components.BorderedPanel;

/**
 * Test application just for local use to test the DesktopPanel component
 */

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {

				MainFrame frame = new MainFrame();
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
	public MainFrame() {

		setBounds(100, 100, 450, 300);
		
		JPanel leftPanel = new BorderedPanel("Browser");
		JPanel stravaPanel = new BorderedPanel("Strava");
		JPanel mapPanel = new BorderedPanel("Map!");

		DesktopPanel desktopPanel = new DesktopPanel();
		desktopPanel.setSlidersWidth(5);
		desktopPanel.setLeftPane(leftPanel);
		desktopPanel.setBottomRightPane(mapPanel);
		desktopPanel.setTopRightPane(stravaPanel);
		getContentPane().add(desktopPanel);
		 
	}

}
