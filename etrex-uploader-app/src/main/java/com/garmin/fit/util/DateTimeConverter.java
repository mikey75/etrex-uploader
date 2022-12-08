package com.garmin.fit.util;

import java.time.Instant;

public class DateTimeConverter {
    static final long FIT_EPOCH_MS = 631065600000L;

    public static String fitTimestampToISO8601(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp * 1000 + FIT_EPOCH_MS);

        return instant.toString();
    }

    public static String parseDateTime(String dateTime) {
        try {
            Instant instant = Instant.parse(dateTime).minusMillis(FIT_EPOCH_MS);
            return String.valueOf(instant.getEpochSecond());
        } catch (Exception e) {
            //no op
        }

        return dateTime;
    }
}
