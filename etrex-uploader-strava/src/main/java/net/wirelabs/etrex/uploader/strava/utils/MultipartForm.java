package net.wirelabs.etrex.uploader.strava.utils;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MultipartForm {
    public static final String QUOTE = "\"";
    public static final String CRLF = "\r\n";
    public static final String DOUBLE_CRLF = "\r\n\r\n";

    private final List<byte[]> formPartArrays = new ArrayList<>();

    @Getter
    private final String boundary = UUID.randomUUID().toString();
    public static MultipartForm newBuilder() {
        return new MultipartForm();
    }

    public MultipartForm addPart(Object key, Object value) {
        if (value instanceof Path) {
            addFilePart(key, value);
        } else {
            formPartArrays.add(("--" + boundary + CRLF + "Content-Disposition: form-data; name=" + QUOTE + key + QUOTE + DOUBLE_CRLF + value + CRLF).getBytes(StandardCharsets.UTF_8));
        }
        return this;
    }

    private void addFilePart(Object key, Object value) {
        try {
            Path path = (Path) value;
            byte[] fileContents = Files.readAllBytes(path);
            String mimeType = guessContentTypeFromFile(path.toFile());
            formPartArrays.add(("--" + boundary + CRLF + "Content-Disposition: form-data; name=" + QUOTE + key + QUOTE + "; filename=" + QUOTE + path.getFileName() + QUOTE + CRLF + "Content-Type: " + mimeType + DOUBLE_CRLF).getBytes(StandardCharsets.UTF_8));
            formPartArrays.add(fileContents);
            formPartArrays.add(CRLF.getBytes(StandardCharsets.UTF_8));
            
        } catch (IOException e) {
            log.warn("Could not attach file {}. This form part will be empty", e.getMessage());
        }
    }

    public List<byte[]> getBody() {

        formPartArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
        return formPartArrays; 
    }

    public String guessContentTypeFromFile(File file) {
        String contentType = URLConnection.guessContentTypeFromName(file.getName());
        return Objects.requireNonNullElse(contentType, "application/octet-stream");
    }
}
