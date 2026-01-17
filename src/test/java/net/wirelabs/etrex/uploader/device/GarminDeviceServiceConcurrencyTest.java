package net.wirelabs.etrex.uploader.device;

import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.utils.Sleeper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.concurrent.CompletableFuture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GarminDeviceServiceConcurrencyTest extends BaseTest {

    private static File GARMIN_DRIVE_1;
    private static File GARMIN_DRIVE_2;
    private static File NON_GARMIN_DRIVE;
    private static AppConfiguration appConfiguration;

    @BeforeAll
    static void before() throws IOException {
        File tempRoot = Files.createTempDirectory("garmin-test").toFile();
        tempRoot.deleteOnExit();
        GARMIN_DRIVE_1 = new File(tempRoot, "disk1");
        GARMIN_DRIVE_2 =  new File(tempRoot, "disk2");
        NON_GARMIN_DRIVE = new File(tempRoot, "disk3");

        for (File d : List.of(GARMIN_DRIVE_1, GARMIN_DRIVE_2, NON_GARMIN_DRIVE)) {
            Files.createDirectories(d.toPath());
            d.deleteOnExit();
        }

        Files.createDirectories(new File(GARMIN_DRIVE_1, "GARMIN").toPath());
        Files.createDirectories(new File(GARMIN_DRIVE_2, "Garmin").toPath());

        appConfiguration = mock(AppConfiguration.class);
        when(appConfiguration.getDeviceDiscoveryDelay()).thenReturn(10L);
        when(appConfiguration.getWaitDriveTimeout()).thenReturn(50L);
    }



    @Test
    void shouldHandleConcurrentFilesystemDriveChanges()  {
        final CompletableFuture<Void> modifierTask;
        List<File> roots = new CopyOnWriteArrayList<>();
        RootsProvider provider = mock(RootsProvider.class);
        when(provider.getRoots()).thenAnswer(invocation -> new ArrayList<>(roots));

        // Run, Forest, run!
        GarminDeviceService garminService = new GarminDeviceService(provider, appConfiguration);
        garminService.start();

        // concurrently modify roots
        modifierTask = runAsync(() -> {
            try {
                for (int i = 0; i < 200; i++) {
                    roots.add(GARMIN_DRIVE_1);
                    roots.add(GARMIN_DRIVE_2);
                    roots.add(NON_GARMIN_DRIVE);

                    Sleeper.sleepMillis(1);

                    roots.remove(GARMIN_DRIVE_2);
                    roots.remove(NON_GARMIN_DRIVE);
                    roots.remove(GARMIN_DRIVE_1);

                    Sleeper.sleepMillis(1);
                }
            } catch (Exception e) {
                fail("Exception in mutation task", e);
            }
        });


        modifierTask.join();
        garminService.stop();

        waitUntilAsserted(Duration.ofSeconds(5), () -> garminService.getThreadHandle().isDone());

        // this is important, it guarantees that no exception was thrown from the service thread while stopping, or
        // from still running threads.
        // That would change the final outcome of the roots state - which is the final check of the
        // correctness.
        try {
            garminService.getThreadHandle().join();
        } catch (Exception e) {
            fail("Service thread threw an exception", e);
        }

        assertThat(garminService.getRegisteredRoots())
                .allMatch(File::exists)
                .allMatch(f -> f.equals(GARMIN_DRIVE_1) || f.equals(GARMIN_DRIVE_2));
    }
}