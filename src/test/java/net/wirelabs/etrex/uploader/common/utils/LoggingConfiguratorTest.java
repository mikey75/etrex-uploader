package net.wirelabs.etrex.uploader.common.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

class LoggingConfiguratorTest {

    private final String fakeUserDir = "target/temp";
    private final Path fakeConfigFilePath = Paths.get(fakeUserDir, "logback.xml");
    private final Path minimalExisting = Paths.get("src/test/resources/logback/minimalLogback.xml");

    @BeforeEach
    void before() throws IOException {
        // change work.dir to not operate on existing files
        System.setProperty("user.dir", fakeUserDir);
        // delete existing test file
        Files.deleteIfExists(fakeConfigFilePath);
    }

    @Test
    void shouldIssueAConfirmationDialogWhenNoConfigXMLFound() throws IOException {

        try (MockedStatic<LoggingConfigurator> logConf = Mockito.mockStatic(LoggingConfigurator.class)) {

            logConf.when(LoggingConfigurator::configureLogger).thenCallRealMethod();

            LoggingConfigurator.configureLogger();
            logConf.verify(() -> LoggingConfigurator.issueConfirmationDialog(any()), times(1));
            logConf.reset();
            Files.deleteIfExists(fakeConfigFilePath);
        }

    }

    @Test
    void shouldIssueDialogFromJoranExceptionToo() throws IOException {
        // create 'empty' fake config file - the joran exception will be thrown (parsing xml errror)
        Files.createFile(fakeConfigFilePath);

        try (MockedStatic<LoggingConfigurator> logConf = Mockito.mockStatic(LoggingConfigurator.class)) {
            logConf.when(LoggingConfigurator::configureLogger).thenCallRealMethod();

            LoggingConfigurator.configureLogger();
            logConf.verify(() -> LoggingConfigurator.issueConfirmationDialog(contains("Problem parsing XML document")), times(1));
            logConf.reset();
        }
    }

    @Test
    void shouldNotIssueAConfirmationDialogWhenConfigXMLFound() throws IOException {

        // create 'existing' fake config file
        Files.copy(minimalExisting, fakeConfigFilePath);

        try (MockedStatic<LoggingConfigurator> logConf = Mockito.mockStatic(LoggingConfigurator.class)) {
            logConf.when(LoggingConfigurator::configureLogger).thenCallRealMethod();

            LoggingConfigurator.configureLogger();
            logConf.verify(() -> LoggingConfigurator.issueConfirmationDialog(any()), never());
            logConf.reset();
        }

    }
}