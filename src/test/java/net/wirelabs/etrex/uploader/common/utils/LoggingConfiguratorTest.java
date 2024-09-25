package net.wirelabs.etrex.uploader.common.utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;


class LoggingConfiguratorTest {

    private static final Path ORIGINAL_LOGBACK_XML_PATH = Paths.get("logback.xml");
    private static final Path LOGBACK_XML_COPY_PATH = Paths.get("logback.xml.copy");
    private static final Path MINIMAL_EXISTING_LOGBACK_XML_PATH = Paths.get("src/test/resources/logback/minimalLogback.xml");

    @BeforeAll
    static void beforeAll() throws IOException {
        // copy existing logback.xml to safe place
        Files.copy(ORIGINAL_LOGBACK_XML_PATH, LOGBACK_XML_COPY_PATH, StandardCopyOption.REPLACE_EXISTING);
    }

    @AfterAll
    static void afterAll() throws IOException {
        // restore copied xml and remove any remaining copy
        Files.copy(LOGBACK_XML_COPY_PATH, ORIGINAL_LOGBACK_XML_PATH, StandardCopyOption.REPLACE_EXISTING);
        Files.deleteIfExists(LOGBACK_XML_COPY_PATH);
    }

    @Test
    void shouldIssueAConfirmationDialogWhenNoConfigXMLFound() throws IOException {
        Files.delete(Paths.get("logback.xml"));
        try (MockedStatic<LoggingConfigurator> logConf = Mockito.mockStatic(LoggingConfigurator.class)) {

            logConf.when(LoggingConfigurator::configureLogger).thenCallRealMethod();

            LoggingConfigurator.configureLogger();
            logConf.verify(() -> LoggingConfigurator.issueConfirmationDialog(any()), times(1));
        }

    }

    @Test
    void shouldIssueDialogFromJoranExceptionToo() throws IOException {
        // create 'empty' real config file - the joran exception will be thrown (parsing xml errror)
        Files.createFile(ORIGINAL_LOGBACK_XML_PATH);

        try (MockedStatic<LoggingConfigurator> logConf = Mockito.mockStatic(LoggingConfigurator.class)) {
            logConf.when(LoggingConfigurator::configureLogger).thenCallRealMethod();
            LoggingConfigurator.configureLogger();
            logConf.verify(() -> LoggingConfigurator.issueConfirmationDialog(contains("Problem parsing XML document")), times(1));
        }
    }

    @Test
    void shouldNotIssueAConfirmationDialogWhenConfigXMLFound() throws IOException {

        // create minimal 'existing' config file
        Files.copy(MINIMAL_EXISTING_LOGBACK_XML_PATH, ORIGINAL_LOGBACK_XML_PATH,StandardCopyOption.REPLACE_EXISTING);

        try (MockedStatic<LoggingConfigurator> logConf = Mockito.mockStatic(LoggingConfigurator.class)) {
            logConf.when(LoggingConfigurator::configureLogger).thenCallRealMethod();

            LoggingConfigurator.configureLogger();
            logConf.verify(() -> LoggingConfigurator.issueConfirmationDialog(any()), never());

        }

    }
}