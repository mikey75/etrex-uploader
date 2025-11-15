package net.wirelabs.etrex.uploader.strava;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.strava.utils.LocalWebServer;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.tools.TestHttpServer;
import net.wirelabs.etrex.uploader.utils.NetworkingUtils;
import net.wirelabs.etrex.uploader.utils.SwingUtils;
import net.wirelabs.etrex.uploader.utils.SystemUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@Slf4j
class StravaConnectionCheckerTest extends BaseTest {

    private static final String NONEXISTING_HOST = "www.123nonexisting231.pl";
    private final StravaConfiguration configuration = new StravaConfiguration("target/nonexistent-strava-config"); // assumes default config
    private final StravaConnectionChecker stravaConnectionChecker = new StravaConnectionChecker(configuration);


    @Test
    void check() throws IOException {

        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class);
             MockedStatic<SwingUtils> swingUtils = Mockito.mockStatic(SwingUtils.class)) {

            systemUtils.when(() -> SystemUtils.systemExit(anyInt())).thenAnswer(inv -> null); // do nothing
            swingUtils.when(() -> SwingUtils.errorMsg(anyString())).thenAnswer(inv -> null);

            // set localhost:3333 as 'strava address'
            stravaConnectionChecker.setStravaHostName("localhost");
            stravaConnectionChecker.setStravaPort(3333);

            // no local webserver on 3333 - strava down, should exit
            stravaConnectionChecker.checkAndExitIfDown();
            verifyLogged("Starting Strava status check");
            verifyLogged("Strava seems to be down. Exiting!");

            // strava down, but we do not want exit the app (i.e. during uploads)
            // so we should service an exception that is thrown
            assertThatThrownBy(stravaConnectionChecker::checkAndContinueIfDown)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Strava is down, but continuing");
            verifyLogged("Starting Strava status check");
            verifyLogged("Strava seems to be down. Try again!");


            // local webserver on 3333 - strava up
            LocalWebServer webServer = new TestHttpServer(3333);
            stravaConnectionChecker.checkAndExitIfDown();
            verifyLogged("Starting Strava status check");
            verifyLogged("Strava is up and running!");
            webServer.stop();
        }
    }

    @Test
    void checkExceptionAndLogOnUnknownHost() {

        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class);
             MockedStatic<SwingUtils> swingUtils = Mockito.mockStatic(SwingUtils.class)) {
            systemUtils.when(() -> SystemUtils.systemExit(anyInt())).thenAnswer(inv -> null); // do nothing
            swingUtils.when(() -> SwingUtils.errorMsg(anyString())).thenAnswer(inv -> null);

            stravaConnectionChecker.setStravaHostName(NONEXISTING_HOST);
            stravaConnectionChecker.checkAndExitIfDown();
            verifyLogged("Starting Strava status check");
            verifyLogged("Strava or network is down!");
        }


    }

    @Test
    void checkStravaHostsInaccessible() throws IOException {

        // hosts will not be accessible -> no http server, no port
        // so setup test with nonexistent, random port
        int fakeHttpPort = NetworkingUtils.getRandomFreeTcpPort();
        try (MockedStatic<SwingUtils> swingUtils = Mockito.mockStatic(SwingUtils.class);
             MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class)) {


            swingUtils.when(() -> SwingUtils.errorMsg(anyString())).thenAnswer(inv -> null);
            systemUtils.when(() -> SystemUtils.systemExit(anyInt())).thenAnswer(inv -> null);

            stravaConnectionChecker.setStravaHostName("127.0.0.1");
            stravaConnectionChecker.setStravaPort(fakeHttpPort);
            // should log warning, and return false
            stravaConnectionChecker.checkAndExitIfDown();
            // should log hosts unavailability
            verifyLogged("127.0.0.1:" + fakeHttpPort + " is unreachable");    // msg from networkingUtils (no http port)
            verifyLogged("127.0.0.1:" + fakeHttpPort + " inaccessible, assume uploads might fail"); // msg from StravaUtil
        }

    }

    @Test
    void shouldReturnDefaultPortAndHost() {
        log.info("Config object " + stravaConnectionChecker.hashCode());
        assertThat(stravaConnectionChecker.getStravaHostName()).isEqualTo("www.strava.com"); //StravaUtil.STRAVA_HOST_NAME);
        assertThat(stravaConnectionChecker.getStravaPort()).isEqualTo(80);
    }


}