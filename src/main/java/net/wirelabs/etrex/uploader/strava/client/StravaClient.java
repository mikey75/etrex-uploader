package net.wirelabs.etrex.uploader.strava.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.squareup.okhttp.*;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.strava.model.SportType;
import net.wirelabs.etrex.uploader.strava.model.Upload;
import net.wirelabs.etrex.uploader.strava.oauth.AuthResponse;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;

/*
 * Created 11/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class StravaClient  {

    private final Gson jsonParser;
    private final OkHttpClient httpClient;
    private final TokenManager tokenManager;

    public StravaClient(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
        this.jsonParser = createJsonParser();
        this.httpClient = new OkHttpClient();

    }
    private Gson createJsonParser() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, (JsonDeserializer<OffsetDateTime>)
                        (json, type, context) -> OffsetDateTime.parse(json.getAsString()));

        return gsonBuilder.create();
    }

    public String execute(Request request) throws StravaException {
        Response response;
        try {
            response = httpClient.newCall(request).execute();
            try (ResponseBody body = response.body()) {
                if (!response.isSuccessful()) {
                    throw new StravaException(body.string());
                }
                StravaUtils.sendRateLimitInfo(response.headers().toMultimap());
                return body.string();
            }
        } catch (IOException e) {
            throw new StravaException(e.getMessage());
        }
    }

    public <T> T makeGetRequest(String endpointUrl, Class<T> type, Map<String,String> parameters) throws StravaException {

        if (parameters != null) {
            endpointUrl = decorateUrlWithParams(endpointUrl, parameters);
        }
        refreshTokenIfExpired();
        Request request = new Request.Builder()
                .headers(commonHeaders())
                .url(HttpUrl.parse(endpointUrl))
                .get()
                .build();
        String result = execute(request);
        return jsonParser.fromJson(result, type);
    }

    private String decorateUrlWithParams(String enpointUlr, Map<String, String> parameters) {
        HttpUrl.Builder url = HttpUrl.parse(enpointUlr).newBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            url.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return url.build().toString();
    }

    private Headers commonHeaders() {

        return new Headers.Builder()
                .add("Authorization", "Bearer " + tokenManager.getAccessToken())
                .add("Accept", "application/json")
                .build();
    }

    public <T> T makePutRequest(String endpointUrl, Object body, Class<T> type) throws StravaException {
        refreshTokenIfExpired();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonParser.toJson(body));
        Request request = new Request.Builder()
                .headers(commonHeaders())
                .url(endpointUrl)
                .put(requestBody)
                .build();

        String result = execute(request);
        return jsonParser.fromJson(result, type);

    }

    public Upload uploadActivity(File file, String name, String description, SportType sportType) throws StravaException {

        String dataType = StravaUtils.guessContentTypeFromFile(file);
        String fileFormat = StravaUtils.guessFileFormat(file);

        RequestBody body = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(dataType), file))
                .addFormDataPart("data_type", fileFormat)
                .addFormDataPart("name", name)
                .addFormDataPart("sport_type", sportType.getValue())
                .addFormDataPart("description", description)
                .build();

        refreshTokenIfExpired();
        Request request = new Request.Builder()
                .headers(commonHeaders())
                .url(Constants.STRAVA_BASE_URL + "/uploads")
                .post(body)
                .build();

        String result = execute(request);
        return jsonParser.fromJson(result, Upload.class);

    }


    // token operations
    public void exchangeAuthCodeForAccessToken(String appId, String clientSecret, String authCode) throws StravaException {

        if (!authCode.isEmpty()) {
            String response = execute(tokenManager.buildGetTokenRequest(appId, clientSecret, authCode));
            AuthResponse authResponse = jsonParser.fromJson(response, AuthResponse.class);
            log.info("Got tokens!");
            tokenManager.updateTokenInfo(authResponse.getAccessToken(), authResponse.getRefreshToken(), authResponse.getExpiresAt());
            tokenManager.updateCredentials(appId, clientSecret);

        } else {
            throw new StravaException("Code was empty");
        }
    }
    private void refreshTokenIfExpired() throws StravaException {
        // if a new token is being issued, block other threads wanting to get it until it is saved
        // enforcing a new token is available for subsequent calls
        synchronized (this) {
            long currentTime = Duration.ofMillis(System.currentTimeMillis()).getSeconds();
            if (tokenManager.getTokenExpires() < currentTime) {
                log.info("Token expired, getting new token");
                String response = execute(tokenManager.buildRefreshTokenRequest());
                RefreshTokenResponse refreshTokenResponse = jsonParser.fromJson(response, RefreshTokenResponse.class);
                tokenManager.updateTokenInfo(refreshTokenResponse.getAccessToken(), refreshTokenResponse.getRefreshToken(), refreshTokenResponse.getExpiresAt());

            }
        }
    }

}
