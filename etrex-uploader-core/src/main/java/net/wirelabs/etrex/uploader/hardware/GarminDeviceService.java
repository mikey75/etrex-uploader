package net.wirelabs.etrex.uploader.hardware;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.common.utils.FileUtils;
import net.wirelabs.etrex.uploader.common.utils.ListUtils;

import net.wirelabs.etrex.uploader.common.utils.Sleeper;
import net.wirelabs.etrex.uploader.hardware.threads.BaseStoppableRunnable;
import net.wirelabs.etrex.uploader.model.garmin.DeviceT;

import net.wirelabs.etrex.uploader.eventbus.EventBus;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


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
        EventBus.publish(EventType.EVT_DRIVE_UNREGISTERED, drive);
        log.info("Garmin drive {} disconnected", drive);
    }

    void registerDrive(File drive) {

        if (!registeredRoots.contains(drive)) {
            registeredRoots.add(drive);
            EventBus.publish(EventType.EVT_DRIVE_REGISTERED, drive);
            Optional<GarminHardwareInfo> hardwareInfo = getHardwareInfo(drive);
            hardwareInfo.ifPresent(this::publishFoundHardwareInfo);
            log.info("Garmin drive {} connected", drive);
        }
    }

    void publishFoundHardwareInfo(GarminHardwareInfo info) {
        EventBus.publish(EventType.EVT_HARDWARE_INFO_AVAILABLE, info);
    }

    private boolean isExistingGarminDrive(File drive) {

        if (fileExistsAndReadable(drive)) {
            List<File> files = FileUtils.listDirectory(drive);
            return files.stream()
                    .anyMatch(this::isGarminDir);
        }
        return false;
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

    private Optional<GarminHardwareInfo> getHardwareInfo(File drive) {

        try (Stream<Path> walk = Files.walk(drive.toPath(), 2)) {

            Stream<Path> result = walk.filter(this::isGarminDeviceXmlFile);
            Optional<Path> deviceXmlFile = result.findFirst();

            if (deviceXmlFile.isPresent()) {
                DeviceT device = parseDeviceXml(deviceXmlFile.get().toFile()).getValue();
                GarminHardwareInfo garminInfo = new GarminHardwareInfo(drive,
                        device.getModel().getDescription(),
                        String.valueOf(device.getModel().getSoftwareVersion()),
                        device.getModel().getPartNumber(),
                        String.valueOf(device.getId()));
                return Optional.of(garminInfo);
            } else {
                log.warn("Can't find " + Constants.GARMIN_DEVICE_XML);
            }
        } catch (IOException e) {
            log.error("I/O error {}", e.getMessage(), e);
        } catch (JAXBException e) {
            log.error("XML parsing failed {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    private boolean isGarminDeviceXmlFile(Path filePath) {
        return filePath.toFile().getName().equals(Constants.GARMIN_DEVICE_XML);
    }

    private boolean isGarminDir(File f) {
        return f.isDirectory() && f.getName().equalsIgnoreCase("GARMIN");
    }

    private boolean isNewGarminDrive(File file) {
        return !registeredRoots.contains(file) && isExistingGarminDrive(file);
    }

    JAXBElement<DeviceT> parseDeviceXml(File file) throws JAXBException {

        JAXBContext jc = JAXBContext.newInstance("net.wirelabs.etrex.uploader.model.garmin");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (JAXBElement<DeviceT>) unmarshaller.unmarshal(file);

    }

}
