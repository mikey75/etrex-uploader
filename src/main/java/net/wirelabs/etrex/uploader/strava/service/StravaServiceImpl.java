package net.wirelabs.etrex.uploader.strava.service;


import com.strava.model.*;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;

import java.io.File;
import java.util.*;


@Slf4j
public class StravaServiceImpl implements StravaService {

    private final AppConfiguration configuration;
    private final transient StravaClient client;
    private SummaryAthlete currentAthlete;

    private String activities;
    private String athlete;
    private String athletes;
    private String athleteActivities;
    private String uploads;

    public StravaServiceImpl(AppConfiguration configuration, StravaClient stravaClient) {
        this.client = stravaClient;
        this.configuration = configuration;
        setupUrls();
    }

    private void setupUrls() {
        String apiBaseUrl = Constants.STRAVA_BASE_URL;
        activities = apiBaseUrl + "/activities";
        athlete = apiBaseUrl + "/athlete";
        athletes = apiBaseUrl + "/athletes";
        athleteActivities = apiBaseUrl + "/athlete/activities";
        uploads = apiBaseUrl + "/uploads";
    }

    @Override
    public SummaryAthlete getCurrentAthlete() throws StravaException {

        if (currentAthlete == null) {
            currentAthlete = client.makeGetRequest(athlete, SummaryAthlete.class, null);
        }
        return currentAthlete;

    }

    @Override
    public List<SummaryActivity> getCurrentAthleteActivities(int page, int perPage) throws StravaException {

        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("per_page", String.valueOf(perPage));

        SummaryActivity[] activitiesList = client.makeGetRequest(athleteActivities,  SummaryActivity[].class, params);
        return Arrays.asList(activitiesList);

    }

    @Override
    public ActivityStats getAthleteStats(Long id) throws StravaException {
        return client.makeGetRequest(athletes + "/" + id + "/stats", ActivityStats.class, null);
    }

    @Override
    public DetailedActivity updateActivity(Long id, UpdatableActivity update) throws StravaException {
        return client.makePutRequest(activities + "/" + id, update, DetailedActivity.class);
    }

    @Override
    public DetailedActivity getActivityById(Long id) throws StravaException {
        return client.makeGetRequest(activities + "/" + id, DetailedActivity.class, null);

    }

    @Override
    public List<SummaryActivity> getCurrentAthleteActivities() throws StravaException {
        return getCurrentAthleteActivities(1, configuration.getPerPage());
    }

    @Override
    public Upload uploadActivity(File file, String name, String desc, SportType sportType, boolean virtual, boolean commute) throws StravaException {

        int uploadWaitTimeSeconds = configuration.getUploadStatusWaitSeconds();
        long uploadStatusTimeout = System.currentTimeMillis() + uploadWaitTimeSeconds * 1000L;

        // make upload request
        Upload upload = client.uploadActivityRequest(file, name, desc, sportType, virtual, commute);

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
        return client.makeGetRequest(activities + "/" + activityId + "/streams" , StreamSet.class, params);
    }

    private Upload getUpload(Long uploadId) throws StravaException {

        String urlPart = uploads + "/" + uploadId;
        return client.makeGetRequest(urlPart, Upload.class,null);
    }


}
    


    

