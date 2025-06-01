package net.wirelabs.etrex.uploader.strava.client;

import com.squareup.okhttp.*;
import com.strava.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.strava.client.token.RefreshTokenResponse;
import net.wirelabs.etrex.uploader.strava.client.token.TokenResponse;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtil;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

import static net.wirelabs.etrex.uploader.strava.utils.JsonUtil.deserialize;
import static net.wirelabs.etrex.uploader.strava.utils.JsonUtil.serialize;

/*
 * Created 12/17/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class StravaClient implements StravaAPI {

    private final transient StravaConfigUpdater stravaUpdater;
    private String activities;
    private String athlete;
    private String athletes;
    private String athleteActivities;
    private String uploads;

    private SummaryAthlete currentAthlete;

    private final transient OkHttpClient httpClient;
    private final StravaConfiguration stravaConfiguration;
    @Getter
    private final AppConfiguration appConfiguration;
    @Getter
    private final String baseUrl;
    private final String baseTokenUrl;

    public StravaClient(StravaConfiguration stravaConfiguration,AppConfiguration appConfiguration, String baseUrl, String baseTokenUrl) {
        this.stravaConfiguration = stravaConfiguration;
        this.stravaUpdater = new StravaConfigUpdater(stravaConfiguration);
        this.appConfiguration = appConfiguration;
        this.baseUrl = baseUrl;
        this.baseTokenUrl = baseTokenUrl;
        this.httpClient = new OkHttpClient();
        setupUrls();
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
                .add("Authorization", "Bearer " + stravaConfiguration.getStravaAccessToken())
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
                .url(uploads)
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



    private void refreshTokenIfExpired() throws StravaException {
        synchronized (this) {
            long currentTime = Duration.ofMillis(System.currentTimeMillis()).getSeconds();
            if (stravaConfiguration.getStravaTokenExpires() < currentTime) {
                log.info("Refreshing token");
                Request request = createRefreshTokenRequest(stravaConfiguration.getStravaAppId(), stravaConfiguration.getStravaClientSecret(), stravaConfiguration.getStravaRefreshToken());
                String response = execute(request);
                RefreshTokenResponse refreshTokenResponse = deserialize(response, RefreshTokenResponse.class);
                stravaUpdater.refreshExpired(refreshTokenResponse);
            }
        }
    }

    public void exchangeAuthCodeForAccessToken(String appId, String clientSecret, String authCode) throws StravaException {

        if (!authCode.isEmpty()) {
            Request tokenRequest = createTokenRequest(appId, clientSecret, authCode);
            String response = execute(tokenRequest);
            TokenResponse tokenResponse = deserialize(response, TokenResponse.class);
            log.info("Got tokens!");
            stravaUpdater.updateToken(tokenResponse);
            stravaUpdater.updateCredentials(appId, clientSecret);
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
                .url(baseTokenUrl)
                .post(body)
                .build();
    }


    @Override
    public SummaryAthlete getCurrentAthlete() throws StravaException {

        if (currentAthlete == null) {
            currentAthlete = makeGetRequest(athlete, SummaryAthlete.class, null);
        }
        return currentAthlete;

    }

    @Override
    public List<SummaryActivity> getCurrentAthleteActivities(int page, int perPage) throws StravaException {

        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("per_page", String.valueOf(perPage));

        SummaryActivity[] activitiesList = makeGetRequest(athleteActivities,  SummaryActivity[].class, params);
        return Arrays.asList(activitiesList);

    }

    @Override
    public ActivityStats getAthleteStats(Long id) throws StravaException {
        return makeGetRequest(athletes + "/" + id + "/stats", ActivityStats.class, null);
    }

    @Override
    public DetailedActivity updateActivity(Long id, UpdatableActivity update) throws StravaException {
        return makePutRequest(activities + "/" + id, update, DetailedActivity.class);
    }

    @Override
    public DetailedActivity getActivityById(Long id) throws StravaException {
        return makeGetRequest(activities + "/" + id, DetailedActivity.class, null);

    }

    @Override
    public List<SummaryActivity> getCurrentAthleteActivities() throws StravaException {
        return getCurrentAthleteActivities(1, appConfiguration.getPerPage());
    }

    @Override
    public Upload uploadActivity(File file, String name, String desc, SportType sportType, boolean virtual, boolean commute) throws StravaException {

        int uploadWaitTimeSeconds = appConfiguration.getUploadStatusWaitSeconds();
        long uploadStatusTimeout = System.currentTimeMillis() + uploadWaitTimeSeconds * 1000L;

        // make upload request
        Upload upload = uploadActivityRequest(file, name, desc, sportType, virtual, commute);

        // wait for success or fail
        // i.e. poll Upload's activity id for uploadStatusTimeout seconds
        // in 2 seconds intervals
        while (getUpload(upload.getId()).getActivityId() == null && System.currentTimeMillis() < uploadStatusTimeout) {
            Sleeper.sleepSeconds(2);
        }
        // return upload
        return getUpload(upload.getId());

    }

    @Override
    public StreamSet getActivityStreams(Long activityId, String keys, boolean keyByType) throws StravaException {
        Map<String, String> params = new HashMap<>();
        params.put("keys", keys);
        params.put("key_by_type",String.valueOf(keyByType));
        return makeGetRequest(activities + "/" + activityId + "/streams" , StreamSet.class, params);
    }

    private Upload getUpload(Long uploadId) throws StravaException {
        String urlPart = uploads + "/" + uploadId;
        return makeGetRequest(urlPart, Upload.class,null);
    }

    private void setupUrls() {

        activities = baseUrl + "/activities";
        athlete = baseUrl + "/athlete";
        athletes = baseUrl + "/athletes";
        athleteActivities = baseUrl + "/athlete/activities";
        uploads = baseUrl + "/uploads";
    }
}
