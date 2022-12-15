package net.wirelabs.etrex.uploader.gui.strava.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

import java.io.File;
import java.time.Duration;

import net.wirelabs.etrex.uploader.gui.strava.account.UserAccountPanel;
import net.wirelabs.etrex.uploader.strava.client.StravaException;
import net.wirelabs.etrex.uploader.strava.model.SummaryAthlete;
import net.wirelabs.etrex.uploader.strava.service.IStravaService;
import net.wirelabs.etrex.uploader.strava.service.StravaService;
import org.junit.jupiter.api.Test;

class UserAccountPanelTest {
    
    private final File fakePhoto  = new File("src/test/resources/fakeUserPhoto.png");
    private final IStravaService strava = mock(StravaService.class);
    private final SummaryAthlete fakeUser = new SummaryAthlete().firstname("Fake").lastname("User");
    private final String profilePictureURL = fakePhoto.getAbsolutePath().replace("\\", "/");
    

    @Test
    void testUserWithoutProfilePicture() throws StravaException {
        
        doReturn(fakeUser).when(strava).getCurrentAthlete();

        UserAccountPanel uap = new UserAccountPanel(strava);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
                    assertAthleteNamePresent(uap);
                    assertNoPicture(uap);
                }
        );
    }

    @Test
    void testCorrectUserAndImage() throws StravaException {
        
        fakeUser.setProfile("file:///" + profilePictureURL);
        doReturn(fakeUser).when(strava).getCurrentAthlete();
        
        UserAccountPanel uap = new UserAccountPanel(strava);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
                    assertAthleteNamePresent(uap);
                    assertThat(uap.athletePicture.getIcon()).isNotNull();
                }
        );

    }

    @Test
    void testStravaThrownException() throws StravaException {

        doThrow(StravaException.class).when(strava).getCurrentAthlete();
        
        UserAccountPanel uap = new UserAccountPanel(strava);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
                    assertNoAthleteName(uap);
                    assertNoPicture(uap);
                }
        );
    }

    @Test
    void testImageIOThrownException() throws StravaException {
        
        fakeUser.setProfile("file:///"); // <-- this triggers IOException inside
        doReturn(fakeUser).when(strava).getCurrentAthlete();
        UserAccountPanel uap = new UserAccountPanel(strava);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
                    assertAthleteNamePresent(uap);
                    assertNoPicture(uap);
                }
        );
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