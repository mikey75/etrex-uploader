package net.wirelabs.etrex.uploader.common.configuration;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.linesOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

class SortedPropertiesTest extends BaseTest {

    private static final Path path = Paths.get("target/test-config-alpha.config");

    @BeforeEach
    void before() throws IOException {
        if (path.toFile().exists()) {
            Files.delete(path);
        }
    }

    @Test
    void shouldSortPropertiesAndWriteToFile() throws IOException {

        // given
        OutputStream os = Files.newOutputStream(path);

        // when -> create and save the props to file
        Properties properties = new SortedProperties();
        properties.setProperty("x","zaxon");
        properties.setProperty("a", "budda");
        properties.setProperty("g", "africa");
        properties.store(os, "");

        // then -> check resulting stored file
        assertThat(linesOf(path)).containsExactly("a=budda", "g=africa", "x=zaxon");
    }

    @Test
    void shouldLogException() throws IOException {

        // given
        OutputStream os = Mockito.mock(OutputStream.class);
        doThrow(new IOException("Mock I/O exception")).when(os).write(any(byte[].class));
        // when
        Properties properties = new SortedProperties();
        properties.setProperty("a","k");
        properties.store(os,"");
        // then
        verifyLogged("Writing properties file failed: Mock I/O exception");
    }
}