package net.wirelabs.etrex.uploader.utils;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.ConstraintParser;
import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;

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

    }

    @Test
    void shouldCreateMigConstraint() {
        CC binaryRepresentation = MigComponentConstraintsWrapper.cell(0,0).flowX().grow().alignX("center");
        CC stringRepresentation = ConstraintParser.parseComponentConstraint("cell 0 0, flowx, grow, alignx center");

        assertThat(binaryRepresentation.getCellX()).isZero().isEqualTo(stringRepresentation.getCellX());
        assertThat(binaryRepresentation.getCellY()).isZero().isEqualTo(stringRepresentation.getCellY());
        assertThat(binaryRepresentation.getFlowX()).isTrue().isEqualTo(stringRepresentation.getFlowX());

        assertThat(binaryRepresentation.getHorizontal().getGrow()).isEqualTo(100).isEqualTo(stringRepresentation.getHorizontal().getGrow()); //  grow() means grow 100% vertical and 100% horizontal
        assertThat(binaryRepresentation.getVertical().getGrow()).isEqualTo(100).isEqualTo(stringRepresentation.getVertical().getGrow());
        assertThat(binaryRepresentation.getHorizontal().getAlign().getValue()).isEqualTo(50L).isEqualTo(stringRepresentation.getHorizontal().getAlign().getValue()); // center is 50

    }

}