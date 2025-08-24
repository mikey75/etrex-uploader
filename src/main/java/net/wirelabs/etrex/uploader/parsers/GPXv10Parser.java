package net.wirelabs.etrex.uploader.parsers;

import com.topografix.gpx.x1.x0.GpxDocument;
import com.topografix.gpx.x1.x0.GpxDocument.Gpx.Trk;
import com.topografix.gpx.x1.x0.GpxDocument.Gpx.Trk.Trkseg;
import com.topografix.gpx.x1.x0.GpxDocument.Gpx.Trk.Trkseg.Trkpt;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.jmaps.map.geo.Coordinate;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created 8/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
class GPXv10Parser implements TrackToCoordsParser {

    /**
     * Parses gpx file in geo position format
     *
     * @param file input file
     * @return list of waypoints in GeoPosition format
     */
    public List<Coordinate> parseToGeoPosition(File file) {

            return parse(file).stream()
                    .map(GPSCoordinate::create)
                    .toList();

    }

    /**
     * Parses gpx 1.0 file (note: all tracks and all segments are merged into one set of waypoints)
     *
     * @param file input file
     * @return list of waypoints in GPX own Wpt format
     */
    private List<Trkpt> parse(File file) {
        List<Trkpt> result = new ArrayList<>();
        try {
            GpxDocument root = GpxDocument.Factory.parse(file);

            List<Trk> tracks = root.getGpx().getTrkList();
            for (Trk track : tracks) {
                track.getTrksegList().stream()
                        .map(Trkseg::getTrkptList)
                        .forEach(result::addAll);
            }

        } catch (IOException | XmlException e) {
            log.warn("Could not parse GPS file {}", file, e);
        }
        return result;
    }




}




    