package net.wirelabs.etrex.uploader.gui.settings;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;

import javax.swing.*;

public class SettingsDialog extends JDialog {


	private final ApplicationSettingsPanel applicationSettingsPanel;
	private final StravaSettingsPanel stravaSettingsPanel;
	private final MapsSettingsPanel mapsSettingsPanel;
	private final AppConfiguration configuration;

	JButton cancelBtn = new JButton("Cancel");
	JButton saveBtn = new JButton("Save settings");


	/**
	 * Create the dialog.
	 */
	public SettingsDialog(AppConfiguration appConfiguration) {
		this.configuration = appConfiguration;

		setTitle("Settings");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setAlwaysOnTop(true);
		applicationSettingsPanel = new ApplicationSettingsPanel(appConfiguration);
		stravaSettingsPanel = new StravaSettingsPanel(appConfiguration);
		mapsSettingsPanel = new MapsSettingsPanel(appConfiguration);


		setBounds(100, 100, 521, 500);
		setLayout(new MigLayout("", "[grow]", "[grow][grow][grow][]"));
		add(applicationSettingsPanel, "cell 0 0,grow");
		add(stravaSettingsPanel, "cell 0 1,grow");
		add(mapsSettingsPanel, "cell 0 2,grow");

		add(saveBtn, "flowx,cell 0 3,alignx right");
		add(cancelBtn, "cell 0 3,alignx right");

		cancelBtn.addActionListener(e -> dispose());
		saveBtn.addActionListener(e -> saveConfigAndClose());
	}

	private void saveConfigAndClose() {
		applicationSettingsPanel.updateConfiguration();
		stravaSettingsPanel.updateConfiguration();
		mapsSettingsPanel.updateConfiguration();
		configuration.save();
		dispose();
	}

}
