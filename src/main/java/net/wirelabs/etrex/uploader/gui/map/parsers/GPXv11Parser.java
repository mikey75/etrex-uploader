package net.wirelabs.etrex.uploader.gui.map.parsers;

import com.topografix.gpx.x1.x1.GpxDocument;
import com.topografix.gpx.x1.x1.TrkType;
import com.topografix.gpx.x1.x1.TrksegType;
import com.topografix.gpx.x1.x1.WptType;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.GPSCoordinate;

import net.wirelabs.jmaps.map.geo.Coordinate;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created 8/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
class GPXv11Parser implements TrackToCoordsParser {

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
     * Parses gpx file (note: all tracks and all segments are merged into one set of waypoints)
     *
     * @param file input file
     * @return list of waypoints in GPX's own Wpt format
     */
    private List<WptType> parse(File file) {
        List<WptType> result = new ArrayList<>();
        try {
            GpxDocument root = GpxDocument.Factory.parse(file);

            List<TrkType> tracks = root.getGpx().getTrkList();
            for (TrkType track : tracks) {
                track.getTrksegList().stream()
                        .map(TrksegType::getTrkptList)
                        .forEach(result::addAll);
            }

        } catch (IllegalArgumentException | IOException | XmlException e) {
            log.warn("Could not parse GPS file {}", file, e);
        }
        return result;
    }





}




    