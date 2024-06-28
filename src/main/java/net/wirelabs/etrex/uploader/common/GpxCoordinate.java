package net.wirelabs.etrex.uploader.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx;
import net.wirelabs.etrex.uploader.model.gpx.ver11.WptType;
import net.wirelabs.jmaps.map.geo.Coordinate;

/**
 * Class for easy creation of jmaps coordinate objects from different versions of gpx files
 * (from WptType for gpx 1.1, and TrkPt for gpx 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GpxCoordinate {

    public static Coordinate create(WptType wptType) {
       return new Coordinate(wptType.getLon().doubleValue(), wptType.getLat().doubleValue(), wptType.getEle().doubleValue());
    }

    public static Coordinate create(Gpx.Trk.Trkseg.Trkpt trkpt) {
       return new Coordinate(trkpt.getLon().doubleValue(), trkpt.getLat().doubleValue(), trkpt.getEle().doubleValue());
    }
}
