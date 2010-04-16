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

package java.util;

import com.ibm.icu4jni.util.ICU;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import org.apache.harmony.luni.util.PriviAction;
import org.apache.harmony.luni.util.Util;

/**
 * {@code Locale} represents a language/country/variant combination. Locales are used to
 * alter the presentation of information such as numbers or dates to suit the conventions
 * in the region they describe.
 *
 * <p>The language codes are two-letter lowercase ISO language codes (such as "en") as defined by
 * <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1</a>.
 * The country codes are two-letter uppercase ISO country codes (such as "US") as defined by
 * <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3">ISO 3166-1</a>.
 * The variant codes are unspecified.
 *
 * <p>Note that Java uses several deprecated two-letter codes. The Hebrew ("he") language
 * code is rewritten as "iw", Indonesian ("id") as "in", and Yiddish ("yi") as "ji". This
 * is true even if you construct your own {@code Locale} object, not just of instances returned by
 * the various lookup methods.
 *
 * <p>Just because you can create a {@code Locale} doesn't mean that it makes much sense.
 * Imagine "de_US" for German as spoken in the US, for example. It is also a mistake to
 * assume that all devices have the same locales available. A device sold in the US will
 * almost certainly support en_US and sp_US (English and Spanish, as spoken in the US),
 * but not necessarily en_GB or sp_SP (English as spoken in Great Britain or Spanish as
 * spoken in Spain), for example. The opposite may well be true for a device sold in Europe.
 * (This limitation even affects those locales pre-defined as constants in this class.)
 *
 * <p>You can use {@code getDefault} to get an appropriate locale for the <i>user</i> of
 * the device you're running on, or {@code getAvailableLocales} to get a list of all the locales
 * available on the device you're running on.
 *
 * <a name="default_locale"><h3>Be wary of the default locale</h3></a>
 * <p>Note that there are many convenience methods that automatically use the default locale, but
 * these may not be as convenient as you imagine. The default locale is appropriate for anything
 * that involves presenting data to the user. You should use the user's date/time formats, number
 * formats, rules for conversion to lowercase, and so on. A common mistake is to implicitly use the
 * default locale when producing output meant to be machine-readable. This tends to work on the
 * developer's test devices but fail when run on a device whose user is in a less conventional
 * locale. For example, if you're formatting integers some locales will use non-ASCII decimal
 * digits. As another example, if you're formatting floating-point numbers some locales will use
 * {@code ','} as the decimal point. That's correct for human-readable output, but likely to cause
 * problems if presented to another computer ({@code Double.parseDouble} can't parse such a number,
 * for example). The best choice for computer-readable output is usually {@code Locale.US}: this
 * locale is guaranteed to be available on all devices, and the combination of no surprising
 * behavior and frequent use (especially for computer-computer communication) means that it tends
 * to be the most efficient choice too.
 *
 * @see ResourceBundle
 */
public final class Locale implements Cloneable, Serializable {

    private static final long serialVersionUID = 9149081749638150636L;

    // Initialize a default which is used during static
    // initialization of the default for the platform.
    private static Locale defaultLocale = new Locale();

    /**
     * Locale constant for en_CA.
     */
    public static final Locale CANADA = new Locale("en", "CA");

    /**
     * Locale constant for fr_CA.
     */
    public static final Locale CANADA_FRENCH = new Locale("fr", "CA");

    /**
     * Locale constant for zh_CN.
     */
    public static final Locale CHINA = new Locale("zh", "CN");

    /**
     * Locale constant for zh.
     */
    public static final Locale CHINESE = new Locale("zh", "");

    /**
     * Locale constant for en.
     */
    public static final Locale ENGLISH = new Locale("en", "");

    /**
     * Locale constant for fr_FR.
     */
    public static final Locale FRANCE = new Locale("fr", "FR");

    /**
     * Locale constant for fr.
     */
    public static final Locale FRENCH = new Locale("fr", "");

    /**
     * Locale constant for de.
     */
    public static final Locale GERMAN = new Locale("de", "");

    /**
     * Locale constant for de_DE.
     */
    public static final Locale GERMANY = new Locale("de", "DE");

    /**
     * Locale constant for it.
     */
    public static final Locale ITALIAN = new Locale("it", "");

    /**
     * Locale constant for it_IT.
     */
    public static final Locale ITALY = new Locale("it", "IT");

    /**
     * Locale constant for ja_JP.
     */
    public static final Locale JAPAN = new Locale("ja", "JP");

    /**
     * Locale constant for ja.
     */
    public static final Locale JAPANESE = new Locale("ja", "");

    /**
     * Locale constant for ko_KR.
     */
    public static final Locale KOREA = new Locale("ko", "KR");

    /**
     * Locale constant for ko.
     */
    public static final Locale KOREAN = new Locale("ko", "");

    /**
     * Locale constant for zh_CN.
     */
    public static final Locale PRC = new Locale("zh", "CN");

    /**
     * Locale constant for the root locale. The root locale has an empty language,
     * country, and variant.
     * 
     * @since 1.6
     * @hide
     */
    public static final Locale ROOT = new Locale("", "", "");

    /**
     * Locale constant for zh_CN.
     */
    public static final Locale SIMPLIFIED_CHINESE = new Locale("zh", "CN");

    /**
     * Locale constant for zh_TW.
     */
    public static final Locale TAIWAN = new Locale("zh", "TW");

    /**
     * Locale constant for zh_TW.
     */
    public static final Locale TRADITIONAL_CHINESE = new Locale("zh", "TW");

    /**
     * Locale constant for en_GB.
     */
    public static final Locale UK = new Locale("en", "GB");

    /**
     * Locale constant for en_US.
     */
    public static final Locale US = new Locale("en", "US");

    private static final PropertyPermission setLocalePermission = new PropertyPermission(
            "user.language", "write");

    static {
        String language = AccessController.doPrivileged(new PriviAction<String>("user.language", "en"));
        String region = AccessController.doPrivileged(new PriviAction<String>("user.region", "US"));
        String variant = AccessController.doPrivileged(new PriviAction<String>("user.variant", ""));
        defaultLocale = new Locale(language, region, variant);
    }

    private transient String countryCode;
    private transient String languageCode;
    private transient String variantCode;
    private transient String cachedToStringResult;

    /**
     * Constructs a default which is used during static initialization of the
     * default for the platform.
     */
    private Locale() {
        languageCode = "en";
        countryCode = "US";
        variantCode = "";
    }

    /**
     * Constructs a new {@code Locale} using the specified language.
     *
     * @param language
     *            the language this {@code Locale} represents.
     */
    public Locale(String language) {
        this(language, "", "");
    }

    /**
     * Constructs a new {@code Locale} using the specified language and country codes.
     *
     * @param language
     *            the language this {@code Locale} represents.
     * @param country
     *            the country this {@code Locale} represents.
     */
    public Locale(String language, String country) {
        this(language, country, "");
    }

    /**
     * Constructs a new {@code Locale} using the specified language, country, and
     * variant codes.
     *
     * @param language
     *            the language this {@code Locale} represents.
     * @param country
     *            the country this {@code Locale} represents.
     * @param variant
     *            the variant this {@code Locale} represents.
     * @throws NullPointerException
     *             if {@code language}, {@code country}, or
     *             {@code variant} is {@code null}.
     */
    public Locale(String language, String country, String variant) {
        if (language == null || country == null || variant == null) {
            throw new NullPointerException();
        }
        if(language.length() == 0 && country.length() == 0){
            languageCode = "";
            countryCode = "";
            variantCode = variant;
            return;
        }
        // BEGIN android-changed
        // this.uLocale = new ULocale(language, country, variant);
        // languageCode = uLocale.getLanguage();
        languageCode = Util.toASCIILowerCase(language);
        // END android-changed
        // Map new language codes to the obsolete language
        // codes so the correct resource bundles will be used.
        if (languageCode.equals("he")) {
            languageCode = "iw";
        } else if (languageCode.equals("id")) {
            languageCode = "in";
        } else if (languageCode.equals("yi")) {
            languageCode = "ji";
        }

        // countryCode is defined in ASCII character set
        // BEGIN android-changed
        // countryCode = country.length()!=0?uLocale.getCountry():"";
        countryCode = Util.toASCIIUpperCase(country);
        // END android-changed

        // Work around for be compatible with RI
        variantCode = variant;
    }

    /**
     * Returns a new {@code Locale} with the same language, country and variant codes as
     * this {@code Locale}.
     *
     * @return a shallow copy of this {@code Locale}.
     * @see java.lang.Cloneable
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e); // android-changed
        }
    }

    /**
     * Compares the specified object to this {@code Locale} and returns whether they are
     * equal. The object must be an instance of {@code Locale} and have the same
     * language, country and variant.
     *
     * @param object
     *            the object to compare with this object.
     * @return {@code true} if the specified object is equal to this {@code Locale},
     *         {@code false} otherwise.
     * @see #hashCode
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Locale) {
            Locale o = (Locale) object;
            return languageCode.equals(o.languageCode)
                    && countryCode.equals(o.countryCode)
                    && variantCode.equals(o.variantCode);
        }
        return false;
    }

    /**
     * Gets the list of installed {@code Locale}s. At least a {@code Locale} that is equal to
     * {@code Locale.US} must be contained in this array.
     * 
     * @return an array of {@code Locale}s.
     */
    public static Locale[] getAvailableLocales() {
        return ICU.getAvailableLocales();
    }

    /**
     * Gets the country code for this {@code Locale} or an empty string of no country
     * was set.
     *
     * @return a country code.
     */
    public String getCountry() {
        return countryCode;
    }

    /**
     * Gets the default {@code Locale}.
     *
     * @return the default {@code Locale}.
     */
    public static Locale getDefault() {
        return defaultLocale;
    }

    /**
     * Gets the full country name in the default {@code Locale} for the country code of
     * this {@code Locale}. If there is no matching country name, the country code is
     * returned.
     *
     * @return a country name.
     */
    public final String getDisplayCountry() {
        return getDisplayCountry(getDefault());
    }

    /**
     * Gets the full country name in the specified {@code Locale} for the country code
     * of this {@code Locale}. If there is no matching country name, the country code is
     * returned.
     *
     * @param locale
     *            the {@code Locale} for which the display name is retrieved.
     * @return a country name.
     */
    public String getDisplayCountry(Locale locale) {
        if (countryCode.length() == 0) {
            return countryCode;
        }
        String result = ICU.getDisplayCountryNative(toString(), locale.toString());
        if (result == null) { // TODO: do we need to do this, or does ICU do it for us?
            result = ICU.getDisplayCountryNative(toString(), Locale.getDefault().toString());
        }
        return result;
    }

    /**
     * Gets the full language name in the default {@code Locale} for the language code
     * of this {@code Locale}. If there is no matching language name, the language code
     * is returned.
     *
     * @return a language name.
     */
    public final String getDisplayLanguage() {
        return getDisplayLanguage(getDefault());
    }

    /**
     * Gets the full language name in the specified {@code Locale} for the language code
     * of this {@code Locale}. If there is no matching language name, the language code
     * is returned.
     *
     * @param locale
     *            the {@code Locale} for which the display name is retrieved.
     * @return a language name.
     */
    public String getDisplayLanguage(Locale locale) {
        if (languageCode.length() == 0) {
            return languageCode;
        }
        String result = ICU.getDisplayLanguageNative(toString(), locale.toString());
        if (result == null) { // TODO: do we need to do this, or does ICU do it for us?
            result = ICU.getDisplayLanguageNative(toString(), Locale.getDefault().toString());
        }
        return result;
    }

    /**
     * Gets the full language, country, and variant names in the default {@code Locale}
     * for the codes of this {@code Locale}.
     *
     * @return a {@code Locale} name.
     */
    public final String getDisplayName() {
        return getDisplayName(getDefault());
    }

    /**
     * Gets the full language, country, and variant names in the specified
     * Locale for the codes of this {@code Locale}.
     *
     * @param locale
     *            the {@code Locale} for which the display name is retrieved.
     * @return a {@code Locale} name.
     */
    public String getDisplayName(Locale locale) {
        int count = 0;
        StringBuilder buffer = new StringBuilder();
        if (languageCode.length() > 0) {
            buffer.append(getDisplayLanguage(locale));
            count++;
        }
        if (countryCode.length() > 0) {
            if (count == 1) {
                buffer.append(" (");
            }
            buffer.append(getDisplayCountry(locale));
            count++;
        }
        if (variantCode.length() > 0) {
            if (count == 1) {
                buffer.append(" (");
            } else if (count == 2) {
                buffer.append(",");
            }
            buffer.append(getDisplayVariant(locale));
            count++;
        }
        if (count > 1) {
            buffer.append(")");
        }
        return buffer.toString();
    }

    /**
     * Gets the full variant name in the default {@code Locale} for the variant code of
     * this {@code Locale}. If there is no matching variant name, the variant code is
     * returned.
     *
     * @return a variant name.
     */
    public final String getDisplayVariant() {
        return getDisplayVariant(getDefault());
    }

    /**
     * Gets the full variant name in the specified {@code Locale} for the variant code
     * of this {@code Locale}. If there is no matching variant name, the variant code is
     * returned.
     *
     * @param locale
     *            the {@code Locale} for which the display name is retrieved.
     * @return a variant name.
     */
    public String getDisplayVariant(Locale locale) {
        if (variantCode.length() == 0) {
            return variantCode;
        }
        String result = ICU.getDisplayVariantNative(toString(), locale.toString());
        if (result == null) { // TODO: do we need to do this, or does ICU do it for us?
            result = ICU.getDisplayVariantNative(toString(), Locale.getDefault().toString());
        }
        return result;
    }

    /**
     * Gets the three letter ISO country code which corresponds to the country
     * code for this {@code Locale}.
     *
     * @return a three letter ISO language code.
     * @throws MissingResourceException
     *                if there is no matching three letter ISO country code.
     */
    public String getISO3Country() throws MissingResourceException {
        if (countryCode.length() == 0) {
            return countryCode;
        }
        return ICU.getISO3CountryNative(toString());
    }

    /**
     * Gets the three letter ISO language code which corresponds to the language
     * code for this {@code Locale}.
     *
     * @return a three letter ISO language code.
     * @throws MissingResourceException
     *                if there is no matching three letter ISO language code.
     */
    public String getISO3Language() throws MissingResourceException {
        if (languageCode.length() == 0) {
            return languageCode;
        }
        return ICU.getISO3LanguageNative(toString());
    }

    /**
     * Gets the list of two letter ISO country codes which can be used as the
     * country code for a {@code Locale}.
     *
     * @return an array of strings.
     */
    public static String[] getISOCountries() {
        return ICU.getISOCountries();
    }

    /**
     * Gets the list of two letter ISO language codes which can be used as the
     * language code for a {@code Locale}.
     *
     * @return an array of strings.
     */
    public static String[] getISOLanguages() {
        return ICU.getISOLanguages();
    }

    /**
     * Gets the language code for this {@code Locale} or the empty string of no language
     * was set.
     *
     * @return a language code.
     */
    public String getLanguage() {
        return languageCode;
    }

    /**
     * Gets the variant code for this {@code Locale} or an empty {@code String} if no variant
     * was set.
     *
     * @return a variant code.
     */
    public String getVariant() {
        return variantCode;
    }

    /**
     * Returns an integer hash code for the receiver. Objects which are equal
     * return the same value for this method.
     *
     * @return the receiver's hash.
     * @see #equals
     */
    @Override
    public synchronized int hashCode() {
        return countryCode.hashCode() + languageCode.hashCode()
                + variantCode.hashCode();
    }

    /**
     * Sets the default {@code Locale} to the specified {@code Locale}.
     *
     * @param locale
     *            the new default {@code Locale}.
     * @throws SecurityException
     *                if there is a {@code SecurityManager} in place which does not allow this
     *                operation.
     */
    public synchronized static void setDefault(Locale locale) {
        if (locale != null) {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkPermission(setLocalePermission);
            }
            defaultLocale = locale;
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * Returns the string representation of this {@code Locale}. It consists of the
     * language code, country code and variant separated by underscores.
     * If the language is missing the string begins
     * with an underscore. If the country is missing there are 2 underscores
     * between the language and the variant. The variant cannot stand alone
     * without a language and/or country code: in this case this method would
     * return the empty string.
     *
     * <p>Examples: "en", "en_US", "_US", "en__POSIX", "en_US_POSIX"
     *
     * @return the string representation of this {@code Locale}.
     */
    @Override
    public final String toString() {
        String result = cachedToStringResult;
        return (result == null) ? (cachedToStringResult = toNewString()) : result;
    }

    private String toNewString() {
        // The string form of a locale that only has a variant is the empty string.
        if (languageCode.length() == 0 && countryCode.length() == 0) {
            return "";
        }
        // Otherwise, the output format is "ll_cc_variant", where language and country are always
        // two letters, but the variant is an arbitrary length. A size of 11 characters has room
        // for "en_US_POSIX", the largest "common" value. (In practice, the string form is almost
        // always 5 characters: "ll_cc".)
        StringBuilder result = new StringBuilder(11);
        result.append(languageCode);
        if (countryCode.length() > 0 || variantCode.length() > 0) {
            result.append('_');
        }
        result.append(countryCode);
        if (variantCode.length() > 0) {
            result.append('_');
        }
        result.append(variantCode);
        return result.toString();
    }

    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("country", String.class),
            new ObjectStreamField("hashcode", Integer.TYPE),
            new ObjectStreamField("language", String.class),
            new ObjectStreamField("variant", String.class) };

    private void writeObject(ObjectOutputStream stream) throws IOException {
        ObjectOutputStream.PutField fields = stream.putFields();
        fields.put("country", countryCode);
        fields.put("hashcode", -1);
        fields.put("language", languageCode);
        fields.put("variant", variantCode);
        stream.writeFields();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = stream.readFields();
        countryCode = (String) fields.get("country", "");
        languageCode = (String) fields.get("language", "");
        variantCode = (String) fields.get("variant", "");
    }
}
