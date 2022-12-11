package net.wirelabs.etrex.uploader.strava.service;

import net.wirelabs.etrex.uploader.strava.model.*;
import net.wirelabs.etrex.uploader.strava.client.StravaException;

import java.io.File;
import java.util.List;

/*
 * Created 11/4/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public interface IStravaService {
    SummaryAthlete getCurrentAthlete() throws StravaException;

    List<SummaryActivity> getCurrentAthleteActivities(int page, int perpage) throws StravaException;

    DetailedActivity getActivityById(Long id) throws StravaException;

    List<SummaryActivity> getCurrentAthleteActivities() throws StravaException;

    Upload uploadActivity(File file, String name, String desc, SportType type) throws StravaException;

    ActivityStats getAthleteStats(Long id) throws StravaException;
}
