package net.wirelabs.etrex.uploader.gui.strava.account;


import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtil;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class ApiUsagePanelTest extends BaseTest {

    @Test
    void shouldDisplayApiUsage() {
        AppConfiguration configuration = new AppConfiguration("src/test/resources/config/test.properties");
        assertThat(configuration).isNotNull();
        assertThat(configuration.getApiUsageWarnPercent()).isEqualTo(85);
        configuration.setApiUsageWarnPercent(50);
        ApiUsagePanel p = new ApiUsagePanel(configuration);
        Map<String, List<String>> headers = new HashMap<>();

        // simulate over the limit
        headers.put("x-ratelimit-limit", List.of("100,1000"));
        headers.put("x-ratelimit-usage", List.of("90,800"));

        StravaUtil.sendRateLimitInfo(headers);


        waitUntilAsserted(Duration.ofSeconds(2), () -> {
            assertThat(p.getDailyLimits().getText()).isEqualTo("(800/1000)");
            assertThat(p.getQuarterLimits().getText()).isEqualTo("(90/100)");
            assertThat(p.getDailyLimits().getForeground()).isEqualTo(Color.RED);
            assertThat(p.getQuarterLimits().getForeground()).isEqualTo(Color.RED);
        });

        // simulate within limit
        headers.put("x-ratelimit-limit", List.of("100,1000"));
        headers.put("x-ratelimit-usage", List.of("20,100"));

        StravaUtil.sendRateLimitInfo(headers);

        waitUntilAsserted(Duration.ofSeconds(2), () -> {
            assertThat(p.getDailyLimits().getText()).isEqualTo("(100/1000)");
            assertThat(p.getQuarterLimits().getText()).isEqualTo("(20/100)");
            assertThat(p.getDailyLimits().getForeground()).isEqualTo(p.getForeground());
            assertThat(p.getQuarterLimits().getForeground()).isEqualTo(p.getForeground());
        });

        // simulate no ratelimit info
        // when response does not contain ratelimit headers - the labels should stay as they were last
        headers = new HashMap<>();
        StravaUtil.sendRateLimitInfo(headers);

        waitUntilAsserted(Duration.ofSeconds(2), () -> {
            assertThat(p.getDailyLimits().getText()).isEqualTo("(100/1000)");
            assertThat(p.getQuarterLimits().getText()).isEqualTo("(20/100)");
        });

        // simulate wrong headers
        Map<String, List<String>> wrongHeaders = new HashMap<>();
        wrongHeaders.put("x-non-existent-key1", List.of("1,1"));
        wrongHeaders.put("x-non-existent-key2", List.of("2,1"));

        StravaUtil.sendRateLimitInfo(wrongHeaders);

        // check the limits are not changed
        waitUntilAsserted(Duration.ofSeconds(2), () -> {
            assertThat(p.getDailyLimits().getText()).isEqualTo("(100/1000)");
            assertThat(p.getQuarterLimits().getText()).isEqualTo("(20/100)");
        });
    }

}