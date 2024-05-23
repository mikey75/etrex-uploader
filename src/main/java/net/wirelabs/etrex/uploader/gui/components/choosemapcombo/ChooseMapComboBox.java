package net.wirelabs.etrex.uploader.gui.components.choosemapcombo;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ChooseMapComboBox extends JComboBox<File> {

    private final AppConfiguration configuration;

    public ChooseMapComboBox(AppConfiguration configuration) {

        this.configuration = configuration;
        setRenderer(new ChooseMapComboRenderer());
        setModel(new DefaultComboBoxModel<>(getMapDefinitionFiles().toArray(new File[0])));

    }

    public List<File> getMapDefinitionFiles() {

        List<File> configuredMaps = new ArrayList<>();

        // get and sort maps from app's default location
        String currentDir = System.getProperty("user.dir");
        List<File> defaultMaps = FileUtils.listDirectory(new File(currentDir + File.separator + "maps")).stream()
                .sorted(Comparator.comparing(File::getName))
                .collect(Collectors.toList());

        // get and sort usermaps
        File mapsDefinitionsDir = configuration.getUserMapDefinitonsDir().toFile();
        List<File> sortedUserMaps = FileUtils.listDirectory(mapsDefinitionsDir).stream()
                .sorted(Comparator.comparing(File::getName))
                .collect(Collectors.toList());

        // add default map(s)
        configuredMaps.addAll(defaultMaps);
        // add sorted usermaps
        configuredMaps.addAll(sortedUserMaps);
        return configuredMaps;
    }

}