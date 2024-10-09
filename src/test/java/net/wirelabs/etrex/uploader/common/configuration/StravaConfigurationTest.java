package net.wirelabs.etrex.uploader.common.configuration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class StravaConfigurationTest {

    public static final String TEST_USER_DIR = "src/test/resources/config/";
    public static final File TEST_STRAVA_CONFIG_FILE = new File(TEST_USER_DIR, "testStrava.properties");
    private static String originalUserDir;

    @BeforeEach
    void beforeEach() {
        // store current user.dir
        originalUserDir = System.getProperty("user.dir");
        // since the configuration works on real "user.dir" env 'root' directory, we need to change it for test
        // to work on our testfile instead of default
        System.setProperty("user.dir", TEST_USER_DIR);
        // create test file
        createDefaultTestFile();
    }

    @AfterEach
    void afterEach() {
        // restore user.dir
        System.setProperty("user.dir",originalUserDir);

    }

    @Test
    void shouldLoadDefaultFile() {
        StravaConfiguration s = new StravaConfiguration();
        assertDefaultValues(s);
    }

    @Test
    void testCorrectStravaConfig() {

        StravaConfiguration s = new StravaConfiguration(TEST_STRAVA_CONFIG_FILE.getName());
        assertDefaultValues(s);
        assertThat(s.hasAllTokensAndCredentials()).isTrue();

    }

    @Test
    void shouldSavedChangedConfig() {

        StravaConfiguration s = new StravaConfiguration(TEST_STRAVA_CONFIG_FILE.getName());

        s.setStravaAppId("111");
        s.setStravaAccessToken("aaa");
        s.setStravaRefreshToken("bbb");
        s.setStravaTokenExpires(1234823L);
        s.setStravaClientSecret("xxx");
        s.save();

        // now reload file and check changed values
        StravaConfiguration load = new StravaConfiguration(TEST_STRAVA_CONFIG_FILE.getName());

        //verify
        assertThat(load.getStravaAppId()).isEqualTo("111");
        assertThat(load.getStravaAccessToken()).isEqualTo("aaa");
        assertThat(load.getStravaRefreshToken()).isEqualTo("bbb");
        assertThat(load.getStravaTokenExpires()).isEqualTo(1234823L);
        assertThat(load.getStravaClientSecret()).isEqualTo("xxx");

    }

    @Test
    void shouldReactToIncompleteData() {
        StravaConfiguration s = new StravaConfiguration(TEST_STRAVA_CONFIG_FILE.getName());

        s.setStravaAppId("111");
        s.setStravaAccessToken("");
        s.setStravaRefreshToken("");
        s.setStravaTokenExpires(1234823L);
        s.setStravaClientSecret("");
        s.save();

        // reload configuration
        StravaConfiguration loaded = new StravaConfiguration(TEST_STRAVA_CONFIG_FILE.getName());
        // verify
        assertThat(loaded.hasAllTokensAndCredentials()).isFalse();
    }

    private static void createDefaultTestFile() {

         /*
        strava.client.secret=aaaa
        strava.token.refresh=1234567
        strava.token.expires.at=12345678
        strava.token.access=9321093109301
        strava.app.id=10101020
         */

        StravaConfiguration s = new StravaConfiguration(TEST_STRAVA_CONFIG_FILE.getName());
        s.setStravaClientSecret("aaaa");
        s.setStravaRefreshToken("1234567");
        s.setStravaTokenExpires(12345678L);
        s.setStravaAccessToken("9321093109301");
        s.setStravaAppId("10101020");
        s.save();
    }

    private static void assertDefaultValues(StravaConfiguration s) {
        assertThat(s.getStravaClientSecret()).isEqualTo("aaaa");
        assertThat(s.getStravaRefreshToken()).isEqualTo("1234567");
        assertThat(s.getStravaTokenExpires()).isEqualTo(12345678);
        assertThat(s.getStravaAccessToken()).isEqualTo("9321093109301");
        assertThat(s.getStravaAppId()).isEqualTo("10101020");
    }
}