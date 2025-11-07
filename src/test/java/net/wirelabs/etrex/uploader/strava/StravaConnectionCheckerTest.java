package net.wirelabs.etrex.uploader.strava;

import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.strava.utils.LocalWebServer;
import net.wirelabs.etrex.uploader.strava.utils.StravaUtil;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.tools.TestHttpServer;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.utils.SystemUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

class StravaConnectionCheckerTest extends BaseTest {

    private final StravaConfiguration configuration = new StravaConfiguration("target/nonexistent-strava-config"); // assumes default config
    private final StravaConnectionChecker stravaConnectionChecker = new StravaConnectionChecker(configuration);


    @Test
    void check() throws IOException {
        try (MockedStatic<StravaUtil> stravaUtil = Mockito.mockStatic(StravaUtil.class, Mockito.CALLS_REAL_METHODS);
             MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class);
             MockedStatic<SwingUtils> swingUtils = Mockito.mockStatic(SwingUtils.class)) {

            systemUtils.when(() -> SystemUtils.systemExit(anyInt())).thenAnswer(inv -> null); // do nothing
            swingUtils.when(() -> SwingUtils.errorMsg(anyString())).thenAnswer(inv -> null);

            // set localhost:3333 as 'strava address'
            stravaUtil.when(StravaUtil::getStravaHostName).thenReturn("localhost");
            stravaUtil.when(StravaUtil::getStravaPort).thenReturn(3333);


            // no local webserver on 3333 - strava down
            stravaConnectionChecker.checkStravaIsUp();
            verifyLogged("Starting Strava status check");
            verifyLogged("Strava seems to be down. Exiting!");

            // local webserver on 3333 - strava up
            LocalWebServer webServer = new TestHttpServer(3333);
            stravaConnectionChecker.checkStravaIsUp();
            verifyLogged("Starting Strava status check");
            verifyLogged("Strava is up and running!");
            webServer.stop();
        }
    }

}