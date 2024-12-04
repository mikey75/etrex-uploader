package net.wirelabs.etrex.uploader.common.parsers;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.jmaps.map.geo.Coordinate;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static net.wirelabs.etrex.uploader.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class TrackParserTest extends BaseTest {

    private final TrackParser trackParser = new TrackParser();
    private static final File NONEXISTENT_GPX_FILE = new File("src/test/resources/nonexisting.gpx");

    @Test
    void shouldNotProcessBadXmlGpx() {
        // v1.0
        List<Coordinate> coords = trackParser.parseTrackFile(BAD_XML_GPX_1_0_FILE);
        assertThat(coords).isEmpty();
        verifyLogged("Could not parse GPS file " + BAD_XML_GPX_1_0_FILE);
        // v1.1
        coords = trackParser.parseTrackFile(BAD_XML_GPX_1_1_FILE);
        assertThat(coords).isEmpty();
        verifyLogged("Could not parse GPS file " +BAD_XML_GPX_1_1_FILE);

    }

    @Test
    void shouldNotProcessBadXmlTCXFile() {
        List<Coordinate> coords = trackParser.parseTrackFile(BAD_XML_TCX_FILE);
        assertThat(coords).isEmpty();
        verifyLogged("Could not parse GPS file " + BAD_XML_TCX_FILE);
    }

    @Test
    void shouldNotProcessBadXmlFitFile() {
        List<Coordinate> coords = trackParser.parseTrackFile(BAD_FIT_FILE);
        assertThat(coords).isEmpty();
        verifyLogged("Could not parse GPS file " + BAD_FIT_FILE);
    }

    @Test
    void shouldProcessGpxVerion10() {
        List<Coordinate> coords = trackParser.parseTrackFile(GPX_FILE_VER_1_0);
        assertThat(coords).isNotEmpty().hasSize(1000);
        assertElevationPresentInResultingCoordinates(coords);
    }

    @Test
    void shouldProcessGpxVerion11() {
        List<Coordinate> coords = trackParser.parseTrackFile(GPX_FILE_VER_1_1);
        assertThat(coords).isNotEmpty().hasSize(1511);
        assertElevationPresentInResultingCoordinates(coords);
    }

    @Test
    void shouldProcessTCX() {
        List<Coordinate> coords = trackParser.parseTrackFile(TCX_FILE);
        assertThat(coords).isNotEmpty().hasSize(263);
        assertElevationPresentInResultingCoordinates(coords);
    }

    @Test
    void shouldProcessFIT() {

        List<Coordinate> coords = trackParser.parseTrackFile(FIT_FILE);
        assertThat(coords).isNotEmpty().hasSize(933);
        assertElevationPresentInResultingCoordinates(coords);

    }

    @Test
    void shouldNotProcessNonExistentGPX() {
        List<Coordinate> coords = trackParser.parseTrackFile(NONEXISTENT_GPX_FILE);
        assertThat(coords).isEmpty();
    }

    @Test
    void shouldProcessPolylineTrack() {
        // this polyline is taken from one of strava activities, i checked it on strava and it has 175 waypoints
        String polyline = "qorxHimgeCKE?E?FDCCU?m@LsANe@Fg@JQJCDK?U?RCHACFOD?ADCCDH@IBBC?EL?NBq@B^@@GQBB?CBJB?CE?HD?S@@GGD?GL@@NDMDBATMs@J\\?JEEA_@?JCFAEOQIJAHB@NMD@FDFXKRD[?e@A@CJ?LDFAEMAFGBK@`@@?ACDHEFGR?HHHAu@IYIA@HCLLFDCCA?E@LEs@GBHJAHDn@Gr@M^APOHGLGp@BZEPEBG@BO@LASCBAA?DAGBHABHDC@GM@GB@@HCE?FCG?I?DDBGIDBC@?TEG?KGJBA@?j@z@d@\\f@Pj@JH?NJXf@RXJBDHBNPNDN?@GCCG@BEL?KEIUQAI@A?MGMGEEOQY@ICIo@Be@IOQ_A[GBO`@G^@C";
        List<Coordinate> coords = trackParser.parsePolyline(polyline, 1E5F);
        assertThat(coords).isNotEmpty().hasSize(175);
        // polylines have no elevation
        assertZeroElevationInResultingCoordinates(coords);

    }

    @Test
    void shouldBeEmptyOnNonTrackFile() {

        List<Coordinate> points = trackParser.parseTrackFile(NOT_TRACK_FILE);
        verifyLogged("Unsupported track file: " + NOT_TRACK_FILE.getName());
        assertThat(points).isEmpty();
    }

    @Test
    void assumeElevationZeroWhenNotPresent() {

        List<Coordinate> coords = trackParser.parseTrackFile(NO_ELEVATION_GPX_1_0_FILE);
        assertZeroElevationInResultingCoordinates(coords);

        coords = trackParser.parseTrackFile(NO_ELEVATION_GPX_1_1_FILE);
        assertZeroElevationInResultingCoordinates(coords);

        coords = trackParser.parseTrackFile(NO_ELEVATION_TCX_FILE);
        assertZeroElevationInResultingCoordinates(coords);

        coords = trackParser.parseTrackFile(NO_ELEVATION_FIT_FILE);
        assertZeroElevationInResultingCoordinates(coords);

    }

    private static void assertZeroElevationInResultingCoordinates(List<Coordinate> coords) {

        assertThat(coords).isNotEmpty()
                .extracting(Coordinate::getAltitude)
                .containsOnly(Double.valueOf(0));
    }

    private static void assertElevationPresentInResultingCoordinates(List<Coordinate> coords) {

        assertThat(coords).isNotEmpty()
                .extracting(Coordinate::getAltitude)
                .doesNotContain(Double.valueOf(0));
    }

}