package net.wirelabs.etrex.uploader.gui.settings;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;

import javax.swing.*;

public class SettingsDialog extends JDialog {


	JButton cancelBtn = new JButton("Cancel");
	JButton saveBtn = new JButton("Save settings");


	/**
	 * Create the dialog.
	 */
	public SettingsDialog(AppConfiguration appConfiguration) {

		setTitle("Settings");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		JPanel applicationSettingsPanel = new ApplicationSettingsPanel(appConfiguration);
		JPanel stravaSettingsPanel = new StravaSettingsPanel(appConfiguration);
		JPanel mapsSettingsPanel = new MapsSettingsPanel(appConfiguration);


		setBounds(100, 100, 521, 437);
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
		dispose();
	}

}
