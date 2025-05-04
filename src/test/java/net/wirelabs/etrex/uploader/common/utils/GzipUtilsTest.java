package net.wirelabs.etrex.uploader.common.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GzipUtilsTest extends BaseTest {

    // test file contains the below text and is gzipped with gnu gzip
    private static final String EXPECTED_DECOMPRESSED = "She sells sea shells on the seashore.";
    private static final File GZIPPED_FILE = new File("src/test/resources/gzipped.gz");
    private static final File NON_EXISTENT_FILE = new File("nonexistent");

    @Test
    void shouldDecompressGzippedFile() throws IOException {
        assertThat(GzipUtils.isGzipped(GZIPPED_FILE)).isTrue();
        String decompressed = GzipUtils.decompress(GZIPPED_FILE);
        assertThat(decompressed).isEqualTo(EXPECTED_DECOMPRESSED);
    }

    @Test
    void shouldDecompressGzippedBytes() throws IOException {
        assertThat(GzipUtils.isGzipped(GZIPPED_FILE)).isTrue();
        byte[] decompressedBytes = FileUtils.readFileToByteArray(GZIPPED_FILE);
        String decompressed = GzipUtils.decompress(decompressedBytes);
        assertThat(decompressed).isEqualTo(EXPECTED_DECOMPRESSED);
    }

    @Test
    void shouldThrowExceptionWhenFileCannotBeReadDuringDecompress() {

        assertThatThrownBy(() -> GzipUtils.decompress(NON_EXISTENT_FILE))
                .isInstanceOfAny(NoSuchFileException.class,FileNotFoundException.class)
                .hasMessage(NON_EXISTENT_FILE.getName());
    }

    @Test
    void shouldReturnFalseIfFileCannotBeRecognizedBecauseItIsNotExisting() {
        assertThat(GzipUtils.isGzipped(NON_EXISTENT_FILE)).isFalse();
        verifyLogged("Exception during gzip detection: " + NON_EXISTENT_FILE.getName());
    }

}