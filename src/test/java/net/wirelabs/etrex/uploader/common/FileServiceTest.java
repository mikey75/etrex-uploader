package net.wirelabs.etrex.uploader.common;


import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.io.FileUtils.forceDelete;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

/**
 * Created 10/30/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class FileServiceTest extends BaseTest {

    private static final LocalDateTime testDateTimeNow = LocalDateTime.of(2022, 1, 2, 10, 24, 11);
    private static final String EXPECTED_FORMATTED_PART = "2022-01-02-102411";

    private FileService fileService;
    private AppConfiguration appConfiguration;
    private final File root = new File("target/storage-root");
    private final MockedStatic<SystemUtils> systemUtilsMock = mockStatic(SystemUtils.class);

    @AfterEach
    public void afterEach() {
        systemUtilsMock.close();
    }

    @BeforeEach
    void before() throws IOException {
        FileUtils.recursivelyDeleteDirectory(root);
        setupFileServiceMock();
        // setup SystemUtils mock since now it is providing now()
        systemUtilsMock.when(SystemUtils::getNow).thenReturn(testDateTimeNow);
    }

    private void setupFileServiceMock() throws IOException {
        appConfiguration = mock(AppConfiguration.class);
        doReturn(root.toPath()).when(appConfiguration).getStorageRoot();
        doReturn(true).when(appConfiguration).isArchiveAfterUpload();
        doReturn(true).when(appConfiguration).isDeleteAfterUpload();
        fileService = Mockito.spy(new FileService(appConfiguration));
    }

    @Test
    void directoryInitializationTest() {
        assertThat(root).isNotNull();
        List<String> s = Arrays.stream(Objects.requireNonNull(root.listFiles()))
                .map(File::getName)
                .toList();

        assertThat(s).containsExactlyInAnyOrder(FileService.UPLOADED_FILES_SUBFOLDER, FileService.TRACKS_ARCHIVE_SUBFOLDER);

    }

    @Test
    void archiveAndDeleteTest() throws IOException {

        File track = new File("target/file.gpx");
        File uploadDir = new File(root, FileService.UPLOADED_FILES_SUBFOLDER);
        File targetDir = new File(uploadDir, "2022/01");
        createTestTrack(track);
        //when
        fileService.archiveAndDelete(track);
        // then
        assertThat(targetDir).isDirectoryContaining(f -> f.getName().equals(track.getName()));
        assertThat(track).doesNotExist();
        // when saving track that exists (previous step)
        createTestTrack(track);
        fileService.archiveAndDelete(track);
        // then check if timestamped copy is created
        assertThat(targetDir).isDirectoryContaining(f ->
                f.getName().equals(("file-" + EXPECTED_FORMATTED_PART + ".gpx")));
        verifyLogged("Target file exists. Changing name by adding current timestamp to filename");
        verifyLoggedTimes(2, "Archiving " + track.getPath());
        verifyLoggedTimes(2, "Deleting " + track.getPath());

    }

    @Test
    void shouldNotDeleteCurrentgpx() throws IOException {
        File track = new File("target/Current.gpx");
        File uploadDir = new File(root, FileService.UPLOADED_FILES_SUBFOLDER);
        File targetDir = new File(uploadDir, "2022/01");
        createTestTrack(track);
        //when
        fileService.archiveAndDelete(track);
        // then

        // then check if file was not deleted
        assertThat(track).exists().isFile();
        verifyNeverLogged("Deleting " + track.getPath());
        // check if archived

        assertThat(targetDir).isDirectoryContaining(f ->
                f.getName().equals(track.getName()));
        verifyLogged("Archiving " + track.getPath());

    }

    @Test
    void shouldNotArchiveWhenNotConfigured() throws IOException {
        doReturn(false).when(appConfiguration).isArchiveAfterUpload();
        doReturn(false).when(appConfiguration).isDeleteAfterUpload();

        File track = new File("target/file.gpx");
        File uploadDir = new File(root, FileService.UPLOADED_FILES_SUBFOLDER);

        createTestTrack(track);
        //when
        fileService.archiveAndDelete(track);

        assertThat(uploadDir).isEmptyDirectory(); // file not copied
        assertThat(track).exists(); // file not deleted
        verifyNeverLogged("Deleting " + track.getPath()); // deletion not logged
    }

    private void createTestTrack(File track) throws IOException {
        if (track.exists()) {
            forceDelete(track);
        }
        if (!track.createNewFile()) {
            fail("Could not create file for test");
        }
    }
}