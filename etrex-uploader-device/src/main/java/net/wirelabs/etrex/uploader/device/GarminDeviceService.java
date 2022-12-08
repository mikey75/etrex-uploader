package net.wirelabs.etrex.uploader.device;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.eventbus.EventBus;
import net.wirelabs.etrex.uploader.common.thread.BaseStoppableRunnable;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.etrex.uploader.common.utils.ListUtils;
import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.model.garmin.DeviceT;


/**
 * Observes filesystem for new drive appearance and checks if they're garmin drives
 * (by looking if they contain 'Garmin' subdir)
 * publishes events of drive connect/disconect
 * maintains the current list of available drives
 */
@Slf4j
public class GarminDeviceService extends BaseStoppableRunnable {


    private final RootsProvider rootsProvider;
    @Getter
    private final List<File> registeredRoots = new ArrayList<>();
    private final Configuration configuration;

    // constructor with custom provider
    public GarminDeviceService(RootsProvider rootsProvider, Configuration configuration) {
        this.configuration = configuration;
        this.rootsProvider = rootsProvider;
    }

    // constructor with default provider
    public GarminDeviceService(Configuration configuration) {
        this.configuration = configuration;
        this.rootsProvider = new RootsProvider();
    }
    
    @Override
    public void stop() {
        log.info("Garmin Device Service stopping");
        super.stop();
    }

    @Override
    public void run() {

        log.info("Starting Garmin device discovery thread");
        List<File> roots = rootsProvider.getRoots();

        // registration of anything already connected
        log.info("Registering already connected drives");
        registerAlreadyConnected(roots);

        // listen for and register new connections 
        log.info("Listening for new drives");
        while (!shouldExit.get()) {

            roots = rootsProvider.getRoots();
            findAndRegisterNewRoots(roots);
            findAndUnregisterMissingRoots(roots);
            Sleeper.sleepMillis(configuration.getDeviceDiscoveryDelay());
        }
        log.info("Device observer stopped");

    }

    private void findAndUnregisterMissingRoots(List<File> roots) {

        ListUtils.findElementsOfANotPresentInB(registeredRoots, roots)
                .forEach(this::unregisterDrive);
    }

    private void findAndRegisterNewRoots(List<File> roots) {

        roots.stream()
                .filter(this::isNewGarminDrive)
                .forEach(this::registerDrive);
    }

    private void registerAlreadyConnected(List<File> roots) {

        roots.stream()
                .filter(this::isExistingGarminDrive)
                .forEach(this::registerDrive);
    }

    void unregisterDrive(File drive) {

        registeredRoots.remove(drive);
        EventBus.publish(EventType.DEVICE_DRIVE_UNREGISTERED, drive);
        log.info("Garmin drive {} disconnected", drive);
    }

    void registerDrive(File drive) {

        if (!registeredRoots.contains(drive)) {
            registeredRoots.add(drive);
            EventBus.publish(EventType.DEVICE_DRIVE_REGISTERED, drive);
            publishHardwareInfo(drive);
            log.info("Garmin drive {} connected", drive);
        }
    }

    void publishFoundHardwareInfo(DeviceT info) {
        EventBus.publish(EventType.DEVICE_INFO_AVAILABLE, info);
    }
    
    private boolean fileExistsAndReadable(File file) {

        boolean result = false;
        long timeout = System.currentTimeMillis() + configuration.getWaitDriveTimeout();

        while (!shouldExit.get() && !result && System.currentTimeMillis() < timeout) {
            result = file.exists() && file.canRead();
        }
        if (!result) {
            log.error("Failed waiting for drive {} being available", file);
        }
        return result;

    }

    private void publishHardwareInfo(File drive) {

        GarminUtils.getGarminDeviceXmlFile(drive).ifPresent(deviceXmlFile -> {
            try {
                DeviceT garminInfo = GarminUtils.parseDeviceXml(deviceXmlFile.toFile());
                publishFoundHardwareInfo(garminInfo);
            } catch (JAXBException e) {
                log.warn("Can't parse {} on drive {}", Constants.GARMIN_DEVICE_XML, drive);
            }
        });

    }
    
    private boolean isGarminDir(File f) {
        return f.isDirectory() && f.getName().equalsIgnoreCase("GARMIN");
    }

    private boolean isNewGarminDrive(File file) {
        return !registeredRoots.contains(file) && isExistingGarminDrive(file);
    }

    private boolean isExistingGarminDrive(File drive) {

        if (fileExistsAndReadable(drive)) {
            List<File> files = FileUtils.listDirectory(drive);
            return files.stream()
                    .anyMatch(this::isGarminDir);
        }
        return false;
    }
}
