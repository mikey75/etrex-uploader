package net.wirelabs.etrex.uploader.strava.client;

import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.strava.client.token.RefreshTokenResponse;
import net.wirelabs.etrex.uploader.strava.client.token.TokenResponse;
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
    void refreshExpired() {
        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse();
        refreshTokenResponse.setAccessToken("dddd");
        refreshTokenResponse.setRefreshToken("zzzzz");
        refreshTokenResponse.setTokenType("refresh");
        refreshTokenResponse.setExpiresAt(222222222222L);
        refreshTokenResponse.setExpiresIn(222L);

        configUpdater.refreshExpired(refreshTokenResponse);

        assertThat(stravaConfiguration.getStravaRefreshToken()).isEqualTo("zzzzz");
        assertThat(stravaConfiguration.getStravaTokenExpires()).isEqualTo(222222222222L);
        assertThat(stravaConfiguration.getStravaAccessToken()).isEqualTo("dddd");
        verifyLogged("[Refresh token] - updated strava config");
    }

    @Test
    void updateToken() {

        TokenResponse response = new TokenResponse();
        response.setTokenType("access");
        response.setExpiresAt(1111111111L);
        response.setAccessToken("bbbb");
        response.setRefreshToken("ccccc");
        response.setExpiresIn(222L);


        configUpdater.updateToken(response);
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