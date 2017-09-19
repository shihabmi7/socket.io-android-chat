
package com.github.nkzawa.socketio.androidchat.utils;

import android.net.ParseException;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/***
 * Provides helper methods for date utilities.
 ***/
public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();

    /***
     * Converts ISO date string to UTC timezone equivalent.
     *
     * @param dateAndTime ISO formatted time string.
     ****/
    public static String getUtcTime(String dateAndTime) {
        Date d = parseDate(dateAndTime);

        String format = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

        // Convert Local Time to UTC
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return sdf.format(d);
    }

    /****
     * Parses date string and return a {@link Date} object
     *
     * @return The ISO formatted date object
     *****/
    public static Date parseDate(String date) {

        if (date == null) {
            return null;
        }

        StringBuffer sbDate = new StringBuffer();
        sbDate.append(date);
        String newDate = null;
        Date dateDT = null;

        try {
            newDate = sbDate.substring(0, 19).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String rDate = newDate.replace("T", " ");
        String nDate = rDate.replaceAll("-", "/");

        try {
            dateDT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).parse(nDate);
            // Log.v( TAG, "#parseDate dateDT: " + dateDT );
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateDT;
    }

    /***
     * Converts UTC time formatted as ISO to device local time.
     * <p/>
     * <br/>
     * <br/>
     * Sample usage
     * <p/>
     * <pre>
     *
     * {
     * 	SimpleDateFormat sdf = new SimpleDateFormat(&quot;yyyy-MM-dd'T'HH:mm:ss.SSS'Z'&quot;);
     * 	d = toLocalTime(&quot;2014-10-08T09:46:04.455Z&quot;, sdf);
     * }
     * </pre>
     *
     * @param utcDate
     * @param format
     * @return Date
     * @throws Exception
     */
    public static Date toLocalTime(String utcDate, SimpleDateFormat sdf) throws Exception {

        // create a new Date object using
        // the timezone of the specified city
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date localDate = sdf.parse(utcDate);

        sdf.setTimeZone(TimeZone.getDefault());
        String dateFormateInUTC = sdf.format(localDate);

        return sdf.parse(dateFormateInUTC);
    }

    /**
     * Returns abbreviated (3 letters) day of the week.
     *
     * @param date ISO format date
     * @return The name of the day of the week
     */
    public static String getDayOfWeekAbbreviated(String date) {
        Date dateDT = parseDate(date);

        if (dateDT == null) {
            return null;
        }

        // Get current date
        Calendar c = Calendar.getInstance();
        // it is very important to
        // set the date of
        // the calendar.
        c.setTime(dateDT);
        int day = c.get(Calendar.DAY_OF_WEEK);

        String dayStr = null;

        switch (day) {

            case Calendar.SUNDAY:
                dayStr = "Sun";
                break;

            case Calendar.MONDAY:
                dayStr = "Mon";
                break;

            case Calendar.TUESDAY:
                dayStr = "Tue";
                break;

            case Calendar.WEDNESDAY:
                dayStr = "Wed";
                break;

            case Calendar.THURSDAY:
                dayStr = "Thu";
                break;

            case Calendar.FRIDAY:
                dayStr = "Fri";
                break;

            case Calendar.SATURDAY:
                dayStr = "Sat";
                break;
        }

        return dayStr;
    }

    /***
     * Gets the name of the month from the given date.
     *
     * @param date ISO format date
     * @return Returns the name of the month
     ***/
    public static String getMonth(String date) {
        Date dateDT = parseDate(date);

        if (dateDT == null) {
            return null;
        }

        // Get current date
        Calendar c = Calendar.getInstance();
        // it is very important to
        // set the date of
        // the calendar.
        c.setTime(dateDT);
        int day = c.get(Calendar.MONTH);

        String dayStr = null;

        switch (day) {

            case Calendar.JANUARY:
                dayStr = "January";
                break;

            case Calendar.FEBRUARY:
                dayStr = "February";
                break;

            case Calendar.MARCH:
                dayStr = "March";
                break;

            case Calendar.APRIL:
                dayStr = "April";
                break;

            case Calendar.MAY:
                dayStr = "May";
                break;

            case Calendar.JUNE:
                dayStr = "June";
                break;

            case Calendar.JULY:
                dayStr = "July";
                break;

            case Calendar.AUGUST:
                dayStr = "August";
                break;

            case Calendar.SEPTEMBER:
                dayStr = "September";
                break;

            case Calendar.OCTOBER:
                dayStr = "October";
                break;

            case Calendar.NOVEMBER:
                dayStr = "November";
                break;

            case Calendar.DECEMBER:
                dayStr = "December";
                break;
        }

        return dayStr;
    }

    /**
     * Gets abbreviated name of the month from the given date.
     *
     * @param date ISO format date
     * @return Returns the name of the month
     */
    public static String getMonthAbbreviated(String date) {
        Date dateDT = parseDate(date);

        if (dateDT == null) {
            return null;
        }

        // Get current date
        Calendar c = Calendar.getInstance();
        // it is very important to
        // set the date of
        // the calendar.
        c.setTime(dateDT);
        int day = c.get(Calendar.MONTH);

        String dayStr = null;

        switch (day) {

            case Calendar.JANUARY:
                dayStr = "Jan";
                break;

            case Calendar.FEBRUARY:
                dayStr = "Feb";
                break;

            case Calendar.MARCH:
                dayStr = "Mar";
                break;

            case Calendar.APRIL:
                dayStr = "Apr";
                break;

            case Calendar.MAY:
                dayStr = "May";
                break;

            case Calendar.JUNE:
                dayStr = "Jun";
                break;

            case Calendar.JULY:
                dayStr = "Jul";
                break;

            case Calendar.AUGUST:
                dayStr = "Aug";
                break;

            case Calendar.SEPTEMBER:
                dayStr = "Sep";
                break;

            case Calendar.OCTOBER:
                dayStr = "Oct";
                break;

            case Calendar.NOVEMBER:
                dayStr = "Nov";
                break;

            case Calendar.DECEMBER:
                dayStr = "Dec";
                break;
        }

        return dayStr;
    }

    /**
     * Parse string date to formatted date object
     *
     * @param dateString
     * @param dateFormat
     * @return parseDate - Date object or null
     */
    public static Date parseDate(String dateString, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            Date parsedDate = sdf.parse(dateString);
            return parsedDate;
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Format simple date formatted object to string
     *
     * @param date
     * @param dateFormat
     * @return formatDate - Formatted date string
     */
    public static String formatSDF(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatDate = sdf.format(date).trim();
        return formatDate;
    }

    /**
     * Format date object to string
     *
     * @param date
     * @param dateFormat
     * @return formatDate - formatted date string
     */
    public static String formatDate(Date date, String dateFormat) {
        Format formatter = new SimpleDateFormat(dateFormat);
        String formatDate = formatter.format(date).trim();
        return formatDate;
    }

    public static long dateToMillisecond(String fromDateString) {
        SimpleDateFormat sdf;
        Date date = null;
        sdf = new SimpleDateFormat(Tools.YYYY_MM_DD_HH_MM_SS);
        try {
            date = sdf.parse(fromDateString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        long currentMillisec = date.getTime();
        return currentMillisec;

    }

    public static String dateFromDayDifference(String currentDate, long oneday) {
        SimpleDateFormat sdf;
        Date date = null;
        String finalDateString;
        sdf = new SimpleDateFormat(Tools.YYYY_MM_DD_HH_MM_SS);
        try {
            date = sdf.parse(currentDate);
            long currentMillisec = date.getTime();
            Date resultExpectDate = new Date((currentMillisec + oneday));
            String finalDate = sdf.format(resultExpectDate).toString().trim();
            date = sdf.parse(finalDate);
            finalDateString = formatDate(date, Tools.YYYY_MM_DD_HH_MM_SS);
            return finalDateString;
        } catch (Exception e) {
            return "NULL";
        }

    }

    static long getCurrentTime() {
        Date date = new Date();
        return date.getTime();
    }

    static class Tools {

        static String YYYY_MM_DD = "yyyy/MM/dd";
        static String YYYY_MM_DD_HH_MM_SS = "yyyy.MM.dd HH:mm:ss a";

    }


}
