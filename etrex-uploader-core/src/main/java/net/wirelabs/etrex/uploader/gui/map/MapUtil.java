package net.wirelabs.etrex.uploader.gui.map;

import static net.wirelabs.etrex.uploader.common.EventType.*;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wirelabs.etrex.uploader.gui.components.filetree.FileNode;
import net.wirelabs.etrex.uploader.strava.model.SummaryActivity;
import net.wirelabs.etrex.uploader.eventbus.EventBus;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * Created 9/12/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapUtil {
    
    public static void drawTrackFromActivity(SummaryActivity activity) {
        String polyLine = activity.getMap().getSummaryPolyline();
        if (polyLine != null) {
            List<GeoPosition> track = polyLineDecode(polyLine, 1E5F);
            EventBus.publish(MAP_DISPLAY_TRACK, track);
        }
    }

    public static void drawTrackFromSelectedFileNode(FileNode fnode) {
        
        if (fnode == null || !fnode.getFile().isFile()) {
            return;
        }
        
        String filename = fnode.getFile().getName();
        
        if (filename.toUpperCase().endsWith(".FIT")) {
            EventBus.publish(MAP_DISPLAY_FIT_FILE, fnode.getFile());
        } 
        if (filename.toUpperCase().endsWith(".GPX")) {
            EventBus.publish(MAP_DISPLAY_GPX_FILE, fnode.getFile());
        }
    }

    private static List<GeoPosition> polyLineDecode(String str, Float precision) {

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
