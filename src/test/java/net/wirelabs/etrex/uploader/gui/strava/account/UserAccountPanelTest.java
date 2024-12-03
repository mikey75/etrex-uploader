package net.wirelabs.etrex.uploader.gui.strava.account;

import com.strava.model.ActivityStats;
import com.strava.model.ActivityTotal;
import com.strava.model.SummaryAthlete;
import net.wirelabs.etrex.uploader.common.configuration.AppConfiguration;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.strava.service.StravaService;
import net.wirelabs.etrex.uploader.strava.service.StravaServiceImpl;
import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserAccountPanelTest extends BaseTest {

    private final File fakePhoto = new File("src/test/resources/gui/fakeUserPhoto.png");
    private final StravaService strava = mock(StravaServiceImpl.class);
    private final SummaryAthlete fakeUser = new SummaryAthlete().firstname("Fake").lastname("User");
    private final ActivityTotal total = new ActivityTotal().count(1).distance(1000F).elapsedTime(3600);
    private final ActivityStats fakeStats = new ActivityStats().allRideTotals(total).ytdRideTotals(total);
    private final String profilePictureURL = fakePhoto.getAbsolutePath().replace("\\", "/");
    

    @Test
    void testUserWithoutProfilePicture() throws StravaException {
        
        doReturn(fakeUser).when(strava).getCurrentAthlete();

        UserAccountPanel uap = new UserAccountPanel(strava, mock(AppConfiguration.class));

        waitUntilAsserted(Duration.ofSeconds(5), () -> {
                    assertAthleteNamePresent(uap);
                    assertNoPicture(uap);
                }
        );
    }

    @Test
    void testCorrectUserAndImage() throws StravaException {
        
        fakeUser.setProfileMedium("file:///" + profilePictureURL);
        doReturn(fakeUser).when(strava).getCurrentAthlete();
        
        UserAccountPanel uap = new UserAccountPanel(strava, mock(AppConfiguration.class));

        waitUntilAsserted(Duration.ofSeconds(5), () -> {
                    assertAthleteNamePresent(uap);
                    assertThat(uap.athletePicture.getIcon()).isNotNull();
                }
        );

    }

    @Test
    void testStravaThrownException() throws StravaException {

        doThrow(StravaException.class).when(strava).getCurrentAthlete();
        
        UserAccountPanel uap = new UserAccountPanel(strava, mock(AppConfiguration.class));

        waitUntilAsserted(Duration.ofSeconds(5), () -> {
                    assertNoAthleteName(uap);
                    assertNoPicture(uap);
                }
        );
    }

    @Test
    void testImageIOThrownException() throws StravaException {
        
        fakeUser.setProfileMedium("file:///"); // <-- this triggers IOException inside
        doReturn(fakeUser).when(strava).getCurrentAthlete();
        UserAccountPanel uap = new UserAccountPanel(strava, mock(AppConfiguration.class));

        waitUntilAsserted(Duration.ofSeconds(5), () -> {
                    assertAthleteNamePresent(uap);
                    assertNoPicture(uap);
                }
        );
    }

    @Test
    void testCorrectStats() throws StravaException {

        doReturn(fakeStats).when(strava).getAthleteStats(any());
        doReturn(fakeUser).when(strava).getCurrentAthlete();
        UserAccountPanel uap = new UserAccountPanel(strava, mock(AppConfiguration.class));

        waitUntilAsserted(Duration.ofSeconds(5), () -> {
                    assertCorrectStats(uap);
                }
        );
    }

    private static void assertCorrectStats(UserAccountPanel uap) {
        assertThat(uap.totalDist.getText()).isEqualTo("1 km");
        assertThat(uap.totalRides.getText()).isEqualTo("1");
        assertThat(uap.totalTime.getText()).isEqualTo("01 hours");
        assertThat(uap.ytdDist.getText()).isEqualTo("1 km");
        assertThat(uap.ytdRides.getText()).isEqualTo("1");
        assertThat(uap.ytdTime.getText()).isEqualTo("01 hours");
    }

    private static void assertNoAthleteName(UserAccountPanel uap) {
        assertThat(uap.athleteName.getText()).isEqualTo("Couldn't get athlete name");
    }

    private static void assertNoPicture(UserAccountPanel uap) {
        assertThat(uap.athletePicture.getText()).isEqualTo("Couldn't get athlete picture");
        assertThat(uap.athletePicture.getIcon()).isNull();
    }

    private static void assertAthleteNamePresent(UserAccountPanel uap) {
        assertThat(uap.athleteName.getText()).isEqualTo("Fake User");
    }
}