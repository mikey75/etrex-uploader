package net.wirelabs.etrex.uploader.strava.client;

import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.strava.client.token.RefreshTokenResponse;
import net.wirelabs.etrex.uploader.strava.client.token.TokenResponse;
import net.wirelabs.etrex.uploader.strava.utils.JsonUtil;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;

import static org.assertj.core.api.Assertions.assertThat;

class StravaConfigUpdaterTest extends BaseTest {

    private static final File CONFIG = new File("src/test/resources/config/strava.properties");
    private static final File CONFIG_COPY = new File("src/test/resources/config/strava.properties.copy");
    private static final File ACCESS_TOKEN = new File("src/test/resources/token/access_token.json");
    private static final File REFRESH_TOKEN = new File("src/test/resources/token/refresh_token.json");

    private StravaConfiguration stravaConfiguration;
    private StravaConfigUpdater configUpdater;

    @BeforeEach
    void before() throws IOException {

        stravaConfiguration = new StravaConfiguration(CONFIG.getPath());
        configUpdater = new StravaConfigUpdater(stravaConfiguration);
        verifyLogged("Loading " + CONFIG);

        // copy test configuration because it will be changed and saved during tests
        FileUtils.copyFile(CONFIG, CONFIG_COPY, StandardCopyOption.REPLACE_EXISTING);
        assertThat(FileUtils.readFileToString(CONFIG, StandardCharsets.UTF_8)).isEqualTo(FileUtils.readFileToString(CONFIG_COPY, StandardCharsets.UTF_8));
        // check if starting strava configuration is what the config file says
        assertThat(stravaConfiguration.getStravaClientSecret()).isEqualTo("aaaa");
        assertThat(stravaConfiguration.getStravaAppId()).isEqualTo("10101020");
        assertThat(stravaConfiguration.getStravaRefreshToken()).isEqualTo("1234567");
        assertThat(stravaConfiguration.getStravaTokenExpires()).isEqualTo(12345678);
        assertThat(stravaConfiguration.getStravaAccessToken()).isEqualTo("9321093109301");

    }

    @AfterEach
    void after() throws IOException {
        FileUtils.copyFile(CONFIG_COPY, CONFIG, StandardCopyOption.REPLACE_EXISTING);
        assertThat(FileUtils.readFileToString(CONFIG, StandardCharsets.UTF_8)).isEqualTo(FileUtils.readFileToString(CONFIG_COPY, StandardCharsets.UTF_8));
        FileUtils.deleteQuietly(CONFIG_COPY);
        assertThat(CONFIG_COPY).doesNotExist();
        verifyLogged("Saving configuration " + CONFIG);
    }

    @Test
    void refreshExpired() throws IOException {
        String tokenFile = FileUtils.readFileToString(REFRESH_TOKEN, StandardCharsets.UTF_8);
        RefreshTokenResponse refreshTokenResponse = JsonUtil.deserialize(tokenFile,RefreshTokenResponse.class);

        assertThat(refreshTokenResponse.getTokenType()).isEqualTo("refresh");
        assertThat(refreshTokenResponse.getRefreshToken()).isEqualTo("zzzzz");
        assertThat(refreshTokenResponse.getExpiresAt()).isEqualTo(222222222222L);
        assertThat(refreshTokenResponse.getAccessToken()).isEqualTo("dddd");
        assertThat(refreshTokenResponse.getExpiresIn()).isEqualTo(222L);

        configUpdater.refreshExpired(refreshTokenResponse);

        assertThat(stravaConfiguration.getStravaRefreshToken()).isEqualTo("zzzzz");
        assertThat(stravaConfiguration.getStravaTokenExpires()).isEqualTo(222222222222L);
        assertThat(stravaConfiguration.getStravaAccessToken()).isEqualTo("dddd");
        verifyLogged("[Refresh token] - updated strava config");
    }

    @Test
    void updateToken() throws IOException {
        String tokenFile = FileUtils.readFileToString(ACCESS_TOKEN, StandardCharsets.UTF_8);
        TokenResponse tokenResponse = JsonUtil.deserialize(tokenFile,TokenResponse.class);

        assertThat(tokenResponse.getTokenType()).isEqualTo("access");
        assertThat(tokenResponse.getRefreshToken()).isEqualTo("ccccc");
        assertThat(tokenResponse.getExpiresAt()).isEqualTo(1111111111L);
        assertThat(tokenResponse.getAccessToken()).isEqualTo("bbbb");
        assertThat(tokenResponse.getExpiresIn()).isEqualTo(333L);
        assertThat(tokenResponse.getAthlete()).isNotNull();
        assertThat(tokenResponse.getAthlete().getId()).isEqualTo(12345678);


        configUpdater.updateToken(tokenResponse);
        assertThat(stravaConfiguration.getStravaAccessToken()).isEqualTo("bbbb");
        assertThat(stravaConfiguration.getStravaRefreshToken()).isEqualTo("ccccc");
        assertThat(stravaConfiguration.getStravaTokenExpires()).isEqualTo(1111111111L);
        verifyLogged("[Update token] - updated strava config");


    }

    @Test
    void updateCredentials() {
        configUpdater.updateCredentials("newAppId","newClientSecret");
        assertThat(stravaConfiguration.getStravaClientSecret()).isEqualTo("newClientSecret");
        assertThat(stravaConfiguration.getStravaAppId()).isEqualTo("newAppId");
        assertThat(stravaConfiguration.getStravaRefreshToken()).isEqualTo("1234567");
        assertThat(stravaConfiguration.getStravaTokenExpires()).isEqualTo(12345678);
        assertThat(stravaConfiguration.getStravaAccessToken()).isEqualTo("9321093109301");
        verifyLogged("[Update credentials] - updated strava config");
    }
}