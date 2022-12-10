package net.wirelabs.etrex.uploader.strava.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.squareup.okhttp.*;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Created 11/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public  class StravaClient {

    protected OkHttpClient httpClient;
    public Gson jsonParser;
    protected Configuration configuration;

    public StravaClient(Configuration configuration) {
        this.jsonParser = createJsonParser();
        this.httpClient = new OkHttpClient();
        this.configuration = configuration;
    }

    private Gson createJsonParser() {

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, (JsonDeserializer<OffsetDateTime>)
                        (json, type, context) -> OffsetDateTime.parse(json.getAsString()));

        jsonParser = gsonBuilder.create();
        return jsonParser;
    }

    public String execute(Request request) throws StravaException {
        Response response;
        try {
            response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new StravaException(response.message());
            }
            try (ResponseBody body = response.body()) {
                    return body.string();
            }
        } catch (IOException e) {
            throw new StravaException(e.getMessage());
        }

    }

    public <T> T makePostRequest(String endpointUrl, RequestBody body, Class<T> type) throws StravaException {

        getNewAccessTokenIfExpired();
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + configuration.getStravaAccessToken())
                .header("Accept", "application/json")
                .url(endpointUrl).post(body).build();
        String result = execute(request);
        return jsonParser.fromJson(result, type);


    }

    public <T> T makeGetRequest(String endpointUrl, Class<T> type) throws StravaException {

        getNewAccessTokenIfExpired();
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + configuration.getStravaAccessToken())
                .header("Accept", "application/json")
                .url(endpointUrl)
                .get()
                .build();
        String result = execute(request);
        return jsonParser.fromJson(result, type);


    }

    public <T> T makeParameterizedGetRequest(String endpointUrl, Map<String, String> parameters, Class<T> type) throws StravaException {
        String finalUrl = applyParameterToURL(endpointUrl, parameters);
        return makeGetRequest(finalUrl,type);
    }

    private String applyParameterToURL(String endpointUrl, Map<String,String> parameters) {
        HttpUrl.Builder url = HttpUrl.parse(endpointUrl).newBuilder();
        for (Map.Entry<String,String> entry: parameters.entrySet()) {
            url.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return url.build().toString();
    }

    public void getNewAccessTokenIfExpired() throws StravaException {
        if (configuration.getStravaAccessToken().isBlank() || configuration.getStravaRefreshToken().isBlank()) {
            throw new StravaException("Tokens unavailable, application will run without strava");
        }
        // if a new token is issued, block other threads wanting to get it until it is saved
        // enforcing a new token is available for subsequent calls
        synchronized(this) {

            Long tokenExpiresAt = configuration.getStravaTokenExpires();

            if (tokenExpiresAt < getCurrentTime()) {
                log.info("Token expired, getting new token using refresh token");
                RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(configuration.getStravaAppId(), configuration.getStravaClientSecret(), configuration.getStravaRefreshToken());
                String response = execute(refreshTokenRequest.buildRequest());
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

}
