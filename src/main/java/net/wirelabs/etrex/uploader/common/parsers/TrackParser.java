package net.wirelabs.etrex.uploader.common.parsers;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.jmaps.map.geo.Coordinate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.wirelabs.etrex.uploader.common.utils.TrackFileUtils.*;

/*
 * Created 12/21/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class TrackParser  {

    private final TrackToCoordsParser fitParser = new FITParser();
    private final TrackToCoordsParser gpxV11Parser = new GPXv11Parser();
    private final TrackToCoordsParser gpxV10Parser = new GPXv10Parser();
    private final TrackToCoordsParser tcxParser = new TCXParser();

    public List<Coordinate> parseTrackFile(File file) {

        if (isGpx10File(file)) {
            return gpxV10Parser.parseToGeoPosition(file);
        }
        if (isGpx11File(file)) {
            return gpxV11Parser.parseToGeoPosition(file);
        }
        if (isFitFile(file)) {
            return fitParser.parseToGeoPosition(file);
        }
        if (isTcxFile(file)) {
            return tcxParser.parseToGeoPosition(file);
        }
        log.warn("Unsupported track file: {}", file.getName());
        return Collections.emptyList();
    }

    public List<Coordinate> parsePolyline(String polyLine, Float precision) {
        ArrayList<Coordinate> coordinates = new ArrayList<>();

        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < polyLine.length()) {

            int b;
            int shift = 0;
            int result = 0;

            do {
                b = polyLine.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;

            do {
                b = polyLine.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            Coordinate position = new Coordinate((double) lng / precision, (double) lat / precision);
            coordinates.add(position);
        }
        return coordinates;
    }
}

