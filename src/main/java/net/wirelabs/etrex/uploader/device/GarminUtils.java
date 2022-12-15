package net.wirelabs.etrex.uploader.device;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.model.garmin.DeviceT;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class GarminUtils {
    
    public static DeviceT parseDeviceXml(File deviceXml) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("net.wirelabs.etrex.uploader.model.garmin");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return ((JAXBElement<DeviceT>) unmarshaller.unmarshal(deviceXml))
                .getValue();
    }

    public static Optional<Path> getGarminDeviceXmlFile(File drive) {
        try (Stream<Path> walk = Files.walk(drive.toPath(), 2)) {
            Stream<Path> result = walk.filter(f -> f.toFile().getName().equals(Constants.GARMIN_DEVICE_XML));
            return result.findFirst();
        } catch (IOException e) {
            log.warn("I/O exception looking for GarminDevice.xml file", e);
        }
        return Optional.empty();
    }
    
}
