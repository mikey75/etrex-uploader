package net.wirelabs.etrex.uploader.gui.settingsdialog.appsettings.lookandfeelcombo;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.utils.SwingUtils;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static javax.swing.UIManager.*;

@Slf4j
public class LookAndFeelComboBox extends JComboBox<String> {
    @Getter
    private final transient List<LookAndFeelInfo> lookAndFeelInfos;

    public LookAndFeelComboBox() {

        lookAndFeelInfos = Stream.of(getInstalledSystemLafs(), getAdditionalLafs())
                .flatMap(Collection::stream).toList();

        // make a map of installed look and feels (laf class, laf name)
        Map<String, String> map = new HashMap<>();

        lookAndFeelInfos.forEach(laf -> {
            map.put(laf.getClassName(), laf.getName());
            addItem(laf.getClassName());
        });


        setRenderer(new LookAndFeelComboRenderer(map));
        // add change listener
        addActionListener(event -> setSelectedLookAndFeel());
    }

    private void setSelectedLookAndFeel() {
        String selectedLookAndFeel = (String) getSelectedItem();

        if (selectedLookAndFeel != null) {
            try {
                // if selected laf is different from already set, reset to new
                if (!getLookAndFeel().getClass().getName().equals(selectedLookAndFeel)) {
                    changeLookAndFeel(selectedLookAndFeel);
                }
            } catch (Exception e) {
                log.error("Could not set look and feel");
            }
        }
    }

    private List<LookAndFeelInfo> getInstalledSystemLafs() {
        return Arrays.stream(getInstalledLookAndFeels()).toList();
    }

    private List<LookAndFeelInfo> getAdditionalLafs() {
        List<LookAndFeelInfo> otherLafs = new ArrayList<>();
        otherLafs.add(new LookAndFeelInfo(FlatIntelliJLaf.NAME, FlatIntelliJLaf.class.getName()));
        otherLafs.add(new LookAndFeelInfo(FlatMacDarkLaf.NAME, FlatMacDarkLaf.class.getName()));
        otherLafs.add(new LookAndFeelInfo(FlatLightLaf.NAME, FlatLightLaf.class.getName()));
        otherLafs.add(new LookAndFeelInfo(FlatMacLightLaf.NAME, FlatMacLightLaf.class.getName()));
        otherLafs.add(new LookAndFeelInfo(FlatDarculaLaf.NAME, FlatDarculaLaf.class.getName()));
        return otherLafs;
    }

    private void changeLookAndFeel(String selectedLookAndFeelClassName) throws ReflectiveOperationException, UnsupportedLookAndFeelException {
        setLookAndFeel(selectedLookAndFeelClassName);
        SwingUtils.updateComponentsUIState();
        repaint();
    }

}
