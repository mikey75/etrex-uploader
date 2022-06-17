package net.wirelabs.etrex.uploader.hardware;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.ListUtils;
import net.wirelabs.etrex.uploader.Sleeper;

/**
 * Observes filesystem for new garmin drives
 * publishes events of drive connect/disconect
 * maintains the current list of available garmin drives
 */
@Slf4j
public class DriveObserver implements Runnable {

    AtomicBoolean shouldExit = new AtomicBoolean(false);
    AtomicBoolean isRunning = new AtomicBoolean(false);
    List<File> registeredDevices = new ArrayList<>();
    
    public void stop() {
        log.info("Device observer stopping");
        shouldExit.set(true);
    }
    
    @Override
    public void run() {
        
        log.info("Listening for etrex connection");
        List<File> currentRoots = getRoots();

        while (!shouldExit.get()) {
            isRunning.set(true);
            List<File> newRoots = getRoots();

            checkForNewDrive(currentRoots, newRoots);
            checkForDisconnect(currentRoots, newRoots);
            
            Sleeper.sleep(TimeUnit.MILLISECONDS, 250);
            currentRoots = newRoots;
        }
        log.info("Device observer stopped");
        isRunning.set(false);
    }

    private void checkForDisconnect(List<File> roots, List<File> newRoots) {
        if (getRoots().size() < roots.size()) {
            for (File p: roots) {
                if (!newRoots.contains(p) && registeredDevices.contains(p)) {
                    log.info("Garmin drive {} disconnected", p);
                    unregisterDrive(p);
                }
            }
        }
    }

    private void checkForNewDrive(List<File> roots, List<File> newRoots) {
        for (File newRoot : newRoots) {
            if (!roots.contains(newRoot) && isGarminDrive(newRoot)) {
                log.info("Garmin drive {} connected", newRoot);
                registerDrive(newRoot);
            }
        }
    }

    void unregisterDrive(File p) {
        registeredDevices.remove(p);
    }

    void registerDrive(File newRoot) {
        registeredDevices.add(newRoot);
    }

    List<File> getRoots() {
        Iterable<Path> roots = FileSystems.getDefault().getRootDirectories();
        List<Path> pathList = ListUtils.convertIterableToList(roots);
        return pathList.stream().map(Path::toFile).collect(Collectors.toList());
    }

    private boolean isGarminDrive(File drive) {
        
        waitForDriveAvailable(drive);
        File[] files = drive.listFiles();
        return files != null && Arrays.stream(files).anyMatch(f -> f.getName().toUpperCase().contains("GARMIN"));
    }

    private void waitForDriveAvailable(File file) {
        while (!shouldExit.get() && !fileExistsAndReadable(file) ) {
            Sleeper.sleep(TimeUnit.MILLISECONDS, 100);
        }
    }
    boolean fileExistsAndReadable(File file) {
        return file.exists() && file.canRead();
    }


}
