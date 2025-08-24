package net.wirelabs.etrex.uploader.parsers;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.jmaps.map.geo.Coordinate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.wirelabs.etrex.uploader.utils.TrackFileUtils.*;

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

    public List<Coordinate> parsePolyline(String polyLine) {
        return parsePolyline(polyLine, Constants.DEFAULT_POLYLINE_PRECISION);
    }

    public List<Coordinate> parsePolyline(String polyLine, float precision) {
        List<Coordinate> coordinates = new ArrayList<>();
        AtomicInteger index = new AtomicInteger(0);

        int lat = 0;
        int lng = 0;

        while (index.get() < polyLine.length()) {
            lat += decodeNextValue(polyLine, index);
            lng += decodeNextValue(polyLine, index);
            coordinates.add(new Coordinate((double) lng / precision, (double) lat / precision));
        }

        return coordinates;
    }

    private int decodeNextValue(String polyLine, AtomicInteger index) {
        int result = 0;
        int shift = 0;

        while (index.get() < polyLine.length()) {
            int b = polyLine.charAt(index.getAndIncrement()) - 63;
            result |= (b & 0x1F) << shift;
            shift += 5;
            if (b < 0x20) break;
        }

        return ((result & 1) != 0) ? ~(result >> 1) : (result >> 1);
    }
}

