package net.wirelabs.etrex.uploader.common.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static net.wirelabs.etrex.uploader.TestConstants.*;
import static net.wirelabs.etrex.uploader.common.utils.FileUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

/*
 * Created 12/18/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class FileUtilsTest {

    private static final File DIRECTORY = new File("target/test_file_utils_dir");
    private static final File EXISTING_FILE = new File("src/test/resources/fileutils/testfile.txt");
    private static final File COPIED_FILE = new File(DIRECTORY,"testfile.txt");

    private static final File SINGLE_EXTENSION_FILE = new File("testfile.txt");
    private static final File MULTI_EXTENSION_FILE = new File("testfile.txt.abc.gpx");

    @Test
    void shouldParseFileNameExtensionAndName() throws IOException {
        String extension_part;
        String file_part;

        extension_part = getExtensionPart(SINGLE_EXTENSION_FILE.getName());
        file_part = getFilePart(SINGLE_EXTENSION_FILE.getName());

        assertThat(file_part).isEqualTo("testfile");
        assertThat(extension_part).isEqualTo("txt");



        extension_part = getExtensionPart(MULTI_EXTENSION_FILE.getName());
        file_part = getFilePart(MULTI_EXTENSION_FILE.getName());

        assertThat(extension_part).isEqualTo("gpx");
        assertThat(file_part).isEqualTo("testfile.txt.abc");

    }

    @Test
    void shouldDetectGPSFileType() {
        assertThat(isGpx10File(GPX_FILE_VER_1_0)).isTrue();
        assertThat(isGpx11File(GPX_FILE_VER_1_1)).isTrue();
        assertThat(isGpx11File(GPX_FILE_VER_1_0)).isFalse();
        assertThat(isGpx10File(GPX_FILE_VER_1_1)).isFalse();
        assertThat(isTcxFile(TCX_FILE)).isTrue();
        assertThat(isFitFile(FIT_FILE)).isTrue();
    }

    @Test
    void shouldDetectTrackFile() {
        assertThat(isTrackFile(GPX_FILE_VER_1_0)).isTrue();
        assertThat(isTrackFile(GPX_FILE_VER_1_1)).isTrue();
        assertThat(isTrackFile(TCX_FILE)).isTrue();
        assertThat(isTrackFile(FIT_FILE)).isTrue();
        assertThat(isTrackFile(NONEXISTENT_FILE)).isFalse();

    }


    @Test
    void shouldCreateDirIfNotExists() throws IOException {

        // check if directory does not exist before calling create
        assertThat(DIRECTORY).doesNotExist();
        // try creating
        createDirIfDoesNotExist(DIRECTORY);
        assertThat(DIRECTORY).exists();

        // delete created directory after test
        assertThat(DIRECTORY.delete()).isTrue();

    }

    @Test
    void shouldCopyFileToDirAndThenListDirectory() throws IOException {
        // first create directory
        assertThat(DIRECTORY).doesNotExist();
        createDirIfDoesNotExist(DIRECTORY);
        // copy some existing file to directory
        copyFileToDir(EXISTING_FILE, DIRECTORY);

        List<File> files = listDirectory(DIRECTORY);

        assertThat(files).hasSize(1);
        assertThat(files.get(0).getPath()).isEqualTo(COPIED_FILE.getPath());
        assertThat(files.get(0)).hasSameTextualContentAs(EXISTING_FILE);

        assertThat(recursivelyDeleteDirectory(DIRECTORY)).isTrue();
        assertThat(COPIED_FILE).doesNotExist();
        assertThat(DIRECTORY).doesNotExist();

    }


}