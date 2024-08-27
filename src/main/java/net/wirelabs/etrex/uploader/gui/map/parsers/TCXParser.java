package net.wirelabs.etrex.uploader.gui.map.parsers;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.GPSCoordinate;
import net.wirelabs.etrex.uploader.model.tcx.*;
import net.wirelabs.jmaps.map.geo.Coordinate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class TCXParser extends BaseParser {

    private Unmarshaller unmarshaller;

    public TCXParser() {

        try {
            this.unmarshaller = JAXBContext.newInstance(TCX_MODEL_PKG).createUnmarshaller();

        } catch (JAXBException e) {
            log.error("JAXB exception {}", e.getMessage(), e);
        }

    }

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
            JAXBElement<TrainingCenterDatabaseT> root = (JAXBElement<TrainingCenterDatabaseT>) unmarshaller.unmarshal(file);
            // collect only first track from first course
            // might consider config options to load all from multi course/multi track file
            CourseT course = root.getValue().getCourses().getCourse().get(0);


            List<TrackT> tracks = course.getTrack();
            for (TrackT track : tracks) {
                result.addAll(track.getTrackpoint());
            }

        } catch (JAXBException e) {
            logParseErrorMessage(file, e);
        }
        return result;
    }


}
