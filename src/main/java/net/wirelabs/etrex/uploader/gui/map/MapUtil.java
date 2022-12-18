package net.wirelabs.etrex.uploader.gui.map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wirelabs.etrex.uploader.common.eventbus.EventBus;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static net.wirelabs.etrex.uploader.common.EventType.*;

/**
 * Created 9/12/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapUtil {
    
    public static void drawTrackFromPolyLine(String polyLine) {

        if (polyLine != null) {
            List<GeoPosition> track = polyLineToGeoPosition(polyLine, 1E5F);
            EventBus.publish(MAP_DISPLAY_TRACK, track);
        }
    }

    public static void drawTrackFromFile(File file) {

        if (file != null && file.isFile()) {
            String filename = file.getName();

            if (filename.toUpperCase().endsWith(".FIT")) {
                EventBus.publish(MAP_DISPLAY_FIT_FILE, file);
            }
            if (filename.toUpperCase().endsWith(".GPX")) {
                EventBus.publish(MAP_DISPLAY_GPX_FILE, file);
            }
        }
    }

    private static List<GeoPosition> polyLineToGeoPosition(String str, Float precision) {

        ArrayList<GeoPosition> coordinates = new ArrayList<>();

        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < str.length()) {

            int b;
            int shift = 0;
            int result = 0;

            do {
                b = str.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;

            do {
                b = str.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            GeoPosition position = new GeoPosition((double) lat / precision, (double) lng / precision);
            coordinates.add(position);
        }
        return coordinates;
    }
}
