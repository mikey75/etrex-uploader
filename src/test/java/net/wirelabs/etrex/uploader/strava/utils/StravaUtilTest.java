package net.wirelabs.etrex.uploader.strava.utils;

import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.eventbus.EventBus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;

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
    void shouldPublishUsageInfo() {

        try (MockedStatic<EventBus> evbus = Mockito.mockStatic(EventBus.class)) {

            // if info empty - publish
            StravaUtil.sendRateLimitInfo(new HashMap<>());
            evbus.verify(() -> EventBus.publish(eq(EventType.RATELIMIT_INFO_UPDATE),any()),never());

            // info not empty - verify event published, and the published info contains the correct data
            StravaUtil.sendRateLimitInfo(provideHeaders());
            evbus.verify(() -> EventBus.publish(eq(EventType.RATELIMIT_INFO_UPDATE),argThat(arg -> {
                Map<String, Integer> map = (Map<String, Integer>) arg;
                assertThat(map).containsEntry("allowedDaily",2000);
                assertThat(map).containsEntry("allowed15mins",200);
                assertThat(map).containsEntry("currentDaily",50);
                assertThat(map).containsEntry("current15mins",12);
                return true;
            })));

        }

    }

    private Map<String, List<String>> provideHeaders() {
        return Map.of(
                "x-ratelimit-limit", List.of("200,2000"),
                "x-ratelimit-usage", List.of("12,50")
        );
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