/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package libcore.icu;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;
import libcore.util.BasicLruCache;

/**
 * Provides access to ICU's time zone data.
 */
public final class TimeZones {
    private static final String[] availableTimeZones = TimeZone.getAvailableIDs();

    private static final ZoneStringsCache cachedZoneStrings = new ZoneStringsCache();
    static {
        // Ensure that we pull in the zone strings for en_US and the user's default locale.
        // (All devices must support Locale.US, and it's used for things like HTTP headers.)
        // This is especially useful on Android because we'll share this via the Zygote.
        cachedZoneStrings.get(Locale.US);
        cachedZoneStrings.get(Locale.getDefault());
    }

    public static class ZoneStringsCache extends BasicLruCache<Locale, String[][]> {
        // De-duplicate the strings (http://b/2672057).
        private final HashMap<String, String> internTable = new HashMap<String, String>();

        public ZoneStringsCache() {
            // We make room for all the time zones known to the system, since each set of strings
            // isn't particularly large (and we remove duplicates), but is currently (Honeycomb)
            // really expensive to compute.
            // If you change this, you might want to change the scope of the intern table too.
            super(availableTimeZones.length);
        }

        @Override protected String[][] create(Locale locale) {
            long start, nativeStart;
            start = nativeStart = System.currentTimeMillis();
            String[][] result = getZoneStringsImpl(locale.toString(), availableTimeZones);
            long nativeEnd = System.currentTimeMillis();
            internStrings(result);
            // Ending up in this method too often is an easy way to make your app slow, so we ensure
            // it's easy to tell from the log (a) what we were doing, (b) how long it took, and
            // (c) that it's all ICU's fault.
            long end = System.currentTimeMillis();
            long duration = end - start;
            long nativeDuration = nativeEnd - nativeStart;
            Logger.global.info("Loaded time zone names for " + locale + " in " + duration + "ms" +
                    " (" + nativeDuration + "ms in ICU).");
            return result;
        }

        private synchronized void internStrings(String[][] result) {
            for (int i = 0; i < result.length; ++i) {
                for (int j = 1; j <= 4; ++j) {
                    String original = result[i][j];
                    String nonDuplicate = internTable.get(original);
                    if (nonDuplicate == null) {
                        internTable.put(original, original);
                    } else {
                        result[i][j] = nonDuplicate;
                    }
                }
            }
        }
    }

    private static final Comparator<String[]> ZONE_STRINGS_COMPARATOR = new Comparator<String[]>() {
        public int compare(String[] lhs, String[] rhs) {
            return lhs[0].compareTo(rhs[0]);
        }
    };

    private TimeZones() {}

    /**
     * Returns the appropriate string from 'zoneStrings'. Used with getZoneStrings.
     */
    public static String getDisplayName(String[][] zoneStrings, String id, boolean daylight, int style) {
        String[] needle = new String[] { id };
        int index = Arrays.binarySearch(zoneStrings, needle, ZONE_STRINGS_COMPARATOR);
        if (index >= 0) {
            String[] row = zoneStrings[index];
            if (daylight) {
                return (style == TimeZone.LONG) ? row[3] : row[4];
            } else {
                return (style == TimeZone.LONG) ? row[1] : row[2];
            }
        }
        return null;
    }

    /**
     * Returns an array of time zone strings, as used by DateFormatSymbols.getZoneStrings.
     */
    public static String[][] getZoneStrings(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return cachedZoneStrings.get(locale);
    }

    /**
     * Returns an array containing the time zone ids in use in the country corresponding to
     * the given locale. This is not necessary for Java API, but is used by telephony as a
     * fallback.
     */
    public static String[] forLocale(Locale locale) {
        return forCountryCode(locale.getCountry());
    }

    private static native String[] forCountryCode(String countryCode);
    private static native String[][] getZoneStringsImpl(String locale, String[] timeZoneIds);
}
