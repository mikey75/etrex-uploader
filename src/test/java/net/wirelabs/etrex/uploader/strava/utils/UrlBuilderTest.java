package net.wirelabs.etrex.uploader.strava.utils;

import net.wirelabs.etrex.uploader.common.utils.ThreadUtils;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UrlBuilderTest extends BaseTest {

    @Test
    void testDefaultEncodingUrlBuilder() {


        String r = UrlBuilder.create()
                .parse("http://www.kaka.pl")
                .addQueryParam("arg1", "value1:subvalue1")
                .addQueryParam("arg2", "param with space")
                .build();

        assertThat(r).isEqualTo("http://www.kaka.pl?arg1=value1%3Asubvalue1&arg2=param+with+space");

    }

    @Test
    void testNonEncodingBuilder() {

        String r = UrlBuilder.create().doNotEncodeParams()
                .parse("http://www.kaka.pl")
                .addQueryParam("arg1", "value1:subvalue1")
                .addQueryParam("arg2", "param with space")
                .build();

        assertThat(r).isEqualTo("http://www.kaka.pl?arg1=value1:subvalue1&arg2=param with space");

    }

    @Test
    void testManyThreadsGeneratingUrls() {


        for (int i = 0; i < 100; i++) {
            String additionNumber = String.valueOf(i);
            ThreadUtils.runAsync(() -> {
                String r = UrlBuilder.create()
                        .parse("http://www.kaka.pl")
                        .addQueryParam("arg1", "value1")
                        .addQueryParam("arg1", "value2" + additionNumber)
                        .build();
                assertThat(r).isEqualTo("http://www.kaka.pl?arg1=value1&arg2=value2" + additionNumber);
            });
        }
    }
}