package net.wirelabs.etrex.uploader.parsers;

import net.wirelabs.jmaps.map.geo.Coordinate;

import java.io.File;
import java.util.List;

public interface TrackToCoordsParser {
    List<Coordinate> parseToGeoPosition(File file);
}
