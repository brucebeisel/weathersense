package com.bdb.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Bruce
 *
 */
/**
 * @author Bruce
 *
 */
public class TimeUtils {
    private TimeUtils() {
    }

    /**
     * Change the time of the provided calendar to midnight. The year, month and date are not changed.
     *
     * @param c The calendar to change
     */
    @Deprecated
    public static void setToMidnight(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Change the date and time to midnight on the first day of the month. The month and year are not changed.
     *
     * @param c The Calendar to change
     */
    @Deprecated
    public static void setToBeginningOfMonth(Calendar c) {
        setToMidnight(c);
        c.set(Calendar.DAY_OF_MONTH, 1);
    }

    /**
     * Set the time of the provided calendar to the end of the day. The year, month and date are not changed.
     *
     * @param c The Calendar to change
     */
    @Deprecated
    public static void setToEndOfDay(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
    }

    @Deprecated
    public static void setToEndOfMonth(Calendar c) {
        setToBeginningOfMonth(c);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DAY_OF_MONTH, -1);
        setToEndOfDay(c);
    }

    /**
     * Check to see if the two calendars are in the same day.
     *
     * @param first The first calendar that will be compared
     * @param second The second calendar that will be compared
     *
     * @return true if both calendars are in the same day.
     */
    @Deprecated
    public static boolean isSameDay(Calendar first, Calendar second) {
        return first.get(Calendar.DAY_OF_MONTH) == second.get(Calendar.DAY_OF_MONTH)
                && first.get(Calendar.MONTH) == second.get(Calendar.MONTH)
                && first.get(Calendar.YEAR) == second.get(Calendar.YEAR);
    }

    /**
     * Convert a temporal accessor to an Instant.
     * 
     * @param temporal The temporal to convert
     * 
     * @return The Instant
     */
    public static Instant temporalToInstant(TemporalAccessor temporal) {
        //
        // LocalDateTime, LocalDate and LocalTime must have a time zone conversion before being converted to an Instant
        //
        Instant instant;
        if (temporal instanceof LocalDateTime)
            instant = ((LocalDateTime)temporal).atZone(ZoneId.systemDefault()).toInstant();
        else if (temporal instanceof LocalDate)
            instant = ((LocalDate)temporal).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        else if (temporal instanceof LocalTime)
            instant = ((LocalTime)temporal).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
        else
            instant = Instant.from(temporal); // throws DateTimeException

        return instant;
    }

    /**
     * Convert a local date and time to a java.util.date class.
     * 
     * @param temporal The temporal to convert
     * 
     * @return The converted date
     */
    public static Date localDateTimeToDate(TemporalAccessor temporal) {
        return Date.from(temporalToInstant(temporal));
    }

    /**
     * Convert a TemporalAccess to an Epoch value.
     * 
     * @param temporal
     * @return The Epoch time
     */
    public static long localDateTimeToEpochMillis(TemporalAccessor temporal) {
        return temporalToInstant(temporal).toEpochMilli();
    }
}
