package net.wirelabs.etrex.uploader.parsers;

import com.garmin.fit.*;
import com.garmin.fit.util.SemicirclesConverter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.utils.TrackFileUtils;
import net.wirelabs.jmaps.map.geo.Coordinate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

/**
 * Created 12/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
class FITParser implements TrackToCoordsParser {

    private final FitDecoder fitDecoder = new FitDecoder();

    public List<Coordinate> parseToGeoPosition(File fitFile) {
        FitMessages fitMessages;
        try {
            fitMessages = fitDecoder.decode(new FileInputStream(fitFile));
        } catch (FileNotFoundException | FitRuntimeException e) {
            log.error("Could not parse GPS file {}", fitFile);
            return Collections.emptyList();
        }
        List<RecordMesg> records = fitMessages.getRecordMesgs();
        // return only records with position data
        return records.stream()
                .filter(rec -> rec.hasField(RecordMesg.PositionLongFieldNum) && rec.hasField(RecordMesg.PositionLatFieldNum))
                .map(rec -> {
                    double latitude = SemicirclesConverter.semicirclesToDegrees(rec.getPositionLat());
                    double longitude = SemicirclesConverter.semicirclesToDegrees(rec.getPositionLong());
                    // if altitude not present in file, assume zero
                    double altitude = rec.hasField(RecordMesg.AltitudeFieldNum) ? rec.getAltitude().doubleValue() : 0;
                    return new Coordinate(longitude, latitude, altitude);
                })
                .toList();
    }

    @Override
    public boolean isSupported(File file) {
        return TrackFileUtils.isFitFile(file);
    }

}
