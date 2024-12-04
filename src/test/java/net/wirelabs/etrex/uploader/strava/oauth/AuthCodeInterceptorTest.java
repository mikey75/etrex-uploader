package net.wirelabs.etrex.uploader.strava.oauth;

import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.tools.BaseTest;
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
    }

    @Test
    void testIfCorrectOAuthUrlIsPassed() throws IOException {

        String expectedUrl = Constants.STRAVA_AUTHORIZATION_URL +
                "?client_id=fakeAppId" +
                "&redirect_uri=http://127.0.0.1:" + authCodeRetriever.getPort() +
                "&response_type=code" +
                "&approval_prompt=force" +
                "&scope=" + Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE;

        doNothing().when(authCodeRetriever).openSystemBrowser(any());
        doReturn(1).when(authCodeRetriever).getAuthCodeTimeoutSeconds();

        StravaException ex = assertThrows(StravaException.class, () -> authCodeRetriever.getAuthCode(FAKE_APP_ID));
        assertThat(ex).hasMessage("Timed out waiting for code");

        verify(authCodeRetriever).runDesktopBrowserToAuthorizationUrl(expectedUrl);
        verifyLogged("Redirecting user to strava app authorization page");
        authCodeRetriever.shutdown();
    }

    @Test
    void testNoCodeReceived() throws IOException, InterruptedException {

        HttpRequest requestWithoutCode = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + authCodeRetriever.getPort() + "/index.html?aaa=b"))
                .GET()
                .build();

        doNothing().when(authCodeRetriever).openSystemBrowser(any());
        doReturn(3).when(authCodeRetriever).getAuthCodeTimeoutSeconds();

        HttpResponse<String> response = client.send(requestWithoutCode, HttpResponse.BodyHandlers.ofString());

        StravaException ex = assertThrows(StravaException.class, () -> authCodeRetriever.getAuthCode(FAKE_APP_ID));
        assertThat(ex).hasMessage("Timed out waiting for code");
        verifyLogged("Redirecting user to strava app authorization page");

        assertThat(response.body()).isEqualTo(STRAVA_AUTHORIZATION_FAIL_MSG);
        authCodeRetriever.shutdown();
    }

    @Test
    void testCorrectCodeInterceptionWithCorrectScope() throws IOException, InterruptedException, StravaException {

        HttpRequest requestWithCode = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + authCodeRetriever.getPort() + "/index.html?aaa=b&code=" + AUTH_CODE + "&scope=" + Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE))
                .GET()
                .build();
        doNothing().when(authCodeRetriever).openSystemBrowser(any());

        HttpResponse<String> response = client.send(requestWithCode, HttpResponse.BodyHandlers.ofString());
        String code = authCodeRetriever.getAuthCode(FAKE_APP_ID);

        assertThat(code).isEqualTo(AUTH_CODE);
        assertThat(response.body()).isEqualTo(STRAVA_AUTHORIZATION_OK_MSG);
        verifyLogged("Redirecting user to strava app authorization page");
        authCodeRetriever.shutdown();
    }

    @Test
    void testCorrectCodeInterceptionWithIncorrectScope() throws IOException, InterruptedException {

        HttpRequest requestWithCodeAndIncorrectScope = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + authCodeRetriever.getPort() + "/index.html?aaa=b&code=" + AUTH_CODE + "&scope=activity:read"))
                .GET()
                .build();
        doNothing().when(authCodeRetriever).openSystemBrowser(any());

        HttpResponse<String> response = client.send(requestWithCodeAndIncorrectScope, HttpResponse.BodyHandlers.ofString());

        StravaException thrown = assertThrows(StravaException.class, () -> authCodeRetriever.getAuthCode(FAKE_APP_ID));

        assertThat(thrown).hasMessage("You must approve all requested authorization scopes");
        verifyLogged("Redirecting user to strava app authorization page");
        assertThat(response.body()).isEqualTo(STRAVA_AUTHORIZATION_FAIL_MSG);
        authCodeRetriever.shutdown();
    }

    @Test
    void testCodeReceivedButEmpty() throws IOException, InterruptedException {

        HttpRequest requestWithEmptyCode = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + authCodeRetriever.getPort() + "/index.html?aaa=b&code="))
                .GET()
                .build();

        doNothing().when(authCodeRetriever).openSystemBrowser(any());
        doReturn(3).when(authCodeRetriever).getAuthCodeTimeoutSeconds();

        HttpResponse<String> response = client.send(requestWithEmptyCode, HttpResponse.BodyHandlers.ofString());

        StravaException ex = assertThrows(StravaException.class, () -> authCodeRetriever.getAuthCode(FAKE_APP_ID));
        assertThat(ex).hasMessage("Timed out waiting for code");

        assertThat(response.body()).isEqualTo(STRAVA_AUTHORIZATION_FAIL_MSG);
        verifyLogged("Redirecting user to strava app authorization page");
        authCodeRetriever.shutdown();
    }

    @Test
    void testCodeNotReceivedAtAll() throws IOException {

        doNothing().when(authCodeRetriever).openSystemBrowser(any());
        doReturn(3).when(authCodeRetriever).getAuthCodeTimeoutSeconds();

        StravaException ex = assertThrows(StravaException.class, () -> authCodeRetriever.getAuthCode(FAKE_APP_ID));
        assertThat(ex).hasMessage("Timed out waiting for code");
        verifyLogged("Redirecting user to strava app authorization page");
        authCodeRetriever.shutdown();

    }

}