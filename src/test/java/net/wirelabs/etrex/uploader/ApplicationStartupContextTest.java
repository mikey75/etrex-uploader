package net.wirelabs.etrex.uploader;

import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationStartupContextTest extends BaseTest {

    private ApplicationStartupContext ctx;

    private final File appConfigFile = new File(SystemUtils.getWorkDir(), "config.properties");
    private final File stravaConfigFile = new File(SystemUtils.getWorkDir(), "strava.properties");

    @BeforeEach
    void before() throws IOException {
        preserveFiles(appConfigFile, stravaConfigFile);
        ctx = new ApplicationStartupContext();
    }

    @AfterEach
    void after() throws IOException {
        restoreFiles(appConfigFile, stravaConfigFile);
    }

    @Test
    void getAppConfiguration() {

        assertThat(ctx.getAppConfiguration()).isNotNull();
        assertThat(ctx.getStravaConfiguration()).isNotNull();
        assertThat(ctx.getUploadService()).isNotNull();

        assertThat(ctx.getFileService()).isNotNull();
        assertThat(ctx.getGarminDeviceService()).isNotNull();
        assertThat(ctx.getStravaClient()).isNotNull();

        // check if ctx loaded config files
        verifyLogged("Loading config.properties");
        verifyLogged("Loading strava.properties");

        // check if fileservice created dirs
        verifyLogged("Initializing directories");

    }


}