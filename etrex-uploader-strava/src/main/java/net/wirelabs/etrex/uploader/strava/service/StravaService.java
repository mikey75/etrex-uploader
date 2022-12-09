package net.wirelabs.etrex.uploader.strava.service;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import lombok.extern.slf4j.Slf4j;

import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.strava.model.*;
import net.wirelabs.etrex.uploader.strava.client.StravaClientException;


import java.io.File;
import java.net.URLConnection;
import java.util.*;

import static net.wirelabs.etrex.uploader.strava.client.StravaClient.*;
import static net.wirelabs.etrex.uploader.strava.client.StravaClient.STRAVA_ATHLETE_ENDPOINT;


@Slf4j
public class StravaService  implements IStravaService {

    private final StravaClient client;
    private SummaryAthlete currentAthlete;

    public StravaService(Configuration configuration) {
        client = new StravaClient(configuration);

    }

    @Override
    public SummaryAthlete getCurrentAthlete() throws StravaClientException {

        if (currentAthlete == null) {
            currentAthlete = client.makeGetRequest(STRAVA_ATHLETE_ENDPOINT, SummaryAthlete.class);
        }
        return currentAthlete;

    }
    @Override
    public List<SummaryActivity> getCurrentAthleteActivities(int page, int perpage) throws StravaClientException {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("page", String.valueOf(page));
        parameters.put("per_page", String.valueOf(perpage));

        SummaryActivity[] activities = client.makeParameterizedGetRequest(STRAVA_ATHLETE_ACTIVITIES_ENDPOINT,
                parameters, SummaryActivity[].class);
        return Arrays.asList(activities);

    }
    @Override
    public ActivityStats getAthleteStats(Long id) throws StravaClientException {
        return client.makeGetRequest(STRAVA_ATHLETES_ENDPOINT+"/"+id +"/stats", ActivityStats.class);
    }
    @Override
    public DetailedActivity getActivityById(Long id) throws StravaClientException {
        return client.makeGetRequest(STRAVA_ACTIVITIES_ENDPOINT +"/"+String.valueOf(id), DetailedActivity.class);

    }
    @Override
    public List<SummaryActivity> getCurrentAthleteActivities() throws StravaClientException {
        return getCurrentAthleteActivities(1, 30);
    }

    @Override
    public Upload uploadActivity(File file, String name, String desc, SportType type) throws StravaClientException {

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

    private Upload uploadActivityRequest(File file, String name, String description, SportType sportType) throws StravaClientException {

        String dataType = guessContentTypeFromFile(file);
        String fileFormat = guessFileFormat(file);       
        RequestBody uploadBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(dataType), file))
                .addFormDataPart("data_type", fileFormat)
                .addFormDataPart("name", name)
                .addFormDataPart("sport_type", sportType.getValue())
                .addFormDataPart("description", description)
                .build();

        return client.makePostRequest(STRAVA_UPLOAD_ENDPOINT, uploadBody,Upload.class);


    }
    private Upload getUploadStatus(Long uploadId) throws StravaClientException {

        String urlPart = STRAVA_UPLOAD_ENDPOINT + "/" + uploadId;
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
    


    

