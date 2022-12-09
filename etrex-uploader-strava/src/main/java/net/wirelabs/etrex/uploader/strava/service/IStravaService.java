package net.wirelabs.etrex.uploader.strava.service;

import net.wirelabs.etrex.uploader.strava.model.*;
import net.wirelabs.etrex.uploader.strava.client.StravaClientException;

import java.io.File;
import java.util.List;

/**
 * Created 11/4/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public interface IStravaService {
    SummaryAthlete getCurrentAthlete() throws StravaClientException;

    List<SummaryActivity> getCurrentAthleteActivities(int page, int perpage) throws StravaClientException;

    DetailedActivity getActivityById(Long id) throws StravaClientException;

    List<SummaryActivity> getCurrentAthleteActivities() throws StravaClientException;

    Upload uploadActivity(File file, String name, String desc, SportType type) throws StravaClientException;

    ActivityStats getAthleteStats(Long id) throws StravaClientException;
}
