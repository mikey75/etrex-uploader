package net.wirelabs.etrex.uploader.strava.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.utils.ThreadUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AuthCodeRetrieverTest {
    
    private static final int PORT = getRandomFreeTcpPort();
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String AUTH_CODE = "fakeCode123";

    private static final String expectedUrl = Constants.STRAVA_AUTHORIZATION_URL +
            "?client_id=fakeAppId" +
            "&redirect_uri=http://localhost:" + PORT +
            "&response_type=code" +
            "&approval_prompt=force" +
            "&scope=" + Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE;

    private static final HttpRequest requestWithCode = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + PORT + "/index.html?aaa=b&code=" + AUTH_CODE))
            .GET()
            .build();
    
    private AuthCodeRetriever stravaAuthorizer;


    @AfterEach
    void afterEach() {
        stravaAuthorizer.shutdown();
    }

    @Test
    void shouldReturnEmptyCodeIfNoRedirect() throws IOException, URISyntaxException {
        // given
        stravaAuthorizer = getAuthorizerInstance(2L);
        // when
        String authCode = stravaAuthorizer.getAuthCode();
        // then 
        verify(stravaAuthorizer, times(1)).runDesktopBrowserToAuthorizationUrl(expectedUrl);
        assertThat(authCode).isEmpty();

    }

    @Test
    void shouldAcceptProperCode() throws IOException, URISyntaxException, InterruptedException {
        stravaAuthorizer = getAuthorizerInstance(5L);

        final String[] codeInResponse = new String[1];
        ThreadUtils.runAsync(() -> codeInResponse[0] = stravaAuthorizer.getAuthCode());

        // wait until code interceptor run
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(stravaAuthorizer.isAlive()).isTrue());

        // emulate strava redirecting with authcode
        client.send(requestWithCode, HttpResponse.BodyHandlers.ofString());

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(codeInResponse[0]).isEqualTo(AUTH_CODE);
            verify(stravaAuthorizer, times(1)).runDesktopBrowserToAuthorizationUrl(expectedUrl);
        });

    }
    
    private static AuthCodeRetriever getAuthorizerInstance(Long timeOut) throws IOException, URISyntaxException {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getStravaAppId()).thenReturn("fakeAppId");
        when(configuration.getStravaAuthorizerTimeout()).thenReturn(timeOut);
        AuthCodeRetriever stravaAuthorizer = Mockito.spy(new AuthCodeRetriever(configuration));
        doReturn(PORT).when(stravaAuthorizer).getRandomFreeTcpPort();
        doNothing().when(stravaAuthorizer).runDesktopBrowserToAuthorizationUrl(any());
        return stravaAuthorizer;
    }

    private static int getRandomFreeTcpPort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}