/*
 * This document is a part of the source code and related artifacts for CommonUtils, an open source library that
 * provides a set of commonly-used functions for Bukkit plugins.
 *
 * http://github.com/mstiles92/CommonUtils
 *
 * Copyright (c) 2014 Matthew Stiles (mstiles92)
 *
 * Licensed under the Common Development and Distribution License Version 1.0
 * You may not use this file except in compliance with this License.
 *
 * You may obtain a copy of the CDDL-1.0 License at http://opensource.org/licenses/CDDL-1.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the license.
 */

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

    /**
     * Build up a String representing the difference in time between two Calendar objects.
     *
     * @param first the first Calendar object
     * @param second the second Calendar object
     * @return the time difference between the two Calendar objects
     */
    public static String buildTimeDifference(Calendar first, Calendar second) {
        Calendar firstCopy = new GregorianCalendar();
        firstCopy.setTimeInMillis(first.getTimeInMillis());

        if (firstCopy.equals(second)) {
            return "now";
        }

        StringBuilder s = new StringBuilder();

        for (int i = 0; i < calendarConstants.length; i++) {
            int difference = getDifference(calendarConstants[i], firstCopy, second);
            if (difference > 0) {
                s.append(difference).append(" ").append(calendarConstantNames[i]).append((difference > 1) ? "s " : " ");
                firstCopy.add(calendarConstants[i], difference);
            }
        }

        if (s.length() == 0) {
            return "now";
        }

        return s.toString().trim();
    }

    /**
     * Get the difference in time between two calendars for an individual time unit.
     *
     * @param constant the constant representing the time unit to compare
     * @param first the first Calendar object
     * @param second the second Calendar object
     * @return the number of the specified time units between the first and second Calendar objects
     */
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
