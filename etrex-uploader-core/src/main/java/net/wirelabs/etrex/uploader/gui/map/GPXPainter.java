package net.wirelabs.etrex.uploader.gui.map;

import com.garmin.fit.FitDecoder;
import com.garmin.fit.FitMessages;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.util.SemicirclesConverter;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created 8/4/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class GPXPainter {

    private final JXMapViewer mapViewer;
    private final GPXParser gpxParser;
    private final FitDecoder fitDecoder;

    public GPXPainter(JXMapViewer mapViewer) {
        this.mapViewer = mapViewer;
        this.gpxParser = new GPXParser();
        this.fitDecoder = new FitDecoder();
    }

    public PaintResult paintRouteFromTrackPoints(List<GeoPosition> track) {
        
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

    public PaintResult paintRouteFromGpxFile(File gpxFile) {

        if (gpxFile != null) {
            try {
                List<GeoPosition> track = gpxParser.parseToGeoPosition(gpxFile);
                return paintRouteFromTrackPoints(track);
            } catch (JAXBException e) {
                return PaintResult.GPX_FILE_LOAD_ERROR;
            }
        }
        return PaintResult.FAIL;
    }

    public PaintResult paintRouteFromFitFile(File fitFile) {

            FitMessages fitMessages = fitDecoder.decode(fitFile);
            List<RecordMesg> records = fitMessages.getRecordMesgs();
            List<GeoPosition> track = new ArrayList<>();
            
            for (RecordMesg r : records) {
                if (r.hasField(RecordMesg.PositionLongFieldNum) && r.hasField(RecordMesg.PositionLatFieldNum)) {
                    double lattitude = SemicirclesConverter.semicirclesToDegrees(r.getPositionLat());
                    double longitude = SemicirclesConverter.semicirclesToDegrees(r.getPositionLong());
                    GeoPosition position = new GeoPosition(lattitude, longitude);
                    track.add(position);
                }
            }
            return paintRouteFromTrackPoints(track);
    }
}
