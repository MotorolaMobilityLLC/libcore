/*
 * Copyright (C) 2008 The Android Open Source Project
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

import android.compat.annotation.UnsupportedAppUsage;
import android.icu.text.CurrencyMetaInfo;
import android.icu.text.CurrencyMetaInfo.CurrencyFilter;
import android.icu.text.DateTimePatternGenerator;
import android.icu.util.ULocale;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import libcore.util.BasicLruCache;

/**
 * Makes ICU data accessible to Java.
 * @hide
 */
@libcore.api.CorePlatformApi
public final class ICU {

  @UnsupportedAppUsage
  private static final BasicLruCache<String, String> CACHED_PATTERNS =
      new BasicLruCache<String, String>(8);

  private static Locale[] availableLocalesCache;

  private static String[] isoCountries;

  private static String[] isoLanguages;

  private ICU() {
  }

  /**
   * Returns an array of two-letter ISO 639-1 language codes, either from ICU or our cache.
   */
  public static String[] getISOLanguages() {
    if (isoLanguages == null) {
      isoLanguages = getISOLanguagesNative();
    }
    return isoLanguages.clone();
  }

  /**
   * Returns an array of two-letter ISO 3166 country codes, either from ICU or our cache.
   */
  public static String[] getISOCountries() {
    if (isoCountries == null) {
      isoCountries = getISOCountriesNative();
    }
    return isoCountries.clone();
  }

  private static final int IDX_LANGUAGE = 0;
  private static final int IDX_SCRIPT = 1;
  private static final int IDX_REGION = 2;
  private static final int IDX_VARIANT = 3;

  /*
   * Parse the {Language, Script, Region, Variant*} section of the ICU locale
   * ID. This is the bit that appears before the keyword separate "@". The general
   * structure is a series of ASCII alphanumeric strings (subtags)
   * separated by underscores.
   *
   * Each subtag is interpreted according to its position in the list of subtags
   * AND its length (groan...). The various cases are explained in comments
   * below.
   */
  private static void parseLangScriptRegionAndVariants(String string,
          String[] outputArray) {
    final int first = string.indexOf('_');
    final int second = string.indexOf('_', first + 1);
    final int third = string.indexOf('_', second + 1);

    if (first == -1) {
      outputArray[IDX_LANGUAGE] = string;
    } else if (second == -1) {
      // Language and country ("ja_JP") OR
      // Language and script ("en_Latn") OR
      // Language and variant ("en_POSIX").

      outputArray[IDX_LANGUAGE] = string.substring(0, first);
      final String secondString = string.substring(first + 1);

      if (secondString.length() == 4) {
          // 4 Letter ISO script code.
          outputArray[IDX_SCRIPT] = secondString;
      } else if (secondString.length() == 2 || secondString.length() == 3) {
          // 2 or 3 Letter region code.
          outputArray[IDX_REGION] = secondString;
      } else {
          // If we're here, the length of the second half is either 1 or greater
          // than 5. Assume that ICU won't hand us malformed tags, and therefore
          // assume the rest of the string is a series of variant tags.
          outputArray[IDX_VARIANT] = secondString;
      }
    } else if (third == -1) {
      // Language and country and variant ("ja_JP_TRADITIONAL") OR
      // Language and script and variant ("en_Latn_POSIX") OR
      // Language and script and region ("en_Latn_US"). OR
      // Language and variant with multiple subtags ("en_POSIX_XISOP")

      outputArray[IDX_LANGUAGE] = string.substring(0, first);
      final String secondString = string.substring(first + 1, second);
      final String thirdString = string.substring(second + 1);

      if (secondString.length() == 4) {
          // The second subtag is a script.
          outputArray[IDX_SCRIPT] = secondString;

          // The third subtag can be either a region or a variant, depending
          // on its length.
          if (thirdString.length() == 2 || thirdString.length() == 3 ||
                  thirdString.isEmpty()) {
              outputArray[IDX_REGION] = thirdString;
          } else {
              outputArray[IDX_VARIANT] = thirdString;
          }
      } else if (secondString.isEmpty() ||
              secondString.length() == 2 || secondString.length() == 3) {
          // The second string is a region, and the third a variant.
          outputArray[IDX_REGION] = secondString;
          outputArray[IDX_VARIANT] = thirdString;
      } else {
          // Variant with multiple subtags.
          outputArray[IDX_VARIANT] = string.substring(first + 1);
      }
    } else {
      // Language, script, region and variant with 1 or more subtags
      // ("en_Latn_US_POSIX") OR
      // Language, region and variant with 2 or more subtags
      // (en_US_POSIX_VARIANT).
      outputArray[IDX_LANGUAGE] = string.substring(0, first);
      final String secondString = string.substring(first + 1, second);
      if (secondString.length() == 4) {
          outputArray[IDX_SCRIPT] = secondString;
          outputArray[IDX_REGION] = string.substring(second + 1, third);
          outputArray[IDX_VARIANT] = string.substring(third + 1);
      } else {
          outputArray[IDX_REGION] = secondString;
          outputArray[IDX_VARIANT] = string.substring(second + 1);
      }
    }
  }

  /**
   * Returns the appropriate {@code Locale} given a {@code String} of the form returned
   * by {@code toString}. This is very lenient, and doesn't care what's between the underscores:
   * this method can parse strings that {@code Locale.toString} won't produce.
   * Used to remove duplication.
   */
  public static Locale localeFromIcuLocaleId(String localeId) {
    // @ == ULOC_KEYWORD_SEPARATOR_UNICODE (uloc.h).
    final int extensionsIndex = localeId.indexOf('@');

    Map<Character, String> extensionsMap = Collections.EMPTY_MAP;
    Map<String, String> unicodeKeywordsMap = Collections.EMPTY_MAP;
    Set<String> unicodeAttributeSet = Collections.EMPTY_SET;

    if (extensionsIndex != -1) {
      extensionsMap = new HashMap<Character, String>();
      unicodeKeywordsMap = new HashMap<String, String>();
      unicodeAttributeSet = new HashSet<String>();

      // ICU sends us a semi-colon (ULOC_KEYWORD_ITEM_SEPARATOR) delimited string
      // containing all "keywords" it could parse. An ICU keyword is a key-value pair
      // separated by an "=" (ULOC_KEYWORD_ASSIGN).
      //
      // Each keyword item can be one of three things :
      // - A unicode extension attribute list: In this case the item key is "attribute"
      //   and the value is a hyphen separated list of unicode attributes.
      // - A unicode extension keyword: In this case, the item key will be larger than
      //   1 char in length, and the value will be the unicode extension value.
      // - A BCP-47 extension subtag: In this case, the item key will be exactly one
      //   char in length, and the value will be a sequence of unparsed subtags that
      //   represent the extension.
      //
      // Note that this implies that unicode extension keywords are "promoted" to
      // to the same namespace as the top level extension subtags and their values.
      // There can't be any collisions in practice because the BCP-47 spec imposes
      // restrictions on their lengths.
      final String extensionsString = localeId.substring(extensionsIndex + 1);
      final String[] extensions = extensionsString.split(";");
      for (String extension : extensions) {
        // This is the special key for the unicode attributes
        if (extension.startsWith("attribute=")) {
          String unicodeAttributeValues = extension.substring("attribute=".length());
          for (String unicodeAttribute : unicodeAttributeValues.split("-")) {
            unicodeAttributeSet.add(unicodeAttribute);
          }
        } else {
          final int separatorIndex = extension.indexOf('=');

          if (separatorIndex == 1) {
            // This is a BCP-47 extension subtag.
            final String value = extension.substring(2);
            final char extensionId = extension.charAt(0);

            extensionsMap.put(extensionId, value);
          } else {
            // This is a unicode extension keyword.
            unicodeKeywordsMap.put(extension.substring(0, separatorIndex),
            extension.substring(separatorIndex + 1));
          }
        }
      }
    }

    final String[] outputArray = new String[] { "", "", "", "" };
    if (extensionsIndex == -1) {
      parseLangScriptRegionAndVariants(localeId, outputArray);
    } else {
      parseLangScriptRegionAndVariants(localeId.substring(0, extensionsIndex),
          outputArray);
    }
    Locale.Builder builder = new Locale.Builder();
    builder.setLanguage(outputArray[IDX_LANGUAGE]);
    builder.setRegion(outputArray[IDX_REGION]);
    builder.setVariant(outputArray[IDX_VARIANT]);
    builder.setScript(outputArray[IDX_SCRIPT]);
    for (String attribute : unicodeAttributeSet) {
      builder.addUnicodeLocaleAttribute(attribute);
    }
    for (Entry<String, String> keyword : unicodeKeywordsMap.entrySet()) {
      builder.setUnicodeLocaleKeyword(keyword.getKey(), keyword.getValue());
    }

    for (Entry<Character, String> extension : extensionsMap.entrySet()) {
      builder.setExtension(extension.getKey(), extension.getValue());
    }

    return builder.build();
  }

  public static Locale[] localesFromStrings(String[] localeNames) {
    // We need to remove duplicates caused by the conversion of "he" to "iw", et cetera.
    // Java needs the obsolete code, ICU needs the modern code, but we let ICU know about
    // both so that we never need to convert back when talking to it.
    LinkedHashSet<Locale> set = new LinkedHashSet<Locale>();
    for (String localeName : localeNames) {
      set.add(localeFromIcuLocaleId(localeName));
    }
    return set.toArray(new Locale[set.size()]);
  }

  public static Locale[] getAvailableLocales() {
    if (availableLocalesCache == null) {
      availableLocalesCache = localesFromStrings(getAvailableLocalesNative());
    }
    return availableLocalesCache.clone();
  }

  @UnsupportedAppUsage
  @libcore.api.CorePlatformApi
  public static String getBestDateTimePattern(String skeleton, Locale locale) {
    String languageTag = locale.toLanguageTag();
    String key = skeleton + "\t" + languageTag;
    synchronized (CACHED_PATTERNS) {
      String pattern = CACHED_PATTERNS.get(key);
      if (pattern == null) {
        pattern = getBestDateTimePattern0(skeleton, locale);
        CACHED_PATTERNS.put(key, pattern);
      }
      return pattern;
    }
  }

  private static String getBestDateTimePattern0(String skeleton, Locale locale) {
      DateTimePatternGenerator dtpg = DateTimePatternGenerator.getInstance(locale);
      return dtpg.getBestPattern(skeleton);
  }

  @UnsupportedAppUsage
  private static String getBestDateTimePatternNative(String skeleton, String languageTag) {
    return getBestDateTimePattern0(skeleton, Locale.forLanguageTag(languageTag));
  }

  @UnsupportedAppUsage
  @libcore.api.CorePlatformApi
  public static char[] getDateFormatOrder(String pattern) {
    char[] result = new char[3];
    int resultIndex = 0;
    boolean sawDay = false;
    boolean sawMonth = false;
    boolean sawYear = false;

    for (int i = 0; i < pattern.length(); ++i) {
      char ch = pattern.charAt(i);
      if (ch == 'd' || ch == 'L' || ch == 'M' || ch == 'y') {
        if (ch == 'd' && !sawDay) {
          result[resultIndex++] = 'd';
          sawDay = true;
        } else if ((ch == 'L' || ch == 'M') && !sawMonth) {
          result[resultIndex++] = 'M';
          sawMonth = true;
        } else if ((ch == 'y') && !sawYear) {
          result[resultIndex++] = 'y';
          sawYear = true;
        }
      } else if (ch == 'G') {
        // Ignore the era specifier, if present.
      } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
        throw new IllegalArgumentException("Bad pattern character '" + ch + "' in " + pattern);
      } else if (ch == '\'') {
        if (i < pattern.length() - 1 && pattern.charAt(i + 1) == '\'') {
          ++i;
        } else {
          i = pattern.indexOf('\'', i + 1);
          if (i == -1) {
            throw new IllegalArgumentException("Bad quoting in " + pattern);
          }
          ++i;
        }
      } else {
        // Ignore spaces and punctuation.
      }
    }
    return result;
  }

  // --- Errors.

  // --- Native methods accessing ICU's database.

  private static native String[] getAvailableLocalesNative();

    /**
     * Query ICU for the currency being used in the country right now.
     * @param countryCode ISO 3166 two-letter country code
     * @return ISO 4217 3-letter currency code if found, otherwise null.
     */
  public static String getCurrencyCode(String countryCode) {
      CurrencyFilter filter = CurrencyFilter.onRegion(countryCode)
          .withDate(new Date());
      List<String> currencies = CurrencyMetaInfo.getInstance().currencies(filter);
      return currencies.isEmpty() ? null : currencies.get(0);
  }


  public static native String getISO3Country(String languageTag);

  public static native String getISO3Language(String languageTag);

  /**
   * @deprecated Use {@link android.icu.util.ULocale#addLikelySubtags(ULocale)} instead.
   * The method is only kept for @UnsupportedAppUsage.
   */
  @UnsupportedAppUsage
  @Deprecated
  public static Locale addLikelySubtags(Locale locale) {
      return ULocale.addLikelySubtags(ULocale.forLocale(locale)).toLocale();
  }

  /**
   * @return ICU localeID
   * @deprecated Use {@link android.icu.util.ULocale#addLikelySubtags(ULocale)} instead.
   * The method is only kept for @UnsupportedAppUsage.
   */
  @UnsupportedAppUsage
  @Deprecated
  public static String addLikelySubtags(String locale) {
      return ULocale.addLikelySubtags(new ULocale(locale)).getName();
  }

  /**
   * @deprecated use {@link java.util.Locale#getScript()} instead. This has been kept
   *     around only for the support library.
   */
  @UnsupportedAppUsage
  @Deprecated
  public static native String getScript(String locale);

  private static native String[] getISOLanguagesNative();
  private static native String[] getISOCountriesNative();

  /**
   * Takes a BCP-47 language tag (Locale.toLanguageTag()). e.g. en-US, not en_US
   */
  public static native void setDefaultLocale(String languageTag);

  /**
   * Returns a locale name, not a BCP-47 language tag. e.g. en_US not en-US.
   */
  public static native String getDefaultLocale();
}
