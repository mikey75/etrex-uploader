package net.wirelabs.etrex.uploader.configuration;

import com.strava.model.SportType;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class StravaConfigurationTest extends BaseTest {

    private static final File GOOD_STRAVA_CONFIG_FILE = new File("src/test/resources/config/good-strava.properties");
    private static final File MISSING_AUTH_CONFIG_FILE = new File("src/test/resources/config/missing-auth-strava.properties");
    private static final File NON_EXISTING_CONFIG_FILE = new File("target/nonexistent-strava-config");

    @BeforeEach
    void before() throws IOException {
        // preserve files which we're changing since tests might change test file
        preserveFiles(GOOD_STRAVA_CONFIG_FILE, MISSING_AUTH_CONFIG_FILE);
        FileUtils.deleteQuietly(NON_EXISTING_CONFIG_FILE);
    }

    @AfterEach
    void after() throws IOException {
        // restore original files
        restoreFiles(GOOD_STRAVA_CONFIG_FILE, MISSING_AUTH_CONFIG_FILE);
        FileUtils.deleteQuietly(NON_EXISTING_CONFIG_FILE);
    }

    @Test
    void shouldLoadAndVerifyCorrectStravaConfig() {
        StravaConfiguration s = new StravaConfiguration(GOOD_STRAVA_CONFIG_FILE.getPath());

        assertThat(s.getStravaClientSecret()).isEqualTo("aaaa");
        assertThat(s.getStravaRefreshToken()).isEqualTo("1234567");
        assertThat(s.getStravaTokenExpires()).isEqualTo(12345678L);
        assertThat(s.getStravaAccessToken()).isEqualTo("9321093109301");
        assertThat(s.getStravaAppId()).isEqualTo("10101020");
        assertThat(s.getPerPage()).isEqualTo(45);
        assertThat(s.getApiUsageWarnPercent()).isEqualTo(50);
        assertThat(s.getBaseUrl()).isEqualTo(Constants.STRAVA_API_BASE_URL);
        assertThat(s.getBaseTokenUrl()).isEqualTo(Constants.STRAVA_TOKEN_URL);
        assertThat(s.getAuthUrl()).isEqualTo(Constants.STRAVA_AUTH_URL);
        assertThat(s.getAuthCodeTimeout()).isEqualTo(Constants.AUTH_CODE_TIMEOUT_SECONDS);
        assertThat(s.getStravaCheckTimeout()).isEqualTo(100);
        assertThat(s.getDefaultActivityType()).isEqualTo(SportType.WALK);
        assertThat(s.getUploadStatusWaitSeconds()).isEqualTo(120);
        assertThat(s.isUsePolyLines()).isFalse();
        assertThat(s.hasAllTokensAndCredentials()).isTrue();
        assertThat(s.hasAccessToken()).isTrue();
        assertThat(s.hasRefreshToken()).isTrue();
        assertThat(s.hasClientSecret()).isTrue();
        assertThat(s.hasAppId()).isTrue();
    }

    @Test
    void shouldSaveAndVerifyChangedConfig() {

        StravaConfiguration stravaConfiguration = new StravaConfiguration(GOOD_STRAVA_CONFIG_FILE.getPath());

        stravaConfiguration.setStravaAppId("111");
        stravaConfiguration.setStravaAccessToken("aaa");
        stravaConfiguration.setStravaRefreshToken("bbb");
        stravaConfiguration.setStravaTokenExpires(1234823L);
        stravaConfiguration.setStravaClientSecret("xxx");
        stravaConfiguration.save();

        // now reload file and check changed values
        stravaConfiguration = new StravaConfiguration(GOOD_STRAVA_CONFIG_FILE.getPath());

        //verify
        assertThat(stravaConfiguration.getStravaAppId()).isEqualTo("111");
        assertThat(stravaConfiguration.getStravaAccessToken()).isEqualTo("aaa");
        assertThat(stravaConfiguration.getStravaRefreshToken()).isEqualTo("bbb");
        assertThat(stravaConfiguration.getStravaTokenExpires()).isEqualTo(1234823L);
        assertThat(stravaConfiguration.getStravaClientSecret()).isEqualTo("xxx");
        assertThat(stravaConfiguration.hasAllTokensAndCredentials()).isTrue();
    }

    @Test
    void shouldReactToIncompleteAuthData() {
        StravaConfiguration s = new StravaConfiguration(MISSING_AUTH_CONFIG_FILE.getPath());
        assertThat(s.hasAllTokensAndCredentials()).isFalse();
    }

    @Test
    void shouldLoadExistingConfigFile() {

        StravaConfiguration stravaConfiguration = new StravaConfiguration(GOOD_STRAVA_CONFIG_FILE.getPath());
        assertThat(stravaConfiguration.hasClientSecret()).isTrue();
        assertThat(stravaConfiguration.hasAccessToken()).isTrue();
        assertThat(stravaConfiguration.hasRefreshToken()).isTrue();
        assertThat(stravaConfiguration.hasAppId()).isTrue();
        assertThat(stravaConfiguration.hasAllTokensAndCredentials()).isTrue();

    }

    @Test
    void shouldCreateDefaultConfigWhenNotFound()  {

        //File configFile = NON_EXISTING_CONFIG_FILE;
        // remove the target existing config file
       // FileUtils.deleteQuietly(configFile);
        assertThat(NON_EXISTING_CONFIG_FILE).doesNotExist();

        // this should create new file
        StravaConfiguration defaultStravaConfig = new StravaConfiguration(NON_EXISTING_CONFIG_FILE.getPath());


        // assert default values for newly created config
        assertThat(defaultStravaConfig.getStravaAppId()).isEmpty();
        assertThat(defaultStravaConfig.getStravaAccessToken()).isEmpty();
        assertThat(defaultStravaConfig.getStravaRefreshToken()).isEmpty();
        assertThat(defaultStravaConfig.getPerPage()).isEqualTo(Constants.STRAVA_ACTIVITIES_PER_PAGE);
        assertThat(defaultStravaConfig.getApiUsageWarnPercent()).isEqualTo(Constants.STRAVA_API_USAGE_WARN_PERCENT);
        assertThat(defaultStravaConfig.getBaseUrl()).isEqualTo(Constants.STRAVA_API_BASE_URL);
        assertThat(defaultStravaConfig.getBaseTokenUrl()).isEqualTo(Constants.STRAVA_TOKEN_URL);
        assertThat(defaultStravaConfig.getAuthUrl()).isEqualTo(Constants.STRAVA_AUTH_URL);
        assertThat(defaultStravaConfig.getStravaCheckTimeout()).isEqualTo(Constants.STRAVA_CHECK_TIMEOUT);
        assertThat(defaultStravaConfig.getAuthCodeTimeout()).isEqualTo(Constants.AUTH_CODE_TIMEOUT_SECONDS);
        assertThat(defaultStravaConfig.getDefaultActivityType()).isEqualTo(SportType.RIDE);
        assertThat(defaultStravaConfig.getUploadStatusWaitSeconds()).isEqualTo(Constants.UPLOAD_STATUS_WAIT_SECONDS);
        assertThat(defaultStravaConfig.isUsePolyLines()).isTrue();
        assertThat(defaultStravaConfig.hasAllTokensAndCredentials()).isFalse();
        assertThat(defaultStravaConfig.hasAccessToken()).isFalse();
        assertThat(defaultStravaConfig.hasRefreshToken()).isFalse();
        assertThat(defaultStravaConfig.hasClientSecret()).isFalse();
        assertThat(defaultStravaConfig.hasAppId()).isFalse();



    }
}
