package net.wirelabs.etrex.uploader.utils;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.io.IOException;
import java.nio.file.*;

import static org.mockito.Mockito.*;


class LoggingConfiguratorTest {

    private static final Path ORIGINAL_LOGBACK_XML_PATH = Paths.get("logback.xml");
    private static final Path LOGBACK_XML_COPY_PATH = Paths.get("logback.xml.copy");
    private static final Path MINIMAL_EXISTING_LOGBACK_XML_PATH = Paths.get("src/test/resources/logback/minimalLogback.xml");

    private static MockedStatic<SwingUtils> swingUtilsMock;
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
    }

    @AfterEach
    void afterEach() {
        swingUtilsMock.close();
    }

    @Test
    void shouldIssueAConfirmationDialogWhenNoConfigXMLFound() throws IOException {
        Files.deleteIfExists(Paths.get("logback.xml"));
        // when
        LoggingConfigurator.configureLogger();
        // verify confirmation dialog shown
        swingUtilsMock.verify(() -> SwingUtils.issueConfirmationWithExitDialog(any()), times(1));


    }


    @Test
    void shouldIssueDialogFromJoranExceptionToo() throws IOException {
        // create 'empty' real config file - the joran exception will be thrown (parsing xml error)
        Files.createFile(ORIGINAL_LOGBACK_XML_PATH);

        LoggingConfigurator.configureLogger();
        swingUtilsMock.verify(() -> SwingUtils.issueConfirmationWithExitDialog(contains("Problem parsing XML document")), times(1));

    }

    @Test
    void shouldNotIssueAConfirmationDialogWhenConfigXMLFound() throws IOException {

        // create minimal 'existing' config file
        Files.copy(MINIMAL_EXISTING_LOGBACK_XML_PATH, ORIGINAL_LOGBACK_XML_PATH, StandardCopyOption.REPLACE_EXISTING);

        LoggingConfigurator.configureLogger();
        // then
        swingUtilsMock.verify(() -> SwingUtils.issueConfirmationWithExitDialog(any()), never());

    }
}