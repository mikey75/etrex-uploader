package net.wirelabs.etrex.uploader.gui.map.parsers;

import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.jmaps.map.geo.Coordinate;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Created 12/21/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class TrackParser {

    private final FITParser fitParser = new FITParser();
    private final GPXParser gpxParser = new GPXParser();

    public List<Coordinate> parseToGeoPosition(File file) {

        if (FileUtils.isGpxFile(file)) {
            return gpxParser.parseToGeoPosition(file);
        }
        if (FileUtils.isFitFile(file)) {
            return fitParser.parseToGeoPosition(file);
        }
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

