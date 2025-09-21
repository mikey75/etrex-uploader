package net.wirelabs.etrex.uploader.parsers;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.jmaps.map.geo.Coordinate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Created 12/21/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public class TrackParser  {

    private final List<TrackToCoordsParser> parsers = List.of(
            new FITParser(),
            new GPXv10Parser(),
            new GPXv11Parser(),
            new TCXParser()
    );

    public List<Coordinate> parseTrackFile(File file) {
        return parsers.stream()
                .filter(p -> p.isSupported(file))
                .findFirst()
                .map(p -> p.parseToGeoPosition(file))
                .orElseGet(() -> {
                    log.warn("Unsupported track file: {}", file.getName());
                    return Collections.emptyList();
                });
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

