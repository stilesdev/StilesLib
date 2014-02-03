package com.mstiles92.plugins.commonutils.calendar;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalendarUtils {
    private static int[] calendarConstants = new int[] { Calendar.YEAR, Calendar.MONTH, Calendar.WEEK_OF_YEAR, Calendar.DAY_OF_YEAR, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND };
    private static String[] calendarConstantNames = new String[] { "year", "month", "week", "day", "hour", "minute", "second" };

    /**
     * Create a calendar object for the current time plus the time specified by the input string.
     * Example of input string format: 1h30m22s = 1 hour, 30 minutes, and 22 seconds.
     *
     * @param input the string value to parse
     * @return a calendar object for now plus the input time
     */
    public static Calendar parseTimeDifference(String input) {
        Pattern pattern = Pattern.compile(
                "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" +
                "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" +
                "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" +
                "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" +
                "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" +
                "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" +
                "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);

        int[] units = new int[] { 0, 0, 0, 0, 0, 0, 0 };
        boolean match = false;
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            if (matcher.group() == null || matcher.group().isEmpty()) {
                continue;
            }

            for (int i = 0; i < matcher.groupCount(); i++) {
                if (matcher.group(i) != null && !matcher.group(i).isEmpty()) {
                    match = true;
                    break;
                }
            }

            if (match) {
                for (int i = 0; i < units.length; i++) {
                    String data = matcher.group(i + 1);
                    if (data != null && !data.isEmpty()) {
                        units[i] = Integer.parseInt(data);
                    }
                }

                break;
            }
        }

        if (!match) {
            return null;
        }

        Calendar calendar = new GregorianCalendar();
        for (int i = 0; i < units.length; i++) {
            calendar.add(calendarConstants[i], units[i]);
        }

        return calendar;
    }

    public static String buildTimeDifference(Calendar first, Calendar second) {
        if (first.equals(second)) {
            return "now";
        }

        StringBuilder s = new StringBuilder();

        for (int i = 0; i < calendarConstants.length; i++) {
            int difference = getDifference(calendarConstants[i], first, second);
            if (difference > 0) {
                s.append(difference).append(calendarConstantNames[i]).append((difference > 1) ? "s " : " ");
            }
        }

        if (s.length() == 0) {
            return "now";
        }

        return s.toString().trim();
    }

    private static int getDifference(int constant, Calendar first, Calendar second) {
        int difference = 0;
        Calendar temp = new GregorianCalendar();
        temp.setTimeInMillis(first.getTimeInMillis());

        while (!temp.after(second)) {
            temp.add(constant, 1);
            difference += 1;
        }

        return difference - 1;
    }
}
