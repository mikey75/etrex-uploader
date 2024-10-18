package net.wirelabs.etrex.uploader.common.parsers;

import com.garmin.xmlschemas.trainingCenterDatabase.v2.TrackpointT;
import com.topografix.gpx.x1.x0.GpxDocument.Gpx.Trk.Trkseg.Trkpt;
import com.topografix.gpx.x1.x1.WptType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wirelabs.jmaps.map.geo.Coordinate;

/**
 * Class for easy creation of jmaps coordinate objects from different versions of track  files
 * (from WptType for gpx 1.1, and TrkPt for gpx 1.0, anD TrackPointT for tcx)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GPSCoordinate {

    // gpx 1.1
    public static Coordinate create(WptType wptType) {
       return new Coordinate(wptType.getLon().doubleValue(), wptType.getLat().doubleValue(),
               // elevation might not be present so take care for that
               wptType.getEle()!=null ? wptType.getEle().doubleValue() : 0);
    }
    // gpx 1.0
    public static Coordinate create(Trkpt trkpt) {
       return new Coordinate(trkpt.getLon().doubleValue(), trkpt.getLat().doubleValue(),
               // elevation might not be present so take care for that
               trkpt.getEle()!=null ? trkpt.getEle().doubleValue() : 0);
    }
    // tcx
    public static Coordinate create(TrackpointT wptType) {
        return new Coordinate(wptType.getPosition().getLongitudeDegrees(), wptType.getPosition().getLatitudeDegrees(), wptType.getAltitudeMeters());
    }
}
