/*
 * Copyright (C) 2007 The Android Open Source Project
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

package org.apache.harmony.luni.internal.util;

import java.nio.charset.Charsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;
import libcore.base.Objects;

final class ZoneInfo extends TimeZone {

    private static final long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;
    private static final long MILLISECONDS_PER_400_YEARS =
            MILLISECONDS_PER_DAY * (400 * 365 + 100 - 3);

    private static final long UNIX_OFFSET = 62167219200000L;

    private static final int[] NORMAL = new int[] {
        0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334,
    };

    private static final int[] LEAP = new int[] {
        0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335,
    };

    private int mRawOffset;

    private final int[] mTransitions;
    private final int[] mOffsets;
    private final byte[] mTypes;
    private final byte[] mIsDsts;
    private final boolean mUseDst;

    ZoneInfo(String name, int[] transitions, byte[] type, int[] gmtOffsets, byte[] isDsts) {
        mTransitions = transitions;
        mTypes = type;
        mIsDsts = isDsts;
        setID(name);

        // Use the latest non-daylight offset (if any) as the raw offset.
        int lastStd;
        for (lastStd = mTransitions.length - 1; lastStd >= 0; lastStd--) {
            if (mIsDsts[mTypes[lastStd] & 0xFF] == 0) {
                break;
            }
        }
        if (lastStd < 0) {
            lastStd = 0;
        }
        if (lastStd >= mTypes.length) {
            mRawOffset = gmtOffsets[0];
        } else {
            mRawOffset = gmtOffsets[mTypes[lastStd] & 0xFF];
        }

        // Rather than keep offsets from UTC, we use offsets from local time, so the raw offset
        // can be changed and automatically affect all the offsets.
        mOffsets = gmtOffsets;
        for (int i = 0; i < mOffsets.length; i++) {
            mOffsets[i] -= mRawOffset;
        }

        // Is this zone still observing DST?
        // We don't care if they've historically used it: most places have at least once.
        // We want to know whether the last "schedule info" (the unix times in the mTransitions
        // array) is in the future. If it is, DST is still relevant.
        // See http://code.google.com/p/android/issues/detail?id=877.
        // This test means that for somewhere like Morocco, which tried DST in 2009 but has
        // no future plans (and thus no future schedule info) will report "true" from
        // useDaylightTime at the start of 2009 but "false" at the end. This seems appropriate.
        boolean usesDst = false;
        long currentUnixTime = System.currentTimeMillis() / 1000;
        if (mTransitions.length > 0) {
            // (We're really dealing with uint32_t values, so long is most convenient in Java.)
            long latestScheduleTime = mTransitions[mTransitions.length - 1] & 0xffffffff;
            if (currentUnixTime < latestScheduleTime) {
                usesDst = true;
            }
        }
        mUseDst = usesDst;

        mRawOffset *= 1000;
    }

    @Override
    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis) {
        // XXX This assumes Gregorian always; Calendar switches from
        // Julian to Gregorian in 1582.  What calendar system are the
        // arguments supposed to come from?

        long calc = (year / 400) * MILLISECONDS_PER_400_YEARS;
        year %= 400;

        calc += year * (365 * MILLISECONDS_PER_DAY);
        calc += ((year + 3) / 4) * MILLISECONDS_PER_DAY;

        if (year > 0) {
            calc -= ((year - 1) / 100) * MILLISECONDS_PER_DAY;
        }

        boolean isLeap = (year == 0 || (year % 4 == 0 && year % 100 != 0));
        int[] mlen = isLeap ? LEAP : NORMAL;

        calc += mlen[month] * MILLISECONDS_PER_DAY;
        calc += (day - 1) * MILLISECONDS_PER_DAY;
        calc += millis;

        calc -= mRawOffset;
        calc -= UNIX_OFFSET;

        return getOffset(calc);
    }

    @Override
    public int getOffset(long when) {
        int unix = (int) (when / 1000);
        int trans = Arrays.binarySearch(mTransitions, unix);

        if (trans == ~0) {
            return mRawOffset + mOffsets[0] * 1000;
        }
        if (trans < 0) {
            trans = ~trans - 1;
        }

        return mRawOffset + mOffsets[mTypes[trans] & 0xFF] * 1000;
    }

    @Override
    public int getRawOffset() {
        return mRawOffset;
    }

    @Override
    public void setRawOffset(int off) {
        mRawOffset = off;
    }

    @Override
    public boolean inDaylightTime(Date when) {
        int unix = (int) (when.getTime() / 1000);
        int trans = Arrays.binarySearch(mTransitions, unix);

        if (trans == ~0) {
            return mIsDsts[0] != 0;
        }
        if (trans < 0) {
            trans = ~trans - 1;
        }

        return mIsDsts[mTypes[trans] & 0xFF] != 0;
    }

    @Override
    public boolean useDaylightTime() {
        return mUseDst;
    }

    @Override public boolean hasSameRules(TimeZone timeZone) {
        if (!(timeZone instanceof ZoneInfo)) {
            return false;
        }
        ZoneInfo other = (ZoneInfo) timeZone;
        if (mUseDst != other.mUseDst) {
            return false;
        }
        if (!mUseDst) {
            return mRawOffset == other.mRawOffset;
        }
        return mRawOffset == other.mRawOffset
                // Arrays.equals returns true if both arrays are null
                && Arrays.equals(mOffsets, other.mOffsets)
                && Arrays.equals(mIsDsts, other.mIsDsts)
                && Arrays.equals(mTypes, other.mTypes)
                && Arrays.equals(mTransitions, other.mTransitions);
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof ZoneInfo)) {
            return false;
        }
        ZoneInfo other = (ZoneInfo) obj;
        return getID().equals(other.getID()) && hasSameRules(other);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getID().hashCode();
        result = prime * result + Arrays.hashCode(mOffsets);
        result = prime * result + Arrays.hashCode(mIsDsts);
        result = prime * result + mRawOffset;
        result = prime * result + Arrays.hashCode(mTransitions);
        result = prime * result + Arrays.hashCode(mTypes);
        result = prime * result + (mUseDst ? 1231 : 1237);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // First the basics...
        sb.append(getClass().getName() + "[" + getID() + ",mRawOffset=" + mRawOffset +
                ",mUseDst=" + mUseDst + "]");
        // ...followed by a zdump(1)-like description of all our transition data.
        sb.append("\n");
        Formatter f = new Formatter(sb);
        for (int i = 0; i < mTransitions.length; ++i) {
            int type = mTypes[i] & 0xff;
            String utcTime = formatTime(mTransitions[i], TimeZone.getTimeZone("UTC"));
            String localTime = formatTime(mTransitions[i], this);
            int offset = mOffsets[type];
            int gmtOffset = mRawOffset/1000 + offset;
            f.format("%4d : time=%10d %s = %s isDst=%d offset=%5d gmtOffset=%d\n",
                    i, mTransitions[i], utcTime, localTime, mIsDsts[type], offset, gmtOffset);
        }
        return sb.toString();
    }

    private static String formatTime(int s, TimeZone tz) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy zzz");
        sdf.setTimeZone(tz);
        long ms = ((long) s) * 1000L;
        return sdf.format(new Date(ms));
    }
}
