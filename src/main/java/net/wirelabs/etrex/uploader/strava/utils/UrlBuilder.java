package net.wirelabs.etrex.uploader.strava.utils;

import lombok.NoArgsConstructor;

/**
 * Created 12/10/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor
public class UrlBuilder {

    private final StringBuilder url = new StringBuilder();
    private final StringBuilder baseUrl = new StringBuilder();

    public static UrlBuilder newBuilder() {
        return new UrlBuilder();
    }

    public UrlBuilder baseUrl(String url) {
        baseUrl.append(url);
        return this;
    }

    public UrlBuilder addQueryParam(String name, String value) {
        if (url.length() == 0) {
            url.append("?");
        } else {
            url.append("&");
        }
        url.append(name).append("=").append(value);
        return this;

    }

    public String build() {
        return baseUrl.append(url).toString();
    }

}
