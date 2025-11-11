package net.wirelabs.etrex.uploader.strava;

import net.wirelabs.etrex.uploader.common.FileService;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.gui.desktop.devicepanel.common.filetree.UploadDialog;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.mockito.Mockito.*;

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
        stravaConfiguration.setBaseUrl("localhost:8080");
        stravaConfiguration.setBaseTokenUrl("localhost:8080/token");
        StravaClient stravaClient = new StravaClient(stravaConfiguration, appConfiguration);
        UploadService uploadService = spy(new UploadService(stravaClient, fileService));

        UploadDialog fakeUplDlg = spy(new UploadDialog(stravaClient,fileService));
        // make it non-modal so that it does not stay on in test
        fakeUplDlg.setModal(false);
        doNothing().when(fakeUplDlg).setVisible(true); // make it no display graphically
        when(uploadService.createUploadDialog()).thenReturn(fakeUplDlg);

        File uploadFile = new File("src/test/resources/trackfiles/gpx11.gpx");

        uploadService.uploadFile(uploadFile);

        waitUntilAsserted(Duration.ofMillis(500), () -> {
            verify(fakeUplDlg).setTrackFile(uploadFile, stravaConfiguration.getDefaultActivityType());
            verify(fakeUplDlg).setHostCheckupTimeout(stravaConfiguration.getStravaCheckTimeout());
        });

    }

}

