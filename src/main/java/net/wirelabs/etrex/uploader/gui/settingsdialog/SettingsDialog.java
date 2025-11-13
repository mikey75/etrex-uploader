package net.wirelabs.etrex.uploader.gui.settingsdialog;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.common.base.BaseDialog;
import net.wirelabs.etrex.uploader.gui.settingsdialog.appsettings.ApplicationSettingsPanel;
import net.wirelabs.etrex.uploader.gui.settingsdialog.mapsettings.MapsSettingsPanel;
import net.wirelabs.etrex.uploader.gui.settingsdialog.stravasettings.StravaSettingsPanel;

import javax.swing.*;

import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.*;

@Slf4j
public class SettingsDialog extends BaseDialog {


	private final ApplicationSettingsPanel applicationSettingsPanel;
	private final StravaSettingsPanel stravaSettingsPanel;
	private final MapsSettingsPanel mapsSettingsPanel;
	private final AppConfiguration appConfiguration;
	private final StravaConfiguration stravaConfiguration;
	private final LogViewerDialog logViewerDialog = new LogViewerDialog();


	/**
	 * Create the dialog.
	 */
	public SettingsDialog(StravaConfiguration stravaConfiguration, AppConfiguration appConfiguration) {
		super("Settings","","[grow]","[grow][grow][grow][]");
		this.appConfiguration = appConfiguration;
		this.stravaConfiguration = stravaConfiguration;
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);

		applicationSettingsPanel = new ApplicationSettingsPanel(appConfiguration);
		stravaSettingsPanel = new StravaSettingsPanel(stravaConfiguration);
		mapsSettingsPanel = new MapsSettingsPanel(appConfiguration);
		add(applicationSettingsPanel, cell(0,0).grow());
		add(stravaSettingsPanel, cell(0,1).grow());
		add(mapsSettingsPanel, cell(0,2).grow());

		JButton viewLogs = new JButton("View logs");
		JButton cancelBtn = new JButton("Cancel");
		JButton saveBtn = new JButton("Save settings");

		add(viewLogs, cell(0,3).alignX(RIGHT).flowX());
		add(saveBtn, cell(0,3).alignX(RIGHT));
		add(cancelBtn, cell(0,3).alignX(RIGHT));

		cancelBtn.addActionListener(e -> cancelDialog());
		saveBtn.addActionListener(e -> saveConfigAndClose());
		viewLogs.addActionListener(e -> showLogWindow());
		pack();
	}

	private void saveConfigAndClose() {
		applicationSettingsPanel.updateConfiguration();
		stravaSettingsPanel.updateConfiguration();
		mapsSettingsPanel.updateConfiguration();
		appConfiguration.save();
		stravaConfiguration.save();
		dispose();
	}

	private void showLogWindow() {
		cancelDialog();
		logViewerDialog.open();
	}

	private void cancelDialog() {
		// on cancel button click -> check if look and feel was changed
		// if true -> reset to default and dispose dialog/window
		try {
			String currentLaf = String.valueOf(applicationSettingsPanel.getLookAndFeelSelector().getSelectedItem());
			String defaultLaf = appConfiguration.getLookAndFeelClassName();

			if (!currentLaf.equals(defaultLaf)) {
				SwingUtils.setSystemLookAndFeel(appConfiguration.getLookAndFeelClassName());
				SwingUtils.updateComponentsUIState();
				SwingUtils.setGlobalFontSize(appConfiguration.getFontSize());
			}

		} catch (UnsupportedLookAndFeelException | ReflectiveOperationException e) {
			SwingUtils.errorMsg("Error restoring UI state. Close the app and restart");

		} finally {
			dispose();
		}

	}
}
