package net.wirelabs.etrex.uploader.gui.components.choosemapcombo;

import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static net.wirelabs.etrex.uploader.common.utils.FileUtils.createDirIfDoesNotExist;
import static org.apache.commons.io.FileUtils.*;
import static org.assertj.core.api.Assertions.assertThat;


class ChooseMapComboBoxTest {

    private final String testConfig = "src/test/resources/test.properties";
    private final File filesDir = new File("target/filesDir");
    private final File content = new File("src/test/resources/testMap.xml");

    private final AppConfiguration appConfiguration = new AppConfiguration(testConfig);

    @BeforeEach
    void before() throws IOException {
        createDirIfDoesNotExist(filesDir);
        cleanDirectory(filesDir);
    }

    @Test
    void shouldContainOnlyDefaultMapWhenMapDirEmpty()  {

        // spawn combobox with empty dir set as configured maps directory
        appConfiguration.setUserMapDefinitonsDir(filesDir.toPath());
        ChooseMapComboBox chooseMapComboBox = new ChooseMapComboBox(appConfiguration);

        // empty map dir should result in one model item, and this item being defaultMap.xml
        assertThat(chooseMapComboBox.getModel().getSize()).isEqualTo(1);
        assertThat(chooseMapComboBox.getModel().getElementAt(0)).hasName("defaultMap.xml");
    }

    @Test
    void shouldContainMoreEntriesWhenMapDirPopulated() throws IOException {
        File file1 = new File(filesDir + File.separator + "alfa.xml");
        File file2 = new File(filesDir + File.separator + "beta.xml");
        File file3 = new File(filesDir + File.separator + "delta.xml");
        copyFile(content, file3);
        copyFile(content, file2);
        copyFile(content, file1);

        appConfiguration.setUserMapDefinitonsDir(filesDir.toPath());
        ChooseMapComboBox chooseMapComboBox = new ChooseMapComboBox(appConfiguration);

        // populated dir should result in populated combobox with 4 items (default map + 3 user files)
        // entries sorted alphabetically, and still defaultMap as first item
        assertThat(chooseMapComboBox.getModel().getSize()).isEqualTo(4);
        assertThat(chooseMapComboBox.getModel().getElementAt(0)).hasName("defaultMap.xml");
        assertThat(chooseMapComboBox.getModel().getElementAt(1)).hasName("alfa.xml");
        assertThat(chooseMapComboBox.getModel().getElementAt(2)).hasName("beta.xml");
        assertThat(chooseMapComboBox.getModel().getElementAt(3)).hasName("delta.xml");

        // choosing items tests
        chooseMapComboBox.setSelectedIndex(1);
        assertThat(chooseMapComboBox.getSelectedItem()).isEqualTo(file1);
        chooseMapComboBox.setSelectedIndex(2);
        assertThat(chooseMapComboBox.getSelectedItem()).isEqualTo(file2);
        chooseMapComboBox.setSelectedIndex(3);
        assertThat(chooseMapComboBox.getSelectedItem()).isEqualTo(file3);
        chooseMapComboBox.setSelectedIndex(0);
        assertThat(((File) chooseMapComboBox.getSelectedItem())).hasName("defaultMap.xml");

    }

}