package net.wirelabs.etrex.uploader.common.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;

import java.io.File;

import static net.wirelabs.etrex.uploader.TestConstants.*;
import static net.wirelabs.etrex.uploader.common.utils.TrackFileUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

class TrackFileUtilsTest extends BaseTest {

    private static final File NONEXISTENT_FILE = new File("nonexistent.gpx");

    @Test
    void shouldDetectGPSFileType() {
        assertThat(isGpx10File(GPX_FILE_VER_1_0)).isTrue();
        assertThat(isGpx11File(GPX_FILE_VER_1_1)).isTrue();
        assertThat(isGpx11File(GPX_FILE_VER_1_0)).isFalse();
        assertThat(isGpx10File(GPX_FILE_VER_1_1)).isFalse();
        assertThat(isTcxFile(TCX_FILE)).isTrue();
        assertThat(isFitFile(FIT_FILE)).isTrue();

        // test exception logging on not being able to read file
        assertThat(NONEXISTENT_FILE).doesNotExist();
        assertThat(isGpx11File(NONEXISTENT_FILE)).isFalse();
        verifyLogged("Could not read file " + NONEXISTENT_FILE.getName());
    }

    @Test
    void shouldDetectTrackFile() {
        assertThat(isTrackFile(GPX_FILE_VER_1_0)).isTrue();
        assertThat(isTrackFile(GPX_FILE_VER_1_1)).isTrue();
        assertThat(isTrackFile(TCX_FILE)).isTrue();
        assertThat(isTrackFile(FIT_FILE)).isTrue();
        assertThat(isTrackFile(NOT_TRACK_FILE)).isFalse();

    }

}

