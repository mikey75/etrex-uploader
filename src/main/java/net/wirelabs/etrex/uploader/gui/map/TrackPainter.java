package net.wirelabs.etrex.uploader.gui.map;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.HashSet;
import java.util.List;

/**
 * Created 8/4/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class TrackPainter {

    private final JXMapViewer mapViewer;
    private final GPXParser gpxParser;
    private final FITParser fitParser;

    public TrackPainter(JXMapViewer mapViewer) {
        this.mapViewer = mapViewer;
        this.gpxParser = new GPXParser();
        this.fitParser = new FITParser();
    }

    public PaintResult paintTrackFromTrackPoints(List<GeoPosition> track) {
        
        if (track != null && !track.isEmpty()) {
            try {
                RoutePainter routePainter = new RoutePainter(track);
                mapViewer.setOverlayPainter(routePainter);
                mapViewer.zoomToBestFit(new HashSet<>(track), 0.7);
                return PaintResult.SUCCESS;
            } catch (IndexOutOfBoundsException e) {
                return PaintResult.AUTOZOOM_FAILED;
            }
        }
        return PaintResult.FAIL;
    }

    public PaintResult paintTrackFromGpxFile(File gpxFile) {

        if (gpxFile != null ) {
            try {
                List<GeoPosition> track = gpxParser.parseToGeoPosition(gpxFile);
                return paintTrackFromTrackPoints(track);
            } catch (JAXBException e) {
                return PaintResult.GPX_FILE_LOAD_ERROR;
            }
        }
        return PaintResult.FAIL;
    }

    public PaintResult paintTrackFromFitFile(File fitFile) {

           if (fitFile != null) {
               List<GeoPosition> track = fitParser.parseToGeoPosition(fitFile);
               return paintTrackFromTrackPoints(track);
           }
           return PaintResult.FAIL;
    }
}
