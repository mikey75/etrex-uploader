package net.wirelabs.etrex.uploader.strava.client;

import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;

import static org.assertj.core.api.Assertions.assertThat;

class TokenRequestTest extends BaseTest {

    private static final String TOKEN_URL = "https://localhost:8080/oauth/token";
    private static final String BASE_URL = "https://localhost:8080/strava";
    private static final String STRAVA_CONFIG = "src/test/resources/config/good-strava.properties";
    private static final String APP_CONFIG = "src/test/resources/config/test.properties";

    private static final StravaConfiguration config = new StravaConfiguration(STRAVA_CONFIG);
    private static final AppConfiguration appConfig = new AppConfiguration(APP_CONFIG);
    private static final StravaClient client = new StravaClient(updateConfig(), appConfig);

    private static StravaConfiguration updateConfig() {
        config.setBaseTokenUrl(TOKEN_URL);
        config.setBaseUrl(BASE_URL);
        return config;
    }

    @Test
    void getToken() throws IOException,  InterruptedException {
        HttpRequest r = client.createTokenRequest("12345", "secret", "xxx");
        verifyRequest(r, "client_id=12345&client_secret=secret&code=xxx&grant_type=authorization_code");

    }

    @Test
    void refreshToken() throws IOException, InterruptedException {
        HttpRequest r = client.createRefreshTokenRequest("12345", "secret", "xxx");
        verifyRequest(r, "client_id=12345&client_secret=secret&grant_type=refresh_token&refresh_token=xxx");
    }

    private  void verifyRequest(HttpRequest request, String expectedBody) throws IOException, InterruptedException {
        assertThat(request).isNotNull();
        assertThat(request.method()).isEqualTo("POST");
        assertThat(request.uri().toURL().getProtocol()).isEqualTo("https");
        assertThat(request.uri().toURL()).hasToString("https://localhost:8080/oauth/token");

        assertThat(request.bodyPublisher()).isPresent();
        assertThat(request.headers().map().get("Content-type")).contains("application/x-www-form-urlencoded");
        assertThat(request.bodyPublisher()).isPresent();

        String body = readBody(request.bodyPublisher().orElseThrow());
        assertThat(body).isEqualTo(expectedBody);

    }
    public  String readBody(HttpRequest.BodyPublisher bodyPublisher) throws InterruptedException {
        StringBuilder result = new StringBuilder();
        CountDownLatch done = new CountDownLatch(1);

        bodyPublisher.subscribe(new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(1);
            }

            @Override
            public void onNext(ByteBuffer item) {
                byte[] bytes = new byte[item.remaining()];
                item.get(bytes);
                result.append(new String(bytes)); // Default charset; use UTF-8 explicitly if needed
            }

            @Override
            public void onError(Throwable throwable) {
                done.countDown();
            }

            @Override
            public void onComplete() {
                done.countDown();
            }
        });

        done.await(); // Wait until onComplete is called
        return result.toString();
    }


}