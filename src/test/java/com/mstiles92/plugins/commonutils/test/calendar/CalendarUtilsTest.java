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

package com.mstiles92.plugins.commonutils.test.calendar;

import com.mstiles92.plugins.commonutils.calendar.CalendarUtils;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarUtilsTest {
    private Calendar c1 = new GregorianCalendar();
    private Calendar c2 = new GregorianCalendar();
    private static final int tolerance = 10; // milliseconds allowed for computation time

    @Test
    public void testParseTimeDifference() {
        // test 1 second difference
        c1.setTimeInMillis(System.currentTimeMillis());
        c2 = CalendarUtils.parseTimeDifference("1s");
        assertTrue(c2.getTimeInMillis() - c1.getTimeInMillis() - (1000 * 1) < tolerance);

        // test 5 second difference
        c1.setTimeInMillis(System.currentTimeMillis());
        c2 = CalendarUtils.parseTimeDifference("5s");
        assertTrue(c2.getTimeInMillis() - c1.getTimeInMillis() - (1000 * 5) < tolerance);

        // test 5 minute 30 second difference
        c1.setTimeInMillis(System.currentTimeMillis());
        c2 = CalendarUtils.parseTimeDifference("5m30s");
        assertTrue(c2.getTimeInMillis() - c1.getTimeInMillis() - (1000 * 60 * 5) - (1000 * 30) < tolerance);
    }

    @Test
    public void testBuildTimeDifference() {
        // test equal values
        c2.setTimeInMillis(c1.getTimeInMillis());
        assertEquals("now", CalendarUtils.buildTimeDifference(c1, c2));

        // test 1 second difference
        c2.setTimeInMillis(c1.getTimeInMillis() + 1000 * 1);
        assertEquals("1 second", CalendarUtils.buildTimeDifference(c1, c2));

        // test 5 second difference
        c2.setTimeInMillis(c1.getTimeInMillis() + 1000 * 5);
        assertEquals("5 seconds", CalendarUtils.buildTimeDifference(c1, c2));

        // test 5 minute 30 second difference
        c2.setTimeInMillis(c1.getTimeInMillis() + (1000 * 60 * 5) + (1000 * 30));
        assertEquals("5 minutes 30 seconds", CalendarUtils.buildTimeDifference(c1, c2));

        // test 1 day 12 hours 15 minutes 1 second difference
        c2.setTimeInMillis(c1.getTimeInMillis() + (1000 * 60 * 60 * 24 * 1) + (1000 * 60 * 60 * 12) + (1000 * 60 * 15) + (1000 * 1));
        assertEquals("1 day 12 hours 15 minutes 1 second", CalendarUtils.buildTimeDifference(c1, c2));
    }
}
