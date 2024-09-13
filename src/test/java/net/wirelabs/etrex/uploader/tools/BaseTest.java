package net.wirelabs.etrex.uploader.tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
@Slf4j
public abstract class BaseTest {
    // Base test for all tests, for now it only provides log verification
    // but will be expanded with common test features

    private final LogVerifier logVerifier = new LogVerifier();

    public BaseTest() {
    }

    @BeforeEach
    void beforeEach() {
       logVerifier.clearLogs();
    }

    public void verifyNeverLogged(String message) {
        logVerifier.verifyNeverLogged(message);
    }

    public void verifyLogged(String message) {
        logVerifier.verifyLogged(message);
    }

    public void verifyLoggedTimes(int times, String message) {
        logVerifier.verifyLoggedTimes(times, message);
    }


}
