package net.wirelabs.etrex.uploader.strava;

import net.wirelabs.etrex.uploader.strava.model.*;
import net.wirelabs.etrex.uploader.strava.api.StravaApiException;

import java.io.File;
import java.util.List;

/**
 * Created 11/4/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public interface IStravaService {
    SummaryAthlete getCurrentAthlete() throws StravaApiException;

    List<SummaryActivity> getCurrentAthleteActivities(int page, int perpage) throws StravaApiException;

    DetailedActivity getActivityById(Long id) throws StravaApiException;

    List<SummaryActivity> getCurrentAthleteActivities() throws StravaApiException;

    Upload uploadActivity(File file, String name, String desc, String type) throws StravaApiException;

    ActivityStats getAthleteStats(Long id) throws StravaApiException;
}
