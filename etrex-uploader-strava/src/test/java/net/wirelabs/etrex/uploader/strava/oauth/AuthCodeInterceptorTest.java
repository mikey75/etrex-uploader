package net.wirelabs.etrex.uploader.strava.oauth;


import static net.wirelabs.etrex.uploader.common.Constants.AUTHORIZATION_FAIL_MSG;
import static net.wirelabs.etrex.uploader.common.Constants.AUTHORIZATION_OK_MSG;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.strava.client.StravaException;
import org.junit.jupiter.api.Test;

class AuthCodeInterceptorTest {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String AUTH_CODE = "fakeCode123";
    private static final String FAKE_APP_ID = "fakeAppId";

    @Test
    void testIfCorrectOAuthUrlIsPassed() throws IOException {
        OAuth authCodeInterceptor = spy(new OAuth().start());
        
         String expectedUrl = Constants.STRAVA_AUTHORIZATION_URL +
                "?client_id=fakeAppId" +
                "&redirect_uri=http://localhost:" + authCodeInterceptor.getPort() +
                "&response_type=code" +
                "&approval_prompt=force" +
                "&scope=" + Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE;
         
        doNothing().when(authCodeInterceptor).runDesktopBrowserToAuthorizationUrl(any());
        doReturn(1).when(authCodeInterceptor).getAuthCodeTimeoutSeconds();

        StravaException ex = assertThrows(StravaException.class, () -> authCodeInterceptor.getAuthCode(FAKE_APP_ID));
        assertThat(ex).hasMessage("Timed out waiting for code");
        
        verify(authCodeInterceptor).runDesktopBrowserToAuthorizationUrl(expectedUrl);
        authCodeInterceptor.shutdown();
    }

    @Test
    void testNoCodeReceived() throws IOException, InterruptedException {
        OAuth authCodeInterceptor = spy(new OAuth().start());

        HttpRequest requestWithoutCode = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + authCodeInterceptor.getPort() + "/index.html?aaa=b"))
                .GET()
                .build();


        doNothing().when(authCodeInterceptor).runDesktopBrowserToAuthorizationUrl(any());
        doReturn(3).when(authCodeInterceptor).getAuthCodeTimeoutSeconds();
        
        HttpResponse<String> response = client.send(requestWithoutCode, HttpResponse.BodyHandlers.ofString());
        
        StravaException ex = assertThrows(StravaException.class, () -> authCodeInterceptor.getAuthCode(FAKE_APP_ID));
        assertThat(ex).hasMessage("Timed out waiting for code");
        
        assertThat(response.body()).isEqualTo(AUTHORIZATION_FAIL_MSG);
        
    }

    @Test
    void testCorrectCodeInterception() throws IOException, InterruptedException, StravaException {

        OAuth authCodeInterceptor = spy(new OAuth().start());

        HttpRequest requestWithCode = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + authCodeInterceptor.getPort() + "/index.html?aaa=b&code=" + AUTH_CODE))
                .GET()
                .build();

        doNothing().when(authCodeInterceptor).runDesktopBrowserToAuthorizationUrl(any());

        HttpResponse<String> response = client.send(requestWithCode, HttpResponse.BodyHandlers.ofString());
        String code = authCodeInterceptor.getAuthCode(FAKE_APP_ID);
        
        assertThat(code).isEqualTo(AUTH_CODE);
        assertThat(response.body()).isEqualTo(AUTHORIZATION_OK_MSG);
    }

    @Test
    void testCodeReceivedButEmpty() throws IOException, InterruptedException, StravaException {
        OAuth authCodeInterceptor = spy(new OAuth().start());

        HttpRequest requestWithEmptyCode = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + authCodeInterceptor.getPort() + "/index.html?aaa=b&code="))
                .GET()
                .build();

        doNothing().when(authCodeInterceptor).runDesktopBrowserToAuthorizationUrl(any());
        doReturn(3).when(authCodeInterceptor).getAuthCodeTimeoutSeconds();
       
        HttpResponse<String> response = client.send(requestWithEmptyCode, HttpResponse.BodyHandlers.ofString());

        StravaException ex = assertThrows(StravaException.class, () -> authCodeInterceptor.getAuthCode(FAKE_APP_ID));
        assertThat(ex).hasMessage("Timed out waiting for code");
        
        assertThat(response.body()).isEqualTo(AUTHORIZATION_FAIL_MSG);
    }

    @Test
    void testCodeNotReceivedAtAll() throws IOException {
        OAuth authCodeInterceptor = spy(new OAuth().start());
        doNothing().when(authCodeInterceptor).runDesktopBrowserToAuthorizationUrl(any());
        doReturn(3).when(authCodeInterceptor).getAuthCodeTimeoutSeconds();
        
        StravaException ex = assertThrows(StravaException.class, () -> authCodeInterceptor.getAuthCode(FAKE_APP_ID));
        assertThat(ex).hasMessage("Timed out waiting for code");
        
    }

}