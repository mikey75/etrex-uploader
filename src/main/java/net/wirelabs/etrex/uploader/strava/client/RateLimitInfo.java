package net.wirelabs.etrex.uploader.strava.client;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * Created 12/15/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Getter
public class RateLimitInfo {

    private int allowedDaily;
    private int allowedHourly;
    private int currentDaily;
    private int currentHourly;

    public RateLimitInfo(Map<String, List<String>> headers) {
        if (headers.containsKey("x-ratelimit-limit") && headers.containsKey("x-ratelimit-usage")) {
            String rateLimitAllowed = headers.get("x-ratelimit-limit").get(0);
            String rateLimitCurrent = headers.get("x-ratelimit-usage").get(0);

            StringTokenizer tokenizer = new StringTokenizer(rateLimitAllowed, ",");
            allowedHourly = Integer.parseInt(tokenizer.nextToken());
            allowedDaily = Integer.parseInt(tokenizer.nextToken());

            tokenizer = new StringTokenizer(rateLimitCurrent, ",");
            currentHourly = Integer.parseInt(tokenizer.nextToken());
            currentDaily = Integer.parseInt(tokenizer.nextToken());
        }
    }
}
