package net.wirelabs.etrex.uploader.strava.client;

import com.strava.model.*;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.common.configuration.StravaConfiguration;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.tools.BasicStravaEmulator;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

class StravaClientTest extends BaseTest {

    private BasicStravaEmulator emulator;
    private StravaClient stravaClient;
    // use non-production configs making sure nothing touches real config files if they exist
    private final AppConfiguration appConfiguration = spy(new AppConfiguration("src/test/resources/config/test.properties"));
    private final StravaConfiguration stravaConfiguration = spy(new StravaConfiguration("src/test/resources/strava-emulator/strava-emulator-config.properties"));

    @BeforeEach
    void setup() {

        emulator = new BasicStravaEmulator();
        // make sure nothing saves the test configurations
        doNothing().when(stravaConfiguration).save();
        doNothing().when(appConfiguration).save();
        String stravaUrl = "http://localhost:" + emulator.getPort();
        String tokenUrl = stravaUrl + "/oauth/token";
        // redirect strava client/strava service calls to mocked strava server
        stravaClient = new StravaClient(stravaConfiguration, appConfiguration, stravaUrl, tokenUrl);
        emulator.start();
    }

    @AfterEach
    void teardown() {
        emulator.teardown();
    }

    @Test
    void getCurrentAthlete() throws StravaException {
        SummaryAthlete athlete = stravaClient.getCurrentAthlete();

        assertThat(athlete.getId()).isEqualTo(12345678);
        assertThat(athlete.getFirstname()).isEqualTo("Fake");
        assertThat(athlete.getLastname()).isEqualTo("User");
    }

    @Test
    void getCurrentAthleteActivities() throws StravaException {

        List<SummaryActivity> activities = stravaClient.getCurrentAthleteActivities();
        assertThat(activities).hasSize(3);

        List<String> names = activities.stream().map(SummaryActivity::getName).toList();
        assertThat(names).containsExactlyInAnyOrder("Spacerniak #3", "Spacerniak #2", "Spacerniak");
    }

    @Test
    void getCurrentAthleteStats() throws StravaException {

        ActivityStats stats = stravaClient.getAthleteStats(12345678L);
        assertThat(stats.getBiggestRideDistance()).isEqualTo(113821.0);
        assertThat(stats.getBiggestClimbElevationGain()).isEqualTo(94.69999999999999);

        ActivityTotal allrideTotals = stats.getAllRideTotals();
        assertThat(allrideTotals.getCount()).isEqualTo(176);
        assertThat(allrideTotals.getDistance()).isEqualTo(5349675.564086914f);
        assertThat(allrideTotals.getMovingTime()).isEqualTo(1356416);
        assertThat(allrideTotals.getElapsedTime()).isEqualTo(1903942);
        assertThat(allrideTotals.getElevationGain()).isEqualTo(30607.924300193787f);

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
    void uploadActivity() throws StravaException {
        File gpxFile = new File("src/test/resources/trackfiles/gpx11.gpx");

        Awaitility.waitAtMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Upload u = stravaClient.uploadActivity(gpxFile, "test activity", "blablabla", SportType.RIDE, false, false);
            assertThat(u).isNotNull();
            assertThat(u.getActivityId()).isEqualTo(11111L);
            assertThat(u.getId()).isEqualTo(999999L);
            assertThat(u.getExternalId()).isEqualTo("ext_blabla.uploaded_kaka");
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
        emulator.teardown();
        Exception e = assertThrows(StravaException.class, () -> {
            stravaClient.getCurrentAthlete();
        });
        assertThat(e.getMessage()).contains("Failed to connect");

    }

    @Test
    void shouldThrowOnInvalidOrNonExistentResource() {
        long nonExistentActivityId = 10L;
        Exception e = assertThrows(StravaException.class, () -> stravaClient.getActivityById(nonExistentActivityId));
        assertThat(e.getMessage()).isEqualTo("404 Not Found: GET /activities/" + nonExistentActivityId);

    }

    @Test
    void shouldRefreshTokenWhenDetectedExpirationInCall() throws StravaException {

        // set token expires so that refresh token will be triggered on any call
        stravaConfiguration.setStravaTokenExpires(Instant.now().getEpochSecond() - 100); // now - 100 secs - should do
        // when refreshing tokens - new token will be saved to configuration -  we want to avoid that so
        doNothing().when(stravaConfiguration).save();
        // this call will trigger the refresh
        SummaryAthlete athlete = stravaClient.getCurrentAthlete();

        verifyLogged("Refreshing token");
        // this log is part of config updater but physical save does not take place due to spy doNothing on save
        verifyLogged("[Refresh token] - updated strava config");
        assertThat(athlete).isNotNull();
    }

    @Test
    void shouldGetAuthToken() throws StravaException {

        stravaClient.exchangeAuthCodeForAccessToken("1234", "bbbb", "kaka");
        verifyLogged("Got tokens!");
        verifyLogged("[Update token] - updated strava config");
        verifyLogged("[Update credentials] - updated strava config");



    }

    @Test
    void shouldThrowWhenGetAuthTokenWithNoAuthCode() {

        // no auth code - exception
        Exception e = assertThrows(StravaException.class, () -> stravaClient.exchangeAuthCodeForAccessToken("1234","bbbb",""));
        assertThat(e.getMessage()).contains("Could not get tokens. auth code was empty");
    }
}