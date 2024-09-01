package net.wirelabs.etrex.uploader.gui.settings;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.UIManager.*;

@Slf4j
public class LookAndFeelComboBox extends JComboBox<String> {

    public LookAndFeelComboBox() {

        List<LookAndFeelInfo> lafsList = prepareListOfLafs();

        // make a map of installed look and feels (laf class, laf name) and set the classnames to model
        Map<String, String> map = new HashMap<>();
        lafsList.forEach(k -> {
            map.put(k.getClassName(), k.getName());
            addItem(k.getClassName());
        });

        // add renderer to print Laf name instead of classname in the jcombobox
        setRenderer(new BasicComboBoxRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String classname = (String) value;
                setText(map.get(classname));
                return this;
            }
        });
        // add change listener
        addActionListener(event -> setSelectedLookAndFeel());
    }

    @NotNull
    private static List<LookAndFeelInfo> prepareListOfLafs() {
        List<LookAndFeelInfo> lafsList = new ArrayList<>(Arrays.asList(getInstalledLookAndFeels()));
        lafsList.add(new LookAndFeelInfo(FlatIntelliJLaf.NAME, FlatIntelliJLaf.class.getName()));
        lafsList.add(new LookAndFeelInfo(FlatMacDarkLaf.NAME, FlatMacDarkLaf.class.getName()));
        lafsList.add(new LookAndFeelInfo(FlatLightLaf.NAME, FlatLightLaf.class.getName()));
        lafsList.add(new LookAndFeelInfo(FlatMacLightLaf.NAME, FlatMacLightLaf.class.getName()));
        lafsList.add(new LookAndFeelInfo(FlatDarculaLaf.NAME, FlatDarculaLaf.class.getName()));
        return lafsList;
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

    private void changeLookAndFeel(String selectedLookAndFeelClassName) throws ReflectiveOperationException, UnsupportedLookAndFeelException {
        setLookAndFeel(selectedLookAndFeelClassName);
        Arrays.stream(Window.getWindows()).forEach(window -> {
            SwingUtilities.updateComponentTreeUI(window);
            window.setMinimumSize(window.getMinimumSize());
        });
    }

}
