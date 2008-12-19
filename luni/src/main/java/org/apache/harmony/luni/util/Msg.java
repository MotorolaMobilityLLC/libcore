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

package org.apache.harmony.luni.util;


import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
// BEGIN android-added
import java.lang.ref.WeakReference;
import java.util.logging.Logger;
// END android-added

import org.apache.harmony.luni.util.MsgHelp;

/**
 * This class retrieves strings from a resource bundle and returns them,
 * formatting them with MessageFormat when required.
 * <p>
 * It is used by the system classes to provide national language support, by
 * looking up messages in the <code>
 *    org.apache.harmony.luni.util.ExternalMessages
 * </code>
 * resource bundle. Note that if this file is not available, or an invalid key
 * is looked up, or resource bundle support is not available, the key itself
 * will be returned as the associated message. This means that the <em>KEY</em>
 * should a reasonable human-readable (English) string.
 * 
 */
public class Msg {
    // BEGIN android-changed
    private static final String sResource =
        "org.apache.harmony.luni.util.ExternalMessages";
    // END android-changed

    /**
     * Retrieves a message which has no arguments.
     * 
     * @param msg
     *            String the key to look up.
     * @return String the message for that key in the system message bundle.
     */
    static public String getString(String msg) {
        // BEGIN android-changed
        ResourceBundle bundle = MsgHelp.loadBundle(sResource);
        if (bundle == null) {
            return msg;
        }
        // END android-changed
        try {
            return bundle.getString(msg);
        } catch (MissingResourceException e) {
            return msg;
        }
    }

    /**
     * Retrieves a message which takes 1 argument.
     * 
     * @param msg
     *            String the key to look up.
     * @param arg
     *            Object the object to insert in the formatted output.
     * @return String the message for that key in the system message bundle.
     */
    static public String getString(String msg, Object arg) {
        return getString(msg, new Object[] { arg });
    }

    /**
     * Retrieves a message which takes 1 integer argument.
     * 
     * @param msg
     *            String the key to look up.
     * @param arg
     *            int the integer to insert in the formatted output.
     * @return String the message for that key in the system message bundle.
     */
    static public String getString(String msg, int arg) {
        return getString(msg, new Object[] { Integer.toString(arg) });
    }

    /**
     * Retrieves a message which takes 1 character argument.
     * 
     * @param msg
     *            String the key to look up.
     * @param arg
     *            char the character to insert in the formatted output.
     * @return String the message for that key in the system message bundle.
     */
    static public String getString(String msg, char arg) {
        return getString(msg, new Object[] { String.valueOf(arg) });
    }

    /**
     * Retrieves a message which takes 2 arguments.
     * 
     * @param msg
     *            String the key to look up.
     * @param arg1
     *            Object an object to insert in the formatted output.
     * @param arg2
     *            Object another object to insert in the formatted output.
     * @return String the message for that key in the system message bundle.
     */
    static public String getString(String msg, Object arg1, Object arg2) {
        return getString(msg, new Object[] { arg1, arg2 });
    }

    /**
     * Retrieves a message which takes several arguments.
     * 
     * @param msg
     *            String the key to look up.
     * @param args
     *            Object[] the objects to insert in the formatted output.
     * @return String the message for that key in the system message bundle.
     */
    static public String getString(String msg, Object[] args) {
        String format = msg;
        // BEGIN android-added
        ResourceBundle bundle = MsgHelp.loadBundle(sResource);
        // END android-added
        if (bundle != null) {
            try {
                format = bundle.getString(msg);
            } catch (MissingResourceException e) {
            }
        }

        return MsgHelp.format(format, args);
    }
}
