package net.wirelabs.etrex.uploader.strava.client;

import com.strava.model.*;
import net.wirelabs.etrex.uploader.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.tools.emulator.StravaEmu;
import net.wirelabs.etrex.uploader.utils.SystemUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StravaClientTest extends BaseTest {


    private static StravaClient stravaClient;
    // use non-production configs making sure nothing touches real config files if they exist
    private static final AppConfiguration appConfiguration = spy(new AppConfiguration("src/test/resources/config/test.properties"));
    private static final StravaConfiguration stravaConfiguration = spy(new StravaConfiguration("src/test/resources/strava-emulator/strava-emulator-config.properties"));
    private static final String emulatorUrl = "http://localhost:9090"; // spring defined host:port

    @BeforeAll
    static void beforeAll() throws StravaException {
        doNothing().when(stravaConfiguration).save();
        doNothing().when(appConfiguration).save();
        // since we do not have access to StravaConfigUpdater, and we make sure token does not expire
        // make sure to not mess the current config with updated tokens during token refresh calls
        // so that all tests work with the same token
        stravaConfiguration.setStravaTokenExpires(Long.MAX_VALUE); // token never expires
        doNothing().when(stravaConfiguration).setStravaAccessToken(anyString());

        stravaConfiguration.setBaseUrl(emulatorUrl);
        stravaConfiguration.setBaseTokenUrl(emulatorUrl + "/oauth/token");
        stravaConfiguration.setAuthUrl(emulatorUrl + "/authorize");
        stravaConfiguration.setAuthCodeTimeout(2);
        stravaClient = new StravaClient(stravaConfiguration, appConfiguration);
        StravaEmu.main(new String[]{});

    }

    @Test
    void getCurrentAthlete() throws StravaException {
        DetailedAthlete athlete = stravaClient.getCurrentAthlete();
        assertThat(athlete.getId()).isEqualTo(123456789);
        assertThat(athlete.getFirstname()).isEqualTo("Fake");
        assertThat(athlete.getLastname()).isEqualTo("User");
    }

    @Test
    void getCurrentAthleteActivities() throws StravaException {

        List<SummaryActivity> activities = stravaClient.getCurrentAthleteActivities();
        assertThat(activities).hasSize(3);

        List<String> names = activities.stream().map(SummaryActivity::getName).toList();
        assertThat(names).containsExactlyInAnyOrder("Spacerniak #3", "Spacerniak #2", "Spacerniak");
        verifyLogged("[Strava request: GET] http://localhost:9090/athlete/activities?per_page=10&page=1");
    }

    @Test
    void getCurrentAthleteStats() throws StravaException {

        ActivityStats stats = stravaClient.getAthleteStats(123456789L);
        assertThat(stats.getBiggestRideDistance()).isEqualTo(113821.0);
        assertThat(stats.getBiggestClimbElevationGain()).isEqualTo(94.69999999999999);

        ActivityTotal allRideTotals = stats.getAllRideTotals();
        assertThat(allRideTotals.getCount()).isEqualTo(176);
        assertThat(allRideTotals.getDistance()).isEqualTo(5349675.564086914f);
        assertThat(allRideTotals.getMovingTime()).isEqualTo(1356416);
        assertThat(allRideTotals.getElapsedTime()).isEqualTo(1903942);
        assertThat(allRideTotals.getElevationGain()).isEqualTo(30607.924300193787f);

        ActivityTotal ytdTotals = stats.getYtdRideTotals();
        assertThat(ytdTotals.getCount()).isEqualTo(2);
        assertThat(ytdTotals.getDistance()).isEqualTo(22876);
        assertThat(ytdTotals.getMovingTime()).isEqualTo(7403);
        assertThat(ytdTotals.getElapsedTime()).isEqualTo(11235);
        assertThat(ytdTotals.getElevationGain()).isEqualTo(286.7000045776367f);
    }

    @Test
    void getOtherAthleteStats() throws StravaException {
        ActivityStats stats = stravaClient.getAthleteStats(87654321L);
        assertThat(stats.getBiggestRideDistance()).isEqualTo(0.8008281904610115);
        assertThat(stats.getBiggestClimbElevationGain()).isEqualTo(6.027456183070403);

        ActivityTotal recentRideTotals = stats.getRecentRideTotals();
        assertThat(recentRideTotals.getCount()).isEqualTo(1);
        assertThat(recentRideTotals.getDistance()).isEqualTo(5.962134f);
        assertThat(recentRideTotals.getElapsedTime()).isEqualTo(2);
        assertThat(recentRideTotals.getMovingTime()).isEqualTo(5);
        assertThat(recentRideTotals.getAchievementCount()).isEqualTo(9);
        assertThat(recentRideTotals.getElevationGain()).isEqualTo(7.0614014f);
    }

    @Test
    void updateActivity() throws StravaException {
        // get activity
        UpdatableActivity activity = new UpdatableActivity();

        // change some details
        activity.setDescription("Changed activity");
        activity.setCommute(true);
        activity.setName("Changed activity name");
        activity.setSportType(SportType.EBIKERIDE);
        // update
        DetailedActivity updatedActivity = stravaClient.updateActivity(777111L, activity);
        // check what we updated
        assertThat(updatedActivity.getDescription()).isEqualTo("Changed activity");
        assertThat(updatedActivity.getName()).isEqualTo("Changed activity name");
        assertThat(updatedActivity.getSportType()).isEqualTo(SportType.EBIKERIDE);
        assertThat(updatedActivity.isCommute()).isTrue();

        // and for sureness check some original fields were not updated
        assertThat(updatedActivity.getId()).isEqualTo(777111L);
        assertThat(updatedActivity.getDeviceName()).isEqualTo("Garmin eTrex 32x");
    }

    @Test
    void getActivityById() throws StravaException {
        DetailedActivity activity = stravaClient.getActivityById(777111L);
        assertThat(activity.getId()).isEqualTo(777111L);
        assertThat(activity.getTotalPhotoCount()).isEqualTo(13);
        assertThat(activity.getMap()).isNotNull();
        assertThat(activity.getSegmentEfforts()).isNotEmpty();
        assertThat(activity.getDescription()).contains("Mazury, mazury, jeziora i pagóry");
        assertThat(activity.getDeviceName()).isEqualTo("Garmin eTrex 32x");
        assertThat(activity.getName()).isEqualTo("Mikro wersja Mazurskiej Pętli Rowerowej");
    }


    @Test
    void uploadActivity() {
        File gpxFile = new File("src/test/resources/trackfiles/gpx11.gpx");

        waitUntilAsserted(Duration.ofSeconds(5), () -> {
            Upload upload = stravaClient.uploadActivity(gpxFile, "test activity", "blablabla", SportType.RIDE, false, false);
            assertThat(upload).isNotNull();
            assertThat(upload.getActivityId()).isEqualTo(11111L);
            assertThat(upload.getId()).isEqualTo(999999L);
            assertThat(upload.getExternalId()).matches("test activity\\d{13}"); // external id is upload $name + System.currentTimeMillis
            assertThat(upload.getError()).isNull();
            assertThat(upload.getStatus()).isNotNull();
        });
    }

    @Test
    void getActivityStreams() throws StravaException {

        StreamSet s = stravaClient.getActivityStreams(777111L, "distance", true);

        assertThat(s.getDistance()).isNotNull();
        assertThat(s.getDistance().getData()).isNotEmpty();
        assertThat(s.getDistance().getOriginalSize()).isEqualTo(1608);
        assertThat(s.getDistance().getResolution()).isEqualTo(DistanceStream.ResolutionEnum.HIGH);
        assertThat(s.getDistance().getSeriesType()).isEqualTo(DistanceStream.SeriesTypeEnum.DISTANCE);

    }

    @Test
    void shouldThrowExceptionsWhenServerNotAvailable() {
        // no need for emu here so separate client and nonexistent endpoint
        stravaConfiguration.setBaseUrl("http://localhost-nonexistent:9999");
        StravaClient client = new StravaClient(stravaConfiguration, appConfiguration);
        Exception e = assertThrows(StravaException.class, client::getCurrentAthlete);
        assertThat(e.getMessage()).contains("Failed to connect");
    }

    @Test
    void shouldThrowOnInvalidOrNonExistentResource() {
        // activity with id=10 is not present
        Exception e = assertThrows(StravaException.class, () -> stravaClient.getActivityById(10L));
        assertThat(e.getMessage()).isEqualTo("No activity with id=" + 10L);
    }

    @Test
    void shouldRefreshTokenWhenDetectedExpirationInCall() throws StravaException {

        try {
            // set token expires so that refresh token will be triggered on any call
            stravaConfiguration.setStravaTokenExpires(Instant.now().getEpochSecond() - 100); // now - 100 secs - should do
            // when refreshing tokens - new token will be saved to configuration -  we want to avoid that so
            doNothing().when(stravaConfiguration).save();
            // this call will trigger the refresh
            DetailedAthlete athlete = stravaClient.getCurrentAthlete();

            verifyLogged("Refreshing token");
            verifyLogged("[Strava request: POST] http://localhost:9090/oauth/token");
            verifyLogged("[Refresh token] - updated strava config");
            assertThat(athlete).isNotNull();
            verifyLogged("[Strava request: GET] http://localhost:9090/athlete");
        } finally {
            // restore token to never expire again
            stravaConfiguration.setStravaTokenExpires(Long.MAX_VALUE);
        }
    }

    @Test
    void shouldGetAuthToken() throws StravaException {
        stravaClient.exchangeAuthCodeForAccessToken("1234", "bbbb", "supersecretcode");
        verifyLogged("Got tokens!");
        verifyLogged("[Update token] - updated strava config");
        verifyLogged("[Update credentials] - updated strava config");
    }

    @Test
    void shouldThrowWhenGetAuthTokenWithNoAuthCode() {
        // no auth code - exception
        Exception e = assertThrows(StravaException.class, () -> stravaClient.exchangeAuthCodeForAccessToken("1234", "bbbb", ""));
        assertThat(e.getMessage()).contains("Could not get tokens. auth code was empty");
    }

    @Test
    void shouldAuthorizeToStrava() throws IOException, StravaException {
        try (MockedStatic<SystemUtils> systemUtils = Mockito.mockStatic(SystemUtils.class)) {
            systemUtils.when(() -> SystemUtils.openSystemBrowser(any())).thenAnswer(inv -> { // call emulator here, emulator should respond with request to retriever
                String url = inv.getArgument(0, String.class);
                HttpResponse<String> r = emulateBrowserCall(url);
                assertThat(r.statusCode()).isEqualTo(200);
                return null;
            });
            stravaClient.authorizeToStrava("supersecret", "1234");
            verifyLogged("Starting Strava OAuth process");
            verifyLogged("Got tokens");

        }
    }

    private HttpResponse<String> emulateBrowserCall(String url) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}