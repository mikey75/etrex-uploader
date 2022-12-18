package net.wirelabs.etrex.uploader.strava.service;


import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.strava.client.StravaException;
import net.wirelabs.etrex.uploader.strava.model.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        params.put("per_page", String.valueOf(configuration.getPerPage()));

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
        return getCurrentAthleteActivities(1, 30);
    }

    @Override
    public Upload uploadActivity(File file, String name, String desc, SportType sportType) throws StravaException {

        Upload u = client.uploadActivityRequest(file, name, desc, sportType);

        // after upload check upload status
        Upload uploadStatus = getUploadStatus(u.getId());

        // wait for success or fail
        long timeout = System.currentTimeMillis() + 15000L;
        // strava reccomends polling on upload status with 1 sec intervals, median is 8
        // sec.
        while (uploadStatus.getActivityId() == null && System.currentTimeMillis() < timeout) {
            Sleeper.sleepSeconds(2);
            uploadStatus = getUploadStatus(u.getId());
        }

        return uploadStatus;

    }

    private Upload getUploadStatus(Long uploadId) throws StravaException {

        String urlPart = uploads + "/" + uploadId;
        return client.makeGetRequest(urlPart, Upload.class,null);
    }


}
    


    

