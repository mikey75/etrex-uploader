package net.wirelabs.etrex.uploader.gui.settings;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.gui.components.LaFComboRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.UIManager.*;

@Slf4j
public class LookAndFeelComboBox extends JComboBox<String> {

    public LookAndFeelComboBox() {

        // make a map of installed look and feels (laf class, laf name)
        Map<String, String> map = new HashMap<>();
        Arrays.stream(getInstalledLookAndFeels()).forEach(k -> map.put(k.getClassName(),k.getName()));
        // set the classnames to model
        Arrays.stream(getInstalledLookAndFeels()).forEach(a -> addItem(a.getClassName()));
        // add renderer to print Laf name instead of classname in the JComboBox
        setRenderer(new LaFComboRenderer(map));
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

    private void changeLookAndFeel(String selectedLookAndFeelClassName) throws ReflectiveOperationException, UnsupportedLookAndFeelException {
        setLookAndFeel(selectedLookAndFeelClassName);
        Arrays.stream(Window.getWindows()).forEach(window -> {
            SwingUtilities.updateComponentTreeUI(window);
            window.setMinimumSize(window.getMinimumSize());
        });
    }

}
