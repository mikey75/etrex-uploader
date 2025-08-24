package net.wirelabs.etrex.uploader.gui.common.base;

import net.miginfocom.swing.MigLayout;
import net.wirelabs.etrex.uploader.gui.common.TestEvent;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.border.*;

import static org.assertj.core.api.Assertions.assertThat;

class BasePanelsTest {

    @Test
    void shouldInitializeBasePanel() {
        BasePanel p = new BasePanel();
        assertThat(p).isInstanceOf(JPanel.class);
        assertThat(p.getBorder()).isNull();
        assertDefaultLayout(p.layout);
    }

    @Test
    void shouldInitializeBaseBorderPanel() {
        BasePanel p = new BasePanel("Bulbulator ver.1.0");
        assertThat(p).isInstanceOf(JPanel.class);
        assertThat(p.getBorder()).isNotNull().isInstanceOf(TitledBorder.class);
        assertDefaultLayout(p.layout);
        assertThat(((TitledBorder) p.getBorder()).getTitle()).isEqualTo("Bulbulator ver.1.0");
    }

    @Test
    void shouldInitializeBaseEventAwarePanel() {
        TestEventAwarePanel p = new TestEventAwarePanel();
        assertThat(p).isInstanceOf(BaseEventAwarePanel.class);
        assertDefaultLayout(p.layout);
        assertThat(p.getBorder()).isNull();
        assertThat(p.subscribeEvents()).containsOnly(TestEvent.TEST_EVENT_1);

    }

    @Test
    void shouldInitializeBaseEventAwareBorderPanel() {
        TestEventAwarePanel p = new TestEventAwarePanel("Przyczłap do Bulbulatora");
        assertThat(p).isInstanceOf(BaseEventAwarePanel.class);
        assertThat(p.getBorder()).isNotNull();
        assertDefaultLayout(p.layout);
        assertThat(((TitledBorder) p.getBorder()).getTitle()).isEqualTo("Przyczłap do Bulbulatora");
        assertThat(p.subscribeEvents()).containsOnly(TestEvent.TEST_EVENT_1);

    }

    @Test
    void shouldInitializeBaseTitledDialog() {
        BaseDialog d = new BaseDialog("kaka");
        assertThat(d.getTitle()).isEqualTo("kaka");
        assertDefaultLayout(d.layout);
    }

    @Test
    void shouldInitializeBaseDialog() {
        BaseDialog d = new BaseDialog();
        assertThat(d.getTitle()).isEmpty();
        assertDefaultLayout(d.layout);
    }

    private static void assertDefaultLayout(MigLayout d) {
        assertThat(d).isNotNull();
        assertThat(d.getLayoutConstraints()).isEqualTo("");
        assertThat(d.getColumnConstraints()).isEqualTo("[grow]");
        assertThat(d.getRowConstraints()).isEqualTo("[grow]");

    }


}