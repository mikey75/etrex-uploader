package net.wirelabs.etrex.uploader.gui.map;

import com.garmin.fit.FitDecoder;
import com.garmin.fit.FitMessages;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.util.SemicirclesConverter;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created 12/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class FITParser {

    private final FitDecoder fitDecoder;

    public FITParser() {
        this.fitDecoder = new FitDecoder();
    }

    public List<GeoPosition> parseToGeoPosition(File fitFile) {
        FitMessages fitMessages = fitDecoder.decode(fitFile);
        List<RecordMesg> records = fitMessages.getRecordMesgs();
        // return only records with position data
        return records.stream()
                .filter(record -> record.hasField(RecordMesg.PositionLongFieldNum) && record.hasField(RecordMesg.PositionLatFieldNum))
                .map(record -> {
                    double lattitude = SemicirclesConverter.semicirclesToDegrees(record.getPositionLat());
                    double longitude = SemicirclesConverter.semicirclesToDegrees(record.getPositionLong());
                    return new GeoPosition(lattitude, longitude);
                })
                .collect(Collectors.toList());
    }

}
