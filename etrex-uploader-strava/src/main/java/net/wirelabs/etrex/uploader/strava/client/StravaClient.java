package net.wirelabs.etrex.uploader.strava.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.strava.oauth.AuthResponse;
import net.wirelabs.etrex.uploader.strava.utils.MultipartForm;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.OffsetDateTime;

/*
 * Created 11/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class StravaClient {

    private final HttpClient httpClient;
    @Getter
    private Gson jsonParser;
    @Getter
    private final TokenManager tokenManager;

    public StravaClient(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
        this.jsonParser = createJsonParser();
        this.httpClient = HttpClient.newHttpClient();
    }

    private Gson createJsonParser() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, (JsonDeserializer<OffsetDateTime>)
                        (json, type, context) -> OffsetDateTime.parse(json.getAsString()));

        jsonParser = gsonBuilder.create();
        return jsonParser;
    }

    public static boolean apiCallSuccessful(HttpResponse<?> resp) {
        return resp.statusCode() >= 200 && resp.statusCode() < 300;
    }

    public String execute(HttpRequest request) throws StravaException {
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (!apiCallSuccessful(response)) {
                throw new StravaException(response.body());
            }
            return response.body();
        } catch (IOException e) {
            throw new StravaException(e.getMessage());
        } catch (InterruptedException e) {
            log.info("Interrupting httpclient thread");
            Thread.currentThread().interrupt(); // interrupt httpclient thread
            throw new StravaException(e.getMessage());
        }
    }

    public <T> T makePutRequest(String endpointUrl, Object body, Class<T> type) throws StravaException {
        refreshTokenIfExpired();
        HttpRequest request = HttpRequest.newBuilder()
                .headers(commonHeaders())
                .uri(URI.create(endpointUrl))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonParser.toJson(body)))
                .build();

        String result = execute(request);
        return jsonParser.fromJson(result, type);

    }

    private String[] commonHeaders() {
        return new String[]{
                "Authorization", "Bearer " + tokenManager.getAccessToken(),
                "Accept", "application/json"
        };
    }

    public <T> T postForm(String endpointUrl, MultipartForm form, Class<T> type) throws StravaException {

        refreshTokenIfExpired();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "multipart/form-data; boundary=" + form.getBoundary())
                .headers(commonHeaders())
                .uri(URI.create(endpointUrl))
                .POST(HttpRequest.BodyPublishers.ofByteArrays(form.getBody()))
                .build();

        String result = execute(request);
        return jsonParser.fromJson(result, type);


    }

    public <T> T makeGetRequest(String endpointUrl, Class<T> type) throws StravaException {

        refreshTokenIfExpired();
        HttpRequest request = HttpRequest.newBuilder()
                .headers(commonHeaders())
                .uri(URI.create(endpointUrl))
                .GET()
                .build();
        String result = execute(request);
        return jsonParser.fromJson(result, type);


    }

    private void refreshTokenIfExpired() throws StravaException {
        // if a new token is being issued, block other threads wanting to get it until it is saved
        // enforcing a new token is available for subsequent calls
        synchronized (this) {

            if (tokenManager.getTokenExpires() < getCurrentTime()) {
                log.info("Token expired, getting new token");
                String response = execute(tokenManager.buildRefreshTokenRequest());
                RefreshTokenResponse refreshTokenResponse = jsonParser.fromJson(response, RefreshTokenResponse.class);
                tokenManager.updateTokenInfo(refreshTokenResponse.getAccessToken(), refreshTokenResponse.getRefreshToken(), refreshTokenResponse.getExpiresAt());

            }
        }
    }

    private static long getCurrentTime() {
        return Duration.ofMillis(System.currentTimeMillis()).getSeconds();
    }

    public void exchangeAuthCodeForAccessToken(String appId, String clientSecret, String authCode) throws StravaException {

        if (!authCode.isEmpty()) {
            String response = execute(tokenManager.buildGetTokenRequest(appId, clientSecret, authCode));
            AuthResponse authResponse = jsonParser.fromJson(response, AuthResponse.class);
            log.info("Got tokens!");
            tokenManager.updateTokenInfo(authResponse.getAccessToken(), authResponse.getRefreshToken(), authResponse.getExpiresAt());
            tokenManager.updateCredentials(appId,clientSecret);
            
        } else {
            throw new StravaException("Code was empty");
        }
    }
    
}
