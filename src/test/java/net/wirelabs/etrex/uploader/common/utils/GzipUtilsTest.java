package net.wirelabs.etrex.uploader.common.utils;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class GzipUtilsTest {

    // test file contains "She sells sea shells on the sea shore"
    // and is gzipped with gnu gzip
    private static final String expectedDecompressed = "She sells sea shells on the seashore.";
    private static final File file = new File("src/test/resources/gzipped.gz");

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

}