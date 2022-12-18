package net.wirelabs.etrex.uploader.common.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Created 12/18/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class FileUtilsTest {


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