package net.wirelabs.etrex.uploader.gui.map;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.model.gpx.GpxType;
import net.wirelabs.etrex.uploader.model.gpx.TrkType;
import net.wirelabs.etrex.uploader.model.gpx.TrksegType;
import net.wirelabs.etrex.uploader.model.gpx.WptType;
import org.jxmapviewer.viewer.GeoPosition;

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
public class GPXParser {
    
    private Unmarshaller unmarshaller;
    
    public GPXParser()  {
        
        try {
            JAXBContext jc = JAXBContext.newInstance("net.wirelabs.etrex.uploader.model.gpx");
            this.unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException e) {
            log.error("JAXB exception {}", e.getMessage(),e);
        }
            
    }

    
    /**
     * Parses gpx file in geoposition format
     *
     * @param file input file
     * @return list of waypoints in GeoPosition format
     * @throws JAXBException - if xml parsing error
     */
    public List<GeoPosition> parseToGeoPosition(File file) throws JAXBException {
        return parseGpxFile(file).stream()
                .map(trackPoint -> new GeoPosition(trackPoint.getLat().doubleValue(), trackPoint.getLon().doubleValue()))
                .collect(Collectors.toList());
    }

    /**
     * Parses gpx file (note: all tracks and all segments are merged into one set of waypoints)
     *
     * @param file input file
     * @return list of waypoints in GPX's own Wpt format
     * @throws JAXBException - if xml parsing error
     */
    public List<WptType> parseGpxFile(File file) throws JAXBException {

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

        throw new IllegalStateException("File does not contain a gpx track");

    }

    
}

    