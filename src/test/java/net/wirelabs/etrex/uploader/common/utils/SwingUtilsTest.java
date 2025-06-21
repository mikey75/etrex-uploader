package net.wirelabs.etrex.uploader.common.utils;

import net.wirelabs.etrex.uploader.gui.components.BasePanel;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.tools.TestDialog;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SwingUtilsTest extends BaseTest {

    @Test
    void shouldCenterComponent() {
        // given
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int panelWidth = 400;
        int panelHeight = 300;

        BasePanel panel = new BasePanel();
        panel.setSize(panelWidth, panelHeight);

        // when
        SwingUtils.centerComponent(panel);

        // then
        int expectedPanelLocationX = (screenSize.width - panelWidth) / 2;
        int expectedPanelLocationY = (screenSize.height - panelHeight) / 2;

        assertThat(panel.getLocation().x).isEqualTo(expectedPanelLocationX);
        assertThat(panel.getLocation().y).isEqualTo(expectedPanelLocationY);

    }

    @Test
    void shouldInvokeErrorMsg() {
        try (MockedStatic<JOptionPane> mockOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            SwingUtils.errorMsg("Error message");
            mockOptionPane.verify(() -> JOptionPane.showMessageDialog(null, "Error message", "Error", JOptionPane.ERROR_MESSAGE));
        }
    }

    @Test
    void shouldInvokeInfoMsg() {
        try (MockedStatic<JOptionPane> mockOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            SwingUtils.infoMsg("Info message");
            mockOptionPane.verify(() -> JOptionPane.showMessageDialog(null, "Info message", "Info", JOptionPane.INFORMATION_MESSAGE));
        }
    }

    @Test
    void shouldInvokeYesNoMsg() {
        try (MockedStatic<JOptionPane> mockOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            SwingUtils.yesNoMsg("Yes or No?");
            mockOptionPane.verify(() -> JOptionPane.showConfirmDialog(null, "Yes or No?", "Confirm action", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE));
        }
    }

    @Test
    void shouldInvokeYesNoCancelMsg() {
        try (MockedStatic<JOptionPane> mockOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            SwingUtils.yesNoCancelMsg("Yes or No or Cancel?");
            mockOptionPane.verify(() -> JOptionPane.showConfirmDialog(null, "Yes or No or Cancel?", "Confirm action", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE));
        }
    }

    @Test
    void shouldSetSystemLookAndFeelByStringClassname() throws UnsupportedLookAndFeelException, ReflectiveOperationException {
        SwingUtils.setSystemLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        assertThat(UIManager.getLookAndFeel()).isInstanceOf(MetalLookAndFeel.class);
    }

    @Test
    void shouldSetSystemLookAndFeelByClassname() throws UnsupportedLookAndFeelException, ReflectiveOperationException {
        SwingUtils.setSystemLookAndFeel(MetalLookAndFeel.class);
        assertThat(UIManager.getLookAndFeel()).isInstanceOf(MetalLookAndFeel.class);
    }

    @Test
    void shouldSetGlobalFontSize() {
        int changedFontSize = 22;
        SwingUtils.setGlobalFontSize(changedFontSize);
        verifyLogged("Setting global font size " + changedFontSize);

        UIDefaults defaults = UIManager.getDefaults();
        assertThat(defaults).isNotEmpty();

        Enumeration<Object> keys = defaults.keys();
        assertThat(keys.hasMoreElements()).isTrue();

        // collect all system font resources to a list
        List<FontUIResource> fonts = new ArrayList<>();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = defaults.get(key);

            if (value instanceof FontUIResource font) {
                fonts.add(font);
            }
        }

        // assert there are fonts
        assertThat(fonts).isNotEmpty();

        // check all fonts have new size
        assertThat(fonts)
                .extracting(FontUIResource::getSize)
                .containsOnly(changedFontSize);

        UIManager.getDefaults().clear(); // Remove all existing values to restore fontsize, not really needed but for completeness

    }

    @Test
    void shouldReactToKeyPasteEvent() throws AWTException {

        // setup clipboard with wanted text
        final String STRING_TO_PASTE = "ala ma kota 123";
        StringSelection selection = new StringSelection(STRING_TO_PASTE);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);

        Robot robot = new Robot();
        TestDialog dialog = new TestDialog();
        // check initial textField emptiness
        assertThat(dialog.getTextField().getText()).isEmpty();

        robot.setAutoDelay(50); // Slow down robot enough for UI to keep up

        // emit ctrl+v
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_V);

        assertThat(dialog.getTextField().getText()).isEqualTo(STRING_TO_PASTE);


        dialog.setVisible(false);
        SwingUtils.unregisterPasteController();
        dialog.dispose();



    }

    @Test
    void shouldLogErrorWhenPastingNonString() throws AWTException {


        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        clipboard.setContents(new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] { DataFlavor.imageFlavor }; // some flavor that's not string
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.equals(DataFlavor.imageFlavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (flavor.equals(DataFlavor.imageFlavor)) {
                    return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
                }
                throw new UnsupportedFlavorException(flavor);
            }
        }, null);

        Robot robot = new Robot();
        TestDialog dialog = new TestDialog();
        // check initial textField emptiness
        assertThat(dialog.getTextField().getText()).isEmpty();

        robot.setAutoDelay(50); // Slow down robot enough for UI to keep up

        // emit ctrl+v
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_V);

        dialog.setVisible(false);
        SwingUtils.unregisterPasteController();
        dialog.dispose();
        verifyLogged("There was an error pasting the content to the text box");


    }
}