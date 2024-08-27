package net.wirelabs.etrex.uploader.gui.map.parsers;


import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;

@Slf4j
public abstract class BaseParser {

    protected final String TCX_MODEL_PKG = "net.wirelabs.etrex.uploader.model.tcx";
    protected final String GPX11_MODEL_PKG = "net.wirelabs.etrex.uploader.model.gpx.ver11";
    protected final String GPX10_MODEL_PKG = "net.wirelabs.etrex.uploader.model.gpx.ver10";

    protected void logParseErrorMessage(File file, JAXBException e) {
        log.warn("Could not parse GPS file {}", file, e);
    }
}
