package net.wirelabs.etrex.uploader.gui.map.parsers;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.GpxCoordinate;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx.Trk;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx.Trk.Trkseg;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx.Trk.Trkseg.Trkpt;
import net.wirelabs.etrex.uploader.model.gpx.ver11.GpxType;
import net.wirelabs.etrex.uploader.model.gpx.ver11.TrkType;
import net.wirelabs.etrex.uploader.model.gpx.ver11.TrksegType;
import net.wirelabs.etrex.uploader.model.gpx.ver11.WptType;
import net.wirelabs.jmaps.map.geo.Coordinate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.io.FileUtils.readFileToString;

/**
 * Created 8/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
class GPXParser {

    private Unmarshaller unmarshallerForVer11; // for gpx version 1.1
    private Unmarshaller unmarshallerForVer10; // for gpx version 1.0

    public GPXParser() {

        try {
            this.unmarshallerForVer11 = JAXBContext.newInstance("net.wirelabs.etrex.uploader.model.gpx.ver11")
                    .createUnmarshaller();
            this.unmarshallerForVer10 = JAXBContext.newInstance("net.wirelabs.etrex.uploader.model.gpx.ver10")
                    .createUnmarshaller();
        } catch (JAXBException e) {
            log.error("JAXB exception {}", e.getMessage(), e);
        }

    }


    /**
     * Parses gpx file in geoposition format
     *
     * @param file input file
     * @return list of waypoints in GeoPosition format
     */
    public List<Coordinate> parseToGeoPosition(File file) {

        if (isGpx10File(file)) {
            return parseGpx10File(file).stream()
                    .map(GpxCoordinate::create)
                    .collect(Collectors.toList());
        } else {
            return parseGpx11File(file).stream()
                    .map(GpxCoordinate::create)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Parses gpx file (note: all tracks and all segments are merged into one set of waypoints)
     *
     * @param file input file
     * @return list of waypoints in GPX's own Wpt format
     */
    public List<WptType> parseGpx11File(File file) {
        List<WptType> result = new ArrayList<>();
        try {
            JAXBElement<GpxType> root = (JAXBElement<GpxType>) unmarshallerForVer11.unmarshal(file);

            List<TrkType> tracks = root.getValue().getTrk();
            for (TrkType track : tracks) {
                track.getTrkseg().stream()
                        .map(TrksegType::getTrkpt)
                        .forEach(result::addAll);
            }

        } catch (JAXBException e) {
            logParseErrorMessage(file, e);
        }
        return result;
    }

    /**
     * Parses gpx 1.0 file (note: all tracks and all segments are merged into one set of waypoints)
     *
     * @param file input file
     * @return list of waypoints in GPX's own Wpt format
     */
    public List<Trkpt> parseGpx10File(File file) {
        List<Trkpt> result = new ArrayList<>();
        try {
            Gpx root = (Gpx) unmarshallerForVer10.unmarshal(file);

            List<Trk> tracks = root.getTrk();
            for (Trk track : tracks) {
                track.getTrkseg().stream()
                        .map(Trkseg::getTrkpt)
                        .forEach(result::addAll);
            }

        } catch (JAXBException e) {
            logParseErrorMessage(file, e);
        }
        return result;
    }

    boolean isGpx10File(File file) {
        try {
            String content = readFileToString(file, StandardCharsets.UTF_8);
            return content.contains("<gpx version=\"1.0\"") || content.contains("xmlns=\"http://www.topografix.com/GPX/1/0\"");
        } catch (IOException e) {
            log.error("Could not read file {}", file);
            return false;
        }
    }

    private void logParseErrorMessage(File file, JAXBException e) {
        log.warn("Could not parse GPX file {}", file, e);
    }
}




    