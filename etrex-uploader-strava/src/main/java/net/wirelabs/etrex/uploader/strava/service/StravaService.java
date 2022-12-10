package net.wirelabs.etrex.uploader.strava.service;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.strava.client.StravaException;
import net.wirelabs.etrex.uploader.strava.model.*;

import java.io.File;
import java.net.URLConnection;
import java.util.*;


@Slf4j
public class StravaService  implements IStravaService {

    private final StravaClient client;

    private SummaryAthlete currentAthlete;

    private String activities;
    private String athlete;
    private String athletes;
    private String athleteActivities;
    private String uploads;

    public StravaService(Configuration configuration) {

        client = new StravaClient(configuration);
        setupUrls();
    }

    private void setupUrls() {
        String apiBaseUrl = Constants.STRAVA_BASE_URL;
        activities = apiBaseUrl+ "/activities";
        athlete = apiBaseUrl+"/athlete";
        athletes = apiBaseUrl + "/athletes";
        athleteActivities = apiBaseUrl +"/athlete/activities";
        uploads = apiBaseUrl + "/uploads";
    }

    @Override
    public SummaryAthlete getCurrentAthlete() throws StravaException {

        if (currentAthlete == null) {
            currentAthlete = client.makeGetRequest(athlete, SummaryAthlete.class);
        }
        return currentAthlete;

    }

    @Override
    public List<SummaryActivity> getCurrentAthleteActivities(int page, int perpage) throws StravaException {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("page", String.valueOf(page));
        parameters.put("per_page", String.valueOf(perpage));

        SummaryActivity[] activitiesList = client.makeParameterizedGetRequest(athleteActivities,
                parameters, SummaryActivity[].class);
        return Arrays.asList(activitiesList);

    }

    @Override
    public ActivityStats getAthleteStats(Long id) throws StravaException {
        return client.makeGetRequest(athletes +"/" + id +"/stats", ActivityStats.class);
    }

    @Override
    public DetailedActivity getActivityById(Long id) throws StravaException {
        return client.makeGetRequest(activities +"/"+ id, DetailedActivity.class);

    }
    @Override
    public List<SummaryActivity> getCurrentAthleteActivities() throws StravaException {
        return getCurrentAthleteActivities(1, 30);
    }

    @Override
    public Upload uploadActivity(File file, String name, String desc, SportType type) throws StravaException {

        Upload u = uploadActivityRequest(file, name, desc, type);

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

    private Upload uploadActivityRequest(File file, String name, String description, SportType sportType) throws StravaException {

        String dataType = guessContentTypeFromFile(file);
        String fileFormat = guessFileFormat(file);       
        RequestBody uploadBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(dataType), file))
                .addFormDataPart("data_type", fileFormat)
                .addFormDataPart("name", name)
                .addFormDataPart("sport_type", sportType.getValue())
                .addFormDataPart("description", description)
                .build();

        return client.makePostRequest(uploads, uploadBody,Upload.class);


    }
    private Upload getUploadStatus(Long uploadId) throws StravaException {

        String urlPart = uploads + "/" + uploadId;
        return client.makeGetRequest(urlPart,Upload.class);


    }

    private String guessFileFormat(File file) {
        String fname = file.getName().toLowerCase();
        if (fname.endsWith(".gpx")) return "gpx";
        if (fname.endsWith(".tcx")) return "tcx";
        if (fname.endsWith(".fit")) return "fit";
        return "";
    }

    public String guessContentTypeFromFile(File file) {
        String contentType = URLConnection.guessContentTypeFromName(file.getName());
        return Objects.requireNonNullElse(contentType, "application/octet-stream");
    }


}
    


    

