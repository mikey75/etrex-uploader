package net.wirelabs.etrex.uploader.strava.utils;

import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.tools.FakeHttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.CALLS_REAL_METHODS;

class StravaUtilTest extends BaseTest {

    private final List<InetAddress> FAKE_HOST_LIST = getFakeHosts();
    private final String NONEXISTING_HOST = "www.nonexistent.pl";

    @ParameterizedTest
    @MethodSource("provideFilenames")
    void shouldDetectCorrectUploadTypes(String input, String expected) throws StravaException {
        File testFile = new File(input);
        String result = StravaUtil.guessUploadFileFormat(testFile);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionOnUnrecognizedUploadFile() {
        File testFile = new File("garmin.jpg");
        StravaException thrown = Assertions.assertThrows(StravaException.class, () -> StravaUtil.guessUploadFileFormat(testFile));
        assertThat(thrown).hasMessage("The file you're uploading is in unsupported format");

    }

    @Test
    void shouldRecognizeFileContentForHttpRequest() {

        File gpxFile = new File("file.gpx");
        File txtFile = new File("file.txt");
        File pngFile = new File("file.png");
        String type = StravaUtil.guessContentTypeFromFileName(gpxFile);
        // binary/track files are handled as application/octet-stream by http client
        assertThat(type).isEqualTo("application/octet-stream");
        // text files are just text
        type = StravaUtil.guessContentTypeFromFileName(txtFile);
        assertThat(type).isEqualTo("text/plain");
        // jpeg is image/jpeg
        type = StravaUtil.guessContentTypeFromFileName(pngFile);
        assertThat(type).isEqualTo("image/png");

    }

    @Test
    void checkStravaHostsInaccessible() throws IOException {

        // hosts will not be accessible -> no http server, no port
        // so setup test with nonexistent, random port
        int FAKE_HTTP_PORT = NetworkingUtils.getRandomFreeTcpPort();
        try (MockedStatic<NetworkingUtils> netUtils = Mockito.mockStatic(NetworkingUtils.class,CALLS_REAL_METHODS);
             MockedStatic<StravaUtil> stravaUtil = Mockito.mockStatic(StravaUtil.class,CALLS_REAL_METHODS)) {

            stravaUtil.when(() -> {
                int port = StravaUtil.getStravaPort();
                assertThat(port).isNotEqualTo(FAKE_HTTP_PORT);
            }).thenReturn(FAKE_HTTP_PORT);

            netUtils.when(() -> NetworkingUtils.getAllIpsForHost(any())).thenReturn(FAKE_HOST_LIST);

            // should log warning, and return false
            assertThat(StravaUtil.isStravaUp(1000)).isFalse();
            // should log hosts unavailability
            verifyLogged(FAKE_HOST_LIST.get(0).getHostAddress() + ":" + FAKE_HTTP_PORT + " is unreachable");    // msg from networkingUtils (no http port)
            verifyLogged(FAKE_HOST_LIST.get(0).getHostAddress() + ":" + FAKE_HTTP_PORT + " inaccessible, assume uploads might fail"); // msg from stravautil
        }

    }

    @Test
    void checkExceptionAndLogOnUnknownHost() {
        try (MockedStatic<StravaUtil> stravaUtil = Mockito.mockStatic(StravaUtil.class, CALLS_REAL_METHODS)) {
            stravaUtil.when(() -> {
                String hostname = StravaUtil.getStravaHostName();
                assertThat(hostname).isNotEqualTo(NONEXISTING_HOST);
            }).thenReturn(NONEXISTING_HOST);

            assertThat(StravaUtil.isStravaUp(1000)).isFalse();
            verifyLogged("Strava or network is down!");
        }
    }

    @Test
    void testStravaHostAccesible() throws IOException {

        // hosts will be accessible (http server up, alas - we have to run the http port on other than 80 for this
        // because 80 is root/admin only - so we setup fake server on random port
        // and force StravaUtil to use that port

        FakeHttpServer fakeHttpServer = new FakeHttpServer();
        verifyLogged("Fake http server started on port "+ fakeHttpServer.getListeningPort());

        try (MockedStatic<NetworkingUtils> netUtils = Mockito.mockStatic(NetworkingUtils.class,CALLS_REAL_METHODS);
             MockedStatic<StravaUtil> stravaUtil = Mockito.mockStatic(StravaUtil.class, CALLS_REAL_METHODS)) {

            stravaUtil.when(() -> {
                int port = StravaUtil.getStravaPort();
                assertThat(port).isNotEqualTo(fakeHttpServer.getListeningPort());
            }).thenReturn(fakeHttpServer.getListeningPort());

            netUtils.when(() -> NetworkingUtils.getAllIpsForHost(any())).thenReturn(FAKE_HOST_LIST);

            // should return true
            assertThat(StravaUtil.isStravaUp(1000)).isTrue();
            // should not log host unavailability
            verifyNeverLogged(FAKE_HOST_LIST.get(0).getHostAddress() + ":" + fakeHttpServer.getListeningPort() + " is unreachable");    // msg from networkingUtils (no http port)
            verifyNeverLogged(FAKE_HOST_LIST.get(0).getHostAddress() + ":" + fakeHttpServer.getListeningPort() + " inaccessible, assume uploads might fail"); // msg from stravautil
        } finally {
            fakeHttpServer.terminate();
        }
    }

    @Test
    void shouldReturnDefaultPortAndHost() {
        assertThat(StravaUtil.getStravaHostName()).isEqualTo(StravaUtil.STRAVA_HOST_NAME);
        assertThat(StravaUtil.getStravaPort()).isEqualTo(StravaUtil.STRAVA_HTTP_PORT);
    }

    private static List<InetAddress> getFakeHosts() {
        try {
            return List.of(InetAddress.getByName("localhost"), InetAddress.getByName("localhost"));
        } catch (UnknownHostException ex) {
            return Collections.emptyList();
        }
    }


    private static Stream<Arguments> provideFilenames() {
        return Stream.of(
                Arguments.of("file.gpx", "gpx"),
                Arguments.of("file.tcx", "tcx"),
                Arguments.of("file.fit", "fit"),

                Arguments.of("file.gpx.gz", "gpx.gz"),
                Arguments.of("file.tcx.gz", "tcx.gz"),
                Arguments.of("file.fit.gz", "fit.gz")

        );
    }
}