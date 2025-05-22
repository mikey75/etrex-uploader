package net.wirelabs.etrex.uploader.gui.settings;

import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.gui.components.BaseDialog;
import net.wirelabs.etrex.uploader.gui.components.LogViewerDialog;

import javax.swing.*;

public class SettingsDialog extends BaseDialog {


	private final ApplicationSettingsPanel applicationSettingsPanel;
	private final StravaSettingsPanel stravaSettingsPanel;
	private final MapsSettingsPanel mapsSettingsPanel;
	private final AppConfiguration configuration;
	private final LogViewerDialog logViewerDialog = new LogViewerDialog();
	private final JButton viewLogs = new JButton("View logs");
	private final JButton cancelBtn = new JButton("Cancel");
	private final JButton saveBtn = new JButton("Save settings");


	/**
	 * Create the dialog.
	 */
	public SettingsDialog(AppConfiguration appConfiguration) {
		super("Settings","","[grow]","[grow][grow][grow][]");
		this.configuration = appConfiguration;
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);

		applicationSettingsPanel = new ApplicationSettingsPanel(appConfiguration);
		stravaSettingsPanel = new StravaSettingsPanel(appConfiguration);
		mapsSettingsPanel = new MapsSettingsPanel(appConfiguration);
		add(applicationSettingsPanel, "cell 0 0,grow");
		add(stravaSettingsPanel, "cell 0 1,grow");
		add(mapsSettingsPanel, "cell 0 2,grow");


		add(viewLogs, "flowx,cell 0 3,alignx right");
		add(saveBtn, "cell 0 3, alignx right");
		add(cancelBtn, "cell 0 3,alignx right");

		cancelBtn.addActionListener(e -> dispose());
		saveBtn.addActionListener(e -> saveConfigAndClose());
		viewLogs.addActionListener(e -> showLogWindow());
		pack();
	}

	private void saveConfigAndClose() {
		applicationSettingsPanel.updateConfiguration();
		stravaSettingsPanel.updateConfiguration();
		mapsSettingsPanel.updateConfiguration();
		configuration.save();
		dispose();
	}

	private void showLogWindow() {
		dispose();
		logViewerDialog.open();
	}

}
