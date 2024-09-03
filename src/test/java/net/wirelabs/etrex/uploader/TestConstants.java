package net.wirelabs.etrex.uploader;

import java.io.File;

public class TestConstants {

    // legal gps track files
    public static final File GPX_FILE_VER_1_0 = new File("src/test/resources/trackfiles/gpx10.gpx");
    public static final File GPX_FILE_VER_1_1 = new File("src/test/resources/trackfiles/gpx11.gpx");
    public static final File TCX_FILE = new File("src/test/resources/trackfiles/tcx1.tcx");
    public static final File FIT_FILE = new File("src/test/resources/trackfiles/track.fit");

    // track files with bad xml inside
    public static final File BAD_XML_GPX_1_0_FILE = new File("src/test/resources/trackfiles/gpx10bad.gpx");
    public static final File BAD_XML_GPX_1_1_FILE = new File("src/test/resources/trackfiles/gpx11bad.gpx");
    public static final File BAD_XML_TCX_FILE = new File("src/test/resources/trackfiles/tcx1bad.tcx");
    public static final File BAD_FIT_FILE = new File("src/test/resources/trackfiles/fitBad.fit");

    // binary and nonexistent files
    public static final File NOT_TRACK_FILE = new File("src/test/resources/trackfiles/not_a_track.bin");
    public static final File NONEXISTENT_FILE = new File("src/test/resources/dupa");

    // config files
    public static final File CONFIG_FILE = new File("src/test/resources/config/test.properties");

}
