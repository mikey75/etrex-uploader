package net.wirelabs.etrex.uploader.utils;

import net.wirelabs.etrex.uploader.utils.DateAndUnitConversionUtil;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created 11/1/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class DateAndUnitConversionUtilTest {

    @Test
    void getElapsedTimeFromSeconds() {
        String s = DateAndUnitConversionUtil.secondsToTimeAsString(1200); // 20 minutes
        assertThat(s).isEqualTo("00:20:00");
        s = DateAndUnitConversionUtil.secondsToTimeAsString(1200+20); // 20 minutes 20 seconds
        assertThat(s).isEqualTo("00:20:20");
        s = DateAndUnitConversionUtil.secondsToTimeAsString(1200+3600+20); // 1hour 20 minutes 20 seconds
        assertThat(s).isEqualTo("01:20:20");

        // corner cases
        s = DateAndUnitConversionUtil.secondsToTimeAsString(0); //0 hour 0 minutes 0 seconds
        assertThat(s).isEqualTo("00:00:00");
        s = DateAndUnitConversionUtil.secondsToTimeAsString(82800+3540+59); // 23 59 59
        assertThat(s).isEqualTo("23:59:59");
        s = DateAndUnitConversionUtil.secondsToTimeAsString(82800+3540+59+1); // 24 00 00
        assertThat(s).isEqualTo("24:00:00");
    }

    @Test
    void getDateTimeFromZoneDateTime() {
        OffsetDateTime testDate = OffsetDateTime.parse("2022-07-03T11:37:26Z");
        String s = DateAndUnitConversionUtil.offsetDateTimeToLocalAsString(testDate);
        assertThat(s).isEqualTo("2022-07-03 11:37");
    }

    @Test
    void getKilometersPerHourFromMetersPerSecond() {
        String kmh = DateAndUnitConversionUtil.metersPerSecToKilometersPerHourAsString(200);
        assertThat(kmh).matches("720[.,]00");

        kmh = DateAndUnitConversionUtil.metersPerSecToKilometersPerHourAsString(-120);
        assertThat(kmh).matches("-432[.,]00");
    }

    @Test
    void convertSecondsToHoursAsString() {
        String hours = DateAndUnitConversionUtil.secondsToHoursAsString(3600+3600);
        assertThat(hours).isEqualTo("02");
        hours = DateAndUnitConversionUtil.secondsToHoursAsString(3600);
        assertThat(hours).isEqualTo("01");
        hours = DateAndUnitConversionUtil.secondsToHoursAsString(60); // this is zero hours ;)
        assertThat(hours).isEqualTo("00");

    }
}