package net.wirelabs.etrex.uploader.gui.settingsdialog;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.gui.common.base.BaseDialog;
import net.wirelabs.etrex.uploader.gui.settingsdialog.appsettings.ApplicationSettingsPanel;
import net.wirelabs.etrex.uploader.gui.settingsdialog.mapsettings.MapsSettingsPanel;
import net.wirelabs.etrex.uploader.gui.settingsdialog.stravasettings.StravaSettingsPanel;

import javax.swing.*;

import static net.wirelabs.etrex.uploader.utils.MigComponentConstraintsWrapper.cc;

@Slf4j
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
		add(applicationSettingsPanel, cc().cell(0,0).grow());
		add(stravaSettingsPanel, cc().cell(0,1).grow());
		add(mapsSettingsPanel, cc().cell(0,2).grow());


		add(viewLogs, cc().cell(0,3).alignX("right").flowX());
		add(saveBtn, cc().cell(0,3).alignX("right"));
		add(cancelBtn, cc().cell(0,3).alignX("right"));

		cancelBtn.addActionListener(e -> cancelDialog());
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
		cancelDialog();
		logViewerDialog.open();
	}

	private void cancelDialog() {
		// on cancel button click -> check if look and feel was changed
		// if true -> reset to default and dispose dialog/window
		try {
			String currentLaf = String.valueOf(applicationSettingsPanel.getLookAndFeelSelector().getSelectedItem());
			String defaultLaf = configuration.getLookAndFeelClassName();

			if (!currentLaf.equals(defaultLaf)) {
				SwingUtils.setSystemLookAndFeel(configuration.getLookAndFeelClassName());
				SwingUtils.updateComponentsUIState();
				SwingUtils.setGlobalFontSize(configuration.getFontSize());
			}

		} catch (UnsupportedLookAndFeelException | ReflectiveOperationException e) {
			SwingUtils.errorMsg("Error restoring UI state. Close the app and restart");

		} finally {
			dispose();
		}

	}
}
