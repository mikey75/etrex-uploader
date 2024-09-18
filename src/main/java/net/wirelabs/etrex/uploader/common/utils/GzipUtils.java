package net.wirelabs.etrex.uploader.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GzipUtils {

    public static String decompress(File f) throws IOException {
        try {
            byte[] byteContent = Files.readAllBytes(f.toPath());
            return decompress(byteContent);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public static String decompress(final byte[] compressed) throws IOException {

        try (GZIPInputStream gzipInput = new GZIPInputStream(new ByteArrayInputStream(compressed));
             StringWriter stringWriter = new StringWriter()) {
            IOUtils.copy(gzipInput, stringWriter, UTF_8);
            return stringWriter.toString();
        }
    }

    public static boolean isGzipped(File f) {
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            int magic = raf.read() & 0xff | ((raf.read() << 8) & 0xff00);
            return magic == GZIPInputStream.GZIP_MAGIC;
        } catch (IOException e) {
            log.error("Exception during gzip detection", e);
            return false;
        }
    }

}
