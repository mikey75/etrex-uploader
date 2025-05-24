package net.wirelabs.etrex.uploader.strava.oauth;

import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static net.wirelabs.etrex.uploader.common.Constants.STRAVA_AUTHORIZATION_FAIL_MSG;
import static net.wirelabs.etrex.uploader.common.Constants.STRAVA_AUTHORIZATION_OK_MSG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthCodeInterceptorTest extends BaseTest {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String AUTH_CODE = "fakeCode123";
    private static final String FAKE_APP_ID = "fakeAppId";
    private static AuthCodeRetriever authCodeRetriever;

    @BeforeEach
    void before() throws IOException {
        authCodeRetriever = Mockito.spy(new AuthCodeRetriever());
        doNothing().when(authCodeRetriever).openSystemBrowser(any());
        verifyLogged("Started auth code interceptor http server on port " + authCodeRetriever.getPort());
        doReturn(2).when(authCodeRetriever).getAuthCodeTimeoutSeconds();
    }

    @AfterEach
    void after() {
        authCodeRetriever.shutdown();
        verifyLogged("auth code interceptor shut down");
    }

    @Test
    void testIfCorrectOAuthUrlIsPassed() throws IOException {

        String expectedUrl = Constants.STRAVA_AUTHORIZATION_URL +
                "?client_id=fakeAppId" +
                "&redirect_uri=http://127.0.0.1:" + authCodeRetriever.getPort() +
                "&response_type=code" +
                "&approval_prompt=force" +
                "&scope=" + Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE;


        runGetAuthAndAssertMessage("Timed out waiting for code", null, null);
        verify(authCodeRetriever).runDesktopBrowserToAuthorizationUrl(expectedUrl);
    }

    @Test
    void testNoCodeReceived() throws IOException, InterruptedException {

        HttpRequest requestWithoutCode = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + authCodeRetriever.getPort() + "/index.html?aaa=b"))
                .GET()
                .build();


        HttpResponse<String> response = client.send(requestWithoutCode, HttpResponse.BodyHandlers.ofString());
        runGetAuthAndAssertMessage("Timed out waiting for code", response.body(), STRAVA_AUTHORIZATION_FAIL_MSG);
    }

    @Test
    void testCorrectCodeInterceptionWithCorrectScope() throws IOException, InterruptedException, StravaException {

        HttpRequest requestWithCode = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + authCodeRetriever.getPort() + "/index.html?aaa=b&code=" + AUTH_CODE + "&scope=" + Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE))
                .GET()
                .build();

        HttpResponse<String> response = client.send(requestWithCode, HttpResponse.BodyHandlers.ofString());
        runGetAuthAndAssertCorrectAuthCodeAndSuccessMessage(response.body());
    }

    @Test
    void testCorrectCodeInterceptionWithIncorrectScope() throws IOException, InterruptedException {

        HttpRequest requestWithCodeAndIncorrectScope = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + authCodeRetriever.getPort() + "/index.html?aaa=b&code=" + AUTH_CODE + "&scope=activity:read"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(requestWithCodeAndIncorrectScope, HttpResponse.BodyHandlers.ofString());
        runGetAuthAndAssertMessage("You must approve all requested authorization scopes", response.body(), STRAVA_AUTHORIZATION_FAIL_MSG);
    }

    @Test
    void testCodeReceivedButEmpty() throws IOException, InterruptedException {

        HttpRequest requestWithEmptyCode = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + authCodeRetriever.getPort() + "/index.html?aaa=b&code="))
                .GET()
                .build();

        HttpResponse<String> response = client.send(requestWithEmptyCode, HttpResponse.BodyHandlers.ofString());
        runGetAuthAndAssertMessage("Timed out waiting for code", response.body(), STRAVA_AUTHORIZATION_FAIL_MSG);
    }

    @Test
    void testCodeNotReceivedAtAll() {
        runGetAuthAndAssertMessage("Timed out waiting for code", null, null);
    }

    private void runGetAuthAndAssertMessage(String message, String responseBody, String bodyMsg) {
        StravaException ex = assertThrows(StravaException.class, () -> authCodeRetriever.getAuthCode(FAKE_APP_ID));
        assertThat(responseBody).isEqualTo(bodyMsg);
        verifyLogged("Starting Strava OAuth process");
        verifyLogged("Redirecting user to strava app authorization page");
        assertThat(ex).hasMessage(message);
    }

    private void runGetAuthAndAssertCorrectAuthCodeAndSuccessMessage(String responseBody) throws IOException, StravaException {
        String code = authCodeRetriever.getAuthCode(FAKE_APP_ID);
        assertThat(code).isEqualTo(AUTH_CODE);
        verifyLogged("Starting Strava OAuth process");
        verifyLogged("Redirecting user to strava app authorization page");
        assertThat(responseBody).isEqualTo(STRAVA_AUTHORIZATION_OK_MSG);
    }
}