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

package java.nio.charset;

/**
 * Provides convenient access to the most important built-in charsets. Saves a hash lookup and
 * unnecessary handling of UnsupportedEncodingException at call sites, compared to using the
 * charset's name.
 *
 * @hide internal use only
 */
public class Charsets {
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    public static final Charset US_ASCII = Charset.forName("US-ASCII");

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    private Charsets() {
    }
}
