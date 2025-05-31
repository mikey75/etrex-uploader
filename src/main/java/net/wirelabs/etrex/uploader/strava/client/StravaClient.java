package net.wirelabs.etrex.uploader.strava.client;

import com.squareup.okhttp.*;
import com.strava.model.SportType;
import com.strava.model.Upload;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtil;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import static net.wirelabs.etrex.uploader.strava.utils.JsonUtil.deserialize;
import static net.wirelabs.etrex.uploader.strava.utils.JsonUtil.serialize;

/*
 * Created 12/17/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class StravaClient  {

    private final OkHttpClient httpClient;
    private final StravaConfiguration configuration;
    @Getter
    private final String baseUrl;
    @Getter
    private final String baseTokenUrl;

    public StravaClient(StravaConfiguration configuration, String baseUrl, String baseTokenUrl) {
        this.configuration = configuration;
        this.baseUrl = baseUrl;
        this.baseTokenUrl = baseTokenUrl;
        this.httpClient = new OkHttpClient();
    }

    public String execute(Request request) throws StravaException {
        Response response;
        try {
            log.debug("[Strava request] {}", request.url());
            response = httpClient.newCall(request).execute();
            try (ResponseBody body = response.body()) {
                if (!response.isSuccessful()) {
                    throw new StravaException(body.string());
                }
                StravaUtil.sendRateLimitInfo(response.headers().toMultimap());
                return body.string();
            }
        } catch (IOException e) {
            throw new StravaException(e.getMessage());
        }
    }

    public <T> T makeGetRequest(String endpointUrl, Class<T> type, Map<String, String> parameters) throws StravaException {

        if (parameters != null) {
            endpointUrl = decorateUrlWithParams(endpointUrl, parameters);
        }

        refreshTokenIfExpired();

        Request request = new Request.Builder()
                .headers(authHeader())
                .url(HttpUrl.parse(endpointUrl))
                .get()
                .build();

        String result = execute(request);
        return deserialize(result, type);
    }

    private String decorateUrlWithParams(String endpointUrl, Map<String, String> parameters) {
        HttpUrl.Builder url = HttpUrl.parse(endpointUrl).newBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            url.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return url.build().toString();
    }

    private Headers authHeader() {
        return new Headers.Builder()
                .add("Authorization", "Bearer " + configuration.getStravaAccessToken())
                .build();
    }

    public <T> T makePutRequest(String endpointUrl, Object body, Class<T> type) throws StravaException {

        refreshTokenIfExpired();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                serialize(body));

        Request request = new Request.Builder()
                .headers(authHeader())
                .url(endpointUrl)
                .put(requestBody)
                .build();

        String result = execute(request);
        return deserialize(result, type);

    }

    public Upload uploadActivityRequest(File file, String name, String description, SportType sportType, boolean virtual, boolean commute) throws StravaException {

        RequestBody body = buildUploadRequest(file, name, description, sportType, virtual, commute);

        refreshTokenIfExpired();

        Request request = new Request.Builder()
                .headers(authHeader())
                .url(baseUrl + "/uploads")
                .post(body)
                .build();

        String result = execute(request);
        return deserialize(result, Upload.class);

    }

    private RequestBody buildUploadRequest(File file, String name, String description, SportType sportType, boolean virtual, boolean commute) throws StravaException {

        String dataType = StravaUtil.guessContentTypeFromFileName(file);
        String fileFormat = StravaUtil.guessUploadFileFormat(file);

        return new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(dataType), file))
                .addFormDataPart("data_type", fileFormat)
                .addFormDataPart("name", name)
                .addFormDataPart("sport_type", sportType.getValue())
                .addFormDataPart("description", description)
                .addFormDataPart("external_id", name + System.currentTimeMillis())
                .addFormDataPart("commute", String.valueOf(commute))
                .addFormDataPart("trainer", String.valueOf(virtual))
                .build();
    }

    private void refreshExpired(RefreshTokenResponse response) {
        configuration.setStravaAccessToken(response.getAccessToken());
        configuration.setStravaRefreshToken(response.getRefreshToken());
        configuration.setStravaTokenExpires(response.getExpiresAt());
        configuration.save();
    }

    private void updateToken(TokenResponse tokenInfo) {
        configuration.setStravaAccessToken(tokenInfo.getAccessToken());
        configuration.setStravaRefreshToken(tokenInfo.getRefreshToken());
        configuration.setStravaTokenExpires(tokenInfo.getExpiresAt());
        configuration.save();
    }

    private void updateCredentials(String appId, String clientSecret) {
        configuration.setStravaAppId(appId);
        configuration.setStravaClientSecret(clientSecret);
        configuration.save();
    }

    private void refreshTokenIfExpired() throws StravaException {
        synchronized (this) {
            long currentTime = Duration.ofMillis(System.currentTimeMillis()).getSeconds();
            if (configuration.getStravaTokenExpires() < currentTime) {
                log.info("Refreshing token");
                Request request = createRefreshTokenRequest(configuration.getStravaAppId(), configuration.getStravaClientSecret(), configuration.getStravaRefreshToken());
                String response = execute(request);
                RefreshTokenResponse refreshTokenResponse = deserialize(response, RefreshTokenResponse.class);
                refreshExpired(refreshTokenResponse);
            }
        }
    }

    public void exchangeAuthCodeForAccessToken(String appId, String clientSecret, String authCode) throws StravaException {

        if (!authCode.isEmpty()) {
            Request tokenRequest = createTokenRequest(appId, clientSecret, authCode);
            String response = execute(tokenRequest);
            TokenResponse tokenResponse = deserialize(response, TokenResponse.class);
            log.info("Got tokens!");
            updateToken(tokenResponse);
            updateCredentials(appId, clientSecret);
        } else {
            throw new StravaException("Could not get tokens. auth code was empty");
        }
    }


    Request createTokenRequest(String appId, String clientSecret, String authCode) {
        RequestBody body = new FormEncodingBuilder()
                .add("client_id", appId)
                .add("client_secret", clientSecret)
                .add("code", authCode)
                .add("grant_type", "authorization_code")
                .build();

        return getTokenRequest(body);
    }

    Request createRefreshTokenRequest(String appId, String clientSecret, String refreshToken) {
        RequestBody body = new FormEncodingBuilder()
                .add("client_id", appId)
                .add("client_secret", clientSecret)
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build();

        return getTokenRequest(body);
    }

    private  Request getTokenRequest(RequestBody body) {
        return new Request.Builder()
                .url(getBaseTokenUrl())
                .post(body)
                .build();
    }
}
