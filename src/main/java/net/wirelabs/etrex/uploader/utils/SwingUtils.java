package net.wirelabs.etrex.uploader.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * Created 10/23/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SwingUtils {

    public static void centerComponent(Component c) {
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - c.getWidth()) / 2;
        final int y = (screenSize.height - c.getHeight()) / 2;
        c.setLocation(x, y);
    }

    public static void errorMsg(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void infoMsg(String message) {
        JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static int yesNoMsg(String message) {
        return JOptionPane.showConfirmDialog(null,
                message, "Confirm action", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

    }

    public static int yesNoCancelMsg(String message) {
        return JOptionPane.showConfirmDialog(null,
                message,
                "Confirm action",JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Set system look and feel by classname string
     */
    public static void setSystemLookAndFeel(String lafClassName) throws UnsupportedLookAndFeelException, ReflectiveOperationException {
        UIManager.setLookAndFeel(lafClassName);
    }

    /**
     * Set system look and feel by classname
     */
    public static void setSystemLookAndFeel(Class<? extends LookAndFeel> lafClass) throws UnsupportedLookAndFeelException, ReflectiveOperationException {
        UIManager.setLookAndFeel(lafClass.getName());
    }


    public static void setGlobalFontSize(int fontSize) {

        log.info("Setting global font size {}", fontSize);
        UIDefaults defaults = UIManager.getDefaults();
        Enumeration<Object> keys = defaults.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = defaults.get(key);
            if (value instanceof FontUIResource font) {
                FontUIResource newFont = new FontUIResource(
                        font.getFamily(),
                        font.getStyle(), fontSize);
                UIManager.put(key, newFont);
            }
        }
    }

    public static void updateComponentsUIState() {
        Arrays.stream(Window.getWindows()).forEach(window -> {
            SwingUtilities.updateComponentTreeUI(window);
            window.setMinimumSize(window.getMinimumSize());
            window.repaint();
            window.pack();
        });
    }

}
