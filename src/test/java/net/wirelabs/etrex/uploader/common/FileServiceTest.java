package net.wirelabs.etrex.uploader.common;


import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created 10/30/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class FileServiceTest {

    private static final LocalDateTime testDateTimeNow = LocalDateTime.of(2022, 1, 2, 10, 24, 11);
    private static final String expectedFormattedPart = "2022-01-02-102411";

    private FileService fileService;
    private AppConfiguration appConfiguration;
    private final File root = new File("target/storage-root");


    @BeforeEach
    void before() throws IOException {
        FileUtils.recursivelyDeleteDirectory(root);
        setupFileServiceMock();
    }

    private void setupFileServiceMock() throws IOException {
        appConfiguration = mock(AppConfiguration.class);
        doReturn(root.getPath()).when(appConfiguration).getStorageRoot();
        doReturn(true).when(appConfiguration).isArchiveAfterUpload();
        doReturn(true).when(appConfiguration).isDeleteAfterUpload();
        fileService = Mockito.spy(new FileService(appConfiguration));

        doReturn(testDateTimeNow).when(fileService).getNow();
    }

    @Test
    void directoryInitializationTest() {
        assertThat(root).isNotNull();
        List<String> s = Arrays.stream(Objects.requireNonNull(root.listFiles()))
                .map(File::getName)
                .collect(Collectors.toList());

        assertThat(s).containsOnly(Constants.UPLOADED_FILES_SUBFOLDER, Constants.TRACKS_REPO);

    }

    @Test
    void archiveAndDeleteTest() throws IOException {

        File track = new File("target/file.gpx");
        File uploadDir = new File(root, Constants.UPLOADED_FILES_SUBFOLDER);
        File targetDir = new File(uploadDir, "2022/01");
        createTestTrack(track);
        //when
        fileService.archiveAndDelete(track);
        // then
        assertThat(targetDir).isDirectoryContaining(f -> f.getName().equals(track.getName()));
        assertThat(track).doesNotExist();
        // when
        createTestTrack(track);
        fileService.archiveAndDelete(track);
        // then check if timestamped copy is created
        assertThat(targetDir).isDirectoryContaining(f ->
                f.getName().equals(("file-" + expectedFormattedPart + ".gpx")));

    }

    @Test
    void shouldNotDeleteCurrentgpx() throws IOException {
        File track = new File("target/Current.gpx");
        File uploadDir = new File(root, Constants.UPLOADED_FILES_SUBFOLDER);
        File targetDir = new File(uploadDir, "2022/01");
        createTestTrack(track);
        //when
        fileService.archiveAndDelete(track);
        // then

        // then check if file was not deleted
        assertThat(track).exists().isFile();
        // check if archived

        assertThat(targetDir).isDirectoryContaining(f ->
                f.getName().equals(("Current.gpx")));

    }

    @Test
    void shouldNotArchiveWhenNotConfigured() throws IOException {
        doReturn(false).when(appConfiguration).isArchiveAfterUpload();
        doReturn(false).when(appConfiguration).isDeleteAfterUpload();

        File track = new File("target/file.gpx");
        File uploadDir = new File(root, Constants.UPLOADED_FILES_SUBFOLDER);

        createTestTrack(track);
        //when
        fileService.archiveAndDelete(track);

        assertThat(uploadDir).isEmptyDirectory(); // file not copied
        assertThat(track).exists(); // file not deleted
    }

    private void createTestTrack(File track) throws IOException {
        if (track.exists()) {
            if (!track.delete()) {
                fail("could not delete file ");
            }
        }
        if (!track.createNewFile()) {
            fail("Could not create file for test");
        }
    }
}