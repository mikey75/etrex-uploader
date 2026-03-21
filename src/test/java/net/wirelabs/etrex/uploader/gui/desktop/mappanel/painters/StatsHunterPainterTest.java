package net.wirelabs.etrex.uploader.gui.desktop.mappanel.painters;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.tools.StatsHuntersEmulator;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class StatsHunterPainterTest extends BaseTest {

    private static StatsHuntersEmulator statsServer;

    @Test
    void init() throws IOException {
        statsServer = new StatsHuntersEmulator();
        statsServer.start();
        AppConfiguration appConfiguration = new AppConfiguration("/target/fake-statshunters.properties"); // create default
        appConfiguration.setStatsHuntersUrl("http://localhost:" + statsServer.getListeningPort() + "/good");
        StatsHunterPainter p = new StatsHunterPainter(appConfiguration);
        assertThat(p.isConfigured()).isTrue();
        verifyLogged("[StatsHunters] Downloading tiles");
        verifyLogged("[StatsHunters] Initialization finished");
        statsServer.stop();
    }

    @Test
    void testBadJson() throws IOException {
        statsServer = new StatsHuntersEmulator();
        statsServer.start();
        AppConfiguration appConfiguration = new AppConfiguration("/target/fake-statshunters.properties"); // create default
        appConfiguration.setStatsHuntersUrl("http://localhost:" + statsServer.getListeningPort() + "/bad");

        StatsHunterPainter p = new StatsHunterPainter(appConfiguration);
        assertThat(p.isConfigured()).isTrue();
        verifyLogged("[StatsHunters] Downloading tiles");
        verifyLogged("[StatsHunters] Exception trying to parse provided json");;
        verifyNeverLogged("[StatsHunters] Initialization finished");
        statsServer.stop();

    }

}