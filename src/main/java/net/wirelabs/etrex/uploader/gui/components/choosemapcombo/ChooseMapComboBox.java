package net.wirelabs.etrex.uploader.gui.components.choosemapcombo;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.gui.EtrexUploader;

import javax.swing.*;
import java.io.File;

@Slf4j
public class ChooseMapComboBox extends JComboBox<File> {

    public ChooseMapComboBox() {
        setRenderer(new ChooseMapComboRenderer());
        setModel(new DefaultComboBoxModel<>(EtrexUploader.configuredMaps.toArray(new File[0])));
    }

}