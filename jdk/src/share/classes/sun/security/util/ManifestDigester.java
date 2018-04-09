/*
 * Copyright (c) 1997, 2011, Oracle and/or its affiliates. All rights reserved.
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

package sun.security.util;

import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * This class is used to compute digests on sections of the Manifest.
 */
public class ManifestDigester {

    public static final String MF_MAIN_ATTRS = "Manifest-Main-Attributes";

    /** the raw bytes of the manifest */
    private byte rawBytes[];

    /** the entries grouped by names */
    private HashMap<String, Entry> entries; // key is a UTF-8 string

    /** state returned by findSection */
    static class Position {
        int endOfFirstLine; // not including newline character

        int endOfSection; // end of section, not including the blank line
                          // between sections
        int startOfNext;  // the start of the next section
    }

    /**
     * find a section in the manifest.
     *
     * @param offset should point to the starting offset with in the
     * raw bytes of the next section.
     *
     * @pos set by
     *
     * @returns false if end of bytes has been reached, otherwise returns
     *          true
     */
    @SuppressWarnings("fallthrough")
    private boolean findSection(int offset, Position pos)
    {
        int i = offset, len = rawBytes.length;
        int last = offset;
        int next;
        boolean allBlank = true;

        pos.endOfFirstLine = -1;

        while (i < len) {
            byte b = rawBytes[i];
            switch(b) {
            case '\r':
                if (pos.endOfFirstLine == -1)
                    pos.endOfFirstLine = i-1;
                if ((i < len) &&  (rawBytes[i+1] == '\n'))
                    i++;
                /* fall through */
            case '\n':
                if (pos.endOfFirstLine == -1)
                    pos.endOfFirstLine = i-1;
                if (allBlank || (i == len-1)) {
                    if (i == len-1)
                        pos.endOfSection = i;
                    else
                        pos.endOfSection = last;
                    pos.startOfNext = i+1;
                    return true;
                }
                else {
                    // start of a new line
                    last = i;
                    allBlank = true;
                }
                break;
            default:
                allBlank = false;
                break;
            }
            i++;
        }
        return false;
    }

    public ManifestDigester(byte bytes[])
    {
        rawBytes = bytes;
        entries = new HashMap<String, Entry>();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Position pos = new Position();

        if (!findSection(0, pos))
            return; // XXX: exception?

        // create an entry for main attributes
        entries.put(MF_MAIN_ATTRS, new Entry().addSection(
                new Section(0, pos.endOfSection + 1, pos.startOfNext, rawBytes)));

        int start = pos.startOfNext;
        while(findSection(start, pos)) {
            int len = pos.endOfFirstLine-start+1;
            int sectionLen = pos.endOfSection-start+1;
            int sectionLenWithBlank = pos.startOfNext-start;

            if (len > 6) {
                if (isNameAttr(bytes, start)) {
                    StringBuilder nameBuf = new StringBuilder(sectionLen);

                    try {
                        nameBuf.append(
                            new String(bytes, start+6, len-6, "UTF8"));

                        int i = start + len;
                        if ((i-start) < sectionLen) {
                            if (bytes[i] == '\r') {
                                i += 2;
                            } else {
                                i += 1;
                            }
                        }

                        while ((i-start) < sectionLen) {
                            if (bytes[i++] == ' ') {
                                // name is wrapped
                                int wrapStart = i;
                                while (((i-start) < sectionLen)
                                        && (bytes[i++] != '\n'));
                                    if (bytes[i-1] != '\n')
                                        return; // XXX: exception?
                                    int wrapLen;
                                    if (bytes[i-2] == '\r')
                                        wrapLen = i-wrapStart-2;
                                    else
                                        wrapLen = i-wrapStart-1;

                            nameBuf.append(new String(bytes, wrapStart,
                                                      wrapLen, "UTF8"));
                            } else {
                                break;
                            }
                        }

                        Entry e = entries.get(nameBuf.toString());
                        if (e == null) {
                            entries.put(nameBuf.toString(), new Entry()
                                .addSection(new Section(start, sectionLen,
                                    sectionLenWithBlank, rawBytes)));
                        } else {
                            e.addSection(new Section(start, sectionLen,
                                    sectionLenWithBlank, rawBytes));
                        }

                    } catch (java.io.UnsupportedEncodingException uee) {
                        throw new IllegalStateException(
                            "UTF8 not available on platform");
                    }
                }
            }
            start = pos.startOfNext;
        }
    }

    private boolean isNameAttr(byte bytes[], int start)
    {
        return ((bytes[start] == 'N') || (bytes[start] == 'n')) &&
               ((bytes[start+1] == 'a') || (bytes[start+1] == 'A')) &&
               ((bytes[start+2] == 'm') || (bytes[start+2] == 'M')) &&
               ((bytes[start+3] == 'e') || (bytes[start+3] == 'E')) &&
               (bytes[start+4] == ':') &&
               (bytes[start+5] == ' ');
    }

    public static class Entry {

        // One Entry for one name, and one name can have multiple sections.
        // According to the JAR File Specification: "If there are multiple
        // individual sections for the same file entry, the attributes in
        // these sections are merged."
        private List<Section> sections = new ArrayList<>();
        boolean oldStyle;

        private Entry addSection(Section sec)
        {
            sections.add(sec);
            return this;
        }

        public byte[] digest(MessageDigest md)
        {
            md.reset();
            for (Section sec : sections) {
                if (oldStyle) {
                    Section.doOldStyle(md, sec.rawBytes, sec.offset, sec.lengthWithBlankLine);
                } else {
                    md.update(sec.rawBytes, sec.offset, sec.lengthWithBlankLine);
                }
            }
            return md.digest();
        }

        /** Netscape doesn't include the new line. Intel and JavaSoft do */

        public byte[] digestWorkaround(MessageDigest md)
        {
            md.reset();
            for (Section sec : sections) {
                md.update(sec.rawBytes, sec.offset, sec.length);
            }
            return md.digest();
        }
    }

    private static class Section {
        int offset;
        int length;
        int lengthWithBlankLine;
        byte[] rawBytes;

        public Section(int offset, int length,
                     int lengthWithBlankLine, byte[] rawBytes)
        {
            this.offset = offset;
            this.length = length;
            this.lengthWithBlankLine = lengthWithBlankLine;
            this.rawBytes = rawBytes;
        }

        private static void doOldStyle(MessageDigest md,
                                byte[] bytes,
                                int offset,
                                int length)
        {
            // this is too gross to even document, but here goes
            // the 1.1 jar verification code ignored spaces at the
            // end of lines when calculating digests, so that is
            // what this code does. It only gets called if we
            // are parsing a 1.1 signed signature file
            int i = offset;
            int start = offset;
            int max = offset + length;
            int prev = -1;
            while(i <max) {
                if ((bytes[i] == '\r') && (prev == ' ')) {
                    md.update(bytes, start, i-start-1);
                    start = i;
                }
                prev = bytes[i];
                i++;
            }
            md.update(bytes, start, i-start);
        }
    }

    public Entry get(String name, boolean oldStyle) {
        Entry e = entries.get(name);
        if (e != null)
            e.oldStyle = oldStyle;
        return e;
    }

    public byte[] manifestDigest(MessageDigest md)
        {
            md.reset();
            md.update(rawBytes, 0, rawBytes.length);
            return md.digest();
        }

}
