package net.wirelabs.etrex.uploader.strava.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/*
 * Created 12/10/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FormBuilder {

    private final StringBuilder formBodyBuilder = new StringBuilder();
    private final Map<String,String> formData = new HashMap<>();

    public static FormBuilder newBuilder() {
        return new FormBuilder();
    }

    public FormBuilder add(String key, String value) {
        formData.put(key,value);
        return this;
    }

    public String build() {

        for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
            if (formBodyBuilder.length() > 0) {
                formBodyBuilder.append("&");
            }
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
            formBodyBuilder.append("=");
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
        }
        return formBodyBuilder.toString();
    }

}
