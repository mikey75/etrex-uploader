package net.wirelabs.etrex.uploader.device;


import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;

import net.wirelabs.etrex.uploader.model.garmin.DeviceT;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class GarminDriveDetectorTest {

    private static final File GARMIN_DRIVE_ONE = new File("target/disk1");
    private static final File GARMIN_DRIVE_TWO = new File("target/disk2");
    private static final File NON_GARMIN_DRIVE = new File("target/disk3");

    private static final File GARMIN_DIR_ONE = new File(GARMIN_DRIVE_ONE, "GARMIN");
    private static final File GARMIN_DIR_TWO = new File(GARMIN_DRIVE_TWO, "GaRmin");
    private static final File DEVICE_XML_FILE = new File("src/test/resources/GarminDevice.xml");

    private GarminDeviceService driveDetector;

    private final List<File> roots = new ArrayList<>();


    @BeforeEach
    void beforeEach(){
        Configuration testConfiguration = mock(Configuration.class);

        when(testConfiguration.getDeviceDiscoveryDelay()).thenReturn(200L);
        when(testConfiguration.getWaitDriveTimeout()).thenReturn(200L);
        when(testConfiguration.getStorageRoot()).thenReturn(System.getProperty("user.home") + File.separator + "etrex-uploader-store");

        RootsProvider rootsProvider = Mockito.spy(new RootsProvider());
        doReturn(roots).when(rootsProvider).getRoots();

        driveDetector = Mockito.spy(new GarminDeviceService(rootsProvider,testConfiguration));
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

        //Application.createConfiguration();

    }

    @Test
    void shouldStartAndStopDetectorService() {
        // given

        assertThat(driveDetector.getThreadHandle()).isNull();
        // when
        driveDetector.start();
        // then
        waitUntilAsserted(Duration.ofSeconds(5), () -> assertThat(driveDetector.getThreadHandle()).isNotNull());

        // when
        driveDetector.stop();
        // then
        waitUntilAsserted(Duration.ofSeconds(5), () -> assertThat(driveDetector.getThreadHandle().isDone()).isTrue());

    }

    @Test
    void shouldDetectOneGarminDrive() {

        //given

        driveDetector.start();


        //Sleeper.sleepSeconds(1);
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
        File targetFile = FileUtils.copyFileToDir(DEVICE_XML_FILE,GARMIN_DRIVE_ONE);
        addDrive(GARMIN_DRIVE_ONE);
        waitUntilAsserted(Duration.ofSeconds(5), () -> {
            verify(driveDetector, times(1)).publishFoundHardwareInfo(any(DeviceT.class));
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
