package net.wirelabs.etrex.uploader.strava;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import lombok.extern.slf4j.Slf4j;

import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.model.strava.*;
import net.wirelabs.etrex.uploader.strava.api.StravaApi;
import net.wirelabs.etrex.uploader.strava.api.StravaApiException;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.squareup.okhttp.MultipartBuilder.FORM;


@Slf4j
public class StravaService extends StravaApi implements IStravaService {

    private SummaryAthlete currentAthlete;

    private final String STRAVA_ACTIVITIES_ENDPOINT = apiBaseUrl+ "/activities";
    private final String STRAVA_ATHLETE_ENDPOINT = apiBaseUrl+"/athlete";
    private final String STRAVA_ATHLETES_ENDPOINT = apiBaseUrl + "/athletes";
    private final String STRAVA_ATHLETE_ACTIVITIES_ENDPOINT = apiBaseUrl +"/athlete/activities";

    public final String STRAVA_UPLOAD_ENDPOINT = "/uploads";

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
    public Upload uploadActivity(File file, String name, String desc, String type) throws StravaApiException {

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

    private Upload uploadActivityRequest(File file, String name, String description, String type) throws StravaApiException {

        RequestBody uploadBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/xml"), file))
                .addFormDataPart("data_type", "gpx") //todo: make autodetect
                .addFormDataPart("name", name)
                .addFormDataPart("sport_type", type)
                .addFormDataPart("description", description)
                .build();

        return makePostRequest(STRAVA_UPLOAD_ENDPOINT, uploadBody,Upload.class);


    }
    private Upload getUploadStatus(Long uploadId) throws StravaApiException {

        String urlPart = STRAVA_UPLOAD_ENDPOINT + "/" + uploadId;
        return makeGetRequest(urlPart,Upload.class);


    }

}
    


    

