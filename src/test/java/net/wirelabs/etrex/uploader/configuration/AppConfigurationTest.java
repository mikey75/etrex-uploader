package net.wirelabs.etrex.uploader.configuration;

import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.configuration.SortedProperties;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.EventBus;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Files.linesOf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;


/**
 * Created 10/25/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class AppConfigurationTest extends BaseTest {

    // nonexisting test config file
    private static final File NONEXISTENT_FILE = new File("target/nonexistent.properties");
    // existing test config file
    private static final File TEST_CONFIG_FILE = new File("src/test/resources/config/test.properties");
    // preservation copy
    private static final File TEST_FILE_COPY = new File("target/test.properties.copy");

    @BeforeEach
    void before() throws IOException {
        // preserve original testfile since the test might change its content
        FileUtils.copyFile(TEST_CONFIG_FILE, TEST_FILE_COPY, StandardCopyOption.REPLACE_EXISTING);
    }
    @AfterEach
    void after() throws IOException {
        // restore the original file
        FileUtils.copyFile(TEST_FILE_COPY,TEST_CONFIG_FILE,StandardCopyOption.REPLACE_EXISTING);
        FileUtils.deleteQuietly(TEST_FILE_COPY);
        assertThat(TEST_FILE_COPY).doesNotExist();
    }

    @Test
    void shouldThrowAndLogWhenCannotSaveConfig() throws IOException {

            AppConfiguration configuration = new AppConfiguration(TEST_CONFIG_FILE.getPath());
            configuration.properties = Mockito.spy(configuration.properties);

            doThrow(new IOException("test file I/O exception"))
                    .when(configuration.properties)
                    .store(any(OutputStream.class), eq(""));

            configuration.save();

            // check event is submitted -> since in test no EventBus client is subscribed to this event
            // it will be found in dead events
            List<Event> events = EventBus.getDeadEvents();
            assertThat(events.stream()
                    .filter(e -> e.getEventType().equals(EventType.ERROR_SAVING_CONFIGURATION))
                    .toList()).isNotEmpty();

            verifyLogged("Saving configuration " + TEST_CONFIG_FILE.getPath());
            verifyLogged("Can't save configuration: test file I/O exception");
    }

    @Test
    void shouldAssertDefaultValuesAndSaveFileWhenConfigFileNonExistent() throws IOException {

        Files.deleteIfExists(NONEXISTENT_FILE.toPath());

        AppConfiguration c = new AppConfiguration(NONEXISTENT_FILE.getPath());
        assertDefaultValues(c);
        verifyLogged(NONEXISTENT_FILE + " file not found or cannot be loaded. Setting default config values.");

        // verify new config file is created
        verifyLogged("Saving new config file with default values");
        assertThat(NONEXISTENT_FILE).exists();

    }

    private void assertDefaultValues(AppConfiguration c) {
        assertThat(c.getStorageRoot()).isEqualTo(Paths.get(System.getProperty("user.home") + File.separator + "etrex-uploader-store"));
        assertThat(c.getUserStorageRoots()).isEmpty();
        assertThat(c.getDeviceDiscoveryDelay()).isEqualTo(Constants.DEFAULT_DRIVE_OBSERVER_DELAY);
        assertThat(c.getWaitDriveTimeout()).isEqualTo(Constants.DEFAULT_WAIT_DRIVE_TIMEOUT);
        assertThat(c.isDeleteAfterUpload()).isTrue();
        assertThat(c.isArchiveAfterUpload()).isTrue();
        assertThat(c.getDefaultActivityType()).isEqualTo(Constants.DEFAULT_SPORT);
        assertThat(c.getTilerThreads()).isEqualTo(Constants.DEFAULT_TILER_THREAD_COUNT);
        assertThat(c.getPerPage()).isEqualTo(Constants.DEFAULT_STRAVA_ACTIVITIES_PER_PAGE);
        assertThat(c.getApiUsageWarnPercent()).isEqualTo(Constants.DEFAULT_API_USAGE_WARN_PERCENT);
        assertThat(c.getUploadStatusWaitSeconds()).isEqualTo(Constants.DEFAULT_UPLOAD_STATUS_WAIT_SECONDS);
        assertThat(c.getMapTrackColor()).isEqualTo(Constants.DEFAULT_TRACK_COLOR);
        assertThat(c.getUserMapDefinitionsDir()).hasToString(Constants.DEFAULT_USER_MAP_DIR);
        assertThat(c.getMapFile()).hasToString(c.getUserMapDefinitionsDir().toString() + File.separator + Constants.DEFAULT_MAP);
        assertThat(c.isUsePolyLines()).isTrue();
        assertThat(c.getLookAndFeelClassName()).isEqualTo(UIManager.getCrossPlatformLookAndFeelClassName());
        assertThat(c.getStravaCheckTimeout()).isEqualTo(Constants.DEFAULT_STRAVA_CHECK_TIMEOUT);
        assertThat(c.isStravaCheckHostBeforeUpload()).isTrue();
        assertThat(c.getMapHomeLatitude()).isEqualTo(Constants.DEFAULT_MAP_HOME_LOCATION.getLatitude());
        assertThat(c.getMapHomeLongitude()).isEqualTo(Constants.DEFAULT_MAP_HOME_LOCATION.getLongitude());
        assertThat(c.isEnableDesktopSliders()).isEqualTo(Constants.DEFAULT_USE_SLIDERS);
        assertThat(c.getFontSize()).isEqualTo(Constants.DEFAULT_FONT_SIZE);
        assertThat(c.getRouteLineWidth()).isEqualTo(Constants.DEFAULT_ROUTE_LINE_WIDTH);
        assertThat(c.getCacheType()).isEqualTo(Constants.DEFAULT_TILE_CACHE_TYPE);
        assertThat(c.getRedisHost()).isEqualTo(Constants.DEFAULT_REDIS_HOST);
        assertThat(c.getRedisPort()).isEqualTo(Constants.DEFAULT_REDIS_PORT);
        assertThat(c.getRedisPoolSize()).isEqualTo(Constants.DEFAULT_REDIS_POOLSIZE);
    }

    @Test
    void shouldReadAndParseCorrectConfig() {
        // when
        AppConfiguration c = new AppConfiguration(TEST_CONFIG_FILE.getPath());
        // then
        assertThat(c.getStorageRoot()).isEqualTo(Paths.get("/test/root"));
        assertThat(c.getUserStorageRoots()).containsExactly(Paths.get("/test/1"), Paths.get("test/2"));
        assertThat(c.isArchiveAfterUpload()).isTrue();
        assertThat(c.isDeleteAfterUpload()).isFalse();
        assertThat(c.getWaitDriveTimeout()).isEqualTo(100L);
        assertThat(c.getDeviceDiscoveryDelay()).isEqualTo(500L);
        verifyLogged("Loading " + TEST_CONFIG_FILE.getPath());

    }

    @Test
    void shouldStoreChangedConfig() throws IOException {

        String[] expectedChange = {
                "system.wait.drive.timeout=10",
                "system.drive.observer.delay=100",
                "system.backup.after.upload=false",
                "map.home.latitude=10.111",
                "map.home.longitude=30.111",
                "system.look.sliders=true"
        };

        // we don't want to modify existing test file, so work on a temp config
        File newConfigFile = new File("target","testfile");
        Files.deleteIfExists(newConfigFile.toPath());

        AppConfiguration c = new AppConfiguration(newConfigFile.toString()); // file will be created anew so that the change evaluation is always correct
        assertDefaultValues(c);
        verifyLogged("Saving new config file with default values");

        // now re-read this file, change some values, save it, and check if they're changed
        c = new AppConfiguration(newConfigFile.getPath());
        c.setArchiveAfterUpload(false);
        c.setDeviceDiscoveryDelay(100L);
        c.setWaitDriveTimeout(10L);
        c.setMapHomeLatitude(10.111);
        c.setMapHomeLongitude(30.111);
        c.setEnableDesktopSliders(true);
        c.save();

        verifyLogged("Loading " + newConfigFile.getPath());
        verifyLogged("Saving configuration " + newConfigFile.getPath());
        // now reload changed file and check
        assertThat(Files.readAllLines(newConfigFile.toPath())).containsAll(Arrays.asList(expectedChange));

    }

    @Test
    void shouldSortPropertiesOnSave() throws IOException {

        // given
        File f = File.createTempFile("props", "sorted");
        OutputStream os = Files.newOutputStream(f.toPath());

        // when -> create and save the props to file
        Properties properties = new SortedProperties();
        properties.setProperty("x", "zaxon");
        properties.setProperty("a", "budda");
        properties.setProperty("g", "africa");
        properties.setProperty("vvv", "łąka");
        properties.setProperty("color", "#ff0000");
        properties.setProperty("multielement", "a,b,c,d");
        properties.store(os, "");

        os.close();

        // since we mess with system based class, first check properties object internals
        String[] expectedSortedKeys = new String[]{"a", "color", "g", "multielement", "vvv", "x"};

        assertThat(expectedSortedKeys).hasSameSizeAs(properties.keySet());

        Enumeration<Object> keys = properties.keys();
        Iterator<String> expected = Arrays.stream(expectedSortedKeys).iterator();

        while (keys.hasMoreElements() && expected.hasNext()) {
            Object key = keys.nextElement();
            if (key instanceof String) {
                assertThat(key).isEqualTo(expected.next());
            }
        }

        // then check resulting stored file
        assertThat(linesOf(f, Charset.defaultCharset()).stream()
                // filter out comment lines added by properties
                .filter(s -> !s.startsWith("#")))
                //check values are sorted alphabetically (by keys! - for stasiej!)
                .containsExactly(
                        "a=budda",
                        "color=\\#ff0000",
                        "g=africa",
                        "multielement=a,b,c,d",
                        "vvv=\\u0142\\u0105ka",
                        "x=zaxon"
                );

        // be polite :)
        Files.deleteIfExists(f.toPath());
    }
}