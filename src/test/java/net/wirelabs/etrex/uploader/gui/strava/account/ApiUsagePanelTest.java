package net.wirelabs.etrex.uploader.gui.strava.account;


import net.wirelabs.etrex.uploader.strava.utils.StravaUtil;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.spy;

/*
 * Created 12/16/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class ApiUsagePanelTest {

    @Test
    void shouldDisplayApiUsage() {
        ApiUsagePanel p = spy(new ApiUsagePanel());
        Map<String, List<String>> headers = new HashMap<>();

        headers.put("X-RateLimit-Limit", List.of("100,1000"));
        headers.put("X-RateLimit-Usage", List.of("90,800"));

        StravaUtil.sendRateLimitInfo(headers);


        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
                    assertThat(p.getDailyLimits().getText()).isEqualTo("(800/1000)");
                    assertThat(p.getQuarterLimits().getText()).isEqualTo("(90/100)");
                });

        // when response does not contain ratelimit headers - the labels should stay as they were
        headers = new HashMap<>();
        StravaUtil.sendRateLimitInfo(headers);

        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            assertThat(p.getDailyLimits().getText()).isEqualTo("(800/1000)");
            assertThat(p.getQuarterLimits().getText()).isEqualTo("(90/100)");
        });
    }

}