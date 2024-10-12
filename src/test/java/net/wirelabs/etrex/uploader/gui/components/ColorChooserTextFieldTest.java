package net.wirelabs.etrex.uploader.gui.components;

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

    @BeforeEach
    void beforeEach() {
        mockColorChooser = Mockito.mockStatic(JColorChooser.class);
    }

    @AfterEach
    void afterEach() {
        mockColorChooser.close();
    }

    @Test
    void shouldInvokeColorChooser() {

        ColorChooserTextField colorChooserTextField = Mockito.spy(new ColorChooserTextField());

        // if text color is empty - the jcolor selector starts with black
        colorChooserTextField.setText("");
        colorChooserTextField.colorChooserInvokerButton.doClick();
        mockColorChooser.verify(() -> JColorChooser.showDialog(any(), any(), eq(Color.BLACK)));

        // if text is not empty - the jcolor starts with decoded text color
        colorChooserTextField.setText("#0A0A0A");
        colorChooserTextField.colorChooserInvokerButton.doClick();
        mockColorChooser.verify(() -> JColorChooser.showDialog(any(), any(), eq(new Color(0x0A, 0x0A, 0x0A))));

        // now simulate choosing rgb(0xF0, 0xF0, 0xF0) on color selection dialog
        mockColorChooser.when(() -> JColorChooser.showDialog(any(), any(), any())).thenReturn(new Color(0xF0, 0xF0, 0xF0));
        // click
        colorChooserTextField.colorChooserInvokerButton.doClick();
        assertThat(colorChooserTextField.getText()).isEqualTo("#F0F0F0");

    }

}