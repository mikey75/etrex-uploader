package net.wirelabs.etrex.uploader.gui.map.parsers;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.GPSCoordinate;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx.Trk;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx.Trk.Trkseg;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx.Trk.Trkseg.Trkpt;
import net.wirelabs.jmaps.map.geo.Coordinate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created 8/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
class GPXv10Parser implements TrackToCoordsParser {

    private final String GPX10_MODEL_PKG = "net.wirelabs.etrex.uploader.model.gpx.ver10";
    private Unmarshaller unmarshaller;

    public GPXv10Parser() {

        try {
            this.unmarshaller = JAXBContext.newInstance(GPX10_MODEL_PKG).createUnmarshaller();
        } catch (JAXBException e) {
            log.error("JAXB exception {}", e.getMessage(), e);
        }

    }


    /**
     * Parses gpx file in geoposition format
     *
     * @param file input file
     * @return list of waypoints in GeoPosition format
     */
    public List<Coordinate> parseToGeoPosition(File file) {

            return parse(file).stream()
                    .map(GPSCoordinate::create)
                    .collect(Collectors.toList());

    }

    /**
     * Parses gpx 1.0 file (note: all tracks and all segments are merged into one set of waypoints)
     *
     * @param file input file
     * @return list of waypoints in GPX's own Wpt format
     */
    private List<Trkpt> parse(File file) {
        List<Trkpt> result = new ArrayList<>();
        try {
            Gpx root = (Gpx) unmarshaller.unmarshal(file);

            List<Trk> tracks = root.getTrk();
            for (Trk track : tracks) {
                track.getTrkseg().stream()
                        .map(Trkseg::getTrkpt)
                        .forEach(result::addAll);
            }

        } catch (JAXBException e) {
            log.warn("Could not parse GPS file {}", file, e);
        }
        return result;
    }




}




    