package net.wirelabs.etrex.uploader.hardware;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

@NoArgsConstructor
@Getter
public class GarminHardwareInfo {

    private String description;
    private String softwareVersion;
    private String partNumber;
    private String serialNumber;

    GarminHardwareInfo(String description, String softwareVersion, String partNumber, String serialNumber) {
        this.description = description;
        this.softwareVersion = softwareVersion;
        this.partNumber = partNumber;
        this.serialNumber = serialNumber;

    }
}