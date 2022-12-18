package net.wirelabs.etrex.uploader.strava.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.stream.Stream;

import net.wirelabs.etrex.uploader.strava.client.StravaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StravaUtilTest {
    
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