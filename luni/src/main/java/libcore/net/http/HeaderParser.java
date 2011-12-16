/*
 * Copyright (C) 2011 The Android Open Source Project
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

package libcore.net.http;

import java.util.ArrayList;
import java.util.List;

final class HeaderParser {

    public interface CacheControlHandler {
        void handle(String directive, String parameter);
    }

    /**
     * Parse a comma-separated list of cache control header values.
     */
    public static void parseCacheControl(String value, CacheControlHandler handler) {
        int pos = 0;
        while (pos < value.length()) {
            int tokenStart = pos;
            pos = skipUntil(value, pos, "=,");
            String directive = value.substring(tokenStart, pos).trim();

            if (pos == value.length() || value.charAt(pos) == ',') {
                pos++; // consume ',' (if necessary)
                handler.handle(directive, null);
                continue;
            }

            pos++; // consume '='
            pos = skipWhitespace(value, pos);

            String parameter;

            // quoted string
            if (pos < value.length() && value.charAt(pos) == '\"') {
                pos++; // consume '"' open quote
                int parameterStart = pos;
                pos = skipUntil(value, pos, "\"");
                parameter = value.substring(parameterStart, pos);
                pos++; // consume '"' close quote (if necessary)

            // unquoted string
            } else {
                int parameterStart = pos;
                pos = skipUntil(value, pos, ",");
                parameter = value.substring(parameterStart, pos).trim();
            }

            handler.handle(directive, parameter);
        }
    }

    /**
     * Parse RFC 2617 challenges. This API is only interested in the scheme
     * name and realm.
     */
    public static List<Challenge> parseChallenges(
            RawHeaders responseHeaders, String challengeHeader) {
        /*
         * auth-scheme = token
         * auth-param  = token "=" ( token | quoted-string )
         * challenge   = auth-scheme 1*SP 1#auth-param
         * realm       = "realm" "=" realm-value
         * realm-value = quoted-string
         */
        List<Challenge> result = new ArrayList<Challenge>();
        for (int h = 0; h < responseHeaders.length(); h++) {
            if (!challengeHeader.equalsIgnoreCase(responseHeaders.getFieldName(h))) {
                continue;
            }
            String value = responseHeaders.getValue(h);
            int pos = 0;
            while (pos < value.length()) {
                int tokenStart = pos;
                pos = skipUntil(value, pos, " ");

                String scheme = value.substring(tokenStart, pos).trim();
                pos = skipWhitespace(value, pos);

                // TODO: This currently only handles schemes with a 'realm' parameter;
                //       It needs to be fixed to handle any scheme and any parameters
                //       http://code.google.com/p/android/issues/detail?id=11140

                if (!value.regionMatches(pos, "realm=\"", 0, "realm=\"".length())) {
                    break; // unexpected challenge parameter; give up
                }

                pos += "realm=\"".length();
                int realmStart = pos;
                pos = skipUntil(value, pos, "\"");
                String realm = value.substring(realmStart, pos);
                pos++; // consume '"' close quote
                pos = skipUntil(value, pos, ",");
                pos++; // consume ',' comma
                pos = skipWhitespace(value, pos);
                result.add(new Challenge(scheme, realm));
            }
        }
        return result;
    }

    /**
     * Returns the next index in {@code input} at or after {@code pos} that
     * contains a character from {@code characters}. Returns the input length if
     * none of the requested characters can be found.
     */
    private static int skipUntil(String input, int pos, String characters) {
        for (; pos < input.length(); pos++) {
            if (characters.indexOf(input.charAt(pos)) != -1) {
                break;
            }
        }
        return pos;
    }

    /**
     * Returns the next non-whitespace character in {@code input} that is white
     * space. Result is undefined if input contains newline characters.
     */
    private static int skipWhitespace(String input, int pos) {
        for (; pos < input.length(); pos++) {
            char c = input.charAt(pos);
            if (c != ' ' && c != '\t') {
                break;
            }
        }
        return pos;
    }

    /**
     * Returns {@code value} as a positive integer, or 0 if it is negative, or
     * -1 if it cannot be parsed.
     */
    public static int parseSeconds(String value) {
        try {
            long seconds = Long.parseLong(value);
            if (seconds > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else if (seconds < 0) {
                return 0;
            } else {
                return (int) seconds;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
