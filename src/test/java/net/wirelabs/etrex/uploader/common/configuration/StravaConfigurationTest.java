package net.wirelabs.etrex.uploader.common.configuration;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class StravaConfigurationTest {

    private static final String TEST_USER_DIR = "src/test/resources/config/";
    private static final String GOOD_STRAVA_CONFIG_FILE = "good-strava.properties";
    private static final String BAD_STRAVA_CONFIG_FILE = "bad-strava.properties";
    private static final Path BACKUP_FILE = Paths.get("target/backup.properties");
    private static final MockedStatic<SystemUtils> sysUtils = Mockito.mockStatic(SystemUtils.class);

    @AfterAll
    static void afterAll() {
        sysUtils.close();
    }

    @Test
    void testCorrectStravaConfig() {
        sysUtils.when(SystemUtils::getWorkDir).thenReturn(TEST_USER_DIR);
        StravaConfiguration s = new StravaConfiguration(GOOD_STRAVA_CONFIG_FILE);
        assertDefaultValues(s);
        assertThat(s.hasAllTokensAndCredentials()).isTrue();
    }

    @Test
    void testCorrectDefaultConfig() {
        sysUtils.when(SystemUtils::getWorkDir).thenReturn(TEST_USER_DIR);
        StravaConfiguration s = new StravaConfiguration(); // will load default => strava.config
        assertDefaultValues(s);
        assertThat(s.hasAllTokensAndCredentials()).isTrue();
    }

    @Test
    void shouldSavedChangedConfig() throws IOException {

        try {
            // backup file which we're changing
            Files.copy(new File(TEST_USER_DIR, GOOD_STRAVA_CONFIG_FILE).toPath(), BACKUP_FILE, StandardCopyOption.REPLACE_EXISTING);

            sysUtils.when(SystemUtils::getWorkDir).thenReturn(TEST_USER_DIR);

            StravaConfiguration s = new StravaConfiguration(GOOD_STRAVA_CONFIG_FILE);

            s.setStravaAppId("111");
            s.setStravaAccessToken("aaa");
            s.setStravaRefreshToken("bbb");
            s.setStravaTokenExpires(1234823L);
            s.setStravaClientSecret("xxx");
            s.save();

            // now reload file and check changed values
            StravaConfiguration load = new StravaConfiguration(GOOD_STRAVA_CONFIG_FILE);

            //verify
            assertThat(load.getStravaAppId()).isEqualTo("111");
            assertThat(load.getStravaAccessToken()).isEqualTo("aaa");
            assertThat(load.getStravaRefreshToken()).isEqualTo("bbb");
            assertThat(load.getStravaTokenExpires()).isEqualTo(1234823L);
            assertThat(load.getStravaClientSecret()).isEqualTo("xxx");
        } finally {
            // restore original file
            Files.copy(BACKUP_FILE, new File(TEST_USER_DIR, GOOD_STRAVA_CONFIG_FILE).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Test
    void shouldReactToIncompleteData() {
        sysUtils.when(SystemUtils::getWorkDir).thenReturn(TEST_USER_DIR);
        StravaConfiguration s = new StravaConfiguration(BAD_STRAVA_CONFIG_FILE);
        assertThat(s.hasAllTokensAndCredentials()).isFalse();
    }

    private static void assertDefaultValues(StravaConfiguration s) {
        assertThat(s.getStravaClientSecret()).isEqualTo("aaaa");
        assertThat(s.getStravaRefreshToken()).isEqualTo("1234567");
        assertThat(s.getStravaTokenExpires()).isEqualTo(12345678L);
        assertThat(s.getStravaAccessToken()).isEqualTo("9321093109301");
        assertThat(s.getStravaAppId()).isEqualTo("10101020");
    }
}
