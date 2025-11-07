package net.wirelabs.etrex.uploader.strava.client;

import com.strava.model.*;
import net.wirelabs.etrex.uploader.strava.StravaException;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/*
 * Created 11/4/22 by Michał Szwaczko (mikey@wirelabs.net)
 */

/**
 * This is Strava API implementation interface
 * <p>
 * It is not a complete API (as in swagger.json), and does not contain all endpoints from there.
 * Only the ones used by the etrex-uploader app.
 * <p>
 * It might get enhanced in the future, if the need arises.
 * For what the app is now (track uploader and activity viewer) this is more than enough.
 */
interface StravaAPI extends Serializable {
    
    DetailedAthlete getCurrentAthlete() throws StravaException;
    ActivityStats getAthleteStats(Long id) throws StravaException;

    List<SummaryActivity> getCurrentAthleteActivities(int page, int perPage) throws StravaException;
    List<SummaryActivity> getCurrentAthleteActivities() throws StravaException;
    DetailedActivity getActivityById(Long id) throws StravaException;

    DetailedActivity updateActivity(Long id, UpdatableActivity update) throws StravaException;
    Upload uploadActivity(File file, String name, String desc, SportType type, boolean virtual, boolean commute) throws StravaException;
    StreamSet getActivityStreams(Long activityId, String keys, boolean keyByType) throws StravaException;
}
