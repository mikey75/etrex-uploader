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
    private static final String expectedDecompressed = "She sells sea shells on the seashore.";
    private static final File file = new File("src/test/resources/gzipped.gz");
    private static final File nonExistentFile = new File("nonexistent");

    @Test
    void shouldDecompressGzippedFile() throws IOException {
        assertThat(GzipUtils.isGzipped(file)).isTrue();
        String decompressed = GzipUtils.decompress(file);
        assertThat(decompressed).isEqualTo(expectedDecompressed);
    }

    @Test
    void shoulDecompressGzippedBytes() throws IOException {
        assertThat(GzipUtils.isGzipped(file)).isTrue();
        byte[] decompressedBytes = FileUtils.readFileToByteArray(file);
        String decompressed = GzipUtils.decompress(decompressedBytes);
        assertThat(decompressed).isEqualTo(expectedDecompressed);
    }

    @Test
    void shouldThrowExceptionWhenFileCannotBeReadDuringDecompress() {

        assertThatThrownBy(() -> GzipUtils.decompress(nonExistentFile))
                .isInstanceOfAny(NoSuchFileException.class,FileNotFoundException.class)
                .hasMessage(nonExistentFile.getName());
    }

    @Test
    void shouldReturnFalseIfFileCannottBeRecognizedBecauseItIsNotExisting() {
        assertThat(GzipUtils.isGzipped(nonExistentFile)).isFalse();
        verifyLogged("Exception during gzip detection: " + nonExistentFile.getName());
    }

}