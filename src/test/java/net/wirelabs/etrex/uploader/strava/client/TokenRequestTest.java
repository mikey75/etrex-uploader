package net.wirelabs.etrex.uploader.strava.client;

import com.squareup.okhttp.Request;
import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
class TokenRequestTest {

    private static final String TOKEN_URL = "https://localhost:8080/oauth/token";
    private static final String BASE_URL = "https://localhost:8080/strava";
    private static final String STRAVA_CONFIG = "src/test/resources/config/good-strava.properties";

    private static final StravaConfiguration config = new StravaConfiguration(STRAVA_CONFIG);
    private static final StravaClient client = new StravaClient(config, BASE_URL, TOKEN_URL);

    @Test
    void getToken() {
        Request r = client.createTokenRequest("kaka", "secret", "xxx");
        verifyRequest(r);

    }

    @Test
    void refreshToken() {
        Request r = client.createRefreshTokenRequest("kaka", "secret", "xxx");
        verifyRequest(r);
    }

    private static void verifyRequest(Request r)  {
        assertThat(r).isNotNull();

        assertThat(r.body()).isNotNull();
        assertThat(r.body().contentType()).hasToString("application/x-www-form-urlencoded");

        assertThat(r.method()).isEqualTo("POST");

        assertThat(r.url().getProtocol()).isEqualTo("https");
        assertThat(r.url()).hasToString("https://localhost:8080/oauth/token");

    }
}