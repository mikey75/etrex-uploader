package net.wirelabs.etrex.uploader.gui.common;

import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.gui.common.components.ButtonedTextField;
import net.wirelabs.etrex.uploader.gui.settingsdialog.mapsettings.ColorChooserTextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class ColorChooserTextFieldTest {

    private MockedStatic<JColorChooser> mockColorChooser;
    private ColorChooserTextField colorChooserTextField;

    @BeforeEach
    void beforeEach() {
        mockColorChooser = Mockito.mockStatic(JColorChooser.class);
    }

    @AfterEach
    void afterEach() {
        mockColorChooser.close();
    }

    @Test
    void shouldCreateComponentWithDefaultConstructor() {
        // default constructor -> text empty, "..." as button text, and no icon
        colorChooserTextField = new ColorChooserTextField();
        // verify
        assertThat(colorChooserTextField.getText()).isEmpty();
        assertThat(colorChooserTextField.getButtonText()).isEqualTo("...");
        assertThat(colorChooserTextField.getButtonIcon()).isNull();
    }

    @Test
    void shouldCreateComponentWithCustomConstructor() {
        // text only constructor -> sets text, and color palette icon
        colorChooserTextField = new ColorChooserTextField("Some text");
        // verify
        assertThat(colorChooserTextField.getText()).isEqualTo("Some text");
        assertThat(colorChooserTextField.getButtonText()).isEqualTo(Constants.EMPTY_STRING);
        assertThat(colorChooserTextField.getButtonIcon()).isNotNull();
    }

    @Test
    void testSetters() {
        colorChooserTextField = new ColorChooserTextField();
        colorChooserTextField.setText("Some text");
        colorChooserTextField.setButtonText("AAA");
        colorChooserTextField.setButtonIcon(new ImageIcon());

        assertThat(colorChooserTextField.getText()).isEqualTo("Some text");
        assertThat(colorChooserTextField.getButtonText()).isEqualTo("AAA");
        assertThat(colorChooserTextField.getButtonIcon()).isNotNull();
    }

    @Test
    void shouldCreateComponent() {
        colorChooserTextField = new ColorChooserTextField();
        // verify creation - there are 2 subcomponents - text field and button
        assertThat(colorChooserTextField.getComponents()).hasSize(2);
        assertThat(colorChooserTextField.getComponents()[0]).isInstanceOf(JTextField.class);
        assertThat(colorChooserTextField.getComponents()[1]).isInstanceOf(JButton.class);
    }

    @Test
    void shouldInvokeColorChooser() {

        colorChooserTextField = new ColorChooserTextField();

        // find button component for click() tests,
        // since it is not exposed in component itself
        // must review all subcomponents
        JButton button = findButton(colorChooserTextField);

        // if text color is empty - the color selector starts with black
        button.doClick(); //
        mockColorChooser.verify(() -> JColorChooser.showDialog(any(), any(), eq(Color.BLACK)));

        // if text is not empty - the color starts with decoded text color
        colorChooserTextField.setText("#0A0A0A");
        button.doClick();
        mockColorChooser.verify(() -> JColorChooser.showDialog(any(), any(), eq(new Color(0x0A, 0x0A, 0x0A))));

        // now simulate choosing rgb(0xF0, 0xF0, 0xF0) on color selection dialog
        mockColorChooser.when(() -> JColorChooser.showDialog(any(), any(), any())).thenReturn(new Color(0xF0, 0xF0, 0xF0));
        // click
        button.doClick();
        assertThat(colorChooserTextField.getText()).isEqualTo("#F0F0F0");

    }

    private static JButton findButton(ButtonedTextField component) {
        Component[] foundComponents = component.getComponents();
        for (Component comp : foundComponents) {
            if (comp instanceof JButton) {
                // there's only one so return immediately if found
                return  (JButton) comp;
            }
        }
        throw new IllegalStateException("No button in this component");
    }

}