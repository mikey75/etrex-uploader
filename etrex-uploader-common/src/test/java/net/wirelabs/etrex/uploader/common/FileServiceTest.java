package net.wirelabs.etrex.uploader.common;


import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

/**
 * Created 10/30/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class FileServiceTest {

    private FileService fileService;
    private AppConfiguration appConfiguration;
    private final File root = new File("target/storage-root");
    
    @BeforeEach
    void before() throws IOException {
        FileUtils.deleteDirectory(root);
        setupFileServiceMock();
        fileService.setupWorkDirectories();
    }

    private void setupFileServiceMock() {
        appConfiguration = mock(AppConfiguration.class);
        doReturn(root.getPath()).when(appConfiguration).getStorageRoot();
        doReturn(true).when(appConfiguration).isArchiveAfterUpload();
        doReturn(true).when(appConfiguration).isDeleteAfterUpload();
        fileService = spy(new FileService(appConfiguration));
    }

    @Test
    void directoryInitializationTest() {
        assertThat(root).isNotNull();
        assertThat(root.listFiles()).hasSize(2);
        List<String> s = Arrays.stream(Objects.requireNonNull(root.listFiles()))
                .map(File::getName)
                .collect(Collectors.toList());
        assertThat(s).containsOnly(Constants.UPLOADED_FILES_SUBFOLDER, Constants.TRACKS_REPO);

    }


    @Test
    void archiveAndDeleteTest() throws IOException {

        File track = new File("target/file.gpx");
        File uploadDir = new File(root, Constants.UPLOADED_FILES_SUBFOLDER);
        File targetDir = new File(uploadDir, FileUtils.getYearMonthTimestampedDir());
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
        assertThat(targetDir).isDirectoryContaining(f -> f.getName().matches("[0-9]{13}-" + track.getName()));

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