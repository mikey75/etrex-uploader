package net.wirelabs.etrex.uploader.strava.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.strava.oauth.AuthResponse;
import net.wirelabs.etrex.uploader.strava.utils.MultipartForm;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.OffsetDateTime;

import static net.wirelabs.etrex.uploader.strava.utils.StravaUtils.buildGetTokenRequest;

/*
 * Created 11/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class StravaClient {

    private final HttpClient httpClient;
    private Gson jsonParser;
    private final Configuration configuration;

    public StravaClient(Configuration configuration) {
        this.jsonParser = createJsonParser();
        this.httpClient = HttpClient.newHttpClient();
        this.configuration = configuration;
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
            Thread.currentThread().interrupt(); // interrupt httpclient thread
            throw new StravaException(e.getMessage());
        }

    }

    public <T> T makePutRequest(String endpointUrl, Object body, Class<T> type) throws StravaException {
        getNewAccessTokenIfExpired();
        HttpRequest request = HttpRequest.newBuilder()
                .headers(commonHeaders())
                .uri(URI.create(endpointUrl))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonParser.toJson(body)))
                .build();

        String result = execute(request);
        return jsonParser.fromJson(result, type);

    }

    private String[] commonHeaders() {
        return new String[] {
                "Authorization", "Bearer " + configuration.getStravaAccessToken(),
                "Accept", "application/json"
        };
    }

    public <T> T postForm(String endpointUrl, MultipartForm form, Class<T> type) throws StravaException {

        getNewAccessTokenIfExpired();
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

        getNewAccessTokenIfExpired();
        HttpRequest request = HttpRequest.newBuilder()
                .headers(commonHeaders())
                .uri(URI.create(endpointUrl))
                .GET()
                .build();
        String result = execute(request);
        return jsonParser.fromJson(result, type);


    }

    public void getNewAccessTokenIfExpired() throws StravaException {
        if (configuration.getStravaAccessToken().isBlank() || configuration.getStravaRefreshToken().isBlank()) {
            throw new StravaException("Tokens unavailable, application will run without strava");
        }
        // if a new token is issued, block other threads wanting to get it until it is saved
        // enforcing a new token is available for subsequent calls
        synchronized (this) {

            Long tokenExpiresAt = configuration.getStravaTokenExpires();

            if (tokenExpiresAt < getCurrentTime()) {
                log.info("Token expired, getting new token using refresh token");
                HttpRequest refreshTokenRequest = StravaUtils.buildTokenRefreshRequest(configuration.getStravaAppId(), configuration.getStravaClientSecret(), configuration.getStravaRefreshToken());
                String response = execute(refreshTokenRequest);
                RefreshTokenResponse refreshTokenResponse = jsonParser.fromJson(response, RefreshTokenResponse.class);
                updateTokenInfo(refreshTokenResponse.getAccessToken(), refreshTokenResponse.getRefreshToken(), refreshTokenResponse.getExpiresAt());

            }
        }
    }

    private static long getCurrentTime() {
        return Duration.ofMillis(System.currentTimeMillis()).getSeconds();
    }

    private void updateTokenInfo(String accessToken, String refreshToken, Long expiresAt) {
        log.info("Updating tokens in configuration");
        configuration.setStravaAccessToken(accessToken);
        configuration.setStravaRefreshToken(refreshToken);
        configuration.setStravaTokenExpires(expiresAt);
        configuration.save();
    }

    public void exchangeAuthCodeForAccessToken(String appId, String clientSecret, String authCode) throws StravaException {
        
        if (!authCode.isEmpty()) {
            HttpRequest request = buildGetTokenRequest(appId,clientSecret,authCode);
            String response = execute(request);
            AuthResponse authResponse = jsonParser.fromJson(response, AuthResponse.class);
            
            log.info("Got tokens!");
            updateTokenInfo(authResponse.getAccessToken(),authResponse.getRefreshToken(), authResponse.getExpiresAt());
            // also save app id and client secret for refresh token request
            configuration.setStravaClientSecret(clientSecret);
            configuration.setStravaAppId(appId);
            configuration.save();
            
        }
        
    }
}
