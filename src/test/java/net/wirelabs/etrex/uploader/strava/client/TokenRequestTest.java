package net.wirelabs.etrex.uploader.strava.client;

import com.squareup.okhttp.Request;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import okio.Buffer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class TokenRequestTest {

    private static final String TOKEN_URL = "https://localhost:8080/oauth/token";
    private static final String BASE_URL = "https://localhost:8080/strava";
    private static final String STRAVA_CONFIG = "src/test/resources/config/good-strava.properties";
    private static final String APP_CONFIG = "src/test/resources/config/test.properties";

    private static final StravaConfiguration config = new StravaConfiguration(STRAVA_CONFIG);
    private static final AppConfiguration appConfig = new AppConfiguration(APP_CONFIG);
    private static final StravaClient client = new StravaClient(config, appConfig, BASE_URL, TOKEN_URL);

    @Test
    void getToken() throws IOException {
        Request r = client.createTokenRequest("kaka", "secret", "xxx");
        verifyRequest(r, "client_id=kaka&client_secret=secret&code=xxx&grant_type=authorization_code");

    }

    @Test
    void refreshToken() throws IOException {
        Request r = client.createRefreshTokenRequest("kaka", "secret", "xxx");
        verifyRequest(r, "client_id=kaka&client_secret=secret&grant_type=refresh_token&refresh_token=xxx");
    }

    private static void verifyRequest(Request request, String expectedBody) throws IOException {
        assertThat(request).isNotNull();
        assertThat(request.method()).isEqualTo("POST");
        assertThat(request.url().getProtocol()).isEqualTo("https");
        assertThat(request.url()).hasToString("https://localhost:8080/oauth/token");

        assertThat(request.body()).isNotNull();
        assertThat(request.body().contentType()).hasToString("application/x-www-form-urlencoded");

        Buffer buffer = new Buffer();
        request.body().writeTo(buffer);
        String bodyAsString = buffer.readUtf8();
        assertThat(bodyAsString).isEqualTo(expectedBody);


    }
}