/*
 * Copyright (C) 2014 The Android Open Source Project
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.util.calendar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <code>CalendarSystem</code> is an abstract class that defines the
 * programming interface to deal with calendar date and time.
 *
 * <p><code>CalendarSystem</code> instances are singletons. For
 * example, there exists only one Gregorian calendar instance in the
 * Java runtime environment. A singleton instance can be obtained
 * calling one of the static factory methods.
 *
 * <h4>CalendarDate</h4>
 *
 * <p>For the methods in a <code>CalendarSystem</code> that manipulate
 * a <code>CalendarDate</code>, <code>CalendarDate</code>s that have
 * been created by the <code>CalendarSystem</code> must be
 * specified. Otherwise, the methods throw an exception. This is
 * because, for example, a Chinese calendar date can't be understood
 * by the Hebrew calendar system.
 *
 * <h4>Calendar names</h4>
 *
 * Each calendar system has a unique name to be identified. The Java
 * runtime in this release supports the following calendar systems.
 *
 * <pre>
 *  Name          Calendar System
 *  ---------------------------------------
 *  gregorian     Gregorian Calendar
 *  julian        Julian Calendar
 *  japanese      Japanese Imperial Calendar
 * </pre>
 *
 * @see CalendarDate
 * @author Masayoshi Okutsu
 * @since 1.5
 */

public abstract class CalendarSystem {

    /////////////////////// Calendar Factory Methods /////////////////////////

    // Map of calendar names and calendar classes;
    private static final Map<String, Class<?>> names;

    // Android changed : Don't use reflection for Class.forName every time.

    static {
        names = new HashMap<>();
        names.put("gregorian", Gregorian.class);
        names.put("japanese", LocalGregorianCalendar.class);
        names.put("julian", JulianCalendar.class);
        // names.put("hebrew", "HebrewCalendar");
        // names.put("iso8601", "ISOCalendar");
        // names.put("taiwanese", "LocalGregorianCalendar");
        // names.put("thaibuddhist", "LocalGregorianCalendar");
    }

    // Map of calendar names and CalendarSystem instances
    private static final ConcurrentMap<String, CalendarSystem> calendars =
            new ConcurrentHashMap<>();

    private final static Gregorian GREGORIAN_INSTANCE = new Gregorian();

    /**
     * Returns the singleton instance of the <code>Gregorian</code>
     * calendar system.
     *
     * @return the <code>Gregorian</code> instance
     */
    public static Gregorian getGregorianCalendar() {
        return GREGORIAN_INSTANCE;
    }

    /**
     * Returns a <code>CalendarSystem</code> specified by the calendar
     * name. The calendar name has to be one of the supported calendar
     * names.
     *
     * @param calendarName the calendar name
     * @return the <code>CalendarSystem</code> specified by
     * <code>calendarName</code>, or null if there is no
     * <code>CalendarSystem</code> associated with the given calendar name.
     */
    public static CalendarSystem forName(String calendarName) {
        if ("gregorian".equals(calendarName)) {
            return GREGORIAN_INSTANCE;
        }

        //Android changed: remove lazy initialization, use classes instead of class names.

        CalendarSystem cal = calendars.get(calendarName);
        if (cal != null) {
            return cal;
        }

        Class<?> calendarClass = names.get(calendarName);
        if (calendarClass == null) {
            return null; // Unknown calendar name
        }

        if (calendarClass.isAssignableFrom(LocalGregorianCalendar.class)) {
            // Create the specific kind of local Gregorian calendar system
            cal = LocalGregorianCalendar.getLocalGregorianCalendar(calendarName);
        } else {
            try {
                cal = (CalendarSystem) calendarClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("internal error", e);
            }
        }
        if (cal == null) {
            return null;
        }

        CalendarSystem cs =  calendars.putIfAbsent(calendarName, cal);
        return (cs == null) ? cal : cs;
    }

    /**
     * Returns a {@link Properties} loaded from lib/calendars.properties.
     *
     * @return a {@link Properties} loaded from lib/calendars.properties
     * @throws IOException if an error occurred when reading from the input stream
     * @throws IllegalArgumentException if the input stream contains any malformed
     *                                  Unicode escape sequences
     */
    public static Properties getCalendarProperties() throws IOException {
        Properties calendarProps = null;
        try {
            String homeDir = AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction("java.home"));
            final String fname = homeDir + File.separator + "lib" + File.separator
                                 + "calendars.properties";
            calendarProps = AccessController.doPrivileged(new PrivilegedExceptionAction<Properties>() {
                @Override
                public Properties run() throws IOException {
                    Properties props = new Properties();
                    try (FileInputStream fis = new FileInputStream(fname)) {
                        props.load(fis);
                    }
                    return props;
                }
            });
        } catch (PrivilegedActionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) cause;
            }
            // Should not happen
            throw new InternalError(cause);
        }
        return calendarProps;
    }

    //////////////////////////////// Calendar API //////////////////////////////////

    /**
     * Returns the name of this calendar system.
     */
    public abstract String getName();

    public abstract CalendarDate getCalendarDate();

    /**
     * Calculates calendar fields from the specified number of
     * milliseconds since the Epoch, January 1, 1970 00:00:00 UTC
     * (Gregorian). This method doesn't check overflow or underflow
     * when adjusting the millisecond value (representing UTC) with
     * the time zone offsets (i.e., the GMT offset and amount of
     * daylight saving).
     *
     * @param millis the offset value in milliseconds from January 1,
     * 1970 00:00:00 UTC (Gregorian).
     * @return a <code>CalendarDate</code> instance that contains the
     * calculated calendar field values.
     */
    public abstract CalendarDate getCalendarDate(long millis);

    public abstract CalendarDate getCalendarDate(long millis, CalendarDate date);

    public abstract CalendarDate getCalendarDate(long millis, TimeZone zone);

    /**
     * Constructs a <code>CalendarDate</code> that is specific to this
     * calendar system. All calendar fields have their initial
     * values. The {@link TimeZone#getDefault() default time zone} is
     * set to the instance.
     *
     * @return a <code>CalendarDate</code> instance that contains the initial
     * calendar field values.
     */
    public abstract CalendarDate newCalendarDate();

    public abstract CalendarDate newCalendarDate(TimeZone zone);

    /**
     * Returns the number of milliseconds since the Epoch, January 1,
     * 1970 00:00:00 UTC (Gregorian), represented by the specified
     * <code>CalendarDate</code>.
     *
     * @param date the <code>CalendarDate</code> from which the time
     * value is calculated
     * @return the number of milliseconds since the Epoch.
     */
    public abstract long getTime(CalendarDate date);

    /**
     * Returns the length in days of the specified year by
     * <code>date</code>. This method does not perform the
     * normalization with the specified <code>CalendarDate</code>. The
     * <code>CalendarDate</code> must be normalized to get a correct
     * value.
     */
    public abstract int getYearLength(CalendarDate date);

    /**
     * Returns the number of months of the specified year. This method
     * does not perform the normalization with the specified
     * <code>CalendarDate</code>. The <code>CalendarDate</code> must
     * be normalized to get a correct value.
     */
    public abstract int getYearLengthInMonths(CalendarDate date);

    /**
     * Returns the length in days of the month specified by the calendar
     * date. This method does not perform the normalization with the
     * specified calendar date. The <code>CalendarDate</code> must
     * be normalized to get a correct value.
     *
     * @param date the date from which the month value is obtained
     * @return the number of days in the month
     * @exception IllegalArgumentException if the specified calendar date
     * doesn't have a valid month value in this calendar system.
     */
    public abstract int getMonthLength(CalendarDate date); // no setter

    /**
     * Returns the length in days of a week in this calendar
     * system. If this calendar system has multiple radix weeks, this
     * method returns only one of them.
     */
    public abstract int getWeekLength();

    /**
     * Returns the <code>Era</code> designated by the era name that
     * has to be known to this calendar system. If no Era is
     * applicable to this calendar system, null is returned.
     *
     * @param eraName the name of the era
     * @return the <code>Era</code> designated by
     * <code>eraName</code>, or <code>null</code> if no Era is
     * applicable to this calendar system or the specified era name is
     * not known to this calendar system.
     */
    public abstract Era getEra(String eraName);

    /**
     * Returns valid <code>Era</code>s of this calendar system. The
     * return value is sorted in the descendant order. (i.e., the first
     * element of the returned array is the oldest era.) If no era is
     * applicable to this calendar system, <code>null</code> is returned.
     *
     * @return an array of valid <code>Era</code>s, or
     * <code>null</code> if no era is applicable to this calendar
     * system.
     */
    public abstract Era[] getEras();

    /**
     * @throws IllegalArgumentException if the specified era name is
     * unknown to this calendar system.
     * @see Era
     */
    public abstract void setEra(CalendarDate date, String eraName);

    /**
     * Returns a <code>CalendarDate</code> of the n-th day of week
     * which is on, after or before the specified date. For example, the
     * first Sunday in April 2002 (Gregorian) can be obtained as
     * below:
     *
     * <pre><code>
     * Gregorian cal = CalendarSystem.getGregorianCalendar();
     * CalendarDate date = cal.newCalendarDate();
     * date.setDate(2004, cal.APRIL, 1);
     * CalendarDate firstSun = cal.getNthDayOfWeek(1, cal.SUNDAY, date);
     * // firstSun represents April 4, 2004.
     * </code></pre>
     *
     * This method returns a new <code>CalendarDate</code> instance
     * and doesn't modify the original date.
     *
     * @param nth specifies the n-th one. A positive number specifies
     * <em>on or after</em> the <code>date</code>. A non-positive number
     * specifies <em>on or before</em> the <code>date</code>.
     * @param dayOfWeek the day of week
     * @param date the date
     * @return the date of the nth <code>dayOfWeek</code> after
     * or before the specified <code>CalendarDate</code>
     */
    public abstract CalendarDate getNthDayOfWeek(int nth, int dayOfWeek,
                                                 CalendarDate date);

    public abstract CalendarDate setTimeOfDay(CalendarDate date, int timeOfDay);

    /**
     * Checks whether the calendar fields specified by <code>date</code>
     * represents a valid date and time in this calendar system. If the
     * given date is valid, <code>date</code> is marked as <em>normalized</em>.
     *
     * @param date the <code>CalendarDate</code> to be validated
     * @return <code>true</code> if all the calendar fields are consistent,
     * otherwise, <code>false</code> is returned.
     * @exception NullPointerException if the specified
     * <code>date</code> is <code>null</code>
     */
    public abstract boolean validate(CalendarDate date);

    /**
     * Normalizes calendar fields in the specified
     * <code>date</code>. Also all {@link CalendarDate#FIELD_UNDEFINED
     * undefined} fields are set to correct values. The actual
     * normalization process is calendar system dependent.
     *
     * @param date the calendar date to be validated
     * @return <code>true</code> if all fields have been normalized;
     * <code>false</code> otherwise.
     * @exception NullPointerException if the specified
     * <code>date</code> is <code>null</code>
     */
    public abstract boolean normalize(CalendarDate date);
}
