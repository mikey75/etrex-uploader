package net.wirelabs.etrex.uploader.common.utils;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


class LoggingConfiguratorTest {

    private static final Path ORIGINAL_LOGBACK_XML_PATH = Paths.get("logback.xml");
    private static final Path LOGBACK_XML_COPY_PATH = Paths.get("logback.xml.copy");
    private static final Path MINIMAL_EXISTING_LOGBACK_XML_PATH = Paths.get("src/test/resources/logback/minimalLogback.xml");

    private static MockedStatic<SwingUtils> swingUtilsMock;
    private static MockedStatic<LoggingConfigurator> logConf;
    private static MockedStatic<SystemUtils> sysUtilsMock;

    @BeforeAll
    static void beforeAll() throws IOException {
        // move existing logback.xml to safe place
        Files.move(ORIGINAL_LOGBACK_XML_PATH, LOGBACK_XML_COPY_PATH, StandardCopyOption.REPLACE_EXISTING);
    }

    @AfterAll
    static void afterAll() throws IOException {
        // restore copied xml and remove any remaining copy
        Files.move(LOGBACK_XML_COPY_PATH, ORIGINAL_LOGBACK_XML_PATH, StandardCopyOption.REPLACE_EXISTING);
        Files.deleteIfExists(LOGBACK_XML_COPY_PATH);
    }

    @BeforeEach
    void beforeEach() {
        swingUtilsMock = Mockito.mockStatic(SwingUtils.class);
        logConf = Mockito.mockStatic(LoggingConfigurator.class);
        sysUtilsMock = Mockito.mockStatic(SystemUtils.class);
    }

    @AfterEach
    void afterEach() {
        swingUtilsMock.close();
        logConf.close();
        sysUtilsMock.close();
    }

    @Test
    void shouldIssueAConfirmationDialogWhenNoConfigXMLFound() throws IOException {
        Files.deleteIfExists(Paths.get("logback.xml"));

        logConf.when(() -> LoggingConfigurator.issueConfirmationDialog(any())).thenCallRealMethod();
        logConf.when(LoggingConfigurator::configureLogger).thenCallRealMethod();

        // when
        LoggingConfigurator.configureLogger();
        // verify confirmation dialog shown
        logConf.verify(() -> LoggingConfigurator.issueConfirmationDialog(any()), times(1));


    }

    @Test
    void shouldIssueConfirmationDialogAndAccept() {
        logConf.when(() -> LoggingConfigurator.issueConfirmationDialog(any())).thenCallRealMethod();
        // setup YES as yesNoMsg dialog response
        swingUtilsMock.when(() -> SwingUtils.yesNoMsg(anyString())).thenReturn(JOptionPane.YES_OPTION);
        // do not really exit() during test on exit
        sysUtilsMock.when(() -> SystemUtils.systemExit(anyInt())).thenAnswer((Answer<Void>) invocation -> null);

        // when
        LoggingConfigurator.issueConfirmationDialog("dupa");
        // user clicked yes - no exit
        sysUtilsMock.verify(() -> SystemUtils.systemExit(anyInt()), never());

    }

    @Test
    void shouldIssueConfirmationDialogAndNotAccept() {
        logConf.when(() -> LoggingConfigurator.issueConfirmationDialog(any())).thenCallRealMethod();
        // setup NO as yesNoMsg dialog response
        swingUtilsMock.when(() -> SwingUtils.yesNoMsg(anyString())).thenReturn(JOptionPane.NO_OPTION);
        // do not really exit() during test on exit
        sysUtilsMock.when(() -> SystemUtils.systemExit(anyInt())).thenAnswer((Answer<Void>) invocation -> null);

        LoggingConfigurator.issueConfirmationDialog("dupa");
        // user clicked no - exit will be executed
        sysUtilsMock.verify(() -> SystemUtils.systemExit(anyInt()), times(1));
    }

    @Test
    void shouldIssueDialogFromJoranExceptionToo() throws IOException {
        // create 'empty' real config file - the joran exception will be thrown (parsing xml errror)
        Files.createFile(ORIGINAL_LOGBACK_XML_PATH);
        logConf.when(LoggingConfigurator::configureLogger).thenCallRealMethod();

        LoggingConfigurator.configureLogger();
        logConf.verify(() -> LoggingConfigurator.issueConfirmationDialog(contains("Problem parsing XML document")), times(1));

    }

    @Test
    void shouldNotIssueAConfirmationDialogWhenConfigXMLFound() throws IOException {

        // create minimal 'existing' config file
        Files.copy(MINIMAL_EXISTING_LOGBACK_XML_PATH, ORIGINAL_LOGBACK_XML_PATH, StandardCopyOption.REPLACE_EXISTING);
        logConf.when(LoggingConfigurator::configureLogger).thenCallRealMethod();

        LoggingConfigurator.configureLogger();
        // then
        logConf.verify(() -> LoggingConfigurator.issueConfirmationDialog(any()), never());



    }
}