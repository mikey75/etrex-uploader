package net.wirelabs.etrex.uploader.common.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Created 12/18/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class FileUtilsTest {

    @Test
    void createDuplicateFileName() {

        File f = new File("test.txt");
        String s = FileUtils.createDuplicateFileName(f);
        LocalDateTime t = LocalDateTime.now();

        assertThat(s).matches("duplicate-of-test-txt-"
                + t.getYear()
                + "-"
                + t.getMonth().getValue()
                + "-"
                + t.getDayOfMonth()
                + "-"
                + String.format("%02d",t.getHour())
                + String.format("%02d",t.getMinute())
                +".*");


    }

    @Test
    void getFilePart() throws IOException {

        File f = new File("dupa.txt");
        File f2 = new File("dupa.txt.abc.gpx");

        String ext = FileUtils.getExtensionPart(f.getName());
        assertThat(ext).isEqualTo("txt");
        ext = FileUtils.getFilePart(f.getName());
        assertThat(ext).isEqualTo("dupa");

        ext = FileUtils.getExtensionPart(f2.getName());
        assertThat(ext).isEqualTo("gpx");
        ext = FileUtils.getFilePart(f2.getName());
        assertThat(ext).isEqualTo("dupa.txt.abc");

    }


}