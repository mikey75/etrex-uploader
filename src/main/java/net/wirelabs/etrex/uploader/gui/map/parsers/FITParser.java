package net.wirelabs.etrex.uploader.gui.map.parsers;

import com.garmin.fit.FitDecoder;
import com.garmin.fit.FitMessages;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.util.SemicirclesConverter;
import net.wirelabs.jmaps.map.geo.Coordinate;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created 12/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class FITParser {

    private final FitDecoder fitDecoder;

    public FITParser() {
        this.fitDecoder = new FitDecoder();
    }

    public List<Coordinate> parseToGeoPosition(File fitFile) {
        FitMessages fitMessages = null;
        try {
            fitMessages = fitDecoder.decode(new FileInputStream(fitFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<RecordMesg> records = fitMessages.getRecordMesgs();
        // return only records with position data
        return records.stream()
                .filter(rec -> rec.hasField(RecordMesg.PositionLongFieldNum) && rec.hasField(RecordMesg.PositionLatFieldNum))
                .map(rec -> {
                    double lattitude = SemicirclesConverter.semicirclesToDegrees(rec.getPositionLat());
                    double longitude = SemicirclesConverter.semicirclesToDegrees(rec.getPositionLong());
                    return new Coordinate(longitude, lattitude);
                })
                .collect(Collectors.toList());
    }

}
