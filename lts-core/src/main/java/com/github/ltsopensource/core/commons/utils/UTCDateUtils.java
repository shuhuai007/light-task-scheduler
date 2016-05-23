package com.github.ltsopensource.core.commons.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date utility classes to parse and format datetimes in Oozie expected datetime formats.
 */
public class UTCDateUtils {

    private static final Pattern GMT_OFFSET_COLON_PATTERN = Pattern.compile("^GMT(\\-|\\+)(\\d{2})(\\d{2})$");

    public static final TimeZone UTC = getTimeZone("UTC");

    public static final String ISO8601_UTC_MASK = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String ISO8601_TZ_MASK_WITHOUT_OFFSET = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    private static String ACTIVE_MASK = ISO8601_UTC_MASK;
    private static TimeZone ACTIVE_TIMEZONE = UTC;

    public static final String OOZIE_PROCESSING_TIMEZONE_KEY = "oozie.processing.timezone";

    public static final String OOZIE_PROCESSING_TIMEZONE_DEFAULT = "UTC";

    private static boolean OOZIE_IN_UTC = true;

    private static final Pattern VALID_TIMEZONE_PATTERN = Pattern.compile("^UTC$|^GMT(\\+|\\-)\\d{4}$");

    /**
     * Configures the Datetime parsing with Oozie processing timezone.
     */
    public static void setConf() {
        String tz = "UTC";
        if (!VALID_TIMEZONE_PATTERN.matcher(tz).matches()) {
            throw new RuntimeException("Invalid Oozie timezone, it must be 'UTC' or 'GMT(+/-)####");
        }
        ACTIVE_TIMEZONE =  TimeZone.getTimeZone(tz);;
        OOZIE_IN_UTC = ACTIVE_TIMEZONE.equals(UTC);
        ACTIVE_MASK = (OOZIE_IN_UTC) ? ISO8601_UTC_MASK : ISO8601_TZ_MASK_WITHOUT_OFFSET + tz.substring(3);
    }

    /**
     * Returns Oozie processing timezone.
     *
     * @return Oozie processing timezone. The returned timezone is <code>UTC</code> or a <code>GMT(+/-)####</code>
     * timezone.
     */
    public static TimeZone getOozieProcessingTimeZone() {
        return ACTIVE_TIMEZONE;
    }

    /**
     * Returns Oozie processing datetime mask.
     * <p>
     * This mask is an ISO8601 datetime mask for the Oozie processing timezone.
     *
     * @return  Oozie processing datetime mask.
     */
    public static String getOozieTimeMask() {
        return ACTIVE_MASK;
    }

    private static DateFormat getISO8601DateFormat(TimeZone tz, String mask) {
        DateFormat dateFormat = new SimpleDateFormat(mask);
        // Stricter parsing to prevent dates such as 2011-12-50T01:00Z (December 50th) from matching
        dateFormat.setLenient(false);
        dateFormat.setTimeZone(tz);
        return dateFormat;
    }

    private static DateFormat getSpecificDateFormat(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(ACTIVE_TIMEZONE);
        return dateFormat;
    }

    /**
     * {@link TimeZone#getTimeZone(String)} takes the timezone ID as an argument; for invalid IDs it returns the
     * <code>GMT</code> TimeZone.  A timezone ID formatted like <code>GMT-####</code> is not a valid ID, however, it will actually
     * map this to the <code>GMT-##:##</code> TimeZone, instead of returning the <code>GMT</code> TimeZone.  We check (later)
     * check that a timezone ID is valid by calling {@link TimeZone#getTimeZone(String)} and seeing if the returned
     * TimeZone ID is equal to the original; because we want to allow <code>GMT-####</code>, while still disallowing actual
     * invalid IDs, we have to manually replace <code>GMT-####</code> with <code>GMT-##:##</code> first.
     *
     * @param tzId The timezone ID
     * @return If tzId matches <code>GMT-####</code>, then we return <code>GMT-##:##</code>; otherwise, we return tzId unaltered
     */
    private static String handleGMTOffsetTZNames(String tzId) {
        Matcher m = GMT_OFFSET_COLON_PATTERN.matcher(tzId);
        if (m.matches() && m.groupCount() == 3) {
            tzId = "GMT" + m.group(1) + m.group(2) + ":" + m.group(3);
        }
        return tzId;
    }

    /**
     * Returns the {@link TimeZone} for the given timezone ID.
     *
     * @param tzId timezone ID.
     * @return  the {@link TimeZone} for the given timezone ID.
     */
    public static TimeZone getTimeZone(String tzId) {
        if (tzId == null) {
            throw new IllegalArgumentException("Invalid TimeZone: " + tzId);
        }
        tzId = handleGMTOffsetTZNames(tzId);    // account for GMT-####
        TimeZone tz = TimeZone.getTimeZone(tzId);
        // If these are not equal, it means that the tzId is not valid (invalid tzId's return GMT)
        if (!tz.getID().equals(tzId)) {
            throw new IllegalArgumentException("Invalid TimeZone: " + tzId);
        }
        return tz;
    }

    /**
     * Parses a datetime in ISO8601 format in UTC timezone
     *
     * @param s string with the datetime to parse.
     * @return the corresponding {@link Date} instance for the parsed date.
     * @throws ParseException thrown if the given string was not an ISO8601 UTC value.
     */
    public static Date parseDateUTC(String s) throws ParseException {
        return getISO8601DateFormat(UTC, ISO8601_UTC_MASK).parse(s);
    }

    /**
     * Parses a datetime in ISO8601 format in the Oozie processing timezone.
     *
     * @param s string with the datetime to parse.
     * @return the corresponding {@link Date} instance for the parsed date.
     * @throws ParseException thrown if the given string was not an ISO8601 value for the Oozie processing timezon.
     */
    public static Date parseDateOozieTZ(String s) throws ParseException {
        s = s.trim();
        ParsePosition pos = new ParsePosition(0);
        Date d = getISO8601DateFormat(ACTIVE_TIMEZONE, ACTIVE_MASK).parse(s, pos);
        if (d == null) {
            throw new ParseException("Could not parse [" + s + "] using [" + ACTIVE_MASK + "] mask",
                                     pos.getErrorIndex());
        }
        if (d != null && s.length() > pos.getIndex()) {
            throw new ParseException("Correct datetime string is followed by invalid characters: " + s, pos.getIndex());
        }
        return d;
    }

    /**
     * Formats a {@link Date} as a string in ISO8601 format using Oozie processing timezone.
     *
     * @param d {@link Date} to format.
     * @return the ISO8601 string for the given date, <code>NULL</code> if the {@link Date} instance was
     * <code>NULL</code>
     */
    public static String formatDateOozieTZ(Date d) {
        return (d != null) ? getISO8601DateFormat(ACTIVE_TIMEZONE, ACTIVE_MASK).format(d) : "NULL";
    }

    /**
     * Formats a {@link Date} as a string using the specified format mask.
     * <p>
     * The format mask must be a {@link SimpleDateFormat} valid format mask.
     *
     * @param d {@link Date} to format.
     * @return the string for the given date using the specified format mask,
     * <code>NULL</code> if the {@link Date} instance was <code>NULL</code>
     */
    public static String formatDateCustom(Date d, String format) {
        return (d != null) ? getSpecificDateFormat(format).format(d) : "NULL";
    }

    /**
     * Formats a {@link Calendar} as a string in ISO8601 format using Oozie processing timezone.
     *
     * @param c {@link Calendar} to format.
     * @return the ISO8601 string for the given date, <code>NULL</code> if the {@link Calendar} instance was
     * <code>NULL</code>
     */
    public static String formatDateOozieTZ(Calendar c) {
        return (c != null) ? formatDateOozieTZ(c.getTime()) : "NULL";
    }

    /**
     * Formats a {@link Calendar} as a string in ISO8601 format without adjusting its timezone.  However, the mask will still
     * ensure that the returned date is in the Oozie processing timezone.
     *
     * @param c {@link Calendar} to format.
     * @return the ISO8601 string for the given date, <code>NULL</code> if the {@link Calendar} instance was
     * <code>NULL</code>
     */
    public static String formatDate(Calendar c) {
        return (c != null) ? getISO8601DateFormat(c.getTimeZone(), ACTIVE_MASK).format(c.getTime()) : "NULL";
    }

    /**
     * This function returns number of hour in a day when given a Calendar with appropriate TZ. It consider DST to find
     * the number of hours. Generally it is 24. At some tZ, in one day of a year it is 23 and another day it is 25
     *
     * @param cal: The date for which the number of hours is requested
     * @return number of hour in that day.
     */
    public static int hoursInDay(Calendar cal) {
        Calendar localCal = new GregorianCalendar(cal.getTimeZone());
        localCal.set(Calendar.MILLISECOND, 0);
        localCal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 30, 0);
        localCal.add(Calendar.HOUR_OF_DAY, 24);
        switch (localCal.get(Calendar.HOUR_OF_DAY)) {
            case 1:
                return 23;
            case 23:
                return 25;
            default: // Case 0
                return 24;
        }
    }

    /**
     * Determine whether a specific date is on DST change day
     *
     * @param cal: Date to know if it is DST change day. Appropriate TZ is specified
     * @return true , if it DST change date otherwise false
     */
    public static boolean isDSTChangeDay(Calendar cal) {
        return hoursInDay(cal) != 24;
    }

    /**
     * Create a Calendar instance using the specified date and Time zone
     * @param dateString
     * @param tz : TimeZone
     * @return appropriate Calendar object
     * @throws Exception
     */
    public static Calendar getCalendar(String dateString, TimeZone tz) throws Exception {
        Date date = UTCDateUtils.parseDateOozieTZ(dateString);
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);
        calDate.setTimeZone(tz);
        return calDate;
    }



    /**
     * Create a Calendar instance for UTC time zone using the specified date.
     * @param dateString
     * @return appropriate Calendar object
     * @throws Exception
     */
    public static Calendar getCalendar(String dateString) throws Exception {
        return getCalendar(dateString, ACTIVE_TIMEZONE);
    }

    /**
     * Convert java.sql.Timestamp to java.util.Date
     *
     * @param timestamp java.sql.Timestamp
     * @return java.util.Date
     */
    public static Date toDate(Timestamp timestamp) {
        if (timestamp != null) {
            long milliseconds = timestamp.getTime();
            return new Date(milliseconds);
        }
        return null;
    }

    /**
     * Convert java.util.Date to java.sql.Timestamp
     *
     * @param d java.util.Date
     * @return java.sql.Timestamp
     */
    public static Timestamp convertDateToTimestamp(Date d) {
        if (d != null) {
            return new Timestamp(d.getTime());
        }
        return null;
    }

}
