package net.wirelabs.etrex.uploader.gui.map.parsers;

import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.etrex.uploader.model.gpx.ver10.Gpx;
import net.wirelabs.etrex.uploader.model.gpx.ver11.WptType;
import net.wirelabs.jmaps.map.geo.Coordinate;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;



class GPXParserTest {

    private static final File GPX_FILE_VER_1_0 = new File("src/test/resources/gpx10.gpx");
    private static final File GPX_FILE_VER_1_1 = new File("src/test/resources/gpx11.gpx");
    private static final File NON_GPX_FILE = new File("src/test/resources/test.png");

    private final GPXParser parser = new GPXParser();

    @Test
    void shouldProcessVerion10() {

        List<Gpx.Trk.Trkseg.Trkpt> points = parser.parseOldGpxFile(GPX_FILE_VER_1_0);
        assertThat(points).isNotEmpty();
        List<Coordinate> coords = parser.parseToGeoPosition(GPX_FILE_VER_1_0);
        assertThat(coords).isNotEmpty();
        assertThat(points).hasSameSizeAs(coords);

    }

    @Test
    void shouldProcessVerion11() {

        List<WptType> points = parser.parseGpxFile(GPX_FILE_VER_1_1);
        assertThat(points).isNotEmpty();
        List<Coordinate> coords = parser.parseToGeoPosition(GPX_FILE_VER_1_1);
        assertThat(coords).isNotEmpty();
        assertThat(points).hasSameSizeAs(coords);
    }

    @Test
    void shouldDetectGpxAndVersion() {

        assertThat(FileUtils.isGpxFile(GPX_FILE_VER_1_0)).isTrue();
        assertThat(FileUtils.isGpxFile(GPX_FILE_VER_1_1)).isTrue();
        assertThat(FileUtils.isOldGpxFile(GPX_FILE_VER_1_0)).isTrue();
        assertThat(FileUtils.isOldGpxFile(GPX_FILE_VER_1_1)).isFalse();
    }

    @Test
    void shouldThrowWhenNonGpxFile() {
        List<?> points = parser.parseGpxFile(NON_GPX_FILE);
        assertThat(points).isEmpty();

        points = parser.parseOldGpxFile(NON_GPX_FILE);
        assertThat(points).isEmpty();

    }
}