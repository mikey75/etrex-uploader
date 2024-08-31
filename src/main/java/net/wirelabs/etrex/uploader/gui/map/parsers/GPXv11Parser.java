package net.wirelabs.etrex.uploader.gui.map.parsers;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.GPSCoordinate;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created 8/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
class GPXv11Parser implements TrackToCoordsParser {
    private final String GPX11_MODEL_PKG = "net.wirelabs.etrex.uploader.model.gpx.ver11";
    private Unmarshaller unmarshaller; // for gpx version 1.1

    public GPXv11Parser() {

        try {
            this.unmarshaller = JAXBContext.newInstance(GPX11_MODEL_PKG).createUnmarshaller();

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

            return parse(file).stream()
                    .map(GPSCoordinate::create)
                    .collect(Collectors.toList());

    }

    /**
     * Parses gpx file (note: all tracks and all segments are merged into one set of waypoints)
     *
     * @param file input file
     * @return list of waypoints in GPX's own Wpt format
     */
    private List<WptType> parse(File file) {
        List<WptType> result = new ArrayList<>();
        try {
            JAXBElement<GpxType> root = (JAXBElement<GpxType>) unmarshaller.unmarshal(file);

            List<TrkType> tracks = root.getValue().getTrk();
            for (TrkType track : tracks) {
                track.getTrkseg().stream()
                        .map(TrksegType::getTrkpt)
                        .forEach(result::addAll);
            }

        } catch (IllegalArgumentException | JAXBException e) {
            log.warn("Could not parse GPS file {}", file, e);
        }
        return result;
    }





}




    