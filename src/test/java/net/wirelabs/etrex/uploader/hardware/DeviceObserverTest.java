package net.wirelabs.etrex.uploader.hardware;


import static net.wirelabs.etrex.uploader.hardware.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import net.wirelabs.etrex.uploader.DirectoryCreator;
import net.wirelabs.etrex.uploader.Sleeper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeviceObserverTest {

    private DriveObserver deviceObserver;
    private static final String GARMIN_DIR_1 = "GARMIN";
    private static final String GARMIN_DIR_2 = "garmin";

    private static final File DISK_1 = new File("target/disk1");
    private static final File DISK_2 = new File("target/disk2");
    private static final File NON_GARMIN_DRIVE = new File("target/disk3");
    
    private final List<File> firstRoots = Collections.emptyList();
    private List<File> newRoots = Collections.unmodifiableList(new ArrayList<>());

    @BeforeEach
    void before() {
        // create fake drives
        DirectoryCreator.createDirIfDoesNotExist(new File(DISK_1, GARMIN_DIR_1));
        DirectoryCreator.createDirIfDoesNotExist(new File(DISK_2, GARMIN_DIR_2));
        DirectoryCreator.createDirIfDoesNotExist(NON_GARMIN_DRIVE);
        
        waitUntilAsserted(Duration.ofSeconds(2), () -> {
            assertThat(DISK_1).isDirectoryContaining(file -> file.getName().endsWith(GARMIN_DIR_1));
            assertThat(DISK_2).isDirectoryContaining(file -> file.getName().endsWith(GARMIN_DIR_2));
        });
        
        deviceObserver = spy(new DriveObserver());
        
    }

    @AfterEach
    void after() {
        deviceObserver.stop();
        waitUntilAsserted(Duration.ofSeconds(10),
                () -> assertThat(deviceObserver.isRunning).isFalse()
        );
    }

    @Test
    void testOneGarminAppears() {

        
        
        addDrives(DISK_1);
        doReturn(firstRoots,firstRoots,newRoots,newRoots,newRoots,newRoots)
                .when(deviceObserver).getRoots();
       
        new Thread(deviceObserver).start();
       
        
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
                    verify(deviceObserver, times(1)).registerDrive(any());
                    assertThat(deviceObserver.registeredDevices).containsOnly(DISK_1);
                }
        );
       
    }

    @Test
    void testTwoGarminsAppear() {


        addDrives(DISK_1,DISK_2);

        doReturn(firstRoots, firstRoots, newRoots, newRoots, newRoots, newRoots)
                .when(deviceObserver).getRoots();

        new Thread(deviceObserver).start();

        await().atMost(Duration.ofSeconds(10)).untilAsserted(
                () -> {
                    verify(deviceObserver, times(2)).registerDrive(any());
                    assertThat(deviceObserver.registeredDevices).containsOnly(DISK_1,DISK_2);
                }
        );

    }

    private void addDrives(File ...disk) {
        newRoots = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(disk)));
    }

    @Test
    void shouldDoNothingWhenNonGarminDrive() {
       
        addDrives(NON_GARMIN_DRIVE);
        
        doReturn(firstRoots,firstRoots,newRoots,newRoots,newRoots,newRoots)
                .when(deviceObserver).getRoots();
        new Thread(deviceObserver).start();
        
        await().atMost(Duration.ofSeconds(10)).untilAsserted(
                () -> verify(deviceObserver, atLeast(4)).getRoots()
        );
        
        verify(deviceObserver, never()).registerDrive(any());
        assertThat(deviceObserver.registeredDevices).isEmpty();
    }

    @Test
    void testDisconnect() {

        
        List<File> nextRoots = Lists.newArrayList(DISK_1);

        doReturn(firstRoots,firstRoots,nextRoots,nextRoots,nextRoots,nextRoots,firstRoots,firstRoots,firstRoots)
                .when(deviceObserver).getRoots();

        new Thread(deviceObserver).start();
        
        waitUntilAsserted(Duration.ofSeconds(5), () -> {
            verify(deviceObserver, times(1)).registerDrive(any());
            verify(deviceObserver, times(1)).unregisterDrive(any());
            assertThat(deviceObserver.registeredDevices).isEmpty();
        });
        

    }

}