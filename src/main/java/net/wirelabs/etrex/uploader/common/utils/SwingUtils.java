package net.wirelabs.etrex.uploader.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created 10/23/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SwingUtils {

    private static KeyEventDispatcher keyEventDispatcher;
    private static final KeyboardFocusManager keyboardFocusManager  = KeyboardFocusManager.getCurrentKeyboardFocusManager();

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

    /**
     * Register ctrl+v for all text components with focus
     * so that ctrl+v works on jtextboxes etc
     */
    public static void registerPasteController() {

        keyEventDispatcher = e -> {
            if (isKeyVPressed(e) && isCtrlOrMetaPressed(e)) {
                Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                if (focusOwner instanceof JTextComponent textComponent) {
                    try {
                        return pasted(textComponent);
                    } catch (UnsupportedFlavorException | IOException ex) {
                        log.warn("There was an error pasting the content to the text box ");
                    }
                }
            }
            return false;
        };
        keyboardFocusManager.addKeyEventDispatcher(keyEventDispatcher);
    }

    public static void unregisterPasteController() {
        keyboardFocusManager.removeKeyEventDispatcher(keyEventDispatcher);
    }


    private static boolean pasted(JTextComponent textComponent) throws UnsupportedFlavorException, IOException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        String text = (String) contents.getTransferData(DataFlavor.stringFlavor);
        textComponent.replaceSelection(text);
        return true;
    }

    private static boolean isKeyVPressed(KeyEvent e) {
        return e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_V;
    }

    private static boolean isCtrlOrMetaPressed(KeyEvent e) {
        return e.isControlDown() || e.isMetaDown();
    }

}
