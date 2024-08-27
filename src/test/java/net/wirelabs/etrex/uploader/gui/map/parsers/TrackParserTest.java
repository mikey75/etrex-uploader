package net.wirelabs.etrex.uploader.gui.map.parsers;

import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx.Trk.Trkseg.Trkpt;
import net.wirelabs.etrex.uploader.model.gpx.ver11.WptType;
import net.wirelabs.etrex.uploader.model.tcx.TrackpointT;
import net.wirelabs.jmaps.map.geo.Coordinate;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class TrackParserTest {

    private static final File GPX_FILE_VER_1_0 = new File("src/test/resources/gpx10.gpx");
    private static final File GPX_FILE_VER_1_1 = new File("src/test/resources/gpx11.gpx");
    private static final File TCX_FILE = new File("src/test/resources/tcx1.tcx");
    private static final File NOT_TRACK_FILE = new File("src/test/resources/test.png");

    private final GPXParser gpxParser = new GPXParser();
    private final TCXParser tcxParser = new TCXParser();

    @Test
    void shouldProcessGpxVerion10() {

        List<Trkpt> points = gpxParser.parseGpx10File(GPX_FILE_VER_1_0);
        assertThat(points).isNotEmpty().hasSize(1000);
        List<Coordinate> coords = gpxParser.parseToGeoPosition(GPX_FILE_VER_1_0);
        assertThat(coords).isNotEmpty().hasSize(1000);

    }

    @Test
    void shouldProcessGpxVerion11() {

        List<WptType> points = gpxParser.parseGpx11File(GPX_FILE_VER_1_1);
        assertThat(points).isNotEmpty().hasSize(1511);
        List<Coordinate> coords = gpxParser.parseToGeoPosition(GPX_FILE_VER_1_1);
        assertThat(coords).isNotEmpty().hasSize(1511);
    }

    @Test
    void shouldProcessTCX() {

        List<TrackpointT> points = tcxParser.parseTcxFile(TCX_FILE);
        assertThat(points).isNotEmpty().hasSize(263);
        List<Coordinate> coords = tcxParser.parseToGeoPosition(TCX_FILE);
        assertThat(coords).isNotEmpty().hasSize(263);

    }

    @Test
    void shouldDetectTrack() {

        assertThat(FileUtils.isGpxFile(GPX_FILE_VER_1_0)).isTrue();
        assertThat(FileUtils.isGpxFile(GPX_FILE_VER_1_1)).isTrue();
        assertThat(gpxParser.isGpx10File(GPX_FILE_VER_1_0)).isTrue();
        assertThat(gpxParser.isGpx10File(GPX_FILE_VER_1_1)).isFalse();
        assertThat(FileUtils.isTcxFile(TCX_FILE)).isTrue();
    }

    @Test
    void shouldThrowWhenNonGpxOrNonTcxFile() {

        List<?> points = gpxParser.parseGpx11File(NOT_TRACK_FILE);
        assertThat(points).isEmpty();

        points = gpxParser.parseGpx10File(NOT_TRACK_FILE);
        assertThat(points).isEmpty();

        points = tcxParser.parseTcxFile(NOT_TRACK_FILE);
        assertThat(points).isEmpty();
    }
}