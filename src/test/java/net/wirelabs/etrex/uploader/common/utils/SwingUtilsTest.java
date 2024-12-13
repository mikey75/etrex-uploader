package net.wirelabs.etrex.uploader.common.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.plaf.metal.*;
import java.awt.*;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class SwingUtilsTest extends BaseTest {

    @Test
    void shouldCenterComponent() {
        // given
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int panelWidth = 400;
        int panelHeight = 300;

        JPanel panel = new JPanel();
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
        SwingUtils.setGlobalFontSize(22);
        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        assertThat(lafs).isNotEmpty();
        Arrays.stream(lafs).forEach(laf -> verifyLogged("Setting font size 22 for look " + laf.getName()));
    }

}