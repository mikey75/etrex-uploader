package net.wirelabs.etrex.uploader.strava.utils;

import static net.wirelabs.etrex.uploader.strava.utils.MultipartForm.CRLF;
import static net.wirelabs.etrex.uploader.strava.utils.MultipartForm.DOUBLE_CRLF;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class MultipartFormBodyBuilderTest {

    private static MultipartForm builder;
    private final StringBuilder stringBuilder = new StringBuilder();

    @Test
    void builderTest() throws IOException {

    
        File pngFile = new File("src/test/resources/test.png");
        File binaryFile = new File("src/test/resources/test.bin");
        File nonexistent = new File("src/test/resources/nonexistent.bin");
        
        builder = MultipartForm.newBuilder()
                .addPart("param1", Paths.get(binaryFile.getAbsolutePath()))
                .addPart("param2", Paths.get(pngFile.getAbsolutePath()))
                .addPart("param3", "value3")
                .addPart("param4", "Zażółć gęślą jaźń")
                .addPart("nonexistent", Paths.get(nonexistent.getAbsolutePath()));

        builder.getBody().forEach(f -> stringBuilder.append(new String(f)));


        String result = stringBuilder.toString();
        String PART_SEPARATOR = "--" + builder.getBoundary();
        String END_OF_FORM = "--" + builder.getBoundary() + "--";

        assertThat(result).contains(PART_SEPARATOR + CRLF + "Content-Disposition: form-data; name=\"param1\"; filename=\"test.bin\"" + CRLF +
                        "Content-Type: application/octet-stream" + DOUBLE_CRLF + fileContent(binaryFile))
                .contains(PART_SEPARATOR + CRLF + "Content-Disposition: form-data; name=\"param2\"; filename=\"test.png\"" + CRLF +
                        "Content-Type: image/png" + DOUBLE_CRLF + fileContent(pngFile))
                .contains(PART_SEPARATOR + CRLF + "Content-Disposition: form-data; name=\"param3\"" + DOUBLE_CRLF + "value3")
                .contains(PART_SEPARATOR + CRLF + "Content-Disposition: form-data; name=\"param4\"" + DOUBLE_CRLF + "Zażółć gęślą jaźń")
                .contains(END_OF_FORM);
        
        assertThat(result).doesNotContain(PART_SEPARATOR + CRLF + "Content-Disposition: form-data; name=\"nonexistent\"; filename=\"nonexistent.bin\"");


    }

    private String fileContent(File f) throws IOException {
        byte[] fileContent = Files.readAllBytes(f.toPath());
        return new String(fileContent, StandardCharsets.UTF_8);
    }
}
    
    