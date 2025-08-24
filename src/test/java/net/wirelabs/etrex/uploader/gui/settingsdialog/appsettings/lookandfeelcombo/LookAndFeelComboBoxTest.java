package net.wirelabs.etrex.uploader.gui.settingsdialog.appsettings.lookandfeelcombo;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

import static javax.swing.UIManager.*;
import static org.assertj.core.api.Assertions.assertThat;

class LookAndFeelComboBoxTest extends BaseTest {

    @Test
    void shouldInitializeAndCheckLaFComboBox() {

        int count;
        int selected;

        // create default look and feel combo
        LookAndFeelComboBox lafCombo = new LookAndFeelComboBox();

        // assert default configuration
        count = lafCombo.getItemCount();
        assertThat(count).isPositive();
        selected = lafCombo.getSelectedIndex();
        assertThat(selected).isZero();

        // it should contain all required look and feels
        List<LookAndFeelInfo> info = lafCombo.getLookAndFeelInfos();
        assertThat(info).hasSize(count);
        List<String> classNames = info.stream().map(LookAndFeelInfo::getClassName).toList();
        List<String> modelClassItems = IntStream.range(0, lafCombo.getItemCount()).mapToObj(lafCombo::getItemAt).toList();
        assertThat(modelClassItems).containsExactlyInAnyOrderElementsOf(classNames);


        // make it emit a selection change event
        lafCombo.setSelectedIndex(count - 1);
        // assert selection changed
        assertThat(lafCombo.getSelectedIndex()).isEqualTo(count - 1);

    }
}