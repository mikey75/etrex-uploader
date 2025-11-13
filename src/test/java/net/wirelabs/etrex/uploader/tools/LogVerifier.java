package net.wirelabs.etrex.uploader.tools;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static net.wirelabs.etrex.uploader.tools.BaseTest.waitUntilAsserted;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.Logger.*;

public class LogVerifier {

    private final ListAppender<ILoggingEvent> loggingEventListAppender = new ListAppender<>();
    private final List<ILoggingEvent> logMessages = loggingEventListAppender.list;


    public LogVerifier() {
        Logger logger = (Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);
        loggingEventListAppender.start();
        logger.addAppender(loggingEventListAppender);
        waitUntilAsserted(Duration.ofSeconds(1) , loggingEventListAppender::isStarted);
    }

    public void verifyNeverLogged(String message) {
        assertThat(getCurrentLogStream().noneMatch(s -> s.contains(message))).isTrue();
    }

    public void verifyLogged(String message) {
        assertThat(getCurrentLogStream().anyMatch(s -> s.contains(message))).isTrue();
    }

    public void verifyLoggedTimes(int times, String message) {
        assertThat((int) getCurrentLogStream().filter(s -> s.contains(message)).count()).isEqualTo(times);
    }

    @NotNull
    private  Stream<String> getCurrentLogStream() {
        return logMessages.stream().map(ILoggingEvent::getFormattedMessage);
    }

    public void clearLogs() {
        logMessages.clear();
    }
}
