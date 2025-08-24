package net.wirelabs.etrex.uploader.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlBuilder {

    private final StringBuilder finalUrl = new StringBuilder();
    private boolean utf8encode = true;
    private int paramCount = 0;

    public static UrlBuilder create() {
        return new UrlBuilder();
    }

    public UrlBuilder doNotEncodeParams() {
        this.utf8encode = false;
        return this;
    }

    /**
     * Parse baseUrl
     *
     * @param url baseUrl
     * @return self instance for chaining
     */
    public UrlBuilder parse(String url) {
        // reset buffer and count at the beginning of the url creation
        finalUrl.setLength(0);
        paramCount = 0;
        finalUrl.append(url);
        return this;
    }

    /**
     * Adds query param
     *
     * @param param parameter
     * @param value value
     * @return self instance for chaining
     */
    public UrlBuilder addQueryParam(String param, String value) {
        if (paramCount == 0) {
            // first
            finalUrl.append("?").append(!utf8encode ? param : URLEncoder.encode(param, StandardCharsets.UTF_8))
                    .append("=").append(!utf8encode ? value : URLEncoder.encode(value, StandardCharsets.UTF_8));
        } else {
            // following first
            finalUrl.append("&").append(!utf8encode ? param : URLEncoder.encode(param, StandardCharsets.UTF_8))
                    .append("=").append(!utf8encode ? value : URLEncoder.encode(value, StandardCharsets.UTF_8));
        }
        paramCount++;
        return this;
    }

    /**
     * returns String value of the generated URL
     */
    public String build() {
        // reset param count at the end
        paramCount = 0;
        return finalUrl.toString();
    }

}
