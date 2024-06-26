package net.wirelabs.etrex.uploader.gui.map.parsers;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.etrex.uploader.model.gpx.ver11.GpxType;
import net.wirelabs.etrex.uploader.model.gpx.ver11.TrkType;
import net.wirelabs.etrex.uploader.model.gpx.ver11.TrksegType;
import net.wirelabs.etrex.uploader.model.gpx.ver11.WptType;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx.Trk;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx.Trk.Trkseg;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx.Trk.Trkseg.Trkpt;
import net.wirelabs.jmaps.map.geo.Coordinate;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created 8/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
class GPXParser {

    private Unmarshaller unmarshaller;
    private Unmarshaller unmarshallerOld; // for older version 1.0 gpx

    public GPXParser() {

        try {
            JAXBContext jc11 = JAXBContext.newInstance("net.wirelabs.etrex.uploader.model.gpx.ver11");
            JAXBContext jc10 = JAXBContext.newInstance("net.wirelabs.etrex.uploader.model.gpx.ver10");
            this.unmarshaller = jc11.createUnmarshaller();
            this.unmarshallerOld = jc10.createUnmarshaller();
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

        if (FileUtils.isOldGpxFile(file)) {
            return parseOldGpxFile(file).stream().map(trackPoint -> new Coordinate(trackPoint.getLon().doubleValue(), trackPoint.getLat().doubleValue()))
                    .collect(Collectors.toList());
        } else {
            return parseGpxFile(file).stream()
                    .map(trackPoint -> new Coordinate(trackPoint.getLon().doubleValue(), trackPoint.getLat().doubleValue()))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Parses gpx file (note: all tracks and all segments are merged into one set of waypoints)
     *
     * @param file input file
     * @return list of waypoints in GPX's own Wpt format
     */
    public List<WptType> parseGpxFile(File file) {
        try {
            JAXBElement<GpxType> root = (JAXBElement<GpxType>) unmarshaller.unmarshal(file);

            List<TrkType> tracks = root.getValue().getTrk();
            List<WptType> result = new ArrayList<>();

            if (!tracks.isEmpty()) {
                for (TrkType track : tracks) {
                    track.getTrkseg().stream()
                            .map(TrksegType::getTrkpt)
                            .forEach(result::addAll);
                }
                return result;
            }

        } catch (JAXBException e) {
            log.warn("File does not contain a gpx track");
        }
        return Collections.emptyList();
    }

    /**
     * Parses gpx 1.0 file (note: all tracks and all segments are merged into one set of waypoints)
     *
     * @param file input file
     * @return list of waypoints in GPX's own Wpt format
     */
    public List<Trkpt> parseOldGpxFile(File file) {
        try {
            Gpx root = (Gpx) unmarshallerOld.unmarshal(file);

            List<Trk> tracks = root.getTrk();
            List<Trkpt> result = new ArrayList<>();

            if (!tracks.isEmpty()) {
                for (Trk track : tracks) {
                    track.getTrkseg().stream()
                            .map(Trkseg::getTrkpt)
                            .forEach(result::addAll);
                }
                return result;
            }

        } catch (JAXBException e) {
            log.warn("File does not contain a gpx track");
        }
        return Collections.emptyList();
    }
}




    