package net.wirelabs.etrex.uploader.device;


import com.garmin.xmlschemas.garminDevice.v2.DeviceT;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.io.FileUtils.copyFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class GarminDeviceServiceTest extends BaseTest {

    private static final File GARMIN_DRIVE_ONE = new File("target/disk1");
    private static final File GARMIN_DRIVE_TWO = new File("target/disk2");
    private static final File NON_GARMIN_DRIVE = new File("target/disk3");

    private static final File GARMIN_DIR_ONE = new File(GARMIN_DRIVE_ONE, "GARMIN");
    private static final File GARMIN_DIR_TWO = new File(GARMIN_DRIVE_TWO, "GaRmin");
    private static final File DEVICE_XML_FILE = new File("src/test/resources/garmin", Constants.GARMIN_DEVICE_XML);
    private static final File DEVICE_XML_FILE_INVALID = new File("src/test/resources/garmin/GarminDevice-invalid.xml");

    private GarminDeviceService garminDeviceService;

    private final List<File> roots = new ArrayList<>();
    private final AppConfiguration testApplicationConfiguration = mock(AppConfiguration.class);

    @BeforeEach
    void beforeEach() {

        when(testApplicationConfiguration.getDeviceDiscoveryDelay()).thenReturn(200L);
        when(testApplicationConfiguration.getWaitDriveTimeout()).thenReturn(200L);
        when(testApplicationConfiguration.getStorageRoot()).thenReturn(Paths.get(Constants.DEFAULT_LOCAL_STORE));

        RootsProvider mockRootsProvider = Mockito.spy(new RootsProvider());
        doReturn(roots).when(mockRootsProvider).getRoots();

        garminDeviceService = Mockito.spy(new GarminDeviceService(mockRootsProvider, testApplicationConfiguration));
    }

    @AfterEach
    void after() {
        // not every test executes service.start() so stop service only if it is running
        if (garminDeviceService.getThreadHandle() != null) {
            stopAndAssertTermination();
        }
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
        assertThat(garminDeviceService.getThreadHandle()).isNull();
        // when & then
        startAndAssertStarted();
        stopAndAssertTermination();
    }

    @Test
    void shouldDetectOneGarminDrive() {
        //given
        startAndAssertStarted();
        //when
        addDrive(GARMIN_DRIVE_ONE);
        //then
        waitUntilAsserted(Duration.ofSeconds(5), () -> {
            verify(garminDeviceService, times(1)).registerDrive(GARMIN_DRIVE_ONE);
            assertThat(garminDeviceService.getRegisteredRoots()).hasSize(1).containsOnly(GARMIN_DRIVE_ONE);
        });

    }

    @Test
    void shouldDetectTwoGarminDrives() {
        //given
        startAndAssertStarted();
        // when
        addDrive(GARMIN_DRIVE_ONE, GARMIN_DRIVE_TWO);
        // then
        waitUntilAsserted(Duration.ofSeconds(5), () -> {
            assertThat(garminDeviceService.getRegisteredRoots()).hasSize(2).containsExactly(GARMIN_DRIVE_ONE, GARMIN_DRIVE_TWO);
        });

    }

    @Test
    void shouldDetectDriveDisconnection() {
        // given
        garminDeviceService.getRegisteredRoots().add(GARMIN_DRIVE_ONE); // make drive appear as if it is already registered
        assertThat(garminDeviceService.getRegisteredRoots()).contains(GARMIN_DRIVE_ONE); // assure drive exists
        startAndAssertStarted();
        // when
        removeDrive(GARMIN_DRIVE_ONE);
        // then
        waitUntilAsserted(Duration.ofSeconds(5), () -> assertThat(garminDeviceService.getRegisteredRoots()).isEmpty());

    }

    @Test
    void shouldNotRegisterDriveIfNotGarmin() {
        // given
        startAndAssertStarted();
        // when
        addDrive(NON_GARMIN_DRIVE);
        // then
        assertThat(garminDeviceService.getRegisteredRoots()).isEmpty();

    }

    @Test
    void shouldFindAndPublishDeviceInfo() throws IOException {
        // given
        startAndAssertStarted();
        // when
        FileUtils.copyFileToDir(DEVICE_XML_FILE, GARMIN_DRIVE_ONE);
        addDrive(GARMIN_DRIVE_ONE);
        // then
        waitUntilAsserted(Duration.ofSeconds(5), () -> verify(garminDeviceService, times(1)).publishFoundHardwareInfo(any(DeviceT.class)));
    }

    @Test
    void shouldNotFindDeviceInfoFileIfNotExists() throws IOException {
        // given
        FileUtils.recursivelyDeleteDirectory(GARMIN_DRIVE_ONE); // delete garmin files
        startAndAssertStarted();
        // when
        addDrive(GARMIN_DRIVE_ONE);
        // then
        waitUntilAsserted(Duration.ofSeconds(2), () -> {
            verifyLogged("Listening for new drives");
            verifyLogged("Failed waiting for drive " + GARMIN_DRIVE_ONE.getPath() + " being available");
        });
    }

    @Test
    void shouldLogWarningWhenXMLisInvalid() throws IOException {
        // given
        startAndAssertStarted();
        // when
        copyFile(DEVICE_XML_FILE_INVALID, new File(GARMIN_DRIVE_ONE,Constants.GARMIN_DEVICE_XML),StandardCopyOption.REPLACE_EXISTING);
        addDrive(GARMIN_DRIVE_ONE);
        // then
        // when garmin xml is invalid, we should issue warning and the reason but still listen for new drives
        waitUntilAsserted(Duration.ofSeconds(2), () -> {
            verifyLogged("Can't parse " + Constants.GARMIN_DEVICE_XML + " on drive " + GARMIN_DRIVE_ONE);
            verifyLogged("Listening for new drives");
        });
    }

    @Test
    void shouldUseDefaultRootProviderWhenNotSpecified() {
        GarminDeviceService garminDeviceServiceDef = new GarminDeviceService(testApplicationConfiguration);
        assertThat(garminDeviceServiceDef.getRootsProvider()).isNotNull().isInstanceOf(RootsProvider.class);
    }

    private void addDrive(File... disk) {
        roots.addAll(Arrays.asList(disk));
    }

    private void removeDrive(File... disk) {
        roots.removeAll(Arrays.asList(disk));
    }

    private void stopAndAssertTermination() {
        garminDeviceService.stop();
        waitUntilAsserted(Duration.ofSeconds(5), () -> {
            assertThat(garminDeviceService.getThreadHandle().isDone()).isTrue();
            verifyLogged("Garmin Device Service stopping");
            verifyLogged("Device observer stopped");
        });
    }

    private void startAndAssertStarted() {
        garminDeviceService.start();
        waitUntilAsserted(Duration.ofSeconds(5), () -> assertThat(garminDeviceService.getThreadHandle()).isNotNull());
        verifyLogged("Starting Garmin device discovery thread");
        verifyLogged("Registering already connected drives");
        verifyLogged("Listening for new drives");
    }
}
