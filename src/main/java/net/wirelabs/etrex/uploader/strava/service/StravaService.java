package net.wirelabs.etrex.uploader.strava.service;

import net.wirelabs.etrex.uploader.StravaException;
import net.wirelabs.etrex.uploader.strava.model.*;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/*
 * Created 11/4/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public interface StravaService extends Serializable {
    
    SummaryAthlete getCurrentAthlete() throws StravaException;
    ActivityStats getAthleteStats(Long id) throws StravaException;

    List<SummaryActivity> getCurrentAthleteActivities(int page, int perpage) throws StravaException;
    List<SummaryActivity> getCurrentAthleteActivities() throws StravaException;
    DetailedActivity getActivityById(Long id) throws StravaException;

    DetailedActivity updateActivity(Long id, UpdatableActivity update) throws StravaException;
    Upload uploadActivity(File file, String name, String desc, SportType type) throws StravaException;

}
