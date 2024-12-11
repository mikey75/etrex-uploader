package net.wirelabs.etrex.uploader.common.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    private static final File LISTER_DIR = new File("src/test/resources/fileutils/lister");

    @Test
    void shouldParseFileNameExtensionAndName() throws IOException {
        String extensionPart;
        String filePart;

        extensionPart = getExtensionPart(SINGLE_EXTENSION_FILE.getName());
        filePart = getFilePart(SINGLE_EXTENSION_FILE.getName());

        assertThat(filePart).isEqualTo("testfile");
        assertThat(extensionPart).isEqualTo("txt");



        extensionPart = getExtensionPart(MULTI_EXTENSION_FILE.getName());
        filePart = getFilePart(MULTI_EXTENSION_FILE.getName());

        assertThat(extensionPart).isEqualTo("gpx");
        assertThat(filePart).isEqualTo("testfile.txt.abc");

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

    @Test
    void shouldListSortedFilesInDirectory() {
        // sorting is alphabethic, uppercase first.
        List<File> sorted = listDirectorySorted(LISTER_DIR);
        assertThat(sorted).isNotEmpty()
                .extracting(File::getName)
                .containsExactly("Ala.txt", "KaKZ.txt", "Kaka.txt", "Zosia.txt", "kaks.txt");
    }

    @Test
    void shouldListFilesInDirectory() {
        List<File> unsorted = listDirectory(LISTER_DIR);
        assertThat(unsorted).isNotEmpty()
                .extracting(File::getName)
                .containsExactlyInAnyOrder("Zosia.txt", "Ala.txt", "Kaka.txt", "KaKZ.txt", "kaks.txt");
    }
}