package net.wirelabs.etrex.uploader.tools;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.api.Assertions;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogVerifier {

    private static ListAppender<ILoggingEvent> listAppender;
    private static List<ILoggingEvent> logi = new ArrayList<>();

    public static void initLogging() {
        Logger logger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        listAppender = new ListAppender<>();
        listAppender.list.clear();
        listAppender.start();
        logger.addAppender(listAppender);
    }


    public static void verifyLogged(String message) {
        logi = listAppender.list;
        List<String> msgList = logi.stream().map(ILoggingEvent::getFormattedMessage).collect(Collectors.toList());


        String str = String.join("", msgList);
            Assertions.assertThat(str).contains(message);

    }
}
