package net.wirelabs.etrex.uploader.gui.settings;

import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.jmaps.map.model.map.MapDefinition;
import net.wirelabs.jmaps.map.readers.MapReader;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ChooseMapComboBox extends JComboBox<File> {

    private final AppConfiguration configuration;

    public ChooseMapComboBox(AppConfiguration configuration) {
        super();
        this.configuration = configuration;

        setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("No map files ...");
                    return this;
                }
                File item = (File) value;
                MapDefinition m = MapReader.loadMapDefinitionFile(item);
                setText(m.getName());
                return this;
            }
        });



        File[] files = getMapFiles();
        if (files.length > 0) {
            setModel(new DefaultComboBoxModel<>(files));
        }
    }

     public File[] getMapFiles() {

        try {
            FileUtils.createDirIfDoesNotExist(configuration.getMapDefinitonsDir().toFile());
            Path dir = configuration.getMapDefinitonsDir();
            List<File> files = FileUtils.listDirectory(dir.toFile());
            return files.toArray(new File[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}