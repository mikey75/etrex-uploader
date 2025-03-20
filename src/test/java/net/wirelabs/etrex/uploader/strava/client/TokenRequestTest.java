package net.wirelabs.etrex.uploader.strava.client;

import com.squareup.okhttp.Request;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
class TokenRequestTest {

    @Test
    void getToken() {
        Request r = TokenRequest.createTokenRequest("kaka","secret", "xxx");
        verifyRequest(r);

    }

    @Test
    void refreshToken() {
        Request r = TokenRequest.createRefreshTokenRequest("kaka","secret", "xxx");
        verifyRequest(r);
    }

    private static void verifyRequest(Request r)  {
        assertThat(r).isNotNull();

        assertThat(r.body()).isNotNull();
        assertThat(r.body().contentType()).hasToString("application/x-www-form-urlencoded");

        assertThat(r.method()).isEqualTo("POST");

        assertThat(r.url().getProtocol()).isEqualTo("https");
        assertThat(r.url()).hasToString("https://www.strava.com/oauth/token");

    }
}