package net.wirelabs.etrex.uploader.gui;

import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class UploadServiceTest extends BaseTest {


    private AppConfiguration appConfiguration;
    private StravaConfiguration stravaConfiguration;
    private FileService fileService;

    @BeforeEach
    void before() throws IOException {
        appConfiguration = spy(new AppConfiguration("src/test/resources/config/test.properties"));
        stravaConfiguration = spy(new StravaConfiguration("src/test/resources/strava-emulator/strava-emulator-config.properties"));
        when(appConfiguration.getStorageRoot()).thenReturn(new File("target").toPath());
        fileService = spy(new FileService(appConfiguration));

        verifyLogged("Loading src/test/resources/config/test.properties");
        verifyLogged("Loading src/test/resources/strava-emulator/strava-emulator-config.properties");
        verifyLogged("Initializing directories");

    }

    @Test
    void shouldDisplayUploadDialog() {

        StravaClient stravaClient = new StravaClient(stravaConfiguration, appConfiguration, "localhost:8080", "localhost:8080/token");
        UploadService uploadService = new UploadService(stravaClient, fileService);
        // make it non-modal so that it does not stay on in test
        uploadService.getUploadDialog().setModal(false);

        File uploadFile = new File("src/test/resources/trackfiles/gpx11.gpx");

        uploadService.uploadFile(uploadFile);
        uploadService.getUploadDialog().dispose();
        assertThat(uploadService.getUploadDialog()).isNotNull();
        assertThat(uploadService.getUploadDialog().getTrackFile().getPath()).isEqualTo(uploadFile.getPath());

    }

}

