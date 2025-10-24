package net.wirelabs.etrex.uploader.parsers;

import com.garmin.xmlschemas.trainingCenterDatabase.v2.*;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.utils.TrackFileUtils;
import net.wirelabs.jmaps.map.geo.Coordinate;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public class TCXParser implements TrackToCoordsParser {


    /**
     * Parses tcx file in geo position format
     *
     * @param file input file
     * @return list of waypoints in GeoPosition format
     */
    public List<Coordinate> parseToGeoPosition(File file) {

            return parseTcxFile(file).stream()
                    .map(GPSCoordinate::create)
                    .toList();

    }

    @Override
    public boolean isSupported(File file) {
        return TrackFileUtils.isTcxFile(file);
    }

    /**
     * Parses tcx file (note: only first course/activity and first track, no merging yet as in gpx)
     *
     * @param file input file - file can be a recorded activity or route/course (both can be displayed on the map)
     * @return list of waypoints in TCX own TrackPointT format
     */
    public List<TrackpointT> parseTcxFile(File file) {
        List<TrackpointT> result = new ArrayList<>();
        try {
            TrainingCenterDatabaseT root = TrainingCenterDatabaseDocument.Factory.parse(file).getTrainingCenterDatabase();

            ActivityListT activities = root.getActivities();
            if (activities != null) { // track is an activity
                log.info("TCX file is a recorded activity");
                List<ActivityLapT> laps = activities.getActivityList().get(0).getLapList();
                for (ActivityLapT lap: laps) {
                    List<TrackT> tracks = lap.getTrackList();
                    collectTrackPoints(result, tracks);
                }
                return result;
            }
            
            CourseListT courses = root.getCourses();
            if (courses != null) { // track is a course/route
                log.info("TCX file is a course/route - not a recorded activity");
                List<TrackT> tracks = courses.getCourseList().get(0).getTrackList();
                collectTrackPoints(result, tracks);
                return result;
            }

        } catch (IOException | XmlException | IndexOutOfBoundsException  e) {
            log.warn("Could not parse GPS file {}", file, e);
        }
        return result;
    }

    private void collectTrackPoints(List<TrackpointT> result, List<TrackT> tracks) {
        for (TrackT track: tracks) {
            result.addAll(track.getTrackpointList());
        }
    }

}
