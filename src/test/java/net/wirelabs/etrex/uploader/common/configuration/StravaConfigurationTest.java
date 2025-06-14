package net.wirelabs.etrex.uploader.common.configuration;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class StravaConfigurationTest extends BaseTest {

    private static final File GOOD_STRAVA_CONFIG_FILE = new File("src/test/resources/config/good-strava.properties");
    private static final File BAD_STRAVA_CONFIG_FILE = new File("src/test/resources/config/bad-strava.properties");

    @BeforeEach
    void before() throws IOException {
        // preserve files which we're changing since tests might change test file
        preserveFiles(GOOD_STRAVA_CONFIG_FILE,BAD_STRAVA_CONFIG_FILE);
    }

    @AfterEach
    void after() throws IOException {
        // restore original files
        restoreFiles(GOOD_STRAVA_CONFIG_FILE,BAD_STRAVA_CONFIG_FILE);
    }

    @Test
    void testCorrectStravaConfig() {
        StravaConfiguration s = new StravaConfiguration(GOOD_STRAVA_CONFIG_FILE.getPath());
        assertDefaultValues(s);
        assertThat(s.hasAllTokensAndCredentials()).isTrue();
    }


    @Test
    void shouldSavedChangedConfig() {

        StravaConfiguration s = new StravaConfiguration(GOOD_STRAVA_CONFIG_FILE.getPath());

        s.setStravaAppId("111");
        s.setStravaAccessToken("aaa");
        s.setStravaRefreshToken("bbb");
        s.setStravaTokenExpires(1234823L);
        s.setStravaClientSecret("xxx");
        s.save();

        // now reload file and check changed values
        StravaConfiguration load = new StravaConfiguration(GOOD_STRAVA_CONFIG_FILE.getPath());

        //verify
        assertThat(load.getStravaAppId()).isEqualTo("111");
        assertThat(load.getStravaAccessToken()).isEqualTo("aaa");
        assertThat(load.getStravaRefreshToken()).isEqualTo("bbb");
        assertThat(load.getStravaTokenExpires()).isEqualTo(1234823L);
        assertThat(load.getStravaClientSecret()).isEqualTo("xxx");
        assertThat(load.hasAllTokensAndCredentials()).isTrue();
    }

    @Test
    void shouldReactToIncompleteData() {
        StravaConfiguration s = new StravaConfiguration(BAD_STRAVA_CONFIG_FILE.getPath());
        assertThat(s.hasAllTokensAndCredentials()).isFalse();
    }

    @Test
    void shouldTestUtilityMethods() {

        StravaConfiguration s = new StravaConfiguration(GOOD_STRAVA_CONFIG_FILE.getPath());
        assertThat(s.hasClientSecret()).isTrue();
        assertThat(s.hasAccessToken()).isTrue();
        assertThat(s.hasRefreshToken()).isTrue();
        assertThat(s.hasAppId()).isTrue();
        assertThat(s.hasAllTokensAndCredentials()).isTrue();

    }

    private static void assertDefaultValues(StravaConfiguration s) {
        assertThat(s.getStravaClientSecret()).isEqualTo("aaaa");
        assertThat(s.getStravaRefreshToken()).isEqualTo("1234567");
        assertThat(s.getStravaTokenExpires()).isEqualTo(12345678L);
        assertThat(s.getStravaAccessToken()).isEqualTo("9321093109301");
        assertThat(s.getStravaAppId()).isEqualTo("10101020");
    }
}
