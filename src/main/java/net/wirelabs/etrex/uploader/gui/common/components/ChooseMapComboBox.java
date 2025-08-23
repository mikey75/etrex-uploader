package net.wirelabs.etrex.uploader.gui.common.components;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.gui.EtrexUploader;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.io.File;

@Getter
@Slf4j
public class ChooseMapComboBox extends JComboBox<File> {

    private final File[] configuredMapFiles = EtrexUploader.getConfiguredMaps().toArray(new File[0]);
    private final BasicComboBoxRenderer chooseMapComboBoxRenderer = new ChooseMapComboRenderer();
    private final DefaultComboBoxModel<File> chooseMapComboBoxModel = new DefaultComboBoxModel<>(configuredMapFiles);

    public ChooseMapComboBox() {
        setRenderer(chooseMapComboBoxRenderer);
        setModel(chooseMapComboBoxModel);
    }

}