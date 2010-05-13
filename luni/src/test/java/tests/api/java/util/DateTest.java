/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package tests.api.java.util;

import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

@TestTargetClass(Date.class)
public class DateTest extends junit.framework.TestCase {

    /**
     * @tests java.util.Date#Date()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "Date",
        args = {}
    )
    public void test_Constructor() {
        // Test for method java.util.Date()
        GregorianCalendar gc = new GregorianCalendar(1998, Calendar.OCTOBER,
                13, 19, 9);
        long oldTime = gc.getTime().getTime();
        long now = new Date().getTime();
        assertTrue("Created incorrect date: " + oldTime + " now: " + now,
                oldTime < now);
    }

    /**
     * @tests java.util.Date#Date(int, int, int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "Date",
        args = {int.class, int.class, int.class}
    )
    public void test_ConstructorIII() {
        // Test for method java.util.Date(int, int, int)
        Date d1 = new Date(70, 0, 1); // the epoch + local time

        // the epoch + local time
        Date d2 = new Date(0 + d1.getTimezoneOffset() * 60 * 1000);

        assertTrue("Created incorrect date", d1.equals(d2));

        Date date = new Date(99, 5, 22);
        Calendar cal = new GregorianCalendar(1999, Calendar.JUNE, 22);
        assertTrue("Wrong time zone", date.equals(cal.getTime()));
    }

    /**
     * @tests java.util.Date#Date(int, int, int, int, int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "Date",
        args = {int.class, int.class, int.class, int.class, int.class}
    )
    public void test_ConstructorIIIII() {
        // Test for method java.util.Date(int, int, int, int, int)

        // the epoch + local time + (1 hour and 1 minute)
        Date d1 = new Date(70, 0, 1, 1, 1);

        // the epoch + local time + (1 hour and 1 minute)
        Date d2 = new Date(0 + d1.getTimezoneOffset() * 60 * 1000 + 60 * 60
                * 1000 + 60 * 1000);

        assertTrue("Created incorrect date", d1.equals(d2));
    }

    /**
     * @tests java.util.Date#Date(int, int, int, int, int, int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "Date",
        args = {int.class, int.class, int.class, int.class, int.class, int.class}
    )
    public void test_ConstructorIIIIII() {
        // Test for method java.util.Date(int, int, int, int, int, int)

        // the epoch + local time + (1 hour and 1 minute + 1 second)
        Date d1 = new Date(70, 0, 1, 1, 1, 1);

        // the epoch + local time + (1 hour and 1 minute + 1 second)
        Date d2 = new Date(0 + d1.getTimezoneOffset() * 60 * 1000 + 60 * 60
                * 1000 + 60 * 1000 + 1000);

        assertTrue("Created incorrect date", d1.equals(d2));
    }

    /**
     * @tests java.util.Date#Date(long)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "Date",
        args = {long.class}
    )
    public void test_ConstructorJ() {
        // Test for method java.util.Date(long)
        assertTrue("Used to test", true);
    }

    /**
     * @tests java.util.Date#Date(java.lang.String)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "Date",
        args = {java.lang.String.class}
    )
    public void test_ConstructorLjava_lang_String() {
        // Test for method java.util.Date(java.lang.String)
        Date d1 = new Date("January 1, 1970, 00:00:00 GMT"); // the epoch
        Date d2 = new Date(0); // the epoch
        assertTrue("Created incorrect date", d1.equals(d2));

        try {
            // Regression for HARMONY-238
            new Date(null);
            fail("Constructor Date((String)null) should "
                + "throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * @tests java.util.Date#after(java.util.Date)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "after",
        args = {java.util.Date.class}
    )
    public void test_afterLjava_util_Date() {
        // Test for method boolean java.util.Date.after(java.util.Date)
        Date d1 = new Date(0);
        Date d2 = new Date(1900000);
        assertTrue("Older was returned as newer", d2.after(d1));
        assertTrue("Newer was returned as older", !d1.after(d2));

        try {
            d1.after(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            //expected
        }
    }

    /**
     * @tests java.util.Date#before(java.util.Date)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "before",
        args = {java.util.Date.class}
    )
    public void test_beforeLjava_util_Date() {
        // Test for method boolean java.util.Date.before(java.util.Date)
        Date d1 = new Date(0);
        Date d2 = new Date(1900000);
        assertTrue("Older was returned as newer", !d2.before(d1));
        assertTrue("Newer was returned as older", d1.before(d2));

        try {
            d1.before(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            //expected
        }
    }

    /**
     * @tests java.util.Date#clone()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "clone",
        args = {}
    )
    public void test_clone() {
        // Test for method java.lang.Object java.util.Date.clone()
        Date d1 = new Date(100000);
        Date d2 = (Date) d1.clone();
        assertTrue(
                "Cloning date results in same reference--new date is equivalent",
                d1 != d2);
        assertTrue("Cloning date results unequal date", d1.equals(d2));
    }

    /**
     * @tests java.util.Date#compareTo(java.util.Date)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "compareTo",
        args = {java.util.Date.class}
    )
    public void test_compareToLjava_util_Date() {
        // Test for method int java.util.Date.compareTo(java.util.Date)
        final int someNumber = 10000;
        Date d1 = new Date(someNumber);
        Date d2 = new Date(someNumber);
        Date d3 = new Date(someNumber + 1);
        Date d4 = new Date(someNumber - 1);
        assertEquals("Comparing a date to itself did not answer zero", 0, d1
                .compareTo(d1));
        assertEquals("Comparing equal dates did not answer zero", 0, d1
                .compareTo(d2));
        assertEquals("date1.compareTo(date2), where date1 > date2, did not result in 1",
                1, d1.compareTo(d4));
        assertEquals("date1.compareTo(date2), where date1 < date2, did not result in -1",
                -1, d1.compareTo(d3));

        try {
            d1.compareTo(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            //expected
        }
    }

    /**
     * @tests java.util.Date#equals(java.lang.Object)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "equals",
        args = {java.lang.Object.class}
    )
    public void test_equalsLjava_lang_Object() {
        // Test for method boolean java.util.Date.equals(java.lang.Object)
        Date d1 = new Date(0);
        Date d2 = new Date(1900000);
        Date d3 = new Date(1900000);
        assertTrue("Equality test failed", d2.equals(d3));
        assertTrue("Equality test failed", !d1.equals(d2));
    }

    /**
     * @tests java.util.Date#getDate()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "getDate",
        args = {}
    )
    public void test_getDate() {
        // Test for method int java.util.Date.getDate()
        Date d = new GregorianCalendar(1998, Calendar.OCTOBER, 13, 19, 9)
                .getTime();
        assertEquals("Returned incorrect date", 13, d.getDate());
    }

    /**
     * @tests java.util.Date#getDay()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "getDay",
        args = {}
    )
    public void test_getDay() {
        // Test for method int java.util.Date.getDay()
        Date d = new GregorianCalendar(1998, Calendar.OCTOBER, 13, 19, 9)
                .getTime();
        assertEquals("Returned incorrect day", 2, d.getDay());
    }

    /**
     * @tests java.util.Date#getHours()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "getHours",
        args = {}
    )
    public void test_getHours() {
        // Test for method int java.util.Date.getHours()
        Date d = new GregorianCalendar(1998, Calendar.OCTOBER, 13, 19, 9)
                .getTime();
        assertEquals("Returned incorrect hours", 19, d.getHours());
    }

    /**
     * @tests java.util.Date#getMinutes()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "getMinutes",
        args = {}
    )
    public void test_getMinutes() {
        // Test for method int java.util.Date.getMinutes()
        Date d = new GregorianCalendar(1998, Calendar.OCTOBER, 13, 19, 9)
                .getTime();
        assertEquals("Returned incorrect minutes", 9, d.getMinutes());
    }

    /**
     * @tests java.util.Date#getMonth()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "getMonth",
        args = {}
    )
    public void test_getMonth() {
        // Test for method int java.util.Date.getMonth()
        Date d = new GregorianCalendar(1998, Calendar.OCTOBER, 13, 19, 9)
                .getTime();
        assertEquals("Returned incorrect month", 9, d.getMonth());
    }

    /**
     * @tests java.util.Date#getSeconds()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "getSeconds",
        args = {}
    )
    public void test_getSeconds() {
        // Test for method int java.util.Date.getSeconds()
        Date d = new GregorianCalendar(1998, Calendar.OCTOBER, 13, 19, 9)
                .getTime();
        assertEquals("Returned incorrect seconds", 0, d.getSeconds());
    }

    /**
     * @tests java.util.Date#getTime()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "getTime",
        args = {}
    )
    public void test_getTime() {
        // Test for method long java.util.Date.getTime()
        Date d1 = new Date(0);
        Date d2 = new Date(1900000);
        assertEquals("Returned incorrect time", 1900000, d2.getTime());
        assertEquals("Returned incorrect time", 0, d1.getTime());
    }

    /**
     * @tests java.util.Date#getTimezoneOffset()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "getTimezoneOffset",
        args = {}
    )
    public void test_getTimezoneOffset() {
        // Test for method int java.util.Date.getTimezoneOffset()
        assertTrue("Used to test", true);
        int offset = new Date(96, 1, 14).getTimezoneOffset();
        assertTrue(offset > -720 && offset < 720);
    }

    /**
     * @tests java.util.Date#getYear()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "getYear",
        args = {}
    )
    public void test_getYear() {
        // Test for method int java.util.Date.getYear()
        Date d = new GregorianCalendar(1998, Calendar.OCTOBER, 13, 19, 9)
                .getTime();
        assertEquals("Returned incorrect year", 98, d.getYear());
    }

    /**
     * @tests java.util.Date#hashCode()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "hashCode",
        args = {}
    )
    public void test_hashCode() {
        // Test for method int java.util.Date.hashCode()
        Date d1 = new Date(0);
        Date d2 = new Date(1900000);
        assertEquals("Returned incorrect hash", 1900000, d2.hashCode());
        assertEquals("Returned incorrect hash", 0, d1.hashCode());
    }

    /**
     * @tests java.util.Date#parse(java.lang.String)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "parse",
        args = {java.lang.String.class}
    )
    public void test_parseLjava_lang_String() {
        // Test for method long java.util.Date.parse(java.lang.String)
        Date d = new Date(Date.parse("13 October 1998"));
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        assertEquals("Parsed incorrect month", 9, cal.get(Calendar.MONTH));
        assertEquals("Parsed incorrect year", 1998, cal.get(Calendar.YEAR));
        assertEquals("Parsed incorrect date", 13, cal.get(Calendar.DATE));

        d = new Date(Date.parse("Jan-12 1999"));
        assertTrue("Wrong parsed date 1", d.equals(new GregorianCalendar(1999,
                0, 12).getTime()));
        d = new Date(Date.parse("Jan12-1999"));
        assertTrue("Wrong parsed date 2", d.equals(new GregorianCalendar(1999,
                0, 12).getTime()));
        d = new Date(Date.parse("Jan12 69-1"));
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.clear();
        cal.set(1969, Calendar.JANUARY, 12, 1, 0);
        assertTrue("Wrong parsed date 3", d.equals(cal.getTime()));
        d = new Date(Date.parse("6:45:13 3/2/1200 MST"));
        cal.setTimeZone(TimeZone.getTimeZone("MST"));
        cal.clear();
        cal.set(1200, 2, 2, 6, 45, 13);
        assertTrue("Wrong parsed date 4", d.equals(cal.getTime()));
        d = new Date(Date.parse("Mon, 22 Nov 1999 12:52:06 GMT"));
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.clear();
        cal.set(1999, Calendar.NOVEMBER, 22, 12, 52, 06);
        assertTrue("Wrong parsed date 5", d.equals(cal.getTime()));

        try {
            // Regression for HARMONY-259
            Date.parse(null);
            fail("Date.parse(null) should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * @tests java.util.Date#setDate(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "setDate",
        args = {int.class}
    )
    public void test_setDateI() {
        // Test for method void java.util.Date.setDate(int)
        Date d = new GregorianCalendar(1998, Calendar.OCTOBER, 13, 19, 9)
                .getTime();
        d.setDate(23);
        assertEquals("Set incorrect date", 23, d.getDate());
    }

    /**
     * @tests java.util.Date#setHours(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "setHours",
        args = {int.class}
    )
    public void test_setHoursI() {
        // Test for method void java.util.Date.setHours(int)
        Date d = new GregorianCalendar(1998, Calendar.OCTOBER, 13, 19, 9)
                .getTime();
        d.setHours(23);
        assertEquals("Set incorrect hours", 23, d.getHours());
    }

    /**
     * @tests java.util.Date#setMinutes(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "setMinutes",
        args = {int.class}
    )
    public void test_setMinutesI() {
        // Test for method void java.util.Date.setMinutes(int)
        Date d = new GregorianCalendar(1998, Calendar.OCTOBER, 13, 19, 9)
                .getTime();
        d.setMinutes(45);
        assertEquals("Set incorrect mins", 45, d.getMinutes());
    }

    /**
     * @tests java.util.Date#setMonth(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "setMonth",
        args = {int.class}
    )
    public void test_setMonthI() {
        // Test for method void java.util.Date.setMonth(int)
        Date d = new GregorianCalendar(1998, Calendar.OCTOBER, 13, 19, 9)
                .getTime();
        d.setMonth(0);
        assertEquals("Set incorrect month", 0, d.getMonth());
    }

    /**
     * @tests java.util.Date#setSeconds(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "setSeconds",
        args = {int.class}
    )
    public void test_setSecondsI() {
        // Test for method void java.util.Date.setSeconds(int)
        Date d = new GregorianCalendar(1998, Calendar.OCTOBER, 13, 19, 9)
                .getTime();
        d.setSeconds(13);
        assertEquals("Set incorrect seconds", 13, d.getSeconds());
    }

    /**
     * @tests java.util.Date#setTime(long)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "setTime",
        args = {long.class}
    )
    public void test_setTimeJ() {
        // Test for method void java.util.Date.setTime(long)
        Date d1 = new Date(0);
        Date d2 = new Date(1900000);
        d1.setTime(900);
        d2.setTime(890000);
        assertEquals("Returned incorrect time", 890000, d2.getTime());
        assertEquals("Returned incorrect time", 900, d1.getTime());
    }

    /**
     * @tests java.util.Date#setYear(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "setYear",
        args = {int.class}
    )
    public void test_setYearI() {
        // Test for method void java.util.Date.setYear(int)
        Date d = new GregorianCalendar(1998, Calendar.OCTOBER, 13, 19, 9)
                .getTime();
        d.setYear(8);
        assertEquals("Set incorrect year", 8, d.getYear());
    }

    /**
     * @tests java.util.Date#toGMTString()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "toGMTString",
        args = {}
    )
    public void test_toGMTString() {
        // Test for method java.lang.String java.util.Date.toGMTString()
        assertEquals("Did not convert epoch to GMT string correctly", "1 Jan 1970 00:00:00 GMT", new Date(0)
                .toGMTString());
        assertEquals("Did not convert epoch + 1yr to GMT string correctly",
                "1 Jan 1971 00:00:00 GMT", new Date((long) 365 * 24 * 60 * 60 * 1000).toGMTString()
                        );
    }

    /**
     * @tests java.util.Date#toString()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "toString",
        args = {}
    )
    public void test_toString() {
        // Test for method java.lang.String java.util.Date.toString()
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.YEAR, 1970);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date d = cal.getTime();
        String result = d.toString();
        assertTrue("Incorrect result: " + d, result
                .startsWith("Thu Jan 01 00:00:00")
                && result.endsWith("1970"));

        TimeZone tz = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-5"));
        try {
            Date d1 = new Date(0);
            assertTrue("Returned incorrect string: " + d1, d1.toString()
                    .equals("Wed Dec 31 19:00:00 GMT-05:00 1969"));
        } finally {
            TimeZone.setDefault(tz);
        }
    }

    /**
     * @tests java.util.Date#UTC(int, int, int, int, int, int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "UTC",
        args = {int.class, int.class, int.class, int.class, int.class, int.class}
    )
    public void test_UTCIIIIII() {
        // Test for method long java.util.Date.UTC(int, int, int, int, int, int)
        assertTrue("Returned incorrect UTC value for epoch", Date.UTC(70, 0, 1,
                0, 0, 0) == (long) 0);
        assertTrue("Returned incorrect UTC value for epoch +1yr", Date.UTC(71,
                0, 1, 0, 0, 0) == (long) 365 * 24 * 60 * 60 * 1000);
    }
    /**
     * @tests java.util.Date#toLocaleString() Test for method java.lang.String
     *        java.util.Date.toGMTString()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "toLocaleString",
        args = {}
    )
    public void test_toLocaleString() {
        Locale loc = Locale.getDefault();
        Locale.setDefault(Locale.US);
    TimeZone tz = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    try {
            assertEquals("Did not convert epoch to GMT string correctly", "Jan 1, 1970 12:00:00 AM",
                new Date(0).toLocaleString());
            assertEquals("Did not convert epoch + 1yr to GMT string correctly",
                "Jan 1, 1971 12:00:00 AM", new Date((long)365 * 24 * 60 * 60 * 1000)
                        .toLocaleString());
    } finally {
            Locale.setDefault(loc);
        TimeZone.setDefault(tz);
    }
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() {
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     */
    protected void tearDown() {
    }
}
