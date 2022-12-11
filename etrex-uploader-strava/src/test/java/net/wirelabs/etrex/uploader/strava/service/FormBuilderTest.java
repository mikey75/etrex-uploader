package net.wirelabs.etrex.uploader.strava.service;

import net.wirelabs.etrex.uploader.strava.utils.FormBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created 12/10/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class FormBuilderTest {
    @Test
    void shouldBuildCorrectFormParts() {
        String result = FormBuilder.newBuilder()
                .add("DUPA","ZUPA")
                .add("pIPA","KAKA")
                .build();

        assertThat(result).isEqualTo("DUPA=ZUPA&pIPA=KAKA");
    }

    @Test
    void shouldReturnEmptyStringWhenCalledEmpty() {
        String result = FormBuilder.newBuilder().build();
        assertThat(result).isEmpty();
    }

}