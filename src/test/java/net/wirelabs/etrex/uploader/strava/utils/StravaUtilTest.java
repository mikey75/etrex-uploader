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
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;

class StravaUtilTest extends BaseTest {
    
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
        StravaException thrown = Assertions.assertThrows(StravaException.class, () -> {
            StravaUtil.guessUploadFileFormat(testFile);
        });
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

        // hosts will not be accessible -> no http server, no port 80
        List<InetAddress> fakeHostList = getHostAddresses();

        try (MockedStatic<NetworkingUtils> netUtils = Mockito.mockStatic(NetworkingUtils.class)) {
            netUtils.when(() -> NetworkingUtils.isHostTcpPortReachable(anyString(), anyInt(), anyInt())).thenCallRealMethod();
            netUtils.when(() -> NetworkingUtils.getAllIpsForHost(any())).thenReturn(fakeHostList);

            // should log warning, and return false
            assertThat(StravaUtil.isStravaUp(1000)).isFalse();
            verifyLogged("127.0.0.1:80 is unreachable");    // msg from networkingUtils (no http port)
            verifyLogged("Host 127.0.0.1 inaccessible, assume uploads might fail"); // msg from stravautil
        }

    }

    @Test
    void testStravaHostAccesible() throws IOException {

        List<InetAddress> fakeHostList = getHostAddresses();

        // hosts will be accessible (http server up, alas - we have to run the http port on other than 80 for this
        // because 80 is root/admin only - so we setup fake server on another port (8888)
        // and force StravaUtil to use that port

        FakeHttpServer fakeHttpServer = new FakeHttpServer(8888);
        StravaUtil.STRAVA_HTTP_PORT = 8888;

        try (MockedStatic<NetworkingUtils> netUtils = Mockito.mockStatic(NetworkingUtils.class)) {
            netUtils.when(() -> NetworkingUtils.isHostTcpPortReachable(anyString(), anyInt(), anyInt())).thenCallRealMethod();
            netUtils.when(() -> NetworkingUtils.getAllIpsForHost(any())).thenReturn(fakeHostList);
            // should return true
            assertThat(StravaUtil.isStravaUp(1000)).isTrue();
            verifyNeverLogged("127.0.0.1:80 is unreachable");    // msg from networkingUtils (no http port)
            verifyNeverLogged("Host 127.0.0.1 inaccessible, assume uploads might fail"); // msg from stravautil
        }
    }

    private static List<InetAddress> getHostAddresses() throws UnknownHostException {
        return List.of(InetAddress.getLocalHost(), InetAddress.getLocalHost());
    }


    private static Stream<Arguments> provideFilenames() {
        return Stream.of(
                Arguments.of("file.gpx","gpx"),
                Arguments.of("file.tcx","tcx"),
                Arguments.of("file.fit","fit"),

                Arguments.of("file.gpx.gz","gpx.gz"),
                Arguments.of("file.tcx.gz","tcx.gz"),
                Arguments.of("file.fit.gz","fit.gz")
                
                );
    }
}