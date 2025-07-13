package net.wirelabs.etrex.uploader.strava.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtils {

    public static Map<String, String> parseQueryParams(String input) {
        Map<String, String> result = new HashMap<>();
        if (input == null || input.isEmpty()) return result;

        for (String pair : input.split("&")) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                result.put(key, value);
            }
        }
        return result;
    }

    public static Map<String, String> parseMultipartFormData(String contentType, String body) {

        String[] parts = getParts(contentType, body);
        List<String> validParts = Arrays.stream(parts).filter(p -> !p.trim().isEmpty()).filter(p -> !p.equals("--")).toList();
        return extractFormData(validParts);
    }

    private static Map<String, String> extractFormData(List<String> validParts) {
        Map<String, String> form = new HashMap<>();
        for (String part : validParts) {

            String[] sections = part.split("\r\n\r\n", 2);
            if (sections.length < 2) continue;

            String name = getName(sections[0]);
            String value = sections[1].trim();

            if (name != null) {
                form.put(name, value);
            }
        }
        return form;
    }

    private static String getName(String section) {

        String name = null;
        for (String line : section.split("\r\n")) {
            if (line.startsWith("Content-Disposition")) {
                for (String element : line.split(";")) {
                    element = element.trim();
                    if (element.startsWith("name=")) {
                        name = element.substring(6).replace("\"", "");
                    }
                }
            }
        }
        return name;
    }

    private static String[] getParts(String contentType, String body) {
        String boundary = contentType.split("boundary=")[1];
        return body.split("--" + boundary);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ContentTypes {
        public static final String HTML = "text/html";
        public static final String JSON = "application/json";
        public static final String FORM = "application/x-www-form-urlencoded";
        public static final String MULTIPART_FORM = "multipart/form-data";
    }
}
