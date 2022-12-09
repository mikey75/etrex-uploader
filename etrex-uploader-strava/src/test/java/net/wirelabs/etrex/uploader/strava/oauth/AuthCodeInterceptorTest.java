package net.wirelabs.etrex.uploader.strava.oauth;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static net.wirelabs.etrex.uploader.common.Constants.AUTHORIZATION_FAIL_MSG;
import static net.wirelabs.etrex.uploader.common.Constants.AUTHORIZATION_OK_MSG;
import static org.assertj.core.api.Assertions.assertThat;

class AuthCodeInterceptorTest {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String AUTH_CODE = "fakeCode123";
    private static int port;
    private static HttpRequest requestWithCode;
    private static HttpRequest requestWithoutCode;
    private static HttpRequest requestWithEmptyCode;
    private static AuthCodeInterceptor authCodeInterceptor;

    @BeforeAll
    static void before() throws IOException {
        port = getRandomFreeTcpPort();
        authCodeInterceptor = new AuthCodeInterceptor(port);

        requestWithCode = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/index.html?aaa=b&code=" + AUTH_CODE))
                .GET()
                .build();

        requestWithoutCode = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/index.html?aaa=b"))
                .GET()
                .build();

        requestWithEmptyCode = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/index.html?aaa=b&code="))
                .GET()
                .build();
    }

    @Test
    void testAuthCodeInterception() throws IOException, InterruptedException {


        HttpResponse<String> response;

        response = client.send(requestWithCode, HttpResponse.BodyHandlers.ofString());
        assertThat(authCodeInterceptor.getAuthCodeReady()).isTrue();
        assertThat(authCodeInterceptor.getAuthCode()).isEqualTo(AUTH_CODE);
        assertThat(response.body()).isEqualTo(AUTHORIZATION_OK_MSG);


        response = client.send(requestWithoutCode, HttpResponse.BodyHandlers.ofString());
        assertThat(authCodeInterceptor.getAuthCodeReady()).isFalse();
        assertThat(response.body()).isEqualTo(AUTHORIZATION_FAIL_MSG);


        response =  client.send(requestWithEmptyCode, HttpResponse.BodyHandlers.ofString());
        assertThat(authCodeInterceptor.getAuthCodeReady()).isFalse();
        assertThat(response.body()).isEqualTo(AUTHORIZATION_FAIL_MSG);

        authCodeInterceptor.closeAllConnections();
        authCodeInterceptor.stop();

    }

    private static int getRandomFreeTcpPort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }

}