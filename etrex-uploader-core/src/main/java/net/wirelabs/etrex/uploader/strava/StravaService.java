package net.wirelabs.etrex.uploader.strava;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import lombok.extern.slf4j.Slf4j;

import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.strava.model.*;
import net.wirelabs.etrex.uploader.strava.api.StravaApi;
import net.wirelabs.etrex.uploader.strava.api.StravaApiException;


import java.io.File;
import java.net.URLConnection;
import java.util.*;


@Slf4j
public class StravaService extends StravaApi implements IStravaService {

    private SummaryAthlete currentAthlete;

    private final String STRAVA_ACTIVITIES_ENDPOINT = apiBaseUrl+ "/activities";
    private final String STRAVA_ATHLETE_ENDPOINT = apiBaseUrl+"/athlete";
    private final String STRAVA_ATHLETES_ENDPOINT = apiBaseUrl + "/athletes";
    private final String STRAVA_ATHLETE_ACTIVITIES_ENDPOINT = apiBaseUrl +"/athlete/activities";

    public final String STRAVA_UPLOAD_ENDPOINT = apiBaseUrl + "/uploads";

    public StravaService(Configuration configuration) {
        super(configuration);
    }

    @Override
    public SummaryAthlete getCurrentAthlete() throws StravaApiException {

        if (currentAthlete == null) {
            currentAthlete = makeGetRequest(STRAVA_ATHLETE_ENDPOINT, SummaryAthlete.class);
        }
        return currentAthlete;

    }
    @Override
    public List<SummaryActivity> getCurrentAthleteActivities(int page, int perpage) throws StravaApiException {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("page", String.valueOf(page));
        parameters.put("per_page", String.valueOf(perpage));

        SummaryActivity[] activities = makeParameterizedGetRequest(STRAVA_ATHLETE_ACTIVITIES_ENDPOINT,
                parameters, SummaryActivity[].class);
        return Arrays.asList(activities);

    }
    @Override
    public ActivityStats getAthleteStats(Long id) throws StravaApiException {
        return makeGetRequest(STRAVA_ATHLETES_ENDPOINT+"/"+id +"/stats", ActivityStats.class);
    }
    @Override
    public DetailedActivity getActivityById(Long id) throws StravaApiException {
        return makeGetRequest(STRAVA_ACTIVITIES_ENDPOINT +"/"+String.valueOf(id), DetailedActivity.class);

    }
    @Override
    public List<SummaryActivity> getCurrentAthleteActivities() throws StravaApiException {
        return getCurrentAthleteActivities(1, 30);
    }

    @Override
    public Upload uploadActivity(File file, String name, String desc, SportType type) throws StravaApiException {

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

    private Upload uploadActivityRequest(File file, String name, String description, SportType sportType) throws StravaApiException {

        String dataType = guessContentTypeFromFile(file);
        String fileFormat = guessFileFormat(file);       
        RequestBody uploadBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(dataType), file))
                .addFormDataPart("data_type", fileFormat)
                .addFormDataPart("name", name)
                .addFormDataPart("sport_type", sportType.getValue())
                .addFormDataPart("description", description)
                .build();

        return makePostRequest(STRAVA_UPLOAD_ENDPOINT, uploadBody,Upload.class);


    }
    private Upload getUploadStatus(Long uploadId) throws StravaApiException {

        String urlPart = STRAVA_UPLOAD_ENDPOINT + "/" + uploadId;
        return makeGetRequest(urlPart,Upload.class);


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
    


    

