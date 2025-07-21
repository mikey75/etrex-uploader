package net.wirelabs.etrex.uploader.strava.client;

import com.strava.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.strava.StravaException;

import java.io.File;
import java.util.*;

/*
 * Created 12/17/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class StravaClient extends StravaHttpInstrumentation implements StravaAPI {

    @Getter
    private final AppConfiguration appConfiguration;
    private SummaryAthlete currentAthlete;

    public StravaClient(StravaConfiguration stravaConfiguration, AppConfiguration appConfiguration, String baseUrl, String baseTokenUrl) {
        super(stravaConfiguration, baseUrl, baseTokenUrl);
        this.appConfiguration = appConfiguration;
    }

    @Override
    public SummaryAthlete getCurrentAthlete() throws StravaException {
        currentAthlete = (currentAthlete == null) ? makeGetRequest(athleteUrl, SummaryAthlete.class, null) : currentAthlete;
        return currentAthlete;
    }

    @Override
    public List<SummaryActivity> getCurrentAthleteActivities(int page, int perPage) throws StravaException {

        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("per_page", String.valueOf(perPage));

        SummaryActivity[] activitiesList = makeGetRequest(athleteUrl + "/activities", SummaryActivity[].class, params);
        return Arrays.asList(activitiesList);

    }

    @Override
    public ActivityStats getAthleteStats(Long id) throws StravaException {
        return makeGetRequest(athletesUrl + "/" + id + "/stats", ActivityStats.class, null);
    }

    @Override
    public DetailedActivity updateActivity(Long id, UpdatableActivity update) throws StravaException {
        return makePutRequest(activitiesUrl + "/" + id, update, DetailedActivity.class);
    }

    @Override
    public DetailedActivity getActivityById(Long id) throws StravaException {
        return makeGetRequest(activitiesUrl + "/" + id, DetailedActivity.class, null);
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
        params.put("key_by_type", String.valueOf(keyByType));
        return makeGetRequest(activitiesUrl + "/" + activityId + "/streams", StreamSet.class, params);
    }


}
