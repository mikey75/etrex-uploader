package net.wirelabs.etrex.uploader.common.utils;

import java.awt.*;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    public static void setGlobalFontSize(Integer fontsize) {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            log.info("Setting font size {} for look {}" , fontsize, info.getName());
            Hashtable<Object, Object> defaults = UIManager.getDefaults();
            Enumeration<Object> keys = defaults.keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                if ((key instanceof String) && (((String) key).endsWith(".font"))) {
                    FontUIResource font = (FontUIResource) UIManager.get(key);
                    FontUIResource newFont = new FontUIResource(font.getFamily(), font.getStyle(), fontsize);
                    defaults.put(key, newFont);
                }
            }
            
        }
    }
}
