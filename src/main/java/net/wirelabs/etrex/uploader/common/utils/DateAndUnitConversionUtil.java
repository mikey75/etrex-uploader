package net.wirelabs.etrex.uploader.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateAndUnitConversionUtil {

    public static String secondsToTimeAsString(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String secondsToHoursAsString(int totalSecs) {
        int hours = totalSecs / 3600;
        return String.format("%02d", hours);
    }


    public static String metersPerSecToKilometersPerHourAsString(double metersPerSecond) {
        DecimalFormat df = new DecimalFormat("####0.00");
        double kmh =  (3.6 * metersPerSecond);
        return df.format(kmh);
    }

    public static String offsetDateTimeToLocalAsString(OffsetDateTime startDateLocal) {
        LocalDateTime dt = startDateLocal.toLocalDateTime();
        return String.format("%04d-%02d-%02d %02d:%02d", dt.getYear(),dt.getMonthValue(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute());

    }
}
