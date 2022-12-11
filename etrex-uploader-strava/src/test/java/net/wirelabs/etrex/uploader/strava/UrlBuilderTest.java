package net.wirelabs.etrex.uploader.strava;

import net.wirelabs.etrex.uploader.strava.utils.UrlBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created 12/10/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class UrlBuilderTest {

    @Test
    void shouldConstructCorrectUrl() {
        String result = UrlBuilder.newBuilder()
                .addQueryParam("DUPA","ZUPA")
                .addQueryParam("pIPA","KAKA")
                .baseUrl("https://www.onet.pl/kaka")
                .build();

        assertThat(result).isEqualTo("https://www.onet.pl/kaka?DUPA=ZUPA&pIPA=KAKA");
    }

    @Test
    void shouldReturnEmptyStringWhenCalledWithNoBuildParams() {
        String result = UrlBuilder.newBuilder().build();
        assertThat(result).isEmpty();
    }

}