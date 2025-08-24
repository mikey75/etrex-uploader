package net.wirelabs.etrex.uploader.strava.client;

import com.strava.model.SportType;
import com.strava.model.Upload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.strava.client.token.RefreshTokenResponse;
import net.wirelabs.etrex.uploader.strava.client.token.TokenResponse;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtil;
import net.wirelabs.etrex.uploader.utils.UrlBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import static net.wirelabs.etrex.uploader.utils.HttpUtils.*;
import static net.wirelabs.etrex.uploader.utils.JsonUtil.deserialize;
import static net.wirelabs.etrex.uploader.utils.JsonUtil.serialize;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class StravaHttpInstrumentation {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private StravaConfiguration stravaConfiguration;
    private StravaConfigUpdater stravaUpdater;

    protected String activitiesUrl;
    protected String athleteUrl;
    protected String athletesUrl;

    // these are local to the instrumentation so no protected access
    private String uploadsUrl;
    private String baseTokenUrl;



    protected StravaHttpInstrumentation(StravaConfiguration stravaConfiguration, String baseUrl, String baseTokenUrl) {
        setupUrls(baseUrl);
        this.baseTokenUrl = baseTokenUrl;
        this.stravaUpdater = new StravaConfigUpdater(stravaConfiguration);
        this.stravaConfiguration = stravaConfiguration;
    }

    public String executeRequest(HttpRequest request) throws StravaException {
        try {
            log.debug("[Strava request: {}] {}", request.method(), request.uri());
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new StravaException(response.body());
            }

            // Convert headers to a Map<String, List<String>> if needed
            HttpHeaders headers = response.headers();
            StravaUtil.sendRateLimitInfo(headers.map());

            return response.body();

        } catch (ConnectException e) {
            throw new StravaException("Failed to connect!");
        } catch (IOException e) {
            throw new StravaException(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new StravaException(e.getMessage());
        }
    }

    public <T> T makeGetRequest(String endpointUrl, Class<T> type, Map<String, String> parameters) throws StravaException {

        if (parameters != null) {
            endpointUrl = decorateUrlWithParams(endpointUrl, parameters);
        }

        refreshTokenIfExpired();

        HttpRequest request = HttpRequest.newBuilder()
                .header(getAuthHeader().getKey(), getAuthHeader().getValue())
                .uri(URI.create(endpointUrl))
                .GET()
                .build();


        String result = executeRequest(request);
        return deserialize(result, type);
    }

    public <T> T makePutRequest(String endpointUrl, Object body, Class<T> type) throws StravaException {

        refreshTokenIfExpired();

        String jsonBody = serialize(body);


        HttpRequest request = HttpRequest.newBuilder()
                .header(getAuthHeader().getKey(), getAuthHeader().getValue())
                .uri(URI.create(endpointUrl))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();


        String result = executeRequest(request);
        return deserialize(result, type);

    }

    protected AbstractMap.SimpleEntry<String, String> getAuthHeader() {
        return new AbstractMap.SimpleEntry<>("Authorization", "Bearer " + stravaConfiguration.getStravaAccessToken());
    }


    // token related methods
    public void exchangeAuthCodeForAccessToken(String appId, String clientSecret, String authCode) throws StravaException {

        if (!authCode.isEmpty()) {
            HttpRequest tokenRequest = createTokenRequest(appId, clientSecret, authCode);
            String response = executeRequest(tokenRequest);
            TokenResponse tokenResponse = deserialize(response, TokenResponse.class);
            log.info("Got tokens!");
            stravaUpdater.updateToken(tokenResponse);
            stravaUpdater.updateCredentials(appId, clientSecret);
        } else {
            throw new StravaException("Could not get tokens. auth code was empty");
        }
    }

    HttpRequest createRefreshTokenRequest(String appId, String clientSecret, String refreshToken) {
        String body = UrlBuilder.create().parse(baseTokenUrl)
                .addQueryParam("client_id", appId)
                .addQueryParam("client_secret", clientSecret)
                .addQueryParam("grant_type", "refresh_token")
                .addQueryParam("refresh_token", refreshToken)
                .build()
                .replace(baseTokenUrl + "?", ""); // get only the parameter list
        return getTokenRequest(body);
    }

    HttpRequest createTokenRequest(String appId, String clientSecret, String authCode) {
        String body = UrlBuilder.create().parse(baseTokenUrl)
                .addQueryParam("client_id", appId)
                .addQueryParam("client_secret", clientSecret)
                .addQueryParam("code", authCode)
                .addQueryParam("grant_type", "authorization_code")
                .build()
                .replace(baseTokenUrl + "?", ""); // get only the parameter list
        return getTokenRequest(body);
    }

    void refreshTokenIfExpired() throws StravaException {
        synchronized (this) {
            long currentTime = Duration.ofMillis(System.currentTimeMillis()).getSeconds();
            if (stravaConfiguration.getStravaTokenExpires() < currentTime) {
                log.info("Refreshing token");
                HttpRequest request = createRefreshTokenRequest(stravaConfiguration.getStravaAppId(), stravaConfiguration.getStravaClientSecret(), stravaConfiguration.getStravaRefreshToken());
                String response = executeRequest(request);
                RefreshTokenResponse refreshTokenResponse = deserialize(response, RefreshTokenResponse.class);
                stravaUpdater.refreshExpired(refreshTokenResponse);
            }
        }
    }

    private HttpRequest getTokenRequest(String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseTokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    // upload related methods

    public Upload uploadActivityRequest(File file, String name, String description, SportType sportType, boolean virtual, boolean commute) throws StravaException {

        HttpRequest request = buildUploadRequest(file, name, description, sportType, virtual, commute);

        refreshTokenIfExpired();

        String result = executeRequest(request);
        return deserialize(result, Upload.class);

    }

    public Upload getUpload(Long uploadId) throws StravaException {
        String urlPart = uploadsUrl + "/" + uploadId;
        return makeGetRequest(urlPart, Upload.class,null);
    }

    private HttpRequest buildUploadRequest(File file, String name, String description, SportType sportType, boolean virtual, boolean commute) throws StravaException {

        String boundary = UUID.randomUUID().toString();

        try {
            byte[] multipartBody = buildUploadBody(file, name, description, sportType, virtual, commute, boundary);

            return HttpRequest.newBuilder()
                    .uri(URI.create(uploadsUrl))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .header(getAuthHeader().getKey(), getAuthHeader().getValue())
                    .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                    .build();
        } catch (IOException e) {
            throw new StravaException(e.getMessage());
        }
    }

    private byte[] buildUploadBody(File file, String name, String description, SportType sportType, boolean virtual, boolean commute, String boundary)
            throws IOException, StravaException {

        String dataType = StravaUtil.guessContentTypeFromFileName(file);
        String fileFormat = StravaUtil.guessUploadFileFormat(file);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));

        BiConsumer<String, String> addFormField = createFormFieldAdder(boundary, writer);

        // Add all form fields
        addFormField.accept("data_type", fileFormat);
        addFormField.accept("name", name);
        addFormField.accept("sport_type", sportType.getValue());
        addFormField.accept("description", description);
        addFormField.accept("external_id", name + System.currentTimeMillis());
        addFormField.accept("commute", String.valueOf(commute));
        addFormField.accept("trainer", String.valueOf(virtual));

        // Add file
        writer.write(FORM_SEPARATOR + boundary);
        writer.write(LINEFEED);
        writer.write("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"");
        writer.write(LINEFEED);
        writer.write("Content-Type: " + dataType);
        writer.write(LINEFEED);
        writer.write(LINEFEED);
        writer.flush();

        // Write file content
        Files.copy(file.toPath(), output);
        output.flush();

        writer.write(LINEFEED);
        writer.write(FORM_SEPARATOR + boundary + FORM_SEPARATOR);
        writer.write(LINEFEED);
        writer.flush();

        return output.toByteArray();
    }

    @NotNull
    private static BiConsumer<String, String> createFormFieldAdder(String boundary, BufferedWriter writer) {
        return (String fieldName, String value) -> {
            try {
                writer.write(FORM_SEPARATOR + boundary);
                writer.write(LINEFEED);
                writer.write("Content-Disposition: form-data; name=\"" + fieldName + "\"");
                writer.write(LINEFEED);
                writer.write("Content-Type: text/plain; charset=UTF-8");
                writer.write(LINEFEED);
                writer.write(LINEFEED);
                writer.write(value);
                writer.write(LINEFEED);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    private void setupUrls(String baseUrl) {
        activitiesUrl = baseUrl + "/activities";
        athleteUrl = baseUrl + "/athlete";
        athletesUrl = baseUrl + "/athletes";
        uploadsUrl = baseUrl + "/uploads";
    }
}
