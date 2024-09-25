package net.wirelabs.etrex.uploader.common.parsers;

import com.garmin.xmlschemas.trainingCenterDatabase.v2.*;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.GPSCoordinate;
import net.wirelabs.jmaps.map.geo.Coordinate;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class TCXParser implements TrackToCoordsParser {


    /**
     * Parses tcx file in geoposition format
     *
     * @param file input file
     * @return list of waypoints in GeoPosition format
     */
    public List<Coordinate> parseToGeoPosition(File file) {

            return parseTcxFile(file).stream()
                    .map(GPSCoordinate::create)
                    .collect(Collectors.toList());

    }

    /**
     * Parses tcx file (note: only first course and first track, no merging yet as in gpx)
     *
     * @param file input file
     * @return list of waypoints in GPX's own Wpt format
     */
    public List<TrackpointT> parseTcxFile(File file) {
        List<TrackpointT> result = new ArrayList<>();
        try {
            TrainingCenterDatabaseT root = TrainingCenterDatabaseDocument.Factory.parse(file).getTrainingCenterDatabase();

            // collect only first track from first course
            // might consider config options to load all from multi course/multi track file
            CourseT course = root.getCourses().getCourseList().get(0);


            List<TrackT> tracks = course.getTrackList();
            for (TrackT track : tracks) {
                result.addAll(track.getTrackpointList());
            }


        } catch (IOException | XmlException e) {
            log.warn("Could not parse GPS file {}", file, e);
        }
        return result;
    }


}
