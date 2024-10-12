package net.wirelabs.etrex.uploader.device;


import com.garmin.xmlschemas.garminDevice.v2.DeviceT;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class GarminDriveDetectorTest extends BaseTest {

    private static final File GARMIN_DRIVE_ONE = new File("target/disk1");
    private static final File GARMIN_DRIVE_TWO = new File("target/disk2");
    private static final File NON_GARMIN_DRIVE = new File("target/disk3");

    private static final File GARMIN_DIR_ONE = new File(GARMIN_DRIVE_ONE, "GARMIN");
    private static final File GARMIN_DIR_TWO = new File(GARMIN_DRIVE_TWO, "GaRmin");
    private static final File DEVICE_XML_FILE = new File("src/test/resources/garmin", Constants.GARMIN_DEVICE_XML);
    private static final File DEVICE_XML_FILE_INVALID = new File("src/test/resources/garmin/GarminDevice-invalid.xml");

    private GarminDeviceService driveDetector;

    private final List<File> roots = new ArrayList<>();


    @BeforeEach
    void beforeEach(){

        AppConfiguration testApplicationConfiguration = mock(AppConfiguration.class);

        when(testApplicationConfiguration.getDeviceDiscoveryDelay()).thenReturn(200L);
        when(testApplicationConfiguration.getWaitDriveTimeout()).thenReturn(200L);
        when(testApplicationConfiguration.getStorageRoot()).thenReturn(Paths.get(Constants.DEFAULT_LOCAL_STORE));

        RootsProvider rootsProvider = Mockito.spy(new RootsProvider());
        doReturn(roots).when(rootsProvider).getRoots();

        driveDetector = Mockito.spy(new GarminDeviceService(rootsProvider, testApplicationConfiguration));
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        // create fake config


        // create fake drives
        FileUtils.createDirIfDoesNotExist(GARMIN_DIR_ONE);
        FileUtils.createDirIfDoesNotExist(GARMIN_DIR_TWO);
        FileUtils.createDirIfDoesNotExist(NON_GARMIN_DRIVE);

        waitUntilAsserted(Duration.ofSeconds(2), () -> {
            assertThat(GARMIN_DIR_ONE).isDirectory();
            assertThat(GARMIN_DIR_TWO).isDirectory();
            assertThat(NON_GARMIN_DRIVE).isDirectory();
        });

    }

    @Test
    void shouldStartAndStopDetectorService() {
        // given

        assertThat(driveDetector.getThreadHandle()).isNull();
        // when
        driveDetector.start();
        // then
        waitUntilAsserted(Duration.ofSeconds(5), () -> assertThat(driveDetector.getThreadHandle()).isNotNull());
        verifyLogged("Starting Garmin device discovery thread");
        verifyLogged("Registering already connected drives");
        verifyLogged("Listening for new drives");

        // when
        driveDetector.stop();
        // then
        waitUntilAsserted(Duration.ofSeconds(5), () -> assertThat(driveDetector.getThreadHandle().isDone()).isTrue());
        verifyLogged("Garmin Device Service stopping");
        verifyLogged("Device observer stopped");
    }

    @Test
    void shouldDetectOneGarminDrive() {

        //given
        driveDetector.start();

        //when
        addDrive(GARMIN_DRIVE_ONE);

        //then
        waitUntilAsserted(Duration.ofSeconds(5), () -> {
            verify(driveDetector, times(1)).registerDrive(GARMIN_DRIVE_ONE);
            assertThat(driveDetector.getRegisteredRoots()).hasSize(1).containsOnly(GARMIN_DRIVE_ONE);
            assertThat(driveDetector.getRegisteredRoots().get(0)).isEqualTo(GARMIN_DRIVE_ONE);
        });

    }

    @Test
    void shouldDetectTwoGarminDrives() {
        //given

        driveDetector.start();

        Sleeper.sleepSeconds(1);

        // when
        addDrive(GARMIN_DRIVE_ONE, GARMIN_DRIVE_TWO);

        waitUntilAsserted(Duration.ofSeconds(5), () -> {


            assertThat(driveDetector.getRegisteredRoots())
                    .hasSize(2)
                    .containsOnly(GARMIN_DRIVE_ONE, GARMIN_DRIVE_TWO);

            assertThat(driveDetector.getRegisteredRoots().get(0)).isEqualTo(GARMIN_DRIVE_ONE);
            assertThat(driveDetector.getRegisteredRoots().get(1)).isEqualTo(GARMIN_DRIVE_TWO);
        });

    }

    @Test
    void shouldDetectDriveDisconnection() {

        driveDetector.getRegisteredRoots().add(GARMIN_DRIVE_ONE); // make drive appear as if it is already registered
        driveDetector.start();
        Sleeper.sleepSeconds(1);


        removeDrive(GARMIN_DRIVE_ONE);

        // then
        waitUntilAsserted(Duration.ofSeconds(5), () -> {
            assertThat(driveDetector.getRegisteredRoots()).isEmpty();
        });


    }

    @Test
    void shouldNotRegisterDriveIfNotGarmin() {


        driveDetector.start();
        // when
        addDrive(NON_GARMIN_DRIVE);

        // then

        assertThat(driveDetector.getRegisteredRoots()).isEmpty();

    }

    @Test
    void shouldFindAndPublishDeviceInfo() throws IOException  {

        driveDetector.start();
        
        // when
        FileUtils.copyFileToDir(DEVICE_XML_FILE,GARMIN_DRIVE_ONE);
        addDrive(GARMIN_DRIVE_ONE);
        waitUntilAsserted(Duration.ofSeconds(5), () -> {
            verify(driveDetector, times(1)).publishFoundHardwareInfo(any(DeviceT.class));
        });
    }

    @Test
    void shouldNotFindDeviceInfoFileIfNotExists() throws IOException  {

        FileUtils.recursivelyDeleteDirectory(GARMIN_DRIVE_ONE);

        driveDetector.start();
        addDrive(GARMIN_DRIVE_ONE);

        waitUntilAsserted(Duration.ofSeconds(2), () -> {
                    verifyLogged("Listening for new drives");
                    verifyLogged("Failed waiting for drive " + GARMIN_DRIVE_ONE.getPath() + " being available");
                });
    }

    @Test
    void shouldLogWarningWhenXMLisInvalid() throws IOException  {

        driveDetector.start();

        // when
        Files.copy(DEVICE_XML_FILE_INVALID.toPath(), new File(GARMIN_DRIVE_ONE, Constants.GARMIN_DEVICE_XML).toPath(), StandardCopyOption.REPLACE_EXISTING);

        addDrive(GARMIN_DRIVE_ONE);
        // when garmin xml is invalid, we should issue warning and the reason but still listen for new drives
        waitUntilAsserted(Duration.ofSeconds(2), () -> {
            verifyLogged("Can't parse " + Constants.GARMIN_DEVICE_XML + " on drive " + GARMIN_DRIVE_ONE);
            verifyLogged("Listening for new drives");
        });
    }

    @AfterEach
    void after() {
        driveDetector.stop();

        waitUntilAsserted(Duration.ofSeconds(5), () -> assertThat(driveDetector.getThreadHandle().isDone()).isTrue());
    }
    
    private void addDrive(File... disk) {
        roots.addAll(Arrays.asList(disk));
    }

    private void removeDrive(File... disk) {
        roots.removeAll(Arrays.asList(disk));
    }

    private static void waitUntilAsserted(Duration duration, ThrowingRunnable assertion) {
        Awaitility.await().atMost(duration).untilAsserted(assertion);
    }
}
