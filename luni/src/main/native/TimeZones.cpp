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

#define LOG_TAG "TimeZones"

#include <set>
#include <vector>

#include "ErrorCode.h"
#include "JNIHelp.h"
#include "JniConstants.h"
#include "ScopedJavaUnicodeString.h"
#include "ScopedLocalRef.h"
#include "ScopedUtfChars.h"
#include "UniquePtr.h"
#include "unicode/smpdtfmt.h"
#include "unicode/timezone.h"

extern Locale getLocale(JNIEnv* env, jstring localeName);

static jobjectArray TimeZones_forCountryCode(JNIEnv* env, jclass, jstring countryCode) {
    ScopedUtfChars countryChars(env, countryCode);
    if (countryChars.c_str() == NULL) {
        return NULL;
    }

    UniquePtr<StringEnumeration> ids(TimeZone::createEnumeration(countryChars.c_str()));
    if (ids.get() == NULL) {
        return NULL;
    }
    UErrorCode status = U_ZERO_ERROR;
    int32_t idCount = ids->count(status);
    if (U_FAILURE(status)) {
        icu4jni_error(env, status);
        return NULL;
    }

    jobjectArray result = env->NewObjectArray(idCount, JniConstants::stringClass, NULL);
    for (int32_t i = 0; i < idCount; ++i) {
        const UnicodeString* id = ids->snext(status);
        if (U_FAILURE(status)) {
            icu4jni_error(env, status);
            return NULL;
        }
        ScopedLocalRef<jstring> idString(env, env->NewString(id->getBuffer(), id->length()));
        env->SetObjectArrayElement(result, i, idString.get());
    }
    return result;
}

static jstring TimeZones_getDisplayNameImpl(JNIEnv* env, jclass, jstring javaZoneId, jboolean isDST, jint style, jstring localeId) {
    ScopedJavaUnicodeString zoneId(env, javaZoneId);
    UniquePtr<TimeZone> zone(TimeZone::createTimeZone(zoneId.unicodeString()));
    Locale locale = getLocale(env, localeId);
    // Try to get the display name of the TimeZone according to the Locale
    UnicodeString displayName;
    zone->getDisplayName((UBool)isDST, (style == 0 ? TimeZone::SHORT : TimeZone::LONG), locale, displayName);
    return env->NewString(displayName.getBuffer(), displayName.length());
}

struct TimeZoneNames {
    TimeZone* tz;

    UnicodeString longStd;
    UnicodeString shortStd;
    UnicodeString longDst;
    UnicodeString shortDst;

    UDate standardDate;
    UDate daylightSavingDate;
};

static void setStringArrayElement(JNIEnv* env, jobjectArray array, int i, const UnicodeString& s) {
    ScopedLocalRef<jstring> javaString(env, env->NewString(s.getBuffer(), s.length()));
    env->SetObjectArrayElement(array, i, javaString.get());
}

static void TimeZones_getZoneStringsImpl(JNIEnv* env, jclass, jobjectArray result, jstring localeName) {
    Locale locale = getLocale(env, localeName);

    // We could use TimeZone::getDisplayName, but that's even slower
    // because it creates a new SimpleDateFormat each time.
    // We're better off using SimpleDateFormat directly.

    UErrorCode status = U_ZERO_ERROR;
    UnicodeString longPattern("zzzz", 4, US_INV);
    SimpleDateFormat longFormat(longPattern, locale, status);
    // 'z' only uses "common" abbreviations. 'V' allows all known abbreviations.
    // For example, "PST" is in common use in en_US, but "CET" isn't.
    UnicodeString commonShortPattern("z", 1, US_INV);
    SimpleDateFormat shortFormat(commonShortPattern, locale, status);
    UnicodeString allShortPattern("V", 1, US_INV);
    SimpleDateFormat allShortFormat(allShortPattern, locale, status);

    // TODO: use of fixed dates prevents us from using the correct historical name when formatting dates.
    // TODO: use of dates not in the current year could cause us to output obsoleted names.
    // 15th January 2008
    UDate date1 = 1203105600000.0;
    // 15th July 2008
    UDate date2 = 1218826800000.0;

    jobjectArray zoneIds = reinterpret_cast<jobjectArray>(env->GetObjectArrayElement(result, 0));
    size_t zoneIdCount = env->GetArrayLength(zoneIds);

    // In the first pass, we get the long names for the time zone.
    // We also get any commonly-used abbreviations.
    std::vector<TimeZoneNames> table;
    std::set<UnicodeString> usedAbbreviations;
    for (size_t i = 0; i < zoneIdCount; ++i) {
        ScopedLocalRef<jstring> javaZoneId(env,
                reinterpret_cast<jstring>(env->GetObjectArrayElement(zoneIds, i)));
        ScopedJavaUnicodeString zoneId(env, javaZoneId.get());
        UnicodeString id(zoneId.unicodeString());

        TimeZoneNames row;
        row.tz = TimeZone::createTimeZone(id);

        longFormat.setTimeZone(*row.tz);
        shortFormat.setTimeZone(*row.tz);

        int32_t daylightOffset;
        int32_t rawOffset;
        row.tz->getOffset(date1, false, rawOffset, daylightOffset, status);
        if (daylightOffset != 0) {
            // The TimeZone is reporting that we are in daylight time for the winter date.
            // The dates are for the wrong hemisphere, so swap them.
            row.standardDate = date2;
            row.daylightSavingDate = date1;
        } else {
            row.standardDate = date1;
            row.daylightSavingDate = date2;
        }

        longFormat.format(row.standardDate, row.longStd);
        shortFormat.format(row.standardDate, row.shortStd);
        if (row.tz->useDaylightTime()) {
            longFormat.format(row.daylightSavingDate, row.longDst);
            shortFormat.format(row.daylightSavingDate, row.shortDst);
        } else {
            row.longDst = row.longStd;
            row.shortDst = row.shortStd;
        }

        table.push_back(row);
        usedAbbreviations.insert(row.shortStd);
        usedAbbreviations.insert(row.shortDst);
    }

    // In the second pass, we fill in the Java String[][].
    // We also look for any uncommon abbreviations that don't conflict with ones we've already seen.
    jobjectArray longStdArray = reinterpret_cast<jobjectArray>(env->GetObjectArrayElement(result, 1));
    jobjectArray shortStdArray = reinterpret_cast<jobjectArray>(env->GetObjectArrayElement(result, 2));
    jobjectArray longDstArray = reinterpret_cast<jobjectArray>(env->GetObjectArrayElement(result, 3));
    jobjectArray shortDstArray = reinterpret_cast<jobjectArray>(env->GetObjectArrayElement(result, 4));
    UnicodeString gmt("GMT", 3, US_INV);
    for (size_t i = 0; i < table.size(); ++i) {
        TimeZoneNames& row(table[i]);
        // Did we get a GMT offset instead of an abbreviation?
        if (row.shortStd.length() > 3 && row.shortStd.startsWith(gmt)) {
            UnicodeString uncommonStd, uncommonDst;
            allShortFormat.setTimeZone(*row.tz);
            allShortFormat.format(row.standardDate, uncommonStd);
            if (row.tz->useDaylightTime()) {
                allShortFormat.format(row.daylightSavingDate, uncommonDst);
            } else {
                uncommonDst = uncommonStd;
            }

            // If this abbreviation isn't already in use, we can use it.
            if (usedAbbreviations.find(uncommonStd) == usedAbbreviations.end() &&
                    usedAbbreviations.find(uncommonDst) == usedAbbreviations.end()) {
                row.shortStd = uncommonStd;
                row.shortDst = uncommonDst;
                usedAbbreviations.insert(uncommonStd);
                usedAbbreviations.insert(uncommonDst);
            }
        }
        // Fill in whatever we got.
        setStringArrayElement(env, longStdArray, i, row.longStd);
        setStringArrayElement(env, shortStdArray, i, row.shortStd);
        setStringArrayElement(env, longDstArray, i, row.longDst);
        setStringArrayElement(env, shortDstArray, i, row.shortDst);
        delete row.tz;
    }
}

static JNINativeMethod gMethods[] = {
    NATIVE_METHOD(TimeZones, getDisplayNameImpl, "(Ljava/lang/String;ZILjava/lang/String;)Ljava/lang/String;"),
    NATIVE_METHOD(TimeZones, forCountryCode, "(Ljava/lang/String;)[Ljava/lang/String;"),
    NATIVE_METHOD(TimeZones, getZoneStringsImpl, "([[Ljava/lang/String;Ljava/lang/String;)V"),
};
int register_libcore_icu_TimeZones(JNIEnv* env) {
    return jniRegisterNativeMethods(env, "libcore/icu/TimeZones", gMethods, NELEM(gMethods));
}
