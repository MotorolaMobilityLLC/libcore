// This is a generated file: do not edit! Edit keysym2ucs.h if necessary.

/*
 * Copyright (c) 2005, 2009, Oracle and/or its affiliates. All rights reserved.
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

package sun.awt.X11;
import java.util.Hashtable;
import sun.misc.Unsafe;

import sun.util.logging.PlatformLogger;

public class XKeysym {

    public static void main( String args[] ) {
       System.out.println( "Cyrillc zhe:"+convertKeysym(0x06d6, 0));
       System.out.println( "Arabic sheen:"+convertKeysym(0x05d4, 0));
       System.out.println( "Latin a breve:"+convertKeysym(0x01e3, 0));
       System.out.println( "Latin f:"+convertKeysym(0x066, 0));
       System.out.println( "Backspace:"+Integer.toHexString(convertKeysym(0xff08, 0)));
       System.out.println( "Ctrl+f:"+Integer.toHexString(convertKeysym(0x066, XConstants.ControlMask)));
    }

    private XKeysym() {}

    static class Keysym2JavaKeycode  {
        int jkeycode;
        int keyLocation;
        int getJavaKeycode() {
            return jkeycode;
        }
        int getKeyLocation() {
            return keyLocation;
        }
        Keysym2JavaKeycode(int jk, int loc) {
            jkeycode = jk;
            keyLocation = loc;
        }
    };
    private static Unsafe unsafe = XlibWrapper.unsafe;
    static Hashtable<Long, Keysym2JavaKeycode>  keysym2JavaKeycodeHash = new Hashtable<Long, Keysym2JavaKeycode>();
    static Hashtable<Long, Character> keysym2UCSHash = new Hashtable<Long, Character>();
    static Hashtable<Long, Long> uppercaseHash = new Hashtable<Long, Long>();
    // TODO: or not to do: add reverse lookup javakeycode2keysym,
    // for robot only it seems to me. After that, we can remove lookup table
    // from XWindow.c altogether.
    // Another use for reverse lookup: query keyboard state, for some keys.
    static Hashtable<Integer, Long> javaKeycode2KeysymHash = new Hashtable<Integer, Long>();
    static long keysym_lowercase = unsafe.allocateMemory(Native.getLongSize());
    static long keysym_uppercase = unsafe.allocateMemory(Native.getLongSize());
    static Keysym2JavaKeycode kanaLock = new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_KANA_LOCK,
                                                                java.awt.event.KeyEvent.KEY_LOCATION_STANDARD);
    private static PlatformLogger keyEventLog = PlatformLogger.getLogger("sun.awt.X11.kye.XKeysym");
    public static char convertKeysym( long ks, int state ) {

        /* First check for Latin-1 characters (1:1 mapping) */
        if ((ks >= 0x0020 && ks <= 0x007e) ||
            (ks >= 0x00a0 && ks <= 0x00ff)) {
            if( (state & XConstants.ControlMask) != 0 ) {
                if ((ks >= 'A' && ks <= ']') || (ks == '_') ||
                    (ks >= 'a' && ks <='z')) {
                    ks &= 0x1F;
                }
            }
            return (char)ks;
        }

        /* XXX: Also check for directly encoded 24-bit UCS characters:
         */
        if ((ks & 0xff000000) == 0x01000000)
          return (char)(ks & 0x00ffffff);

        Character ch = keysym2UCSHash.get(ks);
        return ch == null ? (char)0 : ch.charValue();
    }
    static long xkeycode2keysym_noxkb(XKeyEvent ev, int ndx) {
        XToolkit.awtLock();
        try {
            return XlibWrapper.XKeycodeToKeysym(ev.get_display(), ev.get_keycode(), ndx);
        } finally {
            XToolkit.awtUnlock();
        }
    }
    static long xkeycode2keysym_xkb(XKeyEvent ev, int ndx) {
        XToolkit.awtLock();
        try {
            int mods = ev.get_state();
            if ((ndx == 0) && ((mods & XConstants.ShiftMask) != 0)) {
                // I don't know all possible meanings of 'ndx' in case of XKB
                // and don't want to speculate. But this particular case
                // clearly means that caller needs a so called primary keysym.
                mods ^= XConstants.ShiftMask;
            }
            long kbdDesc = XToolkit.getXKBKbdDesc();
            if( kbdDesc != 0 ) {
                XlibWrapper.XkbTranslateKeyCode(kbdDesc, ev.get_keycode(),
                       mods, XlibWrapper.iarg1, XlibWrapper.larg3);
            }else{
                // xkb resources already gone
                keyEventLog.fine("Thread race: Toolkit shutdown before the end of a key event processing.");
                return 0;
            }
            //XXX unconsumed modifiers?
            return Native.getLong(XlibWrapper.larg3);
        } finally {
            XToolkit.awtUnlock();
        }
    }
    static long xkeycode2keysym(XKeyEvent ev, int ndx) {
        XToolkit.awtLock();
        try {
            if (XToolkit.canUseXKBCalls()) {
                return xkeycode2keysym_xkb(ev, ndx);
            }else{
                return xkeycode2keysym_noxkb(ev, ndx);
            }
        } finally {
            XToolkit.awtUnlock();
        }
    }
    static long xkeycode2primary_keysym(XKeyEvent ev) {
        return xkeycode2keysym(ev, 0);
    }
    public static boolean isKPEvent( XKeyEvent ev )
    {
        // Xsun without XKB uses keysymarray[2] keysym to determine if it is KP event.
        // Otherwise, it is [1].
        int ndx = XToolkit.isXsunKPBehavior() &&
                  ! XToolkit.isXKBenabled() ? 2 : 1;
        // Even if XKB is enabled, we have another problem: some symbol tables (e.g. cz) force
        // a regular comma instead of KP_comma for a decimal separator. Result is,
        // bugs like 6454041. So, we will try for keypadness  a keysym with ndx==0 as well.
        XToolkit.awtLock();
        try {
            return (XlibWrapper.IsKeypadKey(
                XlibWrapper.XKeycodeToKeysym(ev.get_display(), ev.get_keycode(), ndx ) ) ||
                   XlibWrapper.IsKeypadKey(
                XlibWrapper.XKeycodeToKeysym(ev.get_display(), ev.get_keycode(), 0 ) ));
        } finally {
            XToolkit.awtUnlock();
        }
    }
    /**
        Return uppercase keysym correspondent to a given keysym.
        If input keysym does not belong to any lower/uppercase pair, return -1.
    */
    public static long getUppercaseAlphabetic( long keysym ) {
        long lc = -1;
        long uc = -1;
        Long stored =  uppercaseHash.get(keysym);
        if (stored != null ) {
            return stored.longValue();
        }
        XToolkit.awtLock();
        try {
            XlibWrapper.XConvertCase(keysym, keysym_lowercase, keysym_uppercase);
            lc = Native.getLong(keysym_lowercase);
            uc = Native.getLong(keysym_uppercase);
            if (lc == uc) {
                //not applicable
                uc = -1;
            }
            uppercaseHash.put(keysym, uc);
        } finally {
            XToolkit.awtUnlock();
        }
        return uc;
    }
    /**
        Get a keypad keysym derived from a keycode.
        I do not check if this is a keypad event, I just presume it.
    */
    private static long getKeypadKeysym( XKeyEvent ev ) {
        int ndx = 0;
        long keysym = XConstants.NoSymbol;
        if( XToolkit.isXsunKPBehavior() &&
            ! XToolkit.isXKBenabled() ) {
            if( (ev.get_state() & XConstants.ShiftMask) != 0 ) { // shift modifier is on
                ndx = 3;
                keysym = xkeycode2keysym(ev, ndx);
            } else {
                ndx = 2;
                keysym = xkeycode2keysym(ev, ndx);
            }
        } else {
            if( (ev.get_state() & XConstants.ShiftMask) != 0 || // shift modifier is on
                ((ev.get_state() & XConstants.LockMask) != 0 && // lock modifier is on
                 (XToolkit.modLockIsShiftLock != 0)) ) {     // it is interpreted as ShiftLock
                ndx = 0;
                keysym = xkeycode2keysym(ev, ndx);
            } else {
                ndx = 1;
                keysym = xkeycode2keysym(ev, ndx);
            }
        }
        return keysym;
    }

    /**
        Return java.awt.KeyEvent constant meaning (Java) keycode, derived from X keysym.
        Some keysyms maps to more than one keycode, these would require extra processing.
    */
    static Keysym2JavaKeycode getJavaKeycode( long keysym ) {
        if(keysym == XKeySymConstants.XK_Mode_switch){
           /* XK_Mode_switch on solaris maps either to VK_ALT_GRAPH (default) or VK_KANA_LOCK */
           if( XToolkit.isKanaKeyboard() ) {
               return kanaLock;
           }
        }else if(keysym == XKeySymConstants.XK_L1){
           /* if it is Sun keyboard, trick hash to return VK_STOP else VK_F11 (default) */
           if( XToolkit.isSunKeyboard() ) {
               keysym = XKeySymConstants.SunXK_Stop;
           }
        }else if(keysym == XKeySymConstants.XK_L2) {
           /* if it is Sun keyboard, trick hash to return VK_AGAIN else VK_F12 (default) */
           if( XToolkit.isSunKeyboard() ) {
               keysym = XKeySymConstants.SunXK_Again;
           }
        }

        return  keysym2JavaKeycodeHash.get( keysym );
    }
    /**
        Return java.awt.KeyEvent constant meaning (Java) keycode, derived from X Window KeyEvent.
        Algorithm is, extract via XKeycodeToKeysym  a proper keysym according to Xlib spec rules and
        err exceptions, then search a java keycode in a table.
    */
    static Keysym2JavaKeycode getJavaKeycode( XKeyEvent ev ) {
        // get from keysym2JavaKeycodeHash.
        long keysym = XConstants.NoSymbol;
        int ndx = 0;
        if( (ev.get_state() & XToolkit.numLockMask) != 0 &&
             isKPEvent(ev)) {
            keysym = getKeypadKeysym( ev );
        } else {
            // we only need primary-layer keysym to derive a java keycode.
            ndx = 0;
            keysym = xkeycode2keysym(ev, ndx);
        }

        Keysym2JavaKeycode jkc = getJavaKeycode( keysym );
        return jkc;
    }
    static int getJavaKeycodeOnly( XKeyEvent ev ) {
        Keysym2JavaKeycode jkc = getJavaKeycode( ev );
        return jkc == null ? java.awt.event.KeyEvent.VK_UNDEFINED : jkc.getJavaKeycode();
    }
    /**
     * Return an integer java keycode apprx as it was before extending keycodes range.
     * This call would ignore for instance XKB and process whatever is on the bottom
     * of keysym stack. Result will not depend on actual locale, will differ between
     * dual/multiple keyboard setup systems (e.g. English+Russian vs French+Russian)
     * but will be someway compatible with old releases.
     */
    static int getLegacyJavaKeycodeOnly( XKeyEvent ev ) {
        long keysym = XConstants.NoSymbol;
        int ndx = 0;
        if( (ev.get_state() & XToolkit.numLockMask) != 0 &&
             isKPEvent(ev)) {
            keysym = getKeypadKeysym( ev );
        } else {
            // we only need primary-layer keysym to derive a java keycode.
            ndx = 0;
            keysym = xkeycode2keysym_noxkb(ev, ndx);
        }
        Keysym2JavaKeycode jkc = getJavaKeycode( keysym );
        return jkc == null ? java.awt.event.KeyEvent.VK_UNDEFINED : jkc.getJavaKeycode();
    }
    static long javaKeycode2Keysym( int jkey ) {
        Long ks = javaKeycode2KeysymHash.get( jkey );
        return  (ks == null ? 0 : ks.longValue());
    }
    /**
        Return keysym derived from a keycode and modifiers.
        Usually an input method does this. However non-system input methods (e.g. Java IMs) do not.
        For rules, see "Xlib - C Language X Interface",
                        MIT X Consortium Standard
                        X Version 11, Release 6
                        Ch. 12.7
        XXX TODO: or maybe not to do: process Mode Lock and therefore
        not only 0-th and 1-st but 2-nd and 3-rd keysyms for a keystroke.
    */
    static long getKeysym( XKeyEvent ev ) {
        long keysym = XConstants.NoSymbol;
        long uppercaseKeysym = XConstants.NoSymbol;
        int  ndx = 0;
        boolean getUppercase = false;
        if ((ev.get_state() & XToolkit.numLockMask) != 0 &&
             isKPEvent(ev)) {
            keysym = getKeypadKeysym( ev );
        } else {
            // XXX: at this point, anything in keysym[23] is ignored.
            //
            // Shift & Lock are off ===> ndx = 0;
            // Shift off & Lock on & Lock is CapsLock ===> ndx = 0;
            //       if keysym[ndx] is lowecase alphabetic, then corresp. uppercase used.
            // Shift on & Lock on & Lock is CapsLock ===> ndx == 1;
            //       if keysym[ndx] is lowecase alphabetic, then corresp. uppercase used.
            // Shift on || (Lock on & Lock is ShiftLock) ===> ndx = 1.
            if ((ev.get_state() & XConstants.ShiftMask) == 0) {     // shift is off
                if ((ev.get_state() & XConstants.LockMask) == 0 ) {  // lock is off
                    ndx = 0;
                    getUppercase = false;
                } else if ((ev.get_state() & XConstants.LockMask) != 0 && // lock is on
                     (XToolkit.modLockIsShiftLock == 0)) { // lock is capslock
                    ndx = 0;
                    getUppercase = true;
                } else if ((ev.get_state() & XConstants.LockMask) != 0 && // lock is on
                     (XToolkit.modLockIsShiftLock != 0)) { // lock is shift lock
                    ndx = 1;
                    getUppercase = false;
                }
            } else { // shift on
                if ((ev.get_state() & XConstants.LockMask) != 0 && // lock is on
                     (XToolkit.modLockIsShiftLock == 0)) { // lock is capslock
                    ndx = 1;
                    getUppercase = true;
                } else {
                    ndx = 1;
                    getUppercase = false;
                }
            }
            keysym = xkeycode2keysym(ev, ndx);
            if (getUppercase && (uppercaseKeysym =  getUppercaseAlphabetic( keysym )) != -1) {
                keysym = uppercaseKeysym;
            }
        }
        return keysym;
    }

    static {
        keysym2UCSHash.put( (long)0xFF08, (char)0x0008); // XK_BackSpace --> <control>
        keysym2UCSHash.put( (long)0xFF09, (char)0x0009); // XK_Tab --> <control>
        keysym2UCSHash.put( (long)0xFF0A, (char)0x000a); // XK_Linefeed --> <control>
        keysym2UCSHash.put( (long)0xFF0B, (char)0x000b); // XK_Clear --> <control>
        keysym2UCSHash.put( (long)0xFF0D, (char)0x000a); // XK_Return --> <control>
        keysym2UCSHash.put( (long)0xFF1B, (char)0x001B); // XK_Escape --> <control>
        keysym2UCSHash.put( (long)0xFFFF, (char)0x007F); // XK_Delete --> <control>
        keysym2UCSHash.put( (long)0xFF80, (char)0x0020); // XK_KP_Space --> SPACE
        keysym2UCSHash.put( (long)0xFF89, (char)0x0009); // XK_KP_Tab --> <control>
        keysym2UCSHash.put( (long)0xFF8D, (char)0x000A); // XK_KP_Enter --> <control>
        keysym2UCSHash.put( (long)0xFF9F, (char)0x007F); // XK_KP_Delete --> <control>
        keysym2UCSHash.put( (long)0xFFBD, (char)0x003d); // XK_KP_Equal --> EQUALS SIGN
        keysym2UCSHash.put( (long)0xFFAA, (char)0x002a); // XK_KP_Multiply --> ASTERISK
        keysym2UCSHash.put( (long)0xFFAB, (char)0x002b); // XK_KP_Add --> PLUS SIGN
        keysym2UCSHash.put( (long)0xFFAC, (char)0x002c); // XK_KP_Separator --> COMMA
        keysym2UCSHash.put( (long)0xFFAD, (char)0x002d); // XK_KP_Subtract --> HYPHEN-MINUS
        keysym2UCSHash.put( (long)0xFFAE, (char)0x002e); // XK_KP_Decimal --> FULL STOP
        keysym2UCSHash.put( (long)0xFFAF, (char)0x002f); // XK_KP_Divide --> SOLIDUS
        keysym2UCSHash.put( (long)0xFFB0, (char)0x0030); // XK_KP_0 --> DIGIT ZERO
        keysym2UCSHash.put( (long)0xFFB1, (char)0x0031); // XK_KP_1 --> DIGIT ONE
        keysym2UCSHash.put( (long)0xFFB2, (char)0x0032); // XK_KP_2 --> DIGIT TWO
        keysym2UCSHash.put( (long)0xFFB3, (char)0x0033); // XK_KP_3 --> DIGIT THREE
        keysym2UCSHash.put( (long)0xFFB4, (char)0x0034); // XK_KP_4 --> DIGIT FOUR
        keysym2UCSHash.put( (long)0xFFB5, (char)0x0035); // XK_KP_5 --> DIGIT FIVE
        keysym2UCSHash.put( (long)0xFFB6, (char)0x0036); // XK_KP_6 --> DIGIT SIX
        keysym2UCSHash.put( (long)0xFFB7, (char)0x0037); // XK_KP_7 --> DIGIT SEVEN
        keysym2UCSHash.put( (long)0xFFB8, (char)0x0038); // XK_KP_8 --> DIGIT EIGHT
        keysym2UCSHash.put( (long)0xFFB9, (char)0x0039); // XK_KP_9 --> DIGIT NINE
        keysym2UCSHash.put( (long)0xFE20, (char)0x0009); // XK_ISO_Left_Tab --> <control>
        keysym2UCSHash.put( (long)0x1a1, (char)0x0104); // XK_Aogonek --> LATIN CAPITAL LETTER A WITH OGONEK
        keysym2UCSHash.put( (long)0x1a2, (char)0x02d8); // XK_breve --> BREVE
        keysym2UCSHash.put( (long)0x1a3, (char)0x0141); // XK_Lstroke --> LATIN CAPITAL LETTER L WITH STROKE
        keysym2UCSHash.put( (long)0x1a5, (char)0x013d); // XK_Lcaron --> LATIN CAPITAL LETTER L WITH CARON
        keysym2UCSHash.put( (long)0x1a6, (char)0x015a); // XK_Sacute --> LATIN CAPITAL LETTER S WITH ACUTE
        keysym2UCSHash.put( (long)0x1a9, (char)0x0160); // XK_Scaron --> LATIN CAPITAL LETTER S WITH CARON
        keysym2UCSHash.put( (long)0x1aa, (char)0x015e); // XK_Scedilla --> LATIN CAPITAL LETTER S WITH CEDILLA
        keysym2UCSHash.put( (long)0x1ab, (char)0x0164); // XK_Tcaron --> LATIN CAPITAL LETTER T WITH CARON
        keysym2UCSHash.put( (long)0x1ac, (char)0x0179); // XK_Zacute --> LATIN CAPITAL LETTER Z WITH ACUTE
        keysym2UCSHash.put( (long)0x1ae, (char)0x017d); // XK_Zcaron --> LATIN CAPITAL LETTER Z WITH CARON
        keysym2UCSHash.put( (long)0x1af, (char)0x017b); // XK_Zabovedot --> LATIN CAPITAL LETTER Z WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x1b1, (char)0x0105); // XK_aogonek --> LATIN SMALL LETTER A WITH OGONEK
        keysym2UCSHash.put( (long)0x1b2, (char)0x02db); // XK_ogonek --> OGONEK
        keysym2UCSHash.put( (long)0x1b3, (char)0x0142); // XK_lstroke --> LATIN SMALL LETTER L WITH STROKE
        keysym2UCSHash.put( (long)0x1b5, (char)0x013e); // XK_lcaron --> LATIN SMALL LETTER L WITH CARON
        keysym2UCSHash.put( (long)0x1b6, (char)0x015b); // XK_sacute --> LATIN SMALL LETTER S WITH ACUTE
        keysym2UCSHash.put( (long)0x1b7, (char)0x02c7); // XK_caron --> CARON
        keysym2UCSHash.put( (long)0x1b9, (char)0x0161); // XK_scaron --> LATIN SMALL LETTER S WITH CARON
        keysym2UCSHash.put( (long)0x1ba, (char)0x015f); // XK_scedilla --> LATIN SMALL LETTER S WITH CEDILLA
        keysym2UCSHash.put( (long)0x1bb, (char)0x0165); // XK_tcaron --> LATIN SMALL LETTER T WITH CARON
        keysym2UCSHash.put( (long)0x1bc, (char)0x017a); // XK_zacute --> LATIN SMALL LETTER Z WITH ACUTE
        keysym2UCSHash.put( (long)0x1bd, (char)0x02dd); // XK_doubleacute --> DOUBLE ACUTE ACCENT
        keysym2UCSHash.put( (long)0x1be, (char)0x017e); // XK_zcaron --> LATIN SMALL LETTER Z WITH CARON
        keysym2UCSHash.put( (long)0x1bf, (char)0x017c); // XK_zabovedot --> LATIN SMALL LETTER Z WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x1c0, (char)0x0154); // XK_Racute --> LATIN CAPITAL LETTER R WITH ACUTE
        keysym2UCSHash.put( (long)0x1c3, (char)0x0102); // XK_Abreve --> LATIN CAPITAL LETTER A WITH BREVE
        keysym2UCSHash.put( (long)0x1c5, (char)0x0139); // XK_Lacute --> LATIN CAPITAL LETTER L WITH ACUTE
        keysym2UCSHash.put( (long)0x1c6, (char)0x0106); // XK_Cacute --> LATIN CAPITAL LETTER C WITH ACUTE
        keysym2UCSHash.put( (long)0x1c8, (char)0x010c); // XK_Ccaron --> LATIN CAPITAL LETTER C WITH CARON
        keysym2UCSHash.put( (long)0x1ca, (char)0x0118); // XK_Eogonek --> LATIN CAPITAL LETTER E WITH OGONEK
        keysym2UCSHash.put( (long)0x1cc, (char)0x011a); // XK_Ecaron --> LATIN CAPITAL LETTER E WITH CARON
        keysym2UCSHash.put( (long)0x1cf, (char)0x010e); // XK_Dcaron --> LATIN CAPITAL LETTER D WITH CARON
        keysym2UCSHash.put( (long)0x1d0, (char)0x0110); // XK_Dstroke --> LATIN CAPITAL LETTER D WITH STROKE
        keysym2UCSHash.put( (long)0x1d1, (char)0x0143); // XK_Nacute --> LATIN CAPITAL LETTER N WITH ACUTE
        keysym2UCSHash.put( (long)0x1d2, (char)0x0147); // XK_Ncaron --> LATIN CAPITAL LETTER N WITH CARON
        keysym2UCSHash.put( (long)0x1d5, (char)0x0150); // XK_Odoubleacute --> LATIN CAPITAL LETTER O WITH DOUBLE ACUTE
        keysym2UCSHash.put( (long)0x1d8, (char)0x0158); // XK_Rcaron --> LATIN CAPITAL LETTER R WITH CARON
        keysym2UCSHash.put( (long)0x1d9, (char)0x016e); // XK_Uring --> LATIN CAPITAL LETTER U WITH RING ABOVE
        keysym2UCSHash.put( (long)0x1db, (char)0x0170); // XK_Udoubleacute --> LATIN CAPITAL LETTER U WITH DOUBLE ACUTE
        keysym2UCSHash.put( (long)0x1de, (char)0x0162); // XK_Tcedilla --> LATIN CAPITAL LETTER T WITH CEDILLA
        keysym2UCSHash.put( (long)0x1e0, (char)0x0155); // XK_racute --> LATIN SMALL LETTER R WITH ACUTE
        keysym2UCSHash.put( (long)0x1e3, (char)0x0103); // XK_abreve --> LATIN SMALL LETTER A WITH BREVE
        keysym2UCSHash.put( (long)0x1e5, (char)0x013a); // XK_lacute --> LATIN SMALL LETTER L WITH ACUTE
        keysym2UCSHash.put( (long)0x1e6, (char)0x0107); // XK_cacute --> LATIN SMALL LETTER C WITH ACUTE
        keysym2UCSHash.put( (long)0x1e8, (char)0x010d); // XK_ccaron --> LATIN SMALL LETTER C WITH CARON
        keysym2UCSHash.put( (long)0x1ea, (char)0x0119); // XK_eogonek --> LATIN SMALL LETTER E WITH OGONEK
        keysym2UCSHash.put( (long)0x1ec, (char)0x011b); // XK_ecaron --> LATIN SMALL LETTER E WITH CARON
        keysym2UCSHash.put( (long)0x1ef, (char)0x010f); // XK_dcaron --> LATIN SMALL LETTER D WITH CARON
        keysym2UCSHash.put( (long)0x1f0, (char)0x0111); // XK_dstroke --> LATIN SMALL LETTER D WITH STROKE
        keysym2UCSHash.put( (long)0x1f1, (char)0x0144); // XK_nacute --> LATIN SMALL LETTER N WITH ACUTE
        keysym2UCSHash.put( (long)0x1f2, (char)0x0148); // XK_ncaron --> LATIN SMALL LETTER N WITH CARON
        keysym2UCSHash.put( (long)0x1f5, (char)0x0151); // XK_odoubleacute --> LATIN SMALL LETTER O WITH DOUBLE ACUTE
        keysym2UCSHash.put( (long)0x1fb, (char)0x0171); // XK_udoubleacute --> LATIN SMALL LETTER U WITH DOUBLE ACUTE
        keysym2UCSHash.put( (long)0x1f8, (char)0x0159); // XK_rcaron --> LATIN SMALL LETTER R WITH CARON
        keysym2UCSHash.put( (long)0x1f9, (char)0x016f); // XK_uring --> LATIN SMALL LETTER U WITH RING ABOVE
        keysym2UCSHash.put( (long)0x1fe, (char)0x0163); // XK_tcedilla --> LATIN SMALL LETTER T WITH CEDILLA
        keysym2UCSHash.put( (long)0x1ff, (char)0x02d9); // XK_abovedot --> DOT ABOVE
        keysym2UCSHash.put( (long)0x2a1, (char)0x0126); // XK_Hstroke --> LATIN CAPITAL LETTER H WITH STROKE
        keysym2UCSHash.put( (long)0x2a6, (char)0x0124); // XK_Hcircumflex --> LATIN CAPITAL LETTER H WITH CIRCUMFLEX
        keysym2UCSHash.put( (long)0x2a9, (char)0x0130); // XK_Iabovedot --> LATIN CAPITAL LETTER I WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x2ab, (char)0x011e); // XK_Gbreve --> LATIN CAPITAL LETTER G WITH BREVE
        keysym2UCSHash.put( (long)0x2ac, (char)0x0134); // XK_Jcircumflex --> LATIN CAPITAL LETTER J WITH CIRCUMFLEX
        keysym2UCSHash.put( (long)0x2b1, (char)0x0127); // XK_hstroke --> LATIN SMALL LETTER H WITH STROKE
        keysym2UCSHash.put( (long)0x2b6, (char)0x0125); // XK_hcircumflex --> LATIN SMALL LETTER H WITH CIRCUMFLEX
        keysym2UCSHash.put( (long)0x2b9, (char)0x0131); // XK_idotless --> LATIN SMALL LETTER DOTLESS I
        keysym2UCSHash.put( (long)0x2bb, (char)0x011f); // XK_gbreve --> LATIN SMALL LETTER G WITH BREVE
        keysym2UCSHash.put( (long)0x2bc, (char)0x0135); // XK_jcircumflex --> LATIN SMALL LETTER J WITH CIRCUMFLEX
        keysym2UCSHash.put( (long)0x2c5, (char)0x010a); // XK_Cabovedot --> LATIN CAPITAL LETTER C WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x2c6, (char)0x0108); // XK_Ccircumflex --> LATIN CAPITAL LETTER C WITH CIRCUMFLEX
        keysym2UCSHash.put( (long)0x2d5, (char)0x0120); // XK_Gabovedot --> LATIN CAPITAL LETTER G WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x2d8, (char)0x011c); // XK_Gcircumflex --> LATIN CAPITAL LETTER G WITH CIRCUMFLEX
        keysym2UCSHash.put( (long)0x2dd, (char)0x016c); // XK_Ubreve --> LATIN CAPITAL LETTER U WITH BREVE
        keysym2UCSHash.put( (long)0x2de, (char)0x015c); // XK_Scircumflex --> LATIN CAPITAL LETTER S WITH CIRCUMFLEX
        keysym2UCSHash.put( (long)0x2e5, (char)0x010b); // XK_cabovedot --> LATIN SMALL LETTER C WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x2e6, (char)0x0109); // XK_ccircumflex --> LATIN SMALL LETTER C WITH CIRCUMFLEX
        keysym2UCSHash.put( (long)0x2f5, (char)0x0121); // XK_gabovedot --> LATIN SMALL LETTER G WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x2f8, (char)0x011d); // XK_gcircumflex --> LATIN SMALL LETTER G WITH CIRCUMFLEX
        keysym2UCSHash.put( (long)0x2fd, (char)0x016d); // XK_ubreve --> LATIN SMALL LETTER U WITH BREVE
        keysym2UCSHash.put( (long)0x2fe, (char)0x015d); // XK_scircumflex --> LATIN SMALL LETTER S WITH CIRCUMFLEX
        keysym2UCSHash.put( (long)0x3a2, (char)0x0138); // XK_kra --> LATIN SMALL LETTER KRA
        keysym2UCSHash.put( (long)0x3a3, (char)0x0156); // XK_Rcedilla --> LATIN CAPITAL LETTER R WITH CEDILLA
        keysym2UCSHash.put( (long)0x3a5, (char)0x0128); // XK_Itilde --> LATIN CAPITAL LETTER I WITH TILDE
        keysym2UCSHash.put( (long)0x3a6, (char)0x013b); // XK_Lcedilla --> LATIN CAPITAL LETTER L WITH CEDILLA
        keysym2UCSHash.put( (long)0x3aa, (char)0x0112); // XK_Emacron --> LATIN CAPITAL LETTER E WITH MACRON
        keysym2UCSHash.put( (long)0x3ab, (char)0x0122); // XK_Gcedilla --> LATIN CAPITAL LETTER G WITH CEDILLA
        keysym2UCSHash.put( (long)0x3ac, (char)0x0166); // XK_Tslash --> LATIN CAPITAL LETTER T WITH STROKE
        keysym2UCSHash.put( (long)0x3b3, (char)0x0157); // XK_rcedilla --> LATIN SMALL LETTER R WITH CEDILLA
        keysym2UCSHash.put( (long)0x3b5, (char)0x0129); // XK_itilde --> LATIN SMALL LETTER I WITH TILDE
        keysym2UCSHash.put( (long)0x3b6, (char)0x013c); // XK_lcedilla --> LATIN SMALL LETTER L WITH CEDILLA
        keysym2UCSHash.put( (long)0x3ba, (char)0x0113); // XK_emacron --> LATIN SMALL LETTER E WITH MACRON
        keysym2UCSHash.put( (long)0x3bb, (char)0x0123); // XK_gcedilla --> LATIN SMALL LETTER G WITH CEDILLA
        keysym2UCSHash.put( (long)0x3bc, (char)0x0167); // XK_tslash --> LATIN SMALL LETTER T WITH STROKE
        keysym2UCSHash.put( (long)0x3bd, (char)0x014a); // XK_ENG --> LATIN CAPITAL LETTER ENG
        keysym2UCSHash.put( (long)0x3bf, (char)0x014b); // XK_eng --> LATIN SMALL LETTER ENG
        keysym2UCSHash.put( (long)0x3c0, (char)0x0100); // XK_Amacron --> LATIN CAPITAL LETTER A WITH MACRON
        keysym2UCSHash.put( (long)0x3c7, (char)0x012e); // XK_Iogonek --> LATIN CAPITAL LETTER I WITH OGONEK
        keysym2UCSHash.put( (long)0x3cc, (char)0x0116); // XK_Eabovedot --> LATIN CAPITAL LETTER E WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x3cf, (char)0x012a); // XK_Imacron --> LATIN CAPITAL LETTER I WITH MACRON
        keysym2UCSHash.put( (long)0x3d1, (char)0x0145); // XK_Ncedilla --> LATIN CAPITAL LETTER N WITH CEDILLA
        keysym2UCSHash.put( (long)0x3d2, (char)0x014c); // XK_Omacron --> LATIN CAPITAL LETTER O WITH MACRON
        keysym2UCSHash.put( (long)0x3d3, (char)0x0136); // XK_Kcedilla --> LATIN CAPITAL LETTER K WITH CEDILLA
        keysym2UCSHash.put( (long)0x3d9, (char)0x0172); // XK_Uogonek --> LATIN CAPITAL LETTER U WITH OGONEK
        keysym2UCSHash.put( (long)0x3dd, (char)0x0168); // XK_Utilde --> LATIN CAPITAL LETTER U WITH TILDE
        keysym2UCSHash.put( (long)0x3de, (char)0x016a); // XK_Umacron --> LATIN CAPITAL LETTER U WITH MACRON
        keysym2UCSHash.put( (long)0x3e0, (char)0x0101); // XK_amacron --> LATIN SMALL LETTER A WITH MACRON
        keysym2UCSHash.put( (long)0x3e7, (char)0x012f); // XK_iogonek --> LATIN SMALL LETTER I WITH OGONEK
        keysym2UCSHash.put( (long)0x3ec, (char)0x0117); // XK_eabovedot --> LATIN SMALL LETTER E WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x3ef, (char)0x012b); // XK_imacron --> LATIN SMALL LETTER I WITH MACRON
        keysym2UCSHash.put( (long)0x3f1, (char)0x0146); // XK_ncedilla --> LATIN SMALL LETTER N WITH CEDILLA
        keysym2UCSHash.put( (long)0x3f2, (char)0x014d); // XK_omacron --> LATIN SMALL LETTER O WITH MACRON
        keysym2UCSHash.put( (long)0x3f3, (char)0x0137); // XK_kcedilla --> LATIN SMALL LETTER K WITH CEDILLA
        keysym2UCSHash.put( (long)0x3f9, (char)0x0173); // XK_uogonek --> LATIN SMALL LETTER U WITH OGONEK
        keysym2UCSHash.put( (long)0x3fd, (char)0x0169); // XK_utilde --> LATIN SMALL LETTER U WITH TILDE
        keysym2UCSHash.put( (long)0x3fe, (char)0x016b); // XK_umacron --> LATIN SMALL LETTER U WITH MACRON
        keysym2UCSHash.put( (long)0x12a1, (char)0x1e02); // XK_Babovedot --> LATIN CAPITAL LETTER B WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12a2, (char)0x1e03); // XK_babovedot --> LATIN SMALL LETTER B WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12a6, (char)0x1e0a); // XK_Dabovedot --> LATIN CAPITAL LETTER D WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12a8, (char)0x1e80); // XK_Wgrave --> LATIN CAPITAL LETTER W WITH GRAVE
        keysym2UCSHash.put( (long)0x12aa, (char)0x1e82); // XK_Wacute --> LATIN CAPITAL LETTER W WITH ACUTE
        keysym2UCSHash.put( (long)0x12ab, (char)0x1e0b); // XK_dabovedot --> LATIN SMALL LETTER D WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12ac, (char)0x1ef2); // XK_Ygrave --> LATIN CAPITAL LETTER Y WITH GRAVE
        keysym2UCSHash.put( (long)0x12b0, (char)0x1e1e); // XK_Fabovedot --> LATIN CAPITAL LETTER F WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12b1, (char)0x1e1f); // XK_fabovedot --> LATIN SMALL LETTER F WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12b4, (char)0x1e40); // XK_Mabovedot --> LATIN CAPITAL LETTER M WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12b5, (char)0x1e41); // XK_mabovedot --> LATIN SMALL LETTER M WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12b7, (char)0x1e56); // XK_Pabovedot --> LATIN CAPITAL LETTER P WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12b8, (char)0x1e81); // XK_wgrave --> LATIN SMALL LETTER W WITH GRAVE
        keysym2UCSHash.put( (long)0x12b9, (char)0x1e57); // XK_pabovedot --> LATIN SMALL LETTER P WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12ba, (char)0x1e83); // XK_wacute --> LATIN SMALL LETTER W WITH ACUTE
        keysym2UCSHash.put( (long)0x12bb, (char)0x1e60); // XK_Sabovedot --> LATIN CAPITAL LETTER S WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12bc, (char)0x1ef3); // XK_ygrave --> LATIN SMALL LETTER Y WITH GRAVE
        keysym2UCSHash.put( (long)0x12bd, (char)0x1e84); // XK_Wdiaeresis --> LATIN CAPITAL LETTER W WITH DIAERESIS
        keysym2UCSHash.put( (long)0x12be, (char)0x1e85); // XK_wdiaeresis --> LATIN SMALL LETTER W WITH DIAERESIS
        keysym2UCSHash.put( (long)0x12bf, (char)0x1e61); // XK_sabovedot --> LATIN SMALL LETTER S WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12d0, (char)0x017); // XK_Wcircumflex -->
        keysym2UCSHash.put( (long)0x12d7, (char)0x1e6a); // XK_Tabovedot --> LATIN CAPITAL LETTER T WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12de, (char)0x0176); // XK_Ycircumflex --> LATIN CAPITAL LETTER Y WITH CIRCUMFLEX
        keysym2UCSHash.put( (long)0x12f0, (char)0x0175); // XK_wcircumflex --> LATIN SMALL LETTER W WITH CIRCUMFLEX
        keysym2UCSHash.put( (long)0x12f7, (char)0x1e6b); // XK_tabovedot --> LATIN SMALL LETTER T WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x12fe, (char)0x0177); // XK_ycircumflex --> LATIN SMALL LETTER Y WITH CIRCUMFLEX
        keysym2UCSHash.put( (long)0x13bc, (char)0x0152); // XK_OE --> LATIN CAPITAL LIGATURE OE
        keysym2UCSHash.put( (long)0x13bd, (char)0x0153); // XK_oe --> LATIN SMALL LIGATURE OE
        keysym2UCSHash.put( (long)0x13be, (char)0x0178); // XK_Ydiaeresis --> LATIN CAPITAL LETTER Y WITH DIAERESIS
        keysym2UCSHash.put( (long)0x47e, (char)0x203e); // XK_overline --> OVERLINE
        keysym2UCSHash.put( (long)0x4a1, (char)0x3002); // XK_kana_fullstop --> IDEOGRAPHIC FULL STOP
        keysym2UCSHash.put( (long)0x4a2, (char)0x300c); // XK_kana_openingbracket --> LEFT CORNER BRACKET
        keysym2UCSHash.put( (long)0x4a3, (char)0x300d); // XK_kana_closingbracket --> RIGHT CORNER BRACKET
        keysym2UCSHash.put( (long)0x4a4, (char)0x3001); // XK_kana_comma --> IDEOGRAPHIC COMMA
        keysym2UCSHash.put( (long)0x4a5, (char)0x30fb); // XK_kana_conjunctive --> KATAKANA MIDDLE DOT
        keysym2UCSHash.put( (long)0x4a6, (char)0x30f2); // XK_kana_WO --> KATAKANA LETTER WO
        keysym2UCSHash.put( (long)0x4a7, (char)0x30a1); // XK_kana_a --> KATAKANA LETTER SMALL A
        keysym2UCSHash.put( (long)0x4a8, (char)0x30a3); // XK_kana_i --> KATAKANA LETTER SMALL I
        keysym2UCSHash.put( (long)0x4a9, (char)0x30a5); // XK_kana_u --> KATAKANA LETTER SMALL U
        keysym2UCSHash.put( (long)0x4aa, (char)0x30a7); // XK_kana_e --> KATAKANA LETTER SMALL E
        keysym2UCSHash.put( (long)0x4ab, (char)0x30a9); // XK_kana_o --> KATAKANA LETTER SMALL O
        keysym2UCSHash.put( (long)0x4ac, (char)0x30e3); // XK_kana_ya --> KATAKANA LETTER SMALL YA
        keysym2UCSHash.put( (long)0x4ad, (char)0x30e5); // XK_kana_yu --> KATAKANA LETTER SMALL YU
        keysym2UCSHash.put( (long)0x4ae, (char)0x30e7); // XK_kana_yo --> KATAKANA LETTER SMALL YO
        keysym2UCSHash.put( (long)0x4af, (char)0x30c3); // XK_kana_tsu --> KATAKANA LETTER SMALL TU
        keysym2UCSHash.put( (long)0x4b0, (char)0x30fc); // XK_prolongedsound --> KATAKANA-HIRAGANA PROLONGED SOUND MARK
        keysym2UCSHash.put( (long)0x4b1, (char)0x30a2); // XK_kana_A --> KATAKANA LETTER A
        keysym2UCSHash.put( (long)0x4b2, (char)0x30a4); // XK_kana_I --> KATAKANA LETTER I
        keysym2UCSHash.put( (long)0x4b3, (char)0x30a6); // XK_kana_U --> KATAKANA LETTER U
        keysym2UCSHash.put( (long)0x4b4, (char)0x30a8); // XK_kana_E --> KATAKANA LETTER E
        keysym2UCSHash.put( (long)0x4b5, (char)0x30aa); // XK_kana_O --> KATAKANA LETTER O
        keysym2UCSHash.put( (long)0x4b6, (char)0x30ab); // XK_kana_KA --> KATAKANA LETTER KA
        keysym2UCSHash.put( (long)0x4b7, (char)0x30ad); // XK_kana_KI --> KATAKANA LETTER KI
        keysym2UCSHash.put( (long)0x4b8, (char)0x30af); // XK_kana_KU --> KATAKANA LETTER KU
        keysym2UCSHash.put( (long)0x4b9, (char)0x30b1); // XK_kana_KE --> KATAKANA LETTER KE
        keysym2UCSHash.put( (long)0x4ba, (char)0x30b3); // XK_kana_KO --> KATAKANA LETTER KO
        keysym2UCSHash.put( (long)0x4bb, (char)0x30b5); // XK_kana_SA --> KATAKANA LETTER SA
        keysym2UCSHash.put( (long)0x4bc, (char)0x30b7); // XK_kana_SHI --> KATAKANA LETTER SI
        keysym2UCSHash.put( (long)0x4bd, (char)0x30b9); // XK_kana_SU --> KATAKANA LETTER SU
        keysym2UCSHash.put( (long)0x4be, (char)0x30bb); // XK_kana_SE --> KATAKANA LETTER SE
        keysym2UCSHash.put( (long)0x4bf, (char)0x30bd); // XK_kana_SO --> KATAKANA LETTER SO
        keysym2UCSHash.put( (long)0x4c0, (char)0x30bf); // XK_kana_TA --> KATAKANA LETTER TA
        keysym2UCSHash.put( (long)0x4c1, (char)0x30c1); // XK_kana_CHI --> KATAKANA LETTER TI
        keysym2UCSHash.put( (long)0x4c2, (char)0x30c4); // XK_kana_TSU --> KATAKANA LETTER TU
        keysym2UCSHash.put( (long)0x4c3, (char)0x30c6); // XK_kana_TE --> KATAKANA LETTER TE
        keysym2UCSHash.put( (long)0x4c4, (char)0x30c8); // XK_kana_TO --> KATAKANA LETTER TO
        keysym2UCSHash.put( (long)0x4c5, (char)0x30ca); // XK_kana_NA --> KATAKANA LETTER NA
        keysym2UCSHash.put( (long)0x4c6, (char)0x30cb); // XK_kana_NI --> KATAKANA LETTER NI
        keysym2UCSHash.put( (long)0x4c7, (char)0x30cc); // XK_kana_NU --> KATAKANA LETTER NU
        keysym2UCSHash.put( (long)0x4c8, (char)0x30cd); // XK_kana_NE --> KATAKANA LETTER NE
        keysym2UCSHash.put( (long)0x4c9, (char)0x30ce); // XK_kana_NO --> KATAKANA LETTER NO
        keysym2UCSHash.put( (long)0x4ca, (char)0x30cf); // XK_kana_HA --> KATAKANA LETTER HA
        keysym2UCSHash.put( (long)0x4cb, (char)0x30d2); // XK_kana_HI --> KATAKANA LETTER HI
        keysym2UCSHash.put( (long)0x4cc, (char)0x30d5); // XK_kana_FU --> KATAKANA LETTER HU
        keysym2UCSHash.put( (long)0x4cd, (char)0x30d8); // XK_kana_HE --> KATAKANA LETTER HE
        keysym2UCSHash.put( (long)0x4ce, (char)0x30db); // XK_kana_HO --> KATAKANA LETTER HO
        keysym2UCSHash.put( (long)0x4cf, (char)0x30de); // XK_kana_MA --> KATAKANA LETTER MA
        keysym2UCSHash.put( (long)0x4d0, (char)0x30df); // XK_kana_MI --> KATAKANA LETTER MI
        keysym2UCSHash.put( (long)0x4d1, (char)0x30e0); // XK_kana_MU --> KATAKANA LETTER MU
        keysym2UCSHash.put( (long)0x4d2, (char)0x30e1); // XK_kana_ME --> KATAKANA LETTER ME
        keysym2UCSHash.put( (long)0x4d3, (char)0x30e2); // XK_kana_MO --> KATAKANA LETTER MO
        keysym2UCSHash.put( (long)0x4d4, (char)0x30e4); // XK_kana_YA --> KATAKANA LETTER YA
        keysym2UCSHash.put( (long)0x4d5, (char)0x30e6); // XK_kana_YU --> KATAKANA LETTER YU
        keysym2UCSHash.put( (long)0x4d6, (char)0x30e8); // XK_kana_YO --> KATAKANA LETTER YO
        keysym2UCSHash.put( (long)0x4d7, (char)0x30e9); // XK_kana_RA --> KATAKANA LETTER RA
        keysym2UCSHash.put( (long)0x4d8, (char)0x30ea); // XK_kana_RI --> KATAKANA LETTER RI
        keysym2UCSHash.put( (long)0x4d9, (char)0x30eb); // XK_kana_RU --> KATAKANA LETTER RU
        keysym2UCSHash.put( (long)0x4da, (char)0x30ec); // XK_kana_RE --> KATAKANA LETTER RE
        keysym2UCSHash.put( (long)0x4db, (char)0x30ed); // XK_kana_RO --> KATAKANA LETTER RO
        keysym2UCSHash.put( (long)0x4dc, (char)0x30ef); // XK_kana_WA --> KATAKANA LETTER WA
        keysym2UCSHash.put( (long)0x4dd, (char)0x30f3); // XK_kana_N --> KATAKANA LETTER N
        keysym2UCSHash.put( (long)0x4de, (char)0x309b); // XK_voicedsound --> KATAKANA-HIRAGANA VOICED SOUND MARK
        keysym2UCSHash.put( (long)0x4df, (char)0x309c); // XK_semivoicedsound --> KATAKANA-HIRAGANA SEMI-VOICED SOUND MARK
        keysym2UCSHash.put( (long)0x590, (char)0x0670); // XK_Farsi_0 --> ARABIC LETTER SUPERSCRIPT ALEF
        keysym2UCSHash.put( (long)0x591, (char)0x06f1); // XK_Farsi_1 --> EXTENDED ARABIC-INDIC DIGIT ONE
        keysym2UCSHash.put( (long)0x592, (char)0x06f2); // XK_Farsi_2 --> EXTENDED ARABIC-INDIC DIGIT TWO
        keysym2UCSHash.put( (long)0x593, (char)0x06f3); // XK_Farsi_3 --> EXTENDED ARABIC-INDIC DIGIT THREE
        keysym2UCSHash.put( (long)0x594, (char)0x06f4); // XK_Farsi_4 --> EXTENDED ARABIC-INDIC DIGIT FOUR
        keysym2UCSHash.put( (long)0x595, (char)0x06f5); // XK_Farsi_5 --> EXTENDED ARABIC-INDIC DIGIT FIVE
        keysym2UCSHash.put( (long)0x596, (char)0x06f6); // XK_Farsi_6 --> EXTENDED ARABIC-INDIC DIGIT SIX
        keysym2UCSHash.put( (long)0x597, (char)0x06f7); // XK_Farsi_7 --> EXTENDED ARABIC-INDIC DIGIT SEVEN
        keysym2UCSHash.put( (long)0x598, (char)0x06f8); // XK_Farsi_8 --> EXTENDED ARABIC-INDIC DIGIT EIGHT
        keysym2UCSHash.put( (long)0x599, (char)0x06f9); // XK_Farsi_9 --> EXTENDED ARABIC-INDIC DIGIT NINE
        keysym2UCSHash.put( (long)0x5a5, (char)0x066a); // XK_Arabic_percent --> ARABIC PERCENT SIGN
        keysym2UCSHash.put( (long)0x5a6, (char)0x0670); // XK_Arabic_superscript_alef --> ARABIC LETTER SUPERSCRIPT ALEF
        keysym2UCSHash.put( (long)0x5a7, (char)0x0679); // XK_Arabic_tteh --> ARABIC LETTER TTEH
        keysym2UCSHash.put( (long)0x5a8, (char)0x067e); // XK_Arabic_peh --> ARABIC LETTER PEH
        keysym2UCSHash.put( (long)0x5a9, (char)0x0686); // XK_Arabic_tcheh --> ARABIC LETTER TCHEH
        keysym2UCSHash.put( (long)0x5aa, (char)0x0688); // XK_Arabic_ddal --> ARABIC LETTER DDAL
        keysym2UCSHash.put( (long)0x5ab, (char)0x0691); // XK_Arabic_rreh --> ARABIC LETTER RREH
        keysym2UCSHash.put( (long)0x5ac, (char)0x060c); // XK_Arabic_comma --> ARABIC COMMA
        keysym2UCSHash.put( (long)0x5ae, (char)0x06d4); // XK_Arabic_fullstop --> ARABIC FULL STOP
        keysym2UCSHash.put( (long)0x5b0, (char)0x0660); // XK_Arabic_0 --> ARABIC-INDIC DIGIT ZERO
        keysym2UCSHash.put( (long)0x5b1, (char)0x0661); // XK_Arabic_1 --> ARABIC-INDIC DIGIT ONE
        keysym2UCSHash.put( (long)0x5b2, (char)0x0662); // XK_Arabic_2 --> ARABIC-INDIC DIGIT TWO
        keysym2UCSHash.put( (long)0x5b3, (char)0x0663); // XK_Arabic_3 --> ARABIC-INDIC DIGIT THREE
        keysym2UCSHash.put( (long)0x5b4, (char)0x0664); // XK_Arabic_4 --> ARABIC-INDIC DIGIT FOUR
        keysym2UCSHash.put( (long)0x5b5, (char)0x0665); // XK_Arabic_5 --> ARABIC-INDIC DIGIT FIVE
        keysym2UCSHash.put( (long)0x5b6, (char)0x0666); // XK_Arabic_6 --> ARABIC-INDIC DIGIT SIX
        keysym2UCSHash.put( (long)0x5b7, (char)0x0667); // XK_Arabic_7 --> ARABIC-INDIC DIGIT SEVEN
        keysym2UCSHash.put( (long)0x5b8, (char)0x0668); // XK_Arabic_8 --> ARABIC-INDIC DIGIT EIGHT
        keysym2UCSHash.put( (long)0x5b9, (char)0x0669); // XK_Arabic_9 --> ARABIC-INDIC DIGIT NINE
        keysym2UCSHash.put( (long)0x5bb, (char)0x061b); // XK_Arabic_semicolon --> ARABIC SEMICOLON
        keysym2UCSHash.put( (long)0x5bf, (char)0x061f); // XK_Arabic_question_mark --> ARABIC QUESTION MARK
        keysym2UCSHash.put( (long)0x5c1, (char)0x0621); // XK_Arabic_hamza --> ARABIC LETTER HAMZA
        keysym2UCSHash.put( (long)0x5c2, (char)0x0622); // XK_Arabic_maddaonalef --> ARABIC LETTER ALEF WITH MADDA ABOVE
        keysym2UCSHash.put( (long)0x5c3, (char)0x0623); // XK_Arabic_hamzaonalef --> ARABIC LETTER ALEF WITH HAMZA ABOVE
        keysym2UCSHash.put( (long)0x5c4, (char)0x0624); // XK_Arabic_hamzaonwaw --> ARABIC LETTER WAW WITH HAMZA ABOVE
        keysym2UCSHash.put( (long)0x5c5, (char)0x0625); // XK_Arabic_hamzaunderalef --> ARABIC LETTER ALEF WITH HAMZA BELOW
        keysym2UCSHash.put( (long)0x5c6, (char)0x0626); // XK_Arabic_hamzaonyeh --> ARABIC LETTER YEH WITH HAMZA ABOVE
        keysym2UCSHash.put( (long)0x5c7, (char)0x0627); // XK_Arabic_alef --> ARABIC LETTER ALEF
        keysym2UCSHash.put( (long)0x5c8, (char)0x0628); // XK_Arabic_beh --> ARABIC LETTER BEH
        keysym2UCSHash.put( (long)0x5c9, (char)0x0629); // XK_Arabic_tehmarbuta --> ARABIC LETTER TEH MARBUTA
        keysym2UCSHash.put( (long)0x5ca, (char)0x062a); // XK_Arabic_teh --> ARABIC LETTER TEH
        keysym2UCSHash.put( (long)0x5cb, (char)0x062b); // XK_Arabic_theh --> ARABIC LETTER THEH
        keysym2UCSHash.put( (long)0x5cc, (char)0x062c); // XK_Arabic_jeem --> ARABIC LETTER JEEM
        keysym2UCSHash.put( (long)0x5cd, (char)0x062d); // XK_Arabic_hah --> ARABIC LETTER HAH
        keysym2UCSHash.put( (long)0x5ce, (char)0x062e); // XK_Arabic_khah --> ARABIC LETTER KHAH
        keysym2UCSHash.put( (long)0x5cf, (char)0x062f); // XK_Arabic_dal --> ARABIC LETTER DAL
        keysym2UCSHash.put( (long)0x5d0, (char)0x0630); // XK_Arabic_thal --> ARABIC LETTER THAL
        keysym2UCSHash.put( (long)0x5d1, (char)0x0631); // XK_Arabic_ra --> ARABIC LETTER REH
        keysym2UCSHash.put( (long)0x5d2, (char)0x0632); // XK_Arabic_zain --> ARABIC LETTER ZAIN
        keysym2UCSHash.put( (long)0x5d3, (char)0x0633); // XK_Arabic_seen --> ARABIC LETTER SEEN
        keysym2UCSHash.put( (long)0x5d4, (char)0x0634); // XK_Arabic_sheen --> ARABIC LETTER SHEEN
        keysym2UCSHash.put( (long)0x5d5, (char)0x0635); // XK_Arabic_sad --> ARABIC LETTER SAD
        keysym2UCSHash.put( (long)0x5d6, (char)0x0636); // XK_Arabic_dad --> ARABIC LETTER DAD
        keysym2UCSHash.put( (long)0x5d7, (char)0x0637); // XK_Arabic_tah --> ARABIC LETTER TAH
        keysym2UCSHash.put( (long)0x5d8, (char)0x0638); // XK_Arabic_zah --> ARABIC LETTER ZAH
        keysym2UCSHash.put( (long)0x5d9, (char)0x0639); // XK_Arabic_ain --> ARABIC LETTER AIN
        keysym2UCSHash.put( (long)0x5da, (char)0x063a); // XK_Arabic_ghain --> ARABIC LETTER GHAIN
        keysym2UCSHash.put( (long)0x5e0, (char)0x0640); // XK_Arabic_tatweel --> ARABIC TATWEEL
        keysym2UCSHash.put( (long)0x5e1, (char)0x0641); // XK_Arabic_feh --> ARABIC LETTER FEH
        keysym2UCSHash.put( (long)0x5e2, (char)0x0642); // XK_Arabic_qaf --> ARABIC LETTER QAF
        keysym2UCSHash.put( (long)0x5e3, (char)0x0643); // XK_Arabic_kaf --> ARABIC LETTER KAF
        keysym2UCSHash.put( (long)0x5e4, (char)0x0644); // XK_Arabic_lam --> ARABIC LETTER LAM
        keysym2UCSHash.put( (long)0x5e5, (char)0x0645); // XK_Arabic_meem --> ARABIC LETTER MEEM
        keysym2UCSHash.put( (long)0x5e6, (char)0x0646); // XK_Arabic_noon --> ARABIC LETTER NOON
        keysym2UCSHash.put( (long)0x5e7, (char)0x0647); // XK_Arabic_ha --> ARABIC LETTER HEH
        keysym2UCSHash.put( (long)0x5e8, (char)0x0648); // XK_Arabic_waw --> ARABIC LETTER WAW
        keysym2UCSHash.put( (long)0x5e9, (char)0x0649); // XK_Arabic_alefmaksura --> ARABIC LETTER ALEF MAKSURA
        keysym2UCSHash.put( (long)0x5ea, (char)0x064a); // XK_Arabic_yeh --> ARABIC LETTER YEH
        keysym2UCSHash.put( (long)0x5eb, (char)0x064b); // XK_Arabic_fathatan --> ARABIC FATHATAN
        keysym2UCSHash.put( (long)0x5ec, (char)0x064c); // XK_Arabic_dammatan --> ARABIC DAMMATAN
        keysym2UCSHash.put( (long)0x5ed, (char)0x064d); // XK_Arabic_kasratan --> ARABIC KASRATAN
        keysym2UCSHash.put( (long)0x5ee, (char)0x064e); // XK_Arabic_fatha --> ARABIC FATHA
        keysym2UCSHash.put( (long)0x5ef, (char)0x064f); // XK_Arabic_damma --> ARABIC DAMMA
        keysym2UCSHash.put( (long)0x5f0, (char)0x0650); // XK_Arabic_kasra --> ARABIC KASRA
        keysym2UCSHash.put( (long)0x5f1, (char)0x0651); // XK_Arabic_shadda --> ARABIC SHADDA
        keysym2UCSHash.put( (long)0x5f2, (char)0x0652); // XK_Arabic_sukun --> ARABIC SUKUN
        keysym2UCSHash.put( (long)0x5f3, (char)0x0653); // XK_Arabic_madda_above --> ARABIC MADDAH ABOVE
        keysym2UCSHash.put( (long)0x5f4, (char)0x0654); // XK_Arabic_hamza_above --> ARABIC HAMZA ABOVE
        keysym2UCSHash.put( (long)0x5f5, (char)0x0655); // XK_Arabic_hamza_below --> ARABIC HAMZA BELOW
        keysym2UCSHash.put( (long)0x5f6, (char)0x0698); // XK_Arabic_jeh --> ARABIC LETTER JEH
        keysym2UCSHash.put( (long)0x5f7, (char)0x06a4); // XK_Arabic_veh --> ARABIC LETTER VEH
        keysym2UCSHash.put( (long)0x5f8, (char)0x06a9); // XK_Arabic_keheh --> ARABIC LETTER KEHEH
        keysym2UCSHash.put( (long)0x5f9, (char)0x06af); // XK_Arabic_gaf --> ARABIC LETTER GAF
        keysym2UCSHash.put( (long)0x5fa, (char)0x06ba); // XK_Arabic_noon_ghunna --> ARABIC LETTER NOON GHUNNA
        keysym2UCSHash.put( (long)0x5fb, (char)0x06be); // XK_Arabic_heh_doachashmee --> ARABIC LETTER HEH DOACHASHMEE
        keysym2UCSHash.put( (long)0x5fc, (char)0x06cc); // XK_Farsi_yeh --> ARABIC LETTER FARSI YEH
        keysym2UCSHash.put( (long)0x5fd, (char)0x06d2); // XK_Arabic_yeh_baree --> ARABIC LETTER YEH BARREE
        keysym2UCSHash.put( (long)0x5fe, (char)0x06c1); // XK_Arabic_heh_goal --> ARABIC LETTER HEH GOAL
        keysym2UCSHash.put( (long)0x680, (char)0x0492); // XK_Cyrillic_GHE_bar --> CYRILLIC CAPITAL LETTER GHE WITH STROKE
        keysym2UCSHash.put( (long)0x690, (char)0x0493); // XK_Cyrillic_ghe_bar --> CYRILLIC SMALL LETTER GHE WITH STROKE
        keysym2UCSHash.put( (long)0x681, (char)0x0496); // XK_Cyrillic_ZHE_descender --> CYRILLIC CAPITAL LETTER ZHE WITH DESCENDER
        keysym2UCSHash.put( (long)0x691, (char)0x0497); // XK_Cyrillic_zhe_descender --> CYRILLIC SMALL LETTER ZHE WITH DESCENDER
        keysym2UCSHash.put( (long)0x682, (char)0x049a); // XK_Cyrillic_KA_descender --> CYRILLIC CAPITAL LETTER KA WITH DESCENDER
        keysym2UCSHash.put( (long)0x692, (char)0x049b); // XK_Cyrillic_ka_descender --> CYRILLIC SMALL LETTER KA WITH DESCENDER
        keysym2UCSHash.put( (long)0x683, (char)0x049c); // XK_Cyrillic_KA_vertstroke --> CYRILLIC CAPITAL LETTER KA WITH VERTICAL STROKE
        keysym2UCSHash.put( (long)0x693, (char)0x049d); // XK_Cyrillic_ka_vertstroke --> CYRILLIC SMALL LETTER KA WITH VERTICAL STROKE
        keysym2UCSHash.put( (long)0x684, (char)0x04a2); // XK_Cyrillic_EN_descender --> CYRILLIC CAPITAL LETTER EN WITH DESCENDER
        keysym2UCSHash.put( (long)0x694, (char)0x04a3); // XK_Cyrillic_en_descender --> CYRILLIC SMALL LETTER EN WITH DESCENDER
        keysym2UCSHash.put( (long)0x685, (char)0x04ae); // XK_Cyrillic_U_straight --> CYRILLIC CAPITAL LETTER STRAIGHT U
        keysym2UCSHash.put( (long)0x695, (char)0x04af); // XK_Cyrillic_u_straight --> CYRILLIC SMALL LETTER STRAIGHT U
        keysym2UCSHash.put( (long)0x686, (char)0x04b0); // XK_Cyrillic_U_straight_bar --> CYRILLIC CAPITAL LETTER STRAIGHT U WITH STROKE
        keysym2UCSHash.put( (long)0x696, (char)0x04b1); // XK_Cyrillic_u_straight_bar --> CYRILLIC SMALL LETTER STRAIGHT U WITH STROKE
        keysym2UCSHash.put( (long)0x687, (char)0x04b2); // XK_Cyrillic_HA_descender --> CYRILLIC CAPITAL LETTER HA WITH DESCENDER
        keysym2UCSHash.put( (long)0x697, (char)0x04b3); // XK_Cyrillic_ha_descender --> CYRILLIC SMALL LETTER HA WITH DESCENDER
        keysym2UCSHash.put( (long)0x688, (char)0x04b6); // XK_Cyrillic_CHE_descender --> CYRILLIC CAPITAL LETTER CHE WITH DESCENDER
        keysym2UCSHash.put( (long)0x698, (char)0x04b7); // XK_Cyrillic_che_descender --> CYRILLIC SMALL LETTER CHE WITH DESCENDER
        keysym2UCSHash.put( (long)0x689, (char)0x04b8); // XK_Cyrillic_CHE_vertstroke --> CYRILLIC CAPITAL LETTER CHE WITH VERTICAL STROKE
        keysym2UCSHash.put( (long)0x699, (char)0x04b9); // XK_Cyrillic_che_vertstroke --> CYRILLIC SMALL LETTER CHE WITH VERTICAL STROKE
        keysym2UCSHash.put( (long)0x68a, (char)0x04ba); // XK_Cyrillic_SHHA --> CYRILLIC CAPITAL LETTER SHHA
        keysym2UCSHash.put( (long)0x69a, (char)0x04bb); // XK_Cyrillic_shha --> CYRILLIC SMALL LETTER SHHA
        keysym2UCSHash.put( (long)0x68c, (char)0x04d8); // XK_Cyrillic_SCHWA --> CYRILLIC CAPITAL LETTER SCHWA
        keysym2UCSHash.put( (long)0x69c, (char)0x04d9); // XK_Cyrillic_schwa --> CYRILLIC SMALL LETTER SCHWA
        keysym2UCSHash.put( (long)0x68d, (char)0x04e2); // XK_Cyrillic_I_macron --> CYRILLIC CAPITAL LETTER I WITH MACRON
        keysym2UCSHash.put( (long)0x69d, (char)0x04e3); // XK_Cyrillic_i_macron --> CYRILLIC SMALL LETTER I WITH MACRON
        keysym2UCSHash.put( (long)0x68e, (char)0x04e8); // XK_Cyrillic_O_bar --> CYRILLIC CAPITAL LETTER BARRED O
        keysym2UCSHash.put( (long)0x69e, (char)0x04e9); // XK_Cyrillic_o_bar --> CYRILLIC SMALL LETTER BARRED O
        keysym2UCSHash.put( (long)0x68f, (char)0x04ee); // XK_Cyrillic_U_macron --> CYRILLIC CAPITAL LETTER U WITH MACRON
        keysym2UCSHash.put( (long)0x69f, (char)0x04ef); // XK_Cyrillic_u_macron --> CYRILLIC SMALL LETTER U WITH MACRON
        keysym2UCSHash.put( (long)0x6a1, (char)0x0452); // XK_Serbian_dje --> CYRILLIC SMALL LETTER DJE
        keysym2UCSHash.put( (long)0x6a2, (char)0x0453); // XK_Macedonia_gje --> CYRILLIC SMALL LETTER GJE
        keysym2UCSHash.put( (long)0x6a3, (char)0x0451); // XK_Cyrillic_io --> CYRILLIC SMALL LETTER IO
        keysym2UCSHash.put( (long)0x6a4, (char)0x0454); // XK_Ukrainian_ie --> CYRILLIC SMALL LETTER UKRAINIAN IE
        keysym2UCSHash.put( (long)0x6a5, (char)0x0455); // XK_Macedonia_dse --> CYRILLIC SMALL LETTER DZE
        keysym2UCSHash.put( (long)0x6a6, (char)0x0456); // XK_Ukrainian_i --> CYRILLIC SMALL LETTER BYELORUSSIAN-UKRAINIAN I
        keysym2UCSHash.put( (long)0x6a7, (char)0x0457); // XK_Ukrainian_yi --> CYRILLIC SMALL LETTER YI
        keysym2UCSHash.put( (long)0x6a8, (char)0x0458); // XK_Cyrillic_je --> CYRILLIC SMALL LETTER JE
        keysym2UCSHash.put( (long)0x6a9, (char)0x0459); // XK_Cyrillic_lje --> CYRILLIC SMALL LETTER LJE
        keysym2UCSHash.put( (long)0x6aa, (char)0x045a); // XK_Cyrillic_nje --> CYRILLIC SMALL LETTER NJE
        keysym2UCSHash.put( (long)0x6ab, (char)0x045b); // XK_Serbian_tshe --> CYRILLIC SMALL LETTER TSHE
        keysym2UCSHash.put( (long)0x6ac, (char)0x045c); // XK_Macedonia_kje --> CYRILLIC SMALL LETTER KJE
        keysym2UCSHash.put( (long)0x6ad, (char)0x0491); // XK_Ukrainian_ghe_with_upturn --> CYRILLIC SMALL LETTER GHE WITH UPTURN
        keysym2UCSHash.put( (long)0x6ae, (char)0x045e); // XK_Byelorussian_shortu --> CYRILLIC SMALL LETTER SHORT U
        keysym2UCSHash.put( (long)0x6af, (char)0x045f); // XK_Cyrillic_dzhe --> CYRILLIC SMALL LETTER DZHE
        keysym2UCSHash.put( (long)0x6b0, (char)0x2116); // XK_numerosign --> NUMERO SIGN
        keysym2UCSHash.put( (long)0x6b1, (char)0x0402); // XK_Serbian_DJE --> CYRILLIC CAPITAL LETTER DJE
        keysym2UCSHash.put( (long)0x6b2, (char)0x0403); // XK_Macedonia_GJE --> CYRILLIC CAPITAL LETTER GJE
        keysym2UCSHash.put( (long)0x6b3, (char)0x0401); // XK_Cyrillic_IO --> CYRILLIC CAPITAL LETTER IO
        keysym2UCSHash.put( (long)0x6b4, (char)0x0404); // XK_Ukrainian_IE --> CYRILLIC CAPITAL LETTER UKRAINIAN IE
        keysym2UCSHash.put( (long)0x6b5, (char)0x0405); // XK_Macedonia_DSE --> CYRILLIC CAPITAL LETTER DZE
        keysym2UCSHash.put( (long)0x6b6, (char)0x0406); // XK_Ukrainian_I --> CYRILLIC CAPITAL LETTER BYELORUSSIAN-UKRAINIAN I
        keysym2UCSHash.put( (long)0x6b7, (char)0x0407); // XK_Ukrainian_YI --> CYRILLIC CAPITAL LETTER YI
        keysym2UCSHash.put( (long)0x6b8, (char)0x0408); // XK_Cyrillic_JE --> CYRILLIC CAPITAL LETTER JE
        keysym2UCSHash.put( (long)0x6b9, (char)0x0409); // XK_Cyrillic_LJE --> CYRILLIC CAPITAL LETTER LJE
        keysym2UCSHash.put( (long)0x6ba, (char)0x040a); // XK_Cyrillic_NJE --> CYRILLIC CAPITAL LETTER NJE
        keysym2UCSHash.put( (long)0x6bb, (char)0x040b); // XK_Serbian_TSHE --> CYRILLIC CAPITAL LETTER TSHE
        keysym2UCSHash.put( (long)0x6bc, (char)0x040c); // XK_Macedonia_KJE --> CYRILLIC CAPITAL LETTER KJE
        keysym2UCSHash.put( (long)0x6bd, (char)0x0490); // XK_Ukrainian_GHE_WITH_UPTURN --> CYRILLIC CAPITAL LETTER GHE WITH UPTURN
        keysym2UCSHash.put( (long)0x6be, (char)0x040e); // XK_Byelorussian_SHORTU --> CYRILLIC CAPITAL LETTER SHORT U
        keysym2UCSHash.put( (long)0x6bf, (char)0x040f); // XK_Cyrillic_DZHE --> CYRILLIC CAPITAL LETTER DZHE
        keysym2UCSHash.put( (long)0x6c0, (char)0x044e); // XK_Cyrillic_yu --> CYRILLIC SMALL LETTER YU
        keysym2UCSHash.put( (long)0x6c1, (char)0x0430); // XK_Cyrillic_a --> CYRILLIC SMALL LETTER A
        keysym2UCSHash.put( (long)0x6c2, (char)0x0431); // XK_Cyrillic_be --> CYRILLIC SMALL LETTER BE
        keysym2UCSHash.put( (long)0x6c3, (char)0x0446); // XK_Cyrillic_tse --> CYRILLIC SMALL LETTER TSE
        keysym2UCSHash.put( (long)0x6c4, (char)0x0434); // XK_Cyrillic_de --> CYRILLIC SMALL LETTER DE
        keysym2UCSHash.put( (long)0x6c5, (char)0x0435); // XK_Cyrillic_ie --> CYRILLIC SMALL LETTER IE
        keysym2UCSHash.put( (long)0x6c6, (char)0x0444); // XK_Cyrillic_ef --> CYRILLIC SMALL LETTER EF
        keysym2UCSHash.put( (long)0x6c7, (char)0x0433); // XK_Cyrillic_ghe --> CYRILLIC SMALL LETTER GHE
        keysym2UCSHash.put( (long)0x6c8, (char)0x0445); // XK_Cyrillic_ha --> CYRILLIC SMALL LETTER HA
        keysym2UCSHash.put( (long)0x6c9, (char)0x0438); // XK_Cyrillic_i --> CYRILLIC SMALL LETTER I
        keysym2UCSHash.put( (long)0x6ca, (char)0x0439); // XK_Cyrillic_shorti --> CYRILLIC SMALL LETTER SHORT I
        keysym2UCSHash.put( (long)0x6cb, (char)0x043a); // XK_Cyrillic_ka --> CYRILLIC SMALL LETTER KA
        keysym2UCSHash.put( (long)0x6cc, (char)0x043b); // XK_Cyrillic_el --> CYRILLIC SMALL LETTER EL
        keysym2UCSHash.put( (long)0x6cd, (char)0x043c); // XK_Cyrillic_em --> CYRILLIC SMALL LETTER EM
        keysym2UCSHash.put( (long)0x6ce, (char)0x043d); // XK_Cyrillic_en --> CYRILLIC SMALL LETTER EN
        keysym2UCSHash.put( (long)0x6cf, (char)0x043e); // XK_Cyrillic_o --> CYRILLIC SMALL LETTER O
        keysym2UCSHash.put( (long)0x6d0, (char)0x043f); // XK_Cyrillic_pe --> CYRILLIC SMALL LETTER PE
        keysym2UCSHash.put( (long)0x6d1, (char)0x044f); // XK_Cyrillic_ya --> CYRILLIC SMALL LETTER YA
        keysym2UCSHash.put( (long)0x6d2, (char)0x0440); // XK_Cyrillic_er --> CYRILLIC SMALL LETTER ER
        keysym2UCSHash.put( (long)0x6d3, (char)0x0441); // XK_Cyrillic_es --> CYRILLIC SMALL LETTER ES
        keysym2UCSHash.put( (long)0x6d4, (char)0x0442); // XK_Cyrillic_te --> CYRILLIC SMALL LETTER TE
        keysym2UCSHash.put( (long)0x6d5, (char)0x0443); // XK_Cyrillic_u --> CYRILLIC SMALL LETTER U
        keysym2UCSHash.put( (long)0x6d6, (char)0x0436); // XK_Cyrillic_zhe --> CYRILLIC SMALL LETTER ZHE
        keysym2UCSHash.put( (long)0x6d7, (char)0x0432); // XK_Cyrillic_ve --> CYRILLIC SMALL LETTER VE
        keysym2UCSHash.put( (long)0x6d8, (char)0x044c); // XK_Cyrillic_softsign --> CYRILLIC SMALL LETTER SOFT SIGN
        keysym2UCSHash.put( (long)0x6d9, (char)0x044b); // XK_Cyrillic_yeru --> CYRILLIC SMALL LETTER YERU
        keysym2UCSHash.put( (long)0x6da, (char)0x0437); // XK_Cyrillic_ze --> CYRILLIC SMALL LETTER ZE
        keysym2UCSHash.put( (long)0x6db, (char)0x0448); // XK_Cyrillic_sha --> CYRILLIC SMALL LETTER SHA
        keysym2UCSHash.put( (long)0x6dc, (char)0x044d); // XK_Cyrillic_e --> CYRILLIC SMALL LETTER E
        keysym2UCSHash.put( (long)0x6dd, (char)0x0449); // XK_Cyrillic_shcha --> CYRILLIC SMALL LETTER SHCHA
        keysym2UCSHash.put( (long)0x6de, (char)0x0447); // XK_Cyrillic_che --> CYRILLIC SMALL LETTER CHE
        keysym2UCSHash.put( (long)0x6df, (char)0x044a); // XK_Cyrillic_hardsign --> CYRILLIC SMALL LETTER HARD SIGN
        keysym2UCSHash.put( (long)0x6e0, (char)0x042e); // XK_Cyrillic_YU --> CYRILLIC CAPITAL LETTER YU
        keysym2UCSHash.put( (long)0x6e1, (char)0x0410); // XK_Cyrillic_A --> CYRILLIC CAPITAL LETTER A
        keysym2UCSHash.put( (long)0x6e2, (char)0x0411); // XK_Cyrillic_BE --> CYRILLIC CAPITAL LETTER BE
        keysym2UCSHash.put( (long)0x6e3, (char)0x0426); // XK_Cyrillic_TSE --> CYRILLIC CAPITAL LETTER TSE
        keysym2UCSHash.put( (long)0x6e4, (char)0x0414); // XK_Cyrillic_DE --> CYRILLIC CAPITAL LETTER DE
        keysym2UCSHash.put( (long)0x6e5, (char)0x0415); // XK_Cyrillic_IE --> CYRILLIC CAPITAL LETTER IE
        keysym2UCSHash.put( (long)0x6e6, (char)0x0424); // XK_Cyrillic_EF --> CYRILLIC CAPITAL LETTER EF
        keysym2UCSHash.put( (long)0x6e7, (char)0x0413); // XK_Cyrillic_GHE --> CYRILLIC CAPITAL LETTER GHE
        keysym2UCSHash.put( (long)0x6e8, (char)0x0425); // XK_Cyrillic_HA --> CYRILLIC CAPITAL LETTER HA
        keysym2UCSHash.put( (long)0x6e9, (char)0x0418); // XK_Cyrillic_I --> CYRILLIC CAPITAL LETTER I
        keysym2UCSHash.put( (long)0x6ea, (char)0x0419); // XK_Cyrillic_SHORTI --> CYRILLIC CAPITAL LETTER SHORT I
        keysym2UCSHash.put( (long)0x6eb, (char)0x041a); // XK_Cyrillic_KA --> CYRILLIC CAPITAL LETTER KA
        keysym2UCSHash.put( (long)0x6ec, (char)0x041b); // XK_Cyrillic_EL --> CYRILLIC CAPITAL LETTER EL
        keysym2UCSHash.put( (long)0x6ed, (char)0x041c); // XK_Cyrillic_EM --> CYRILLIC CAPITAL LETTER EM
        keysym2UCSHash.put( (long)0x6ee, (char)0x041d); // XK_Cyrillic_EN --> CYRILLIC CAPITAL LETTER EN
        keysym2UCSHash.put( (long)0x6ef, (char)0x041e); // XK_Cyrillic_O --> CYRILLIC CAPITAL LETTER O
        keysym2UCSHash.put( (long)0x6f0, (char)0x041f); // XK_Cyrillic_PE --> CYRILLIC CAPITAL LETTER PE
        keysym2UCSHash.put( (long)0x6f1, (char)0x042f); // XK_Cyrillic_YA --> CYRILLIC CAPITAL LETTER YA
        keysym2UCSHash.put( (long)0x6f2, (char)0x0420); // XK_Cyrillic_ER --> CYRILLIC CAPITAL LETTER ER
        keysym2UCSHash.put( (long)0x6f3, (char)0x0421); // XK_Cyrillic_ES --> CYRILLIC CAPITAL LETTER ES
        keysym2UCSHash.put( (long)0x6f4, (char)0x0422); // XK_Cyrillic_TE --> CYRILLIC CAPITAL LETTER TE
        keysym2UCSHash.put( (long)0x6f5, (char)0x0423); // XK_Cyrillic_U --> CYRILLIC CAPITAL LETTER U
        keysym2UCSHash.put( (long)0x6f6, (char)0x0416); // XK_Cyrillic_ZHE --> CYRILLIC CAPITAL LETTER ZHE
        keysym2UCSHash.put( (long)0x6f7, (char)0x0412); // XK_Cyrillic_VE --> CYRILLIC CAPITAL LETTER VE
        keysym2UCSHash.put( (long)0x6f8, (char)0x042c); // XK_Cyrillic_SOFTSIGN --> CYRILLIC CAPITAL LETTER SOFT SIGN
        keysym2UCSHash.put( (long)0x6f9, (char)0x042b); // XK_Cyrillic_YERU --> CYRILLIC CAPITAL LETTER YERU
        keysym2UCSHash.put( (long)0x6fa, (char)0x0417); // XK_Cyrillic_ZE --> CYRILLIC CAPITAL LETTER ZE
        keysym2UCSHash.put( (long)0x6fb, (char)0x0428); // XK_Cyrillic_SHA --> CYRILLIC CAPITAL LETTER SHA
        keysym2UCSHash.put( (long)0x6fc, (char)0x042d); // XK_Cyrillic_E --> CYRILLIC CAPITAL LETTER E
        keysym2UCSHash.put( (long)0x6fd, (char)0x0429); // XK_Cyrillic_SHCHA --> CYRILLIC CAPITAL LETTER SHCHA
        keysym2UCSHash.put( (long)0x6fe, (char)0x0427); // XK_Cyrillic_CHE --> CYRILLIC CAPITAL LETTER CHE
        keysym2UCSHash.put( (long)0x6ff, (char)0x042a); // XK_Cyrillic_HARDSIGN --> CYRILLIC CAPITAL LETTER HARD SIGN
        keysym2UCSHash.put( (long)0x7a1, (char)0x0386); // XK_Greek_ALPHAaccent --> GREEK CAPITAL LETTER ALPHA WITH TONOS
        keysym2UCSHash.put( (long)0x7a2, (char)0x0388); // XK_Greek_EPSILONaccent --> GREEK CAPITAL LETTER EPSILON WITH TONOS
        keysym2UCSHash.put( (long)0x7a3, (char)0x0389); // XK_Greek_ETAaccent --> GREEK CAPITAL LETTER ETA WITH TONOS
        keysym2UCSHash.put( (long)0x7a4, (char)0x038a); // XK_Greek_IOTAaccent --> GREEK CAPITAL LETTER IOTA WITH TONOS
        keysym2UCSHash.put( (long)0x7a5, (char)0x03aa); // XK_Greek_IOTAdieresis --> GREEK CAPITAL LETTER IOTA WITH DIALYTIKA
        keysym2UCSHash.put( (long)0x7a7, (char)0x038c); // XK_Greek_OMICRONaccent --> GREEK CAPITAL LETTER OMICRON WITH TONOS
        keysym2UCSHash.put( (long)0x7a8, (char)0x038e); // XK_Greek_UPSILONaccent --> GREEK CAPITAL LETTER UPSILON WITH TONOS
        keysym2UCSHash.put( (long)0x7a9, (char)0x03ab); // XK_Greek_UPSILONdieresis --> GREEK CAPITAL LETTER UPSILON WITH DIALYTIKA
        keysym2UCSHash.put( (long)0x7ab, (char)0x038f); // XK_Greek_OMEGAaccent --> GREEK CAPITAL LETTER OMEGA WITH TONOS
        keysym2UCSHash.put( (long)0x7ae, (char)0x0385); // XK_Greek_accentdieresis --> GREEK DIALYTIKA TONOS
        keysym2UCSHash.put( (long)0x7af, (char)0x2015); // XK_Greek_horizbar --> HORIZONTAL BAR
        keysym2UCSHash.put( (long)0x7b1, (char)0x03ac); // XK_Greek_alphaaccent --> GREEK SMALL LETTER ALPHA WITH TONOS
        keysym2UCSHash.put( (long)0x7b2, (char)0x03ad); // XK_Greek_epsilonaccent --> GREEK SMALL LETTER EPSILON WITH TONOS
        keysym2UCSHash.put( (long)0x7b3, (char)0x03ae); // XK_Greek_etaaccent --> GREEK SMALL LETTER ETA WITH TONOS
        keysym2UCSHash.put( (long)0x7b4, (char)0x03af); // XK_Greek_iotaaccent --> GREEK SMALL LETTER IOTA WITH TONOS
        keysym2UCSHash.put( (long)0x7b5, (char)0x03ca); // XK_Greek_iotadieresis --> GREEK SMALL LETTER IOTA WITH DIALYTIKA
        keysym2UCSHash.put( (long)0x7b6, (char)0x0390); // XK_Greek_iotaaccentdieresis --> GREEK SMALL LETTER IOTA WITH DIALYTIKA AND TONOS
        keysym2UCSHash.put( (long)0x7b7, (char)0x03cc); // XK_Greek_omicronaccent --> GREEK SMALL LETTER OMICRON WITH TONOS
        keysym2UCSHash.put( (long)0x7b8, (char)0x03cd); // XK_Greek_upsilonaccent --> GREEK SMALL LETTER UPSILON WITH TONOS
        keysym2UCSHash.put( (long)0x7b9, (char)0x03cb); // XK_Greek_upsilondieresis --> GREEK SMALL LETTER UPSILON WITH DIALYTIKA
        keysym2UCSHash.put( (long)0x7ba, (char)0x03b0); // XK_Greek_upsilonaccentdieresis --> GREEK SMALL LETTER UPSILON WITH DIALYTIKA AND TONOS
        keysym2UCSHash.put( (long)0x7bb, (char)0x03ce); // XK_Greek_omegaaccent --> GREEK SMALL LETTER OMEGA WITH TONOS
        keysym2UCSHash.put( (long)0x7c1, (char)0x0391); // XK_Greek_ALPHA --> GREEK CAPITAL LETTER ALPHA
        keysym2UCSHash.put( (long)0x7c2, (char)0x0392); // XK_Greek_BETA --> GREEK CAPITAL LETTER BETA
        keysym2UCSHash.put( (long)0x7c3, (char)0x0393); // XK_Greek_GAMMA --> GREEK CAPITAL LETTER GAMMA
        keysym2UCSHash.put( (long)0x7c4, (char)0x0394); // XK_Greek_DELTA --> GREEK CAPITAL LETTER DELTA
        keysym2UCSHash.put( (long)0x7c5, (char)0x0395); // XK_Greek_EPSILON --> GREEK CAPITAL LETTER EPSILON
        keysym2UCSHash.put( (long)0x7c6, (char)0x0396); // XK_Greek_ZETA --> GREEK CAPITAL LETTER ZETA
        keysym2UCSHash.put( (long)0x7c7, (char)0x0397); // XK_Greek_ETA --> GREEK CAPITAL LETTER ETA
        keysym2UCSHash.put( (long)0x7c8, (char)0x0398); // XK_Greek_THETA --> GREEK CAPITAL LETTER THETA
        keysym2UCSHash.put( (long)0x7c9, (char)0x0399); // XK_Greek_IOTA --> GREEK CAPITAL LETTER IOTA
        keysym2UCSHash.put( (long)0x7ca, (char)0x039a); // XK_Greek_KAPPA --> GREEK CAPITAL LETTER KAPPA
        keysym2UCSHash.put( (long)0x7cb, (char)0x039b); // XK_Greek_LAMBDA --> GREEK CAPITAL LETTER LAMDA
        keysym2UCSHash.put( (long)0x7cc, (char)0x039c); // XK_Greek_MU --> GREEK CAPITAL LETTER MU
        keysym2UCSHash.put( (long)0x7cd, (char)0x039d); // XK_Greek_NU --> GREEK CAPITAL LETTER NU
        keysym2UCSHash.put( (long)0x7ce, (char)0x039e); // XK_Greek_XI --> GREEK CAPITAL LETTER XI
        keysym2UCSHash.put( (long)0x7cf, (char)0x039f); // XK_Greek_OMICRON --> GREEK CAPITAL LETTER OMICRON
        keysym2UCSHash.put( (long)0x7d0, (char)0x03a0); // XK_Greek_PI --> GREEK CAPITAL LETTER PI
        keysym2UCSHash.put( (long)0x7d1, (char)0x03a1); // XK_Greek_RHO --> GREEK CAPITAL LETTER RHO
        keysym2UCSHash.put( (long)0x7d2, (char)0x03a3); // XK_Greek_SIGMA --> GREEK CAPITAL LETTER SIGMA
        keysym2UCSHash.put( (long)0x7d4, (char)0x03a4); // XK_Greek_TAU --> GREEK CAPITAL LETTER TAU
        keysym2UCSHash.put( (long)0x7d5, (char)0x03a5); // XK_Greek_UPSILON --> GREEK CAPITAL LETTER UPSILON
        keysym2UCSHash.put( (long)0x7d6, (char)0x03a6); // XK_Greek_PHI --> GREEK CAPITAL LETTER PHI
        keysym2UCSHash.put( (long)0x7d7, (char)0x03a7); // XK_Greek_CHI --> GREEK CAPITAL LETTER CHI
        keysym2UCSHash.put( (long)0x7d8, (char)0x03a8); // XK_Greek_PSI --> GREEK CAPITAL LETTER PSI
        keysym2UCSHash.put( (long)0x7d9, (char)0x03a9); // XK_Greek_OMEGA --> GREEK CAPITAL LETTER OMEGA
        keysym2UCSHash.put( (long)0x7e1, (char)0x03b1); // XK_Greek_alpha --> GREEK SMALL LETTER ALPHA
        keysym2UCSHash.put( (long)0x7e2, (char)0x03b2); // XK_Greek_beta --> GREEK SMALL LETTER BETA
        keysym2UCSHash.put( (long)0x7e3, (char)0x03b3); // XK_Greek_gamma --> GREEK SMALL LETTER GAMMA
        keysym2UCSHash.put( (long)0x7e4, (char)0x03b4); // XK_Greek_delta --> GREEK SMALL LETTER DELTA
        keysym2UCSHash.put( (long)0x7e5, (char)0x03b5); // XK_Greek_epsilon --> GREEK SMALL LETTER EPSILON
        keysym2UCSHash.put( (long)0x7e6, (char)0x03b6); // XK_Greek_zeta --> GREEK SMALL LETTER ZETA
        keysym2UCSHash.put( (long)0x7e7, (char)0x03b7); // XK_Greek_eta --> GREEK SMALL LETTER ETA
        keysym2UCSHash.put( (long)0x7e8, (char)0x03b8); // XK_Greek_theta --> GREEK SMALL LETTER THETA
        keysym2UCSHash.put( (long)0x7e9, (char)0x03b9); // XK_Greek_iota --> GREEK SMALL LETTER IOTA
        keysym2UCSHash.put( (long)0x7ea, (char)0x03ba); // XK_Greek_kappa --> GREEK SMALL LETTER KAPPA
        keysym2UCSHash.put( (long)0x7eb, (char)0x03bb); // XK_Greek_lambda --> GREEK SMALL LETTER LAMDA
        keysym2UCSHash.put( (long)0x7ec, (char)0x03bc); // XK_Greek_mu --> GREEK SMALL LETTER MU
        keysym2UCSHash.put( (long)0x7ed, (char)0x03bd); // XK_Greek_nu --> GREEK SMALL LETTER NU
        keysym2UCSHash.put( (long)0x7ee, (char)0x03be); // XK_Greek_xi --> GREEK SMALL LETTER XI
        keysym2UCSHash.put( (long)0x7ef, (char)0x03bf); // XK_Greek_omicron --> GREEK SMALL LETTER OMICRON
        keysym2UCSHash.put( (long)0x7f0, (char)0x03c0); // XK_Greek_pi --> GREEK SMALL LETTER PI
        keysym2UCSHash.put( (long)0x7f1, (char)0x03c1); // XK_Greek_rho --> GREEK SMALL LETTER RHO
        keysym2UCSHash.put( (long)0x7f2, (char)0x03c3); // XK_Greek_sigma --> GREEK SMALL LETTER SIGMA
        keysym2UCSHash.put( (long)0x7f3, (char)0x03c2); // XK_Greek_finalsmallsigma --> GREEK SMALL LETTER FINAL SIGMA
        keysym2UCSHash.put( (long)0x7f4, (char)0x03c4); // XK_Greek_tau --> GREEK SMALL LETTER TAU
        keysym2UCSHash.put( (long)0x7f5, (char)0x03c5); // XK_Greek_upsilon --> GREEK SMALL LETTER UPSILON
        keysym2UCSHash.put( (long)0x7f6, (char)0x03c6); // XK_Greek_phi --> GREEK SMALL LETTER PHI
        keysym2UCSHash.put( (long)0x7f7, (char)0x03c7); // XK_Greek_chi --> GREEK SMALL LETTER CHI
        keysym2UCSHash.put( (long)0x7f8, (char)0x03c8); // XK_Greek_psi --> GREEK SMALL LETTER PSI
        keysym2UCSHash.put( (long)0x7f9, (char)0x03c9); // XK_Greek_omega --> GREEK SMALL LETTER OMEGA
        keysym2UCSHash.put( (long)0x8a1, (char)0x23b7); // XK_leftradical --> RADICAL SYMBOL BOTTOM
        keysym2UCSHash.put( (long)0x8a2, (char)0x250c); // XK_topleftradical --> BOX DRAWINGS LIGHT DOWN AND RIGHT
        keysym2UCSHash.put( (long)0x8a3, (char)0x2500); // XK_horizconnector --> BOX DRAWINGS LIGHT HORIZONTAL
        keysym2UCSHash.put( (long)0x8a4, (char)0x2320); // XK_topintegral --> TOP HALF INTEGRAL
        keysym2UCSHash.put( (long)0x8a5, (char)0x2321); // XK_botintegral --> BOTTOM HALF INTEGRAL
        keysym2UCSHash.put( (long)0x8a6, (char)0x2502); // XK_vertconnector --> BOX DRAWINGS LIGHT VERTICAL
        keysym2UCSHash.put( (long)0x8a7, (char)0x23a1); // XK_topleftsqbracket --> LEFT SQUARE BRACKET UPPER CORNER
        keysym2UCSHash.put( (long)0x8a8, (char)0x23a3); // XK_botleftsqbracket --> LEFT SQUARE BRACKET LOWER CORNER
        keysym2UCSHash.put( (long)0x8a9, (char)0x23a4); // XK_toprightsqbracket --> RIGHT SQUARE BRACKET UPPER CORNER
        keysym2UCSHash.put( (long)0x8aa, (char)0x23a6); // XK_botrightsqbracket --> RIGHT SQUARE BRACKET LOWER CORNER
        keysym2UCSHash.put( (long)0x8ab, (char)0x239b); // XK_topleftparens --> LEFT PARENTHESIS UPPER HOOK
        keysym2UCSHash.put( (long)0x8ac, (char)0x239d); // XK_botleftparens --> LEFT PARENTHESIS LOWER HOOK
        keysym2UCSHash.put( (long)0x8ad, (char)0x239e); // XK_toprightparens --> RIGHT PARENTHESIS UPPER HOOK
        keysym2UCSHash.put( (long)0x8ae, (char)0x23a0); // XK_botrightparens --> RIGHT PARENTHESIS LOWER HOOK
        keysym2UCSHash.put( (long)0x8af, (char)0x23a8); // XK_leftmiddlecurlybrace --> LEFT CURLY BRACKET MIDDLE PIECE
        keysym2UCSHash.put( (long)0x8b0, (char)0x23ac); // XK_rightmiddlecurlybrace --> RIGHT CURLY BRACKET MIDDLE PIECE
        keysym2UCSHash.put( (long)0x8bc, (char)0x2264); // XK_lessthanequal --> LESS-THAN OR EQUAL TO
        keysym2UCSHash.put( (long)0x8bd, (char)0x2260); // XK_notequal --> NOT EQUAL TO
        keysym2UCSHash.put( (long)0x8be, (char)0x2265); // XK_greaterthanequal --> GREATER-THAN OR EQUAL TO
        keysym2UCSHash.put( (long)0x8bf, (char)0x222b); // XK_integral --> INTEGRAL
        keysym2UCSHash.put( (long)0x8c0, (char)0x2234); // XK_therefore --> THEREFORE
        keysym2UCSHash.put( (long)0x8c1, (char)0x221d); // XK_variation --> PROPORTIONAL TO
        keysym2UCSHash.put( (long)0x8c2, (char)0x221e); // XK_infinity --> INFINITY
        keysym2UCSHash.put( (long)0x8c5, (char)0x2207); // XK_nabla --> NABLA
        keysym2UCSHash.put( (long)0x8c8, (char)0x223c); // XK_approximate --> TILDE OPERATOR
        keysym2UCSHash.put( (long)0x8c9, (char)0x2243); // XK_similarequal --> ASYMPTOTICALLY EQUAL TO
        keysym2UCSHash.put( (long)0x8cd, (char)0x2104); // XK_ifonlyif --> CENTRE LINE SYMBOL
        keysym2UCSHash.put( (long)0x8ce, (char)0x21d2); // XK_implies --> RIGHTWARDS DOUBLE ARROW
        keysym2UCSHash.put( (long)0x8cf, (char)0x2261); // XK_identical --> IDENTICAL TO
        keysym2UCSHash.put( (long)0x8d6, (char)0x221a); // XK_radical --> SQUARE ROOT
        keysym2UCSHash.put( (long)0x8da, (char)0x2282); // XK_includedin --> SUBSET OF
        keysym2UCSHash.put( (long)0x8db, (char)0x2283); // XK_includes --> SUPERSET OF
        keysym2UCSHash.put( (long)0x8dc, (char)0x2229); // XK_intersection --> INTERSECTION
        keysym2UCSHash.put( (long)0x8dd, (char)0x222a); // XK_union --> UNION
        keysym2UCSHash.put( (long)0x8de, (char)0x2227); // XK_logicaland --> LOGICAL AND
        keysym2UCSHash.put( (long)0x8df, (char)0x2228); // XK_logicalor --> LOGICAL OR
        keysym2UCSHash.put( (long)0x8ef, (char)0x2202); // XK_partialderivative --> PARTIAL DIFFERENTIAL
        keysym2UCSHash.put( (long)0x8f6, (char)0x0192); // XK_function --> LATIN SMALL LETTER F WITH HOOK
        keysym2UCSHash.put( (long)0x8fb, (char)0x2190); // XK_leftarrow --> LEFTWARDS ARROW
        keysym2UCSHash.put( (long)0x8fc, (char)0x2191); // XK_uparrow --> UPWARDS ARROW
        keysym2UCSHash.put( (long)0x8fd, (char)0x2192); // XK_rightarrow --> RIGHTWARDS ARROW
        keysym2UCSHash.put( (long)0x8fe, (char)0x2193); // XK_downarrow --> DOWNWARDS ARROW
        keysym2UCSHash.put( (long)0x9e0, (char)0x25c6); // XK_soliddiamond --> BLACK DIAMOND
        keysym2UCSHash.put( (long)0x9e1, (char)0x2592); // XK_checkerboard --> MEDIUM SHADE
        keysym2UCSHash.put( (long)0x9e2, (char)0x2409); // XK_ht --> SYMBOL FOR HORIZONTAL TABULATION
        keysym2UCSHash.put( (long)0x9e3, (char)0x240c); // XK_ff --> SYMBOL FOR FORM FEED
        keysym2UCSHash.put( (long)0x9e4, (char)0x240d); // XK_cr --> SYMBOL FOR CARRIAGE RETURN
        keysym2UCSHash.put( (long)0x9e5, (char)0x240a); // XK_lf --> SYMBOL FOR LINE FEED
        keysym2UCSHash.put( (long)0x9e8, (char)0x2424); // XK_nl --> SYMBOL FOR NEWLINE
        keysym2UCSHash.put( (long)0x9e9, (char)0x240b); // XK_vt --> SYMBOL FOR VERTICAL TABULATION
        keysym2UCSHash.put( (long)0x9ea, (char)0x2518); // XK_lowrightcorner --> BOX DRAWINGS LIGHT UP AND LEFT
        keysym2UCSHash.put( (long)0x9eb, (char)0x2510); // XK_uprightcorner --> BOX DRAWINGS LIGHT DOWN AND LEFT
        keysym2UCSHash.put( (long)0x9ec, (char)0x250c); // XK_upleftcorner --> BOX DRAWINGS LIGHT DOWN AND RIGHT
        keysym2UCSHash.put( (long)0x9ed, (char)0x2514); // XK_lowleftcorner --> BOX DRAWINGS LIGHT UP AND RIGHT
        keysym2UCSHash.put( (long)0x9ee, (char)0x253c); // XK_crossinglines --> BOX DRAWINGS LIGHT VERTICAL AND HORIZONTAL
        keysym2UCSHash.put( (long)0x9ef, (char)0x23ba); // XK_horizlinescan1 --> HORIZONTAL SCAN LINE-1
        keysym2UCSHash.put( (long)0x9f0, (char)0x23bb); // XK_horizlinescan3 --> HORIZONTAL SCAN LINE-3
        keysym2UCSHash.put( (long)0x9f1, (char)0x2500); // XK_horizlinescan5 --> BOX DRAWINGS LIGHT HORIZONTAL
        keysym2UCSHash.put( (long)0x9f2, (char)0x23bc); // XK_horizlinescan7 --> HORIZONTAL SCAN LINE-7
        keysym2UCSHash.put( (long)0x9f3, (char)0x23bd); // XK_horizlinescan9 --> HORIZONTAL SCAN LINE-9
        keysym2UCSHash.put( (long)0x9f4, (char)0x251c); // XK_leftt --> BOX DRAWINGS LIGHT VERTICAL AND RIGHT
        keysym2UCSHash.put( (long)0x9f5, (char)0x2524); // XK_rightt --> BOX DRAWINGS LIGHT VERTICAL AND LEFT
        keysym2UCSHash.put( (long)0x9f6, (char)0x2534); // XK_bott --> BOX DRAWINGS LIGHT UP AND HORIZONTAL
        keysym2UCSHash.put( (long)0x9f7, (char)0x242c); // XK_topt -->
        keysym2UCSHash.put( (long)0x9f8, (char)0x2502); // XK_vertbar --> BOX DRAWINGS LIGHT VERTICAL
        keysym2UCSHash.put( (long)0xaa1, (char)0x2003); // XK_emspace --> EM SPACE
        keysym2UCSHash.put( (long)0xaa2, (char)0x2002); // XK_enspace --> EN SPACE
        keysym2UCSHash.put( (long)0xaa3, (char)0x2004); // XK_em3space --> THREE-PER-EM SPACE
        keysym2UCSHash.put( (long)0xaa4, (char)0x2005); // XK_em4space --> FOUR-PER-EM SPACE
        keysym2UCSHash.put( (long)0xaa5, (char)0x2007); // XK_digitspace --> FIGURE SPACE
        keysym2UCSHash.put( (long)0xaa6, (char)0x2008); // XK_punctspace --> PUNCTUATION SPACE
        keysym2UCSHash.put( (long)0xaa7, (char)0x2009); // XK_thinspace --> THIN SPACE
        keysym2UCSHash.put( (long)0xaa8, (char)0x200a); // XK_hairspace --> HAIR SPACE
        keysym2UCSHash.put( (long)0xaa9, (char)0x2014); // XK_emdash --> EM DASH
        keysym2UCSHash.put( (long)0xaaa, (char)0x2013); // XK_endash --> EN DASH
        keysym2UCSHash.put( (long)0xaac, (char)0x2423); // XK_signifblank --> OPEN BOX
        keysym2UCSHash.put( (long)0xaae, (char)0x2026); // XK_ellipsis --> HORIZONTAL ELLIPSIS
        keysym2UCSHash.put( (long)0xaaf, (char)0x2025); // XK_doubbaselinedot --> TWO DOT LEADER
        keysym2UCSHash.put( (long)0xab0, (char)0x2153); // XK_onethird --> VULGAR FRACTION ONE THIRD
        keysym2UCSHash.put( (long)0xab1, (char)0x2154); // XK_twothirds --> VULGAR FRACTION TWO THIRDS
        keysym2UCSHash.put( (long)0xab2, (char)0x2155); // XK_onefifth --> VULGAR FRACTION ONE FIFTH
        keysym2UCSHash.put( (long)0xab3, (char)0x2156); // XK_twofifths --> VULGAR FRACTION TWO FIFTHS
        keysym2UCSHash.put( (long)0xab4, (char)0x2157); // XK_threefifths --> VULGAR FRACTION THREE FIFTHS
        keysym2UCSHash.put( (long)0xab5, (char)0x2158); // XK_fourfifths --> VULGAR FRACTION FOUR FIFTHS
        keysym2UCSHash.put( (long)0xab6, (char)0x2159); // XK_onesixth --> VULGAR FRACTION ONE SIXTH
        keysym2UCSHash.put( (long)0xab7, (char)0x215a); // XK_fivesixths --> VULGAR FRACTION FIVE SIXTHS
        keysym2UCSHash.put( (long)0xab8, (char)0x2105); // XK_careof --> CARE OF
        keysym2UCSHash.put( (long)0xabb, (char)0x2012); // XK_figdash --> FIGURE DASH
        keysym2UCSHash.put( (long)0xabc, (char)0x27e8); // XK_leftanglebracket --> MATHEMATICAL LEFT ANGLE BRACKET
        keysym2UCSHash.put( (long)0xabd, (char)0x002e); // XK_decimalpoint --> FULL STOP
        keysym2UCSHash.put( (long)0xabe, (char)0x27e9); // XK_rightanglebracket --> MATHEMATICAL RIGHT ANGLE BRACKET
        keysym2UCSHash.put( (long)0xac3, (char)0x215b); // XK_oneeighth --> VULGAR FRACTION ONE EIGHTH
        keysym2UCSHash.put( (long)0xac4, (char)0x215c); // XK_threeeighths --> VULGAR FRACTION THREE EIGHTHS
        keysym2UCSHash.put( (long)0xac5, (char)0x215d); // XK_fiveeighths --> VULGAR FRACTION FIVE EIGHTHS
        keysym2UCSHash.put( (long)0xac6, (char)0x215e); // XK_seveneighths --> VULGAR FRACTION SEVEN EIGHTHS
        keysym2UCSHash.put( (long)0xac9, (char)0x2122); // XK_trademark --> TRADE MARK SIGN
        keysym2UCSHash.put( (long)0xaca, (char)0x2613); // XK_signaturemark --> SALTIRE
        keysym2UCSHash.put( (long)0xacc, (char)0x25c1); // XK_leftopentriangle --> WHITE LEFT-POINTING TRIANGLE
        keysym2UCSHash.put( (long)0xacd, (char)0x25b7); // XK_rightopentriangle --> WHITE RIGHT-POINTING TRIANGLE
        keysym2UCSHash.put( (long)0xace, (char)0x25cb); // XK_emopencircle --> WHITE CIRCLE
        keysym2UCSHash.put( (long)0xacf, (char)0x25af); // XK_emopenrectangle --> WHITE VERTICAL RECTANGLE
        keysym2UCSHash.put( (long)0xad0, (char)0x2018); // XK_leftsinglequotemark --> LEFT SINGLE QUOTATION MARK
        keysym2UCSHash.put( (long)0xad1, (char)0x2019); // XK_rightsinglequotemark --> RIGHT SINGLE QUOTATION MARK
        keysym2UCSHash.put( (long)0xad2, (char)0x201c); // XK_leftdoublequotemark --> LEFT DOUBLE QUOTATION MARK
        keysym2UCSHash.put( (long)0xad3, (char)0x201d); // XK_rightdoublequotemark --> RIGHT DOUBLE QUOTATION MARK
        keysym2UCSHash.put( (long)0xad4, (char)0x211e); // XK_prescription --> PRESCRIPTION TAKE
        keysym2UCSHash.put( (long)0xad6, (char)0x2032); // XK_minutes --> PRIME
        keysym2UCSHash.put( (long)0xad7, (char)0x2033); // XK_seconds --> DOUBLE PRIME
        keysym2UCSHash.put( (long)0xad9, (char)0x271d); // XK_latincross --> LATIN CROSS
        keysym2UCSHash.put( (long)0xadb, (char)0x25ac); // XK_filledrectbullet --> BLACK RECTANGLE
        keysym2UCSHash.put( (long)0xadc, (char)0x25c0); // XK_filledlefttribullet --> BLACK LEFT-POINTING TRIANGLE
        keysym2UCSHash.put( (long)0xadd, (char)0x25b6); // XK_filledrighttribullet --> BLACK RIGHT-POINTING TRIANGLE
        keysym2UCSHash.put( (long)0xade, (char)0x25cf); // XK_emfilledcircle --> BLACK CIRCLE
        keysym2UCSHash.put( (long)0xadf, (char)0x25ae); // XK_emfilledrect --> BLACK VERTICAL RECTANGLE
        keysym2UCSHash.put( (long)0xae0, (char)0x25e6); // XK_enopencircbullet --> WHITE BULLET
        keysym2UCSHash.put( (long)0xae1, (char)0x25ab); // XK_enopensquarebullet --> WHITE SMALL SQUARE
        keysym2UCSHash.put( (long)0xae2, (char)0x25ad); // XK_openrectbullet --> WHITE RECTANGLE
        keysym2UCSHash.put( (long)0xae3, (char)0x25b3); // XK_opentribulletup --> WHITE UP-POINTING TRIANGLE
        keysym2UCSHash.put( (long)0xae4, (char)0x25bd); // XK_opentribulletdown --> WHITE DOWN-POINTING TRIANGLE
        keysym2UCSHash.put( (long)0xae5, (char)0x2606); // XK_openstar --> WHITE STAR
        keysym2UCSHash.put( (long)0xae6, (char)0x2022); // XK_enfilledcircbullet --> BULLET
        keysym2UCSHash.put( (long)0xae7, (char)0x25aa); // XK_enfilledsqbullet --> BLACK SMALL SQUARE
        keysym2UCSHash.put( (long)0xae8, (char)0x25b2); // XK_filledtribulletup --> BLACK UP-POINTING TRIANGLE
        keysym2UCSHash.put( (long)0xae9, (char)0x25bc); // XK_filledtribulletdown --> BLACK DOWN-POINTING TRIANGLE
        keysym2UCSHash.put( (long)0xaea, (char)0x261c); // XK_leftpointer --> WHITE LEFT POINTING INDEX
        keysym2UCSHash.put( (long)0xaeb, (char)0x261e); // XK_rightpointer --> WHITE RIGHT POINTING INDEX
        keysym2UCSHash.put( (long)0xaec, (char)0x2663); // XK_club --> BLACK CLUB SUIT
        keysym2UCSHash.put( (long)0xaed, (char)0x2666); // XK_diamond --> BLACK DIAMOND SUIT
        keysym2UCSHash.put( (long)0xaee, (char)0x2665); // XK_heart --> BLACK HEART SUIT
        keysym2UCSHash.put( (long)0xaf0, (char)0x2720); // XK_maltesecross --> MALTESE CROSS
        keysym2UCSHash.put( (long)0xaf1, (char)0x2020); // XK_dagger --> DAGGER
        keysym2UCSHash.put( (long)0xaf2, (char)0x2021); // XK_doubledagger --> DOUBLE DAGGER
        keysym2UCSHash.put( (long)0xaf3, (char)0x2713); // XK_checkmark --> CHECK MARK
        keysym2UCSHash.put( (long)0xaf4, (char)0x2717); // XK_ballotcross --> BALLOT X
        keysym2UCSHash.put( (long)0xaf5, (char)0x266f); // XK_musicalsharp --> MUSIC SHARP SIGN
        keysym2UCSHash.put( (long)0xaf6, (char)0x266d); // XK_musicalflat --> MUSIC FLAT SIGN
        keysym2UCSHash.put( (long)0xaf7, (char)0x2642); // XK_malesymbol --> MALE SIGN
        keysym2UCSHash.put( (long)0xaf8, (char)0x2640); // XK_femalesymbol --> FEMALE SIGN
        keysym2UCSHash.put( (long)0xaf9, (char)0x260e); // XK_telephone --> BLACK TELEPHONE
        keysym2UCSHash.put( (long)0xafa, (char)0x2315); // XK_telephonerecorder --> TELEPHONE RECORDER
        keysym2UCSHash.put( (long)0xafb, (char)0x2117); // XK_phonographcopyright --> SOUND RECORDING COPYRIGHT
        keysym2UCSHash.put( (long)0xafc, (char)0x2038); // XK_caret --> CARET
        keysym2UCSHash.put( (long)0xafd, (char)0x201a); // XK_singlelowquotemark --> SINGLE LOW-9 QUOTATION MARK
        keysym2UCSHash.put( (long)0xafe, (char)0x201e); // XK_doublelowquotemark --> DOUBLE LOW-9 QUOTATION MARK
        keysym2UCSHash.put( (long)0xba3, (char)0x003c); // XK_leftcaret --> LESS-THAN SIGN
        keysym2UCSHash.put( (long)0xba6, (char)0x003e); // XK_rightcaret --> GREATER-THAN SIGN
        keysym2UCSHash.put( (long)0xba8, (char)0x2228); // XK_downcaret --> LOGICAL OR
        keysym2UCSHash.put( (long)0xba9, (char)0x2227); // XK_upcaret --> LOGICAL AND
        keysym2UCSHash.put( (long)0xbc0, (char)0x00af); // XK_overbar --> MACRON
        keysym2UCSHash.put( (long)0xbc2, (char)0x22a5); // XK_downtack --> UP TACK
        keysym2UCSHash.put( (long)0xbc3, (char)0x2229); // XK_upshoe --> INTERSECTION
        keysym2UCSHash.put( (long)0xbc4, (char)0x230a); // XK_downstile --> LEFT FLOOR
        keysym2UCSHash.put( (long)0xbc6, (char)0x005f); // XK_underbar --> LOW LINE
        keysym2UCSHash.put( (long)0xbca, (char)0x2218); // XK_jot --> RING OPERATOR
        keysym2UCSHash.put( (long)0xbcc, (char)0x2395); // XK_quad --> APL FUNCTIONAL SYMBOL QUAD
        keysym2UCSHash.put( (long)0xbce, (char)0x22a4); // XK_uptack --> DOWN TACK
        keysym2UCSHash.put( (long)0xbcf, (char)0x25cb); // XK_circle --> WHITE CIRCLE
        keysym2UCSHash.put( (long)0xbd3, (char)0x2308); // XK_upstile --> LEFT CEILING
        keysym2UCSHash.put( (long)0xbd6, (char)0x222a); // XK_downshoe --> UNION
        keysym2UCSHash.put( (long)0xbd8, (char)0x2283); // XK_rightshoe --> SUPERSET OF
        keysym2UCSHash.put( (long)0xbda, (char)0x2282); // XK_leftshoe --> SUBSET OF
        keysym2UCSHash.put( (long)0xbdc, (char)0x22a2); // XK_lefttack --> RIGHT TACK
        keysym2UCSHash.put( (long)0xbfc, (char)0x22a3); // XK_righttack --> LEFT TACK
        keysym2UCSHash.put( (long)0xcdf, (char)0x2017); // XK_hebrew_doublelowline --> DOUBLE LOW LINE
        keysym2UCSHash.put( (long)0xce0, (char)0x05d0); // XK_hebrew_aleph --> HEBREW LETTER ALEF
        keysym2UCSHash.put( (long)0xce1, (char)0x05d1); // XK_hebrew_bet --> HEBREW LETTER BET
        keysym2UCSHash.put( (long)0xce2, (char)0x05d2); // XK_hebrew_gimel --> HEBREW LETTER GIMEL
        keysym2UCSHash.put( (long)0xce3, (char)0x05d3); // XK_hebrew_dalet --> HEBREW LETTER DALET
        keysym2UCSHash.put( (long)0xce4, (char)0x05d4); // XK_hebrew_he --> HEBREW LETTER HE
        keysym2UCSHash.put( (long)0xce5, (char)0x05d5); // XK_hebrew_waw --> HEBREW LETTER VAV
        keysym2UCSHash.put( (long)0xce6, (char)0x05d6); // XK_hebrew_zain --> HEBREW LETTER ZAYIN
        keysym2UCSHash.put( (long)0xce7, (char)0x05d7); // XK_hebrew_chet --> HEBREW LETTER HET
        keysym2UCSHash.put( (long)0xce8, (char)0x05d8); // XK_hebrew_tet --> HEBREW LETTER TET
        keysym2UCSHash.put( (long)0xce9, (char)0x05d9); // XK_hebrew_yod --> HEBREW LETTER YOD
        keysym2UCSHash.put( (long)0xcea, (char)0x05da); // XK_hebrew_finalkaph --> HEBREW LETTER FINAL KAF
        keysym2UCSHash.put( (long)0xceb, (char)0x05db); // XK_hebrew_kaph --> HEBREW LETTER KAF
        keysym2UCSHash.put( (long)0xcec, (char)0x05dc); // XK_hebrew_lamed --> HEBREW LETTER LAMED
        keysym2UCSHash.put( (long)0xced, (char)0x05dd); // XK_hebrew_finalmem --> HEBREW LETTER FINAL MEM
        keysym2UCSHash.put( (long)0xcee, (char)0x05de); // XK_hebrew_mem --> HEBREW LETTER MEM
        keysym2UCSHash.put( (long)0xcef, (char)0x05df); // XK_hebrew_finalnun --> HEBREW LETTER FINAL NUN
        keysym2UCSHash.put( (long)0xcf0, (char)0x05e0); // XK_hebrew_nun --> HEBREW LETTER NUN
        keysym2UCSHash.put( (long)0xcf1, (char)0x05e1); // XK_hebrew_samech --> HEBREW LETTER SAMEKH
        keysym2UCSHash.put( (long)0xcf2, (char)0x05e2); // XK_hebrew_ayin --> HEBREW LETTER AYIN
        keysym2UCSHash.put( (long)0xcf3, (char)0x05e3); // XK_hebrew_finalpe --> HEBREW LETTER FINAL PE
        keysym2UCSHash.put( (long)0xcf4, (char)0x05e4); // XK_hebrew_pe --> HEBREW LETTER PE
        keysym2UCSHash.put( (long)0xcf5, (char)0x05e5); // XK_hebrew_finalzade --> HEBREW LETTER FINAL TSADI
        keysym2UCSHash.put( (long)0xcf6, (char)0x05e6); // XK_hebrew_zade --> HEBREW LETTER TSADI
        keysym2UCSHash.put( (long)0xcf7, (char)0x05e7); // XK_hebrew_qoph --> HEBREW LETTER QOF
        keysym2UCSHash.put( (long)0xcf8, (char)0x05e8); // XK_hebrew_resh --> HEBREW LETTER RESH
        keysym2UCSHash.put( (long)0xcf9, (char)0x05e9); // XK_hebrew_shin --> HEBREW LETTER SHIN
        keysym2UCSHash.put( (long)0xcfa, (char)0x05ea); // XK_hebrew_taw --> HEBREW LETTER TAV
        keysym2UCSHash.put( (long)0xda1, (char)0x0e01); // XK_Thai_kokai --> THAI CHARACTER KO KAI
        keysym2UCSHash.put( (long)0xda2, (char)0x0e02); // XK_Thai_khokhai --> THAI CHARACTER KHO KHAI
        keysym2UCSHash.put( (long)0xda3, (char)0x0e03); // XK_Thai_khokhuat --> THAI CHARACTER KHO KHUAT
        keysym2UCSHash.put( (long)0xda4, (char)0x0e04); // XK_Thai_khokhwai --> THAI CHARACTER KHO KHWAI
        keysym2UCSHash.put( (long)0xda5, (char)0x0e05); // XK_Thai_khokhon --> THAI CHARACTER KHO KHON
        keysym2UCSHash.put( (long)0xda6, (char)0x0e06); // XK_Thai_khorakhang --> THAI CHARACTER KHO RAKHANG
        keysym2UCSHash.put( (long)0xda7, (char)0x0e07); // XK_Thai_ngongu --> THAI CHARACTER NGO NGU
        keysym2UCSHash.put( (long)0xda8, (char)0x0e08); // XK_Thai_chochan --> THAI CHARACTER CHO CHAN
        keysym2UCSHash.put( (long)0xda9, (char)0x0e09); // XK_Thai_choching --> THAI CHARACTER CHO CHING
        keysym2UCSHash.put( (long)0xdaa, (char)0x0e0a); // XK_Thai_chochang --> THAI CHARACTER CHO CHANG
        keysym2UCSHash.put( (long)0xdab, (char)0x0e0b); // XK_Thai_soso --> THAI CHARACTER SO SO
        keysym2UCSHash.put( (long)0xdac, (char)0x0e0c); // XK_Thai_chochoe --> THAI CHARACTER CHO CHOE
        keysym2UCSHash.put( (long)0xdad, (char)0x0e0d); // XK_Thai_yoying --> THAI CHARACTER YO YING
        keysym2UCSHash.put( (long)0xdae, (char)0x0e0e); // XK_Thai_dochada --> THAI CHARACTER DO CHADA
        keysym2UCSHash.put( (long)0xdaf, (char)0x0e0f); // XK_Thai_topatak --> THAI CHARACTER TO PATAK
        keysym2UCSHash.put( (long)0xdb0, (char)0x0e10); // XK_Thai_thothan --> THAI CHARACTER THO THAN
        keysym2UCSHash.put( (long)0xdb1, (char)0x0e11); // XK_Thai_thonangmontho --> THAI CHARACTER THO NANGMONTHO
        keysym2UCSHash.put( (long)0xdb2, (char)0x0e12); // XK_Thai_thophuthao --> THAI CHARACTER THO PHUTHAO
        keysym2UCSHash.put( (long)0xdb3, (char)0x0e13); // XK_Thai_nonen --> THAI CHARACTER NO NEN
        keysym2UCSHash.put( (long)0xdb4, (char)0x0e14); // XK_Thai_dodek --> THAI CHARACTER DO DEK
        keysym2UCSHash.put( (long)0xdb5, (char)0x0e15); // XK_Thai_totao --> THAI CHARACTER TO TAO
        keysym2UCSHash.put( (long)0xdb6, (char)0x0e16); // XK_Thai_thothung --> THAI CHARACTER THO THUNG
        keysym2UCSHash.put( (long)0xdb7, (char)0x0e17); // XK_Thai_thothahan --> THAI CHARACTER THO THAHAN
        keysym2UCSHash.put( (long)0xdb8, (char)0x0e18); // XK_Thai_thothong --> THAI CHARACTER THO THONG
        keysym2UCSHash.put( (long)0xdb9, (char)0x0e19); // XK_Thai_nonu --> THAI CHARACTER NO NU
        keysym2UCSHash.put( (long)0xdba, (char)0x0e1a); // XK_Thai_bobaimai --> THAI CHARACTER BO BAIMAI
        keysym2UCSHash.put( (long)0xdbb, (char)0x0e1b); // XK_Thai_popla --> THAI CHARACTER PO PLA
        keysym2UCSHash.put( (long)0xdbc, (char)0x0e1c); // XK_Thai_phophung --> THAI CHARACTER PHO PHUNG
        keysym2UCSHash.put( (long)0xdbd, (char)0x0e1d); // XK_Thai_fofa --> THAI CHARACTER FO FA
        keysym2UCSHash.put( (long)0xdbe, (char)0x0e1e); // XK_Thai_phophan --> THAI CHARACTER PHO PHAN
        keysym2UCSHash.put( (long)0xdbf, (char)0x0e1f); // XK_Thai_fofan --> THAI CHARACTER FO FAN
        keysym2UCSHash.put( (long)0xdc0, (char)0x0e20); // XK_Thai_phosamphao --> THAI CHARACTER PHO SAMPHAO
        keysym2UCSHash.put( (long)0xdc1, (char)0x0e21); // XK_Thai_moma --> THAI CHARACTER MO MA
        keysym2UCSHash.put( (long)0xdc2, (char)0x0e22); // XK_Thai_yoyak --> THAI CHARACTER YO YAK
        keysym2UCSHash.put( (long)0xdc3, (char)0x0e23); // XK_Thai_rorua --> THAI CHARACTER RO RUA
        keysym2UCSHash.put( (long)0xdc4, (char)0x0e24); // XK_Thai_ru --> THAI CHARACTER RU
        keysym2UCSHash.put( (long)0xdc5, (char)0x0e25); // XK_Thai_loling --> THAI CHARACTER LO LING
        keysym2UCSHash.put( (long)0xdc6, (char)0x0e26); // XK_Thai_lu --> THAI CHARACTER LU
        keysym2UCSHash.put( (long)0xdc7, (char)0x0e27); // XK_Thai_wowaen --> THAI CHARACTER WO WAEN
        keysym2UCSHash.put( (long)0xdc8, (char)0x0e28); // XK_Thai_sosala --> THAI CHARACTER SO SALA
        keysym2UCSHash.put( (long)0xdc9, (char)0x0e29); // XK_Thai_sorusi --> THAI CHARACTER SO RUSI
        keysym2UCSHash.put( (long)0xdca, (char)0x0e2a); // XK_Thai_sosua --> THAI CHARACTER SO SUA
        keysym2UCSHash.put( (long)0xdcb, (char)0x0e2b); // XK_Thai_hohip --> THAI CHARACTER HO HIP
        keysym2UCSHash.put( (long)0xdcc, (char)0x0e2c); // XK_Thai_lochula --> THAI CHARACTER LO CHULA
        keysym2UCSHash.put( (long)0xdcd, (char)0x0e2d); // XK_Thai_oang --> THAI CHARACTER O ANG
        keysym2UCSHash.put( (long)0xdce, (char)0x0e2e); // XK_Thai_honokhuk --> THAI CHARACTER HO NOKHUK
        keysym2UCSHash.put( (long)0xdcf, (char)0x0e2f); // XK_Thai_paiyannoi --> THAI CHARACTER PAIYANNOI
        keysym2UCSHash.put( (long)0xdd0, (char)0x0e30); // XK_Thai_saraa --> THAI CHARACTER SARA A
        keysym2UCSHash.put( (long)0xdd1, (char)0x0e31); // XK_Thai_maihanakat --> THAI CHARACTER MAI HAN-AKAT
        keysym2UCSHash.put( (long)0xdd2, (char)0x0e32); // XK_Thai_saraaa --> THAI CHARACTER SARA AA
        keysym2UCSHash.put( (long)0xdd3, (char)0x0e33); // XK_Thai_saraam --> THAI CHARACTER SARA AM
        keysym2UCSHash.put( (long)0xdd4, (char)0x0e34); // XK_Thai_sarai --> THAI CHARACTER SARA I
        keysym2UCSHash.put( (long)0xdd5, (char)0x0e35); // XK_Thai_saraii --> THAI CHARACTER SARA II
        keysym2UCSHash.put( (long)0xdd6, (char)0x0e36); // XK_Thai_saraue --> THAI CHARACTER SARA UE
        keysym2UCSHash.put( (long)0xdd7, (char)0x0e37); // XK_Thai_sarauee --> THAI CHARACTER SARA UEE
        keysym2UCSHash.put( (long)0xdd8, (char)0x0e38); // XK_Thai_sarau --> THAI CHARACTER SARA U
        keysym2UCSHash.put( (long)0xdd9, (char)0x0e39); // XK_Thai_sarauu --> THAI CHARACTER SARA UU
        keysym2UCSHash.put( (long)0xdda, (char)0x0e3a); // XK_Thai_phinthu --> THAI CHARACTER PHINTHU
        keysym2UCSHash.put( (long)0xddf, (char)0x0e3f); // XK_Thai_baht --> THAI CURRENCY SYMBOL BAHT
        keysym2UCSHash.put( (long)0xde0, (char)0x0e40); // XK_Thai_sarae --> THAI CHARACTER SARA E
        keysym2UCSHash.put( (long)0xde1, (char)0x0e41); // XK_Thai_saraae --> THAI CHARACTER SARA AE
        keysym2UCSHash.put( (long)0xde2, (char)0x0e42); // XK_Thai_sarao --> THAI CHARACTER SARA O
        keysym2UCSHash.put( (long)0xde3, (char)0x0e43); // XK_Thai_saraaimaimuan --> THAI CHARACTER SARA AI MAIMUAN
        keysym2UCSHash.put( (long)0xde4, (char)0x0e44); // XK_Thai_saraaimaimalai --> THAI CHARACTER SARA AI MAIMALAI
        keysym2UCSHash.put( (long)0xde5, (char)0x0e45); // XK_Thai_lakkhangyao --> THAI CHARACTER LAKKHANGYAO
        keysym2UCSHash.put( (long)0xde6, (char)0x0e46); // XK_Thai_maiyamok --> THAI CHARACTER MAIYAMOK
        keysym2UCSHash.put( (long)0xde7, (char)0x0e47); // XK_Thai_maitaikhu --> THAI CHARACTER MAITAIKHU
        keysym2UCSHash.put( (long)0xde8, (char)0x0e48); // XK_Thai_maiek --> THAI CHARACTER MAI EK
        keysym2UCSHash.put( (long)0xde9, (char)0x0e49); // XK_Thai_maitho --> THAI CHARACTER MAI THO
        keysym2UCSHash.put( (long)0xdea, (char)0x0e4a); // XK_Thai_maitri --> THAI CHARACTER MAI TRI
        keysym2UCSHash.put( (long)0xdeb, (char)0x0e4b); // XK_Thai_maichattawa --> THAI CHARACTER MAI CHATTAWA
        keysym2UCSHash.put( (long)0xdec, (char)0x0e4c); // XK_Thai_thanthakhat --> THAI CHARACTER THANTHAKHAT
        keysym2UCSHash.put( (long)0xded, (char)0x0e4d); // XK_Thai_nikhahit --> THAI CHARACTER NIKHAHIT
        keysym2UCSHash.put( (long)0xdf0, (char)0x0e50); // XK_Thai_leksun --> THAI DIGIT ZERO
        keysym2UCSHash.put( (long)0xdf1, (char)0x0e51); // XK_Thai_leknung --> THAI DIGIT ONE
        keysym2UCSHash.put( (long)0xdf2, (char)0x0e52); // XK_Thai_leksong --> THAI DIGIT TWO
        keysym2UCSHash.put( (long)0xdf3, (char)0x0e53); // XK_Thai_leksam --> THAI DIGIT THREE
        keysym2UCSHash.put( (long)0xdf4, (char)0x0e54); // XK_Thai_leksi --> THAI DIGIT FOUR
        keysym2UCSHash.put( (long)0xdf5, (char)0x0e55); // XK_Thai_lekha --> THAI DIGIT FIVE
        keysym2UCSHash.put( (long)0xdf6, (char)0x0e56); // XK_Thai_lekhok --> THAI DIGIT SIX
        keysym2UCSHash.put( (long)0xdf7, (char)0x0e57); // XK_Thai_lekchet --> THAI DIGIT SEVEN
        keysym2UCSHash.put( (long)0xdf8, (char)0x0e58); // XK_Thai_lekpaet --> THAI DIGIT EIGHT
        keysym2UCSHash.put( (long)0xdf9, (char)0x0e59); // XK_Thai_lekkao --> THAI DIGIT NINE
        keysym2UCSHash.put( (long)0xea1, (char)0x3131); // XK_Hangul_Kiyeog --> HANGUL LETTER KIYEOK
        keysym2UCSHash.put( (long)0xea2, (char)0x3132); // XK_Hangul_SsangKiyeog --> HANGUL LETTER SSANGKIYEOK
        keysym2UCSHash.put( (long)0xea3, (char)0x3133); // XK_Hangul_KiyeogSios --> HANGUL LETTER KIYEOK-SIOS
        keysym2UCSHash.put( (long)0xea4, (char)0x3134); // XK_Hangul_Nieun --> HANGUL LETTER NIEUN
        keysym2UCSHash.put( (long)0xea5, (char)0x3135); // XK_Hangul_NieunJieuj --> HANGUL LETTER NIEUN-CIEUC
        keysym2UCSHash.put( (long)0xea6, (char)0x3136); // XK_Hangul_NieunHieuh --> HANGUL LETTER NIEUN-HIEUH
        keysym2UCSHash.put( (long)0xea7, (char)0x3137); // XK_Hangul_Dikeud --> HANGUL LETTER TIKEUT
        keysym2UCSHash.put( (long)0xea8, (char)0x3138); // XK_Hangul_SsangDikeud --> HANGUL LETTER SSANGTIKEUT
        keysym2UCSHash.put( (long)0xea9, (char)0x3139); // XK_Hangul_Rieul --> HANGUL LETTER RIEUL
        keysym2UCSHash.put( (long)0xeaa, (char)0x313a); // XK_Hangul_RieulKiyeog --> HANGUL LETTER RIEUL-KIYEOK
        keysym2UCSHash.put( (long)0xeab, (char)0x313b); // XK_Hangul_RieulMieum --> HANGUL LETTER RIEUL-MIEUM
        keysym2UCSHash.put( (long)0xeac, (char)0x313c); // XK_Hangul_RieulPieub --> HANGUL LETTER RIEUL-PIEUP
        keysym2UCSHash.put( (long)0xead, (char)0x313d); // XK_Hangul_RieulSios --> HANGUL LETTER RIEUL-SIOS
        keysym2UCSHash.put( (long)0xeae, (char)0x313e); // XK_Hangul_RieulTieut --> HANGUL LETTER RIEUL-THIEUTH
        keysym2UCSHash.put( (long)0xeaf, (char)0x313f); // XK_Hangul_RieulPhieuf --> HANGUL LETTER RIEUL-PHIEUPH
        keysym2UCSHash.put( (long)0xeb0, (char)0x3140); // XK_Hangul_RieulHieuh --> HANGUL LETTER RIEUL-HIEUH
        keysym2UCSHash.put( (long)0xeb1, (char)0x3141); // XK_Hangul_Mieum --> HANGUL LETTER MIEUM
        keysym2UCSHash.put( (long)0xeb2, (char)0x3142); // XK_Hangul_Pieub --> HANGUL LETTER PIEUP
        keysym2UCSHash.put( (long)0xeb3, (char)0x3143); // XK_Hangul_SsangPieub --> HANGUL LETTER SSANGPIEUP
        keysym2UCSHash.put( (long)0xeb4, (char)0x3144); // XK_Hangul_PieubSios --> HANGUL LETTER PIEUP-SIOS
        keysym2UCSHash.put( (long)0xeb5, (char)0x3145); // XK_Hangul_Sios --> HANGUL LETTER SIOS
        keysym2UCSHash.put( (long)0xeb6, (char)0x3146); // XK_Hangul_SsangSios --> HANGUL LETTER SSANGSIOS
        keysym2UCSHash.put( (long)0xeb7, (char)0x3147); // XK_Hangul_Ieung --> HANGUL LETTER IEUNG
        keysym2UCSHash.put( (long)0xeb8, (char)0x3148); // XK_Hangul_Jieuj --> HANGUL LETTER CIEUC
        keysym2UCSHash.put( (long)0xeb9, (char)0x3149); // XK_Hangul_SsangJieuj --> HANGUL LETTER SSANGCIEUC
        keysym2UCSHash.put( (long)0xeba, (char)0x314a); // XK_Hangul_Cieuc --> HANGUL LETTER CHIEUCH
        keysym2UCSHash.put( (long)0xebb, (char)0x314b); // XK_Hangul_Khieuq --> HANGUL LETTER KHIEUKH
        keysym2UCSHash.put( (long)0xebc, (char)0x314c); // XK_Hangul_Tieut --> HANGUL LETTER THIEUTH
        keysym2UCSHash.put( (long)0xebd, (char)0x314d); // XK_Hangul_Phieuf --> HANGUL LETTER PHIEUPH
        keysym2UCSHash.put( (long)0xebe, (char)0x314e); // XK_Hangul_Hieuh --> HANGUL LETTER HIEUH
        keysym2UCSHash.put( (long)0xebf, (char)0x314f); // XK_Hangul_A --> HANGUL LETTER A
        keysym2UCSHash.put( (long)0xec0, (char)0x3150); // XK_Hangul_AE --> HANGUL LETTER AE
        keysym2UCSHash.put( (long)0xec1, (char)0x3151); // XK_Hangul_YA --> HANGUL LETTER YA
        keysym2UCSHash.put( (long)0xec2, (char)0x3152); // XK_Hangul_YAE --> HANGUL LETTER YAE
        keysym2UCSHash.put( (long)0xec3, (char)0x3153); // XK_Hangul_EO --> HANGUL LETTER EO
        keysym2UCSHash.put( (long)0xec4, (char)0x3154); // XK_Hangul_E --> HANGUL LETTER E
        keysym2UCSHash.put( (long)0xec5, (char)0x3155); // XK_Hangul_YEO --> HANGUL LETTER YEO
        keysym2UCSHash.put( (long)0xec6, (char)0x3156); // XK_Hangul_YE --> HANGUL LETTER YE
        keysym2UCSHash.put( (long)0xec7, (char)0x3157); // XK_Hangul_O --> HANGUL LETTER O
        keysym2UCSHash.put( (long)0xec8, (char)0x3158); // XK_Hangul_WA --> HANGUL LETTER WA
        keysym2UCSHash.put( (long)0xec9, (char)0x3159); // XK_Hangul_WAE --> HANGUL LETTER WAE
        keysym2UCSHash.put( (long)0xeca, (char)0x315a); // XK_Hangul_OE --> HANGUL LETTER OE
        keysym2UCSHash.put( (long)0xecb, (char)0x315b); // XK_Hangul_YO --> HANGUL LETTER YO
        keysym2UCSHash.put( (long)0xecc, (char)0x315c); // XK_Hangul_U --> HANGUL LETTER U
        keysym2UCSHash.put( (long)0xecd, (char)0x315d); // XK_Hangul_WEO --> HANGUL LETTER WEO
        keysym2UCSHash.put( (long)0xece, (char)0x315e); // XK_Hangul_WE --> HANGUL LETTER WE
        keysym2UCSHash.put( (long)0xecf, (char)0x315f); // XK_Hangul_WI --> HANGUL LETTER WI
        keysym2UCSHash.put( (long)0xed0, (char)0x3160); // XK_Hangul_YU --> HANGUL LETTER YU
        keysym2UCSHash.put( (long)0xed1, (char)0x3161); // XK_Hangul_EU --> HANGUL LETTER EU
        keysym2UCSHash.put( (long)0xed2, (char)0x3162); // XK_Hangul_YI --> HANGUL LETTER YI
        keysym2UCSHash.put( (long)0xed3, (char)0x3163); // XK_Hangul_I --> HANGUL LETTER I
        keysym2UCSHash.put( (long)0xed4, (char)0x11a8); // XK_Hangul_J_Kiyeog --> HANGUL JONGSEONG KIYEOK
        keysym2UCSHash.put( (long)0xed5, (char)0x11a9); // XK_Hangul_J_SsangKiyeog --> HANGUL JONGSEONG SSANGKIYEOK
        keysym2UCSHash.put( (long)0xed6, (char)0x11aa); // XK_Hangul_J_KiyeogSios --> HANGUL JONGSEONG KIYEOK-SIOS
        keysym2UCSHash.put( (long)0xed7, (char)0x11ab); // XK_Hangul_J_Nieun --> HANGUL JONGSEONG NIEUN
        keysym2UCSHash.put( (long)0xed8, (char)0x11ac); // XK_Hangul_J_NieunJieuj --> HANGUL JONGSEONG NIEUN-CIEUC
        keysym2UCSHash.put( (long)0xed9, (char)0x11ad); // XK_Hangul_J_NieunHieuh --> HANGUL JONGSEONG NIEUN-HIEUH
        keysym2UCSHash.put( (long)0xeda, (char)0x11ae); // XK_Hangul_J_Dikeud --> HANGUL JONGSEONG TIKEUT
        keysym2UCSHash.put( (long)0xedb, (char)0x11af); // XK_Hangul_J_Rieul --> HANGUL JONGSEONG RIEUL
        keysym2UCSHash.put( (long)0xedc, (char)0x11b0); // XK_Hangul_J_RieulKiyeog --> HANGUL JONGSEONG RIEUL-KIYEOK
        keysym2UCSHash.put( (long)0xedd, (char)0x11b1); // XK_Hangul_J_RieulMieum --> HANGUL JONGSEONG RIEUL-MIEUM
        keysym2UCSHash.put( (long)0xede, (char)0x11b2); // XK_Hangul_J_RieulPieub --> HANGUL JONGSEONG RIEUL-PIEUP
        keysym2UCSHash.put( (long)0xedf, (char)0x11b3); // XK_Hangul_J_RieulSios --> HANGUL JONGSEONG RIEUL-SIOS
        keysym2UCSHash.put( (long)0xee0, (char)0x11b4); // XK_Hangul_J_RieulTieut --> HANGUL JONGSEONG RIEUL-THIEUTH
        keysym2UCSHash.put( (long)0xee1, (char)0x11b5); // XK_Hangul_J_RieulPhieuf --> HANGUL JONGSEONG RIEUL-PHIEUPH
        keysym2UCSHash.put( (long)0xee2, (char)0x11b6); // XK_Hangul_J_RieulHieuh --> HANGUL JONGSEONG RIEUL-HIEUH
        keysym2UCSHash.put( (long)0xee3, (char)0x11b7); // XK_Hangul_J_Mieum --> HANGUL JONGSEONG MIEUM
        keysym2UCSHash.put( (long)0xee4, (char)0x11b8); // XK_Hangul_J_Pieub --> HANGUL JONGSEONG PIEUP
        keysym2UCSHash.put( (long)0xee5, (char)0x11b9); // XK_Hangul_J_PieubSios --> HANGUL JONGSEONG PIEUP-SIOS
        keysym2UCSHash.put( (long)0xee6, (char)0x11ba); // XK_Hangul_J_Sios --> HANGUL JONGSEONG SIOS
        keysym2UCSHash.put( (long)0xee7, (char)0x11bb); // XK_Hangul_J_SsangSios --> HANGUL JONGSEONG SSANGSIOS
        keysym2UCSHash.put( (long)0xee8, (char)0x11bc); // XK_Hangul_J_Ieung --> HANGUL JONGSEONG IEUNG
        keysym2UCSHash.put( (long)0xee9, (char)0x11bd); // XK_Hangul_J_Jieuj --> HANGUL JONGSEONG CIEUC
        keysym2UCSHash.put( (long)0xeea, (char)0x11be); // XK_Hangul_J_Cieuc --> HANGUL JONGSEONG CHIEUCH
        keysym2UCSHash.put( (long)0xeeb, (char)0x11bf); // XK_Hangul_J_Khieuq --> HANGUL JONGSEONG KHIEUKH
        keysym2UCSHash.put( (long)0xeec, (char)0x11c0); // XK_Hangul_J_Tieut --> HANGUL JONGSEONG THIEUTH
        keysym2UCSHash.put( (long)0xeed, (char)0x11c1); // XK_Hangul_J_Phieuf --> HANGUL JONGSEONG PHIEUPH
        keysym2UCSHash.put( (long)0xeee, (char)0x11c2); // XK_Hangul_J_Hieuh --> HANGUL JONGSEONG HIEUH
        keysym2UCSHash.put( (long)0xeef, (char)0x316d); // XK_Hangul_RieulYeorinHieuh --> HANGUL LETTER RIEUL-YEORINHIEUH
        keysym2UCSHash.put( (long)0xef0, (char)0x3171); // XK_Hangul_SunkyeongeumMieum --> HANGUL LETTER KAPYEOUNMIEUM
        keysym2UCSHash.put( (long)0xef1, (char)0x3178); // XK_Hangul_SunkyeongeumPieub --> HANGUL LETTER KAPYEOUNPIEUP
        keysym2UCSHash.put( (long)0xef2, (char)0x317f); // XK_Hangul_PanSios --> HANGUL LETTER PANSIOS
        keysym2UCSHash.put( (long)0xef3, (char)0x3181); // XK_Hangul_KkogjiDalrinIeung --> HANGUL LETTER YESIEUNG
        keysym2UCSHash.put( (long)0xef4, (char)0x3184); // XK_Hangul_SunkyeongeumPhieuf --> HANGUL LETTER KAPYEOUNPHIEUPH
        keysym2UCSHash.put( (long)0xef5, (char)0x3186); // XK_Hangul_YeorinHieuh --> HANGUL LETTER YEORINHIEUH
        keysym2UCSHash.put( (long)0xef6, (char)0x318d); // XK_Hangul_AraeA --> HANGUL LETTER ARAEA
        keysym2UCSHash.put( (long)0xef7, (char)0x318e); // XK_Hangul_AraeAE --> HANGUL LETTER ARAEAE
        keysym2UCSHash.put( (long)0xef8, (char)0x11eb); // XK_Hangul_J_PanSios --> HANGUL JONGSEONG PANSIOS
        keysym2UCSHash.put( (long)0xef9, (char)0x11f0); // XK_Hangul_J_KkogjiDalrinIeung --> HANGUL JONGSEONG YESIEUNG
        keysym2UCSHash.put( (long)0xefa, (char)0x11f9); // XK_Hangul_J_YeorinHieuh --> HANGUL JONGSEONG YEORINHIEUH
        keysym2UCSHash.put( (long)0xeff, (char)0x20a9); // XK_Korean_Won --> WON SIGN
        keysym2UCSHash.put( (long)0x16a3, (char)0x1e8a); // XK_Xabovedot --> LATIN CAPITAL LETTER X WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x16a6, (char)0x012c); // XK_Ibreve --> LATIN CAPITAL LETTER I WITH BREVE
        keysym2UCSHash.put( (long)0x16a9, (char)0x01b5); // XK_Zstroke --> LATIN CAPITAL LETTER Z WITH STROKE
        keysym2UCSHash.put( (long)0x16aa, (char)0x01e6); // XK_Gcaron --> LATIN CAPITAL LETTER G WITH CARON
        keysym2UCSHash.put( (long)0x16af, (char)0x019f); // XK_Obarred --> LATIN CAPITAL LETTER O WITH MIDDLE TILDE
        keysym2UCSHash.put( (long)0x16b3, (char)0x1e8b); // XK_xabovedot --> LATIN SMALL LETTER X WITH DOT ABOVE
        keysym2UCSHash.put( (long)0x16b6, (char)0x012d); // XK_ibreve --> LATIN SMALL LETTER I WITH BREVE
        keysym2UCSHash.put( (long)0x16b9, (char)0x01b6); // XK_zstroke --> LATIN SMALL LETTER Z WITH STROKE
        keysym2UCSHash.put( (long)0x16ba, (char)0x01e7); // XK_gcaron --> LATIN SMALL LETTER G WITH CARON
        keysym2UCSHash.put( (long)0x16bd, (char)0x01d2); // XK_ocaron --> LATIN SMALL LETTER O WITH CARON
        keysym2UCSHash.put( (long)0x16bf, (char)0x0275); // XK_obarred --> LATIN SMALL LETTER BARRED O
        keysym2UCSHash.put( (long)0x16c6, (char)0x018f); // XK_SCHWA --> LATIN CAPITAL LETTER SCHWA
        keysym2UCSHash.put( (long)0x16f6, (char)0x0259); // XK_schwa --> LATIN SMALL LETTER SCHWA
        keysym2UCSHash.put( (long)0x1ea0, (char)0x1ea0); // XK_Abelowdot --> LATIN CAPITAL LETTER A WITH DOT BELOW
        keysym2UCSHash.put( (long)0x1ea1, (char)0x1ea1); // XK_abelowdot --> LATIN SMALL LETTER A WITH DOT BELOW
        keysym2UCSHash.put( (long)0x1ea2, (char)0x1ea2); // XK_Ahook --> LATIN CAPITAL LETTER A WITH HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ea3, (char)0x1ea3); // XK_ahook --> LATIN SMALL LETTER A WITH HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ea4, (char)0x1ea4); // XK_Acircumflexacute --> LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND ACUTE
        keysym2UCSHash.put( (long)0x1ea5, (char)0x1ea5); // XK_acircumflexacute --> LATIN SMALL LETTER A WITH CIRCUMFLEX AND ACUTE
        keysym2UCSHash.put( (long)0x1ea6, (char)0x1ea6); // XK_Acircumflexgrave --> LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND GRAVE
        keysym2UCSHash.put( (long)0x1ea7, (char)0x1ea7); // XK_acircumflexgrave --> LATIN SMALL LETTER A WITH CIRCUMFLEX AND GRAVE
        keysym2UCSHash.put( (long)0x1ea8, (char)0x1ea8); // XK_Acircumflexhook --> LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ea9, (char)0x1ea9); // XK_acircumflexhook --> LATIN SMALL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE
        keysym2UCSHash.put( (long)0x1eaa, (char)0x1eaa); // XK_Acircumflextilde --> LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND TILDE
        keysym2UCSHash.put( (long)0x1eab, (char)0x1eab); // XK_acircumflextilde --> LATIN SMALL LETTER A WITH CIRCUMFLEX AND TILDE
        keysym2UCSHash.put( (long)0x1eac, (char)0x1eac); // XK_Acircumflexbelowdot --> LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND DOT BELOW
        keysym2UCSHash.put( (long)0x1ead, (char)0x1ead); // XK_acircumflexbelowdot --> LATIN SMALL LETTER A WITH CIRCUMFLEX AND DOT BELOW
        keysym2UCSHash.put( (long)0x1eae, (char)0x1eae); // XK_Abreveacute --> LATIN CAPITAL LETTER A WITH BREVE AND ACUTE
        keysym2UCSHash.put( (long)0x1eaf, (char)0x1eaf); // XK_abreveacute --> LATIN SMALL LETTER A WITH BREVE AND ACUTE
        keysym2UCSHash.put( (long)0x1eb0, (char)0x1eb0); // XK_Abrevegrave --> LATIN CAPITAL LETTER A WITH BREVE AND GRAVE
        keysym2UCSHash.put( (long)0x1eb1, (char)0x1eb1); // XK_abrevegrave --> LATIN SMALL LETTER A WITH BREVE AND GRAVE
        keysym2UCSHash.put( (long)0x1eb2, (char)0x1eb2); // XK_Abrevehook --> LATIN CAPITAL LETTER A WITH BREVE AND HOOK ABOVE
        keysym2UCSHash.put( (long)0x1eb3, (char)0x1eb3); // XK_abrevehook --> LATIN SMALL LETTER A WITH BREVE AND HOOK ABOVE
        keysym2UCSHash.put( (long)0x1eb4, (char)0x1eb4); // XK_Abrevetilde --> LATIN CAPITAL LETTER A WITH BREVE AND TILDE
        keysym2UCSHash.put( (long)0x1eb5, (char)0x1eb5); // XK_abrevetilde --> LATIN SMALL LETTER A WITH BREVE AND TILDE
        keysym2UCSHash.put( (long)0x1eb6, (char)0x1eb6); // XK_Abrevebelowdot --> LATIN CAPITAL LETTER A WITH BREVE AND DOT BELOW
        keysym2UCSHash.put( (long)0x1eb7, (char)0x1eb7); // XK_abrevebelowdot --> LATIN SMALL LETTER A WITH BREVE AND DOT BELOW
        keysym2UCSHash.put( (long)0x1eb8, (char)0x1eb8); // XK_Ebelowdot --> LATIN CAPITAL LETTER E WITH DOT BELOW
        keysym2UCSHash.put( (long)0x1eb9, (char)0x1eb9); // XK_ebelowdot --> LATIN SMALL LETTER E WITH DOT BELOW
        keysym2UCSHash.put( (long)0x1eba, (char)0x1eba); // XK_Ehook --> LATIN CAPITAL LETTER E WITH HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ebb, (char)0x1ebb); // XK_ehook --> LATIN SMALL LETTER E WITH HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ebc, (char)0x1ebc); // XK_Etilde --> LATIN CAPITAL LETTER E WITH TILDE
        keysym2UCSHash.put( (long)0x1ebd, (char)0x1ebd); // XK_etilde --> LATIN SMALL LETTER E WITH TILDE
        keysym2UCSHash.put( (long)0x1ebe, (char)0x1ebe); // XK_Ecircumflexacute --> LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND ACUTE
        keysym2UCSHash.put( (long)0x1ebf, (char)0x1ebf); // XK_ecircumflexacute --> LATIN SMALL LETTER E WITH CIRCUMFLEX AND ACUTE
        keysym2UCSHash.put( (long)0x1ec0, (char)0x1ec0); // XK_Ecircumflexgrave --> LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND GRAVE
        keysym2UCSHash.put( (long)0x1ec1, (char)0x1ec1); // XK_ecircumflexgrave --> LATIN SMALL LETTER E WITH CIRCUMFLEX AND GRAVE
        keysym2UCSHash.put( (long)0x1ec2, (char)0x1ec2); // XK_Ecircumflexhook --> LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ec3, (char)0x1ec3); // XK_ecircumflexhook --> LATIN SMALL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ec4, (char)0x1ec4); // XK_Ecircumflextilde --> LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND TILDE
        keysym2UCSHash.put( (long)0x1ec5, (char)0x1ec5); // XK_ecircumflextilde --> LATIN SMALL LETTER E WITH CIRCUMFLEX AND TILDE
        keysym2UCSHash.put( (long)0x1ec6, (char)0x1ec6); // XK_Ecircumflexbelowdot --> LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND DOT BELOW
        keysym2UCSHash.put( (long)0x1ec7, (char)0x1ec7); // XK_ecircumflexbelowdot --> LATIN SMALL LETTER E WITH CIRCUMFLEX AND DOT BELOW
        keysym2UCSHash.put( (long)0x1ec8, (char)0x1ec8); // XK_Ihook --> LATIN CAPITAL LETTER I WITH HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ec9, (char)0x1ec9); // XK_ihook --> LATIN SMALL LETTER I WITH HOOK ABOVE
        keysym2UCSHash.put( (long)0x1eca, (char)0x1eca); // XK_Ibelowdot --> LATIN CAPITAL LETTER I WITH DOT BELOW
        keysym2UCSHash.put( (long)0x1ecb, (char)0x1ecb); // XK_ibelowdot --> LATIN SMALL LETTER I WITH DOT BELOW
        keysym2UCSHash.put( (long)0x1ecc, (char)0x1ecc); // XK_Obelowdot --> LATIN CAPITAL LETTER O WITH DOT BELOW
        keysym2UCSHash.put( (long)0x1ecd, (char)0x1ecd); // XK_obelowdot --> LATIN SMALL LETTER O WITH DOT BELOW
        keysym2UCSHash.put( (long)0x1ece, (char)0x1ece); // XK_Ohook --> LATIN CAPITAL LETTER O WITH HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ecf, (char)0x1ecf); // XK_ohook --> LATIN SMALL LETTER O WITH HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ed0, (char)0x1ed0); // XK_Ocircumflexacute --> LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND ACUTE
        keysym2UCSHash.put( (long)0x1ed1, (char)0x1ed1); // XK_ocircumflexacute --> LATIN SMALL LETTER O WITH CIRCUMFLEX AND ACUTE
        keysym2UCSHash.put( (long)0x1ed2, (char)0x1ed2); // XK_Ocircumflexgrave --> LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND GRAVE
        keysym2UCSHash.put( (long)0x1ed3, (char)0x1ed3); // XK_ocircumflexgrave --> LATIN SMALL LETTER O WITH CIRCUMFLEX AND GRAVE
        keysym2UCSHash.put( (long)0x1ed4, (char)0x1ed4); // XK_Ocircumflexhook --> LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ed5, (char)0x1ed5); // XK_ocircumflexhook --> LATIN SMALL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ed6, (char)0x1ed6); // XK_Ocircumflextilde --> LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND TILDE
        keysym2UCSHash.put( (long)0x1ed7, (char)0x1ed7); // XK_ocircumflextilde --> LATIN SMALL LETTER O WITH CIRCUMFLEX AND TILDE
        keysym2UCSHash.put( (long)0x1ed8, (char)0x1ed8); // XK_Ocircumflexbelowdot --> LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND DOT BELOW
        keysym2UCSHash.put( (long)0x1ed9, (char)0x1ed9); // XK_ocircumflexbelowdot --> LATIN SMALL LETTER O WITH CIRCUMFLEX AND DOT BELOW
        keysym2UCSHash.put( (long)0x1eda, (char)0x1eda); // XK_Ohornacute --> LATIN CAPITAL LETTER O WITH HORN AND ACUTE
        keysym2UCSHash.put( (long)0x1edb, (char)0x1edb); // XK_ohornacute --> LATIN SMALL LETTER O WITH HORN AND ACUTE
        keysym2UCSHash.put( (long)0x1edc, (char)0x1edc); // XK_Ohorngrave --> LATIN CAPITAL LETTER O WITH HORN AND GRAVE
        keysym2UCSHash.put( (long)0x1edd, (char)0x1edd); // XK_ohorngrave --> LATIN SMALL LETTER O WITH HORN AND GRAVE
        keysym2UCSHash.put( (long)0x1ede, (char)0x1ede); // XK_Ohornhook --> LATIN CAPITAL LETTER O WITH HORN AND HOOK ABOVE
        keysym2UCSHash.put( (long)0x1edf, (char)0x1edf); // XK_ohornhook --> LATIN SMALL LETTER O WITH HORN AND HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ee0, (char)0x1ee0); // XK_Ohorntilde --> LATIN CAPITAL LETTER O WITH HORN AND TILDE
        keysym2UCSHash.put( (long)0x1ee1, (char)0x1ee1); // XK_ohorntilde --> LATIN SMALL LETTER O WITH HORN AND TILDE
        keysym2UCSHash.put( (long)0x1ee2, (char)0x1ee2); // XK_Ohornbelowdot --> LATIN CAPITAL LETTER O WITH HORN AND DOT BELOW
        keysym2UCSHash.put( (long)0x1ee3, (char)0x1ee3); // XK_ohornbelowdot --> LATIN SMALL LETTER O WITH HORN AND DOT BELOW
        keysym2UCSHash.put( (long)0x1ee4, (char)0x1ee4); // XK_Ubelowdot --> LATIN CAPITAL LETTER U WITH DOT BELOW
        keysym2UCSHash.put( (long)0x1ee5, (char)0x1ee5); // XK_ubelowdot --> LATIN SMALL LETTER U WITH DOT BELOW
        keysym2UCSHash.put( (long)0x1ee6, (char)0x1ee6); // XK_Uhook --> LATIN CAPITAL LETTER U WITH HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ee7, (char)0x1ee7); // XK_uhook --> LATIN SMALL LETTER U WITH HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ee8, (char)0x1ee8); // XK_Uhornacute --> LATIN CAPITAL LETTER U WITH HORN AND ACUTE
        keysym2UCSHash.put( (long)0x1ee9, (char)0x1ee9); // XK_uhornacute --> LATIN SMALL LETTER U WITH HORN AND ACUTE
        keysym2UCSHash.put( (long)0x1eea, (char)0x1eea); // XK_Uhorngrave --> LATIN CAPITAL LETTER U WITH HORN AND GRAVE
        keysym2UCSHash.put( (long)0x1eeb, (char)0x1eeb); // XK_uhorngrave --> LATIN SMALL LETTER U WITH HORN AND GRAVE
        keysym2UCSHash.put( (long)0x1eec, (char)0x1eec); // XK_Uhornhook --> LATIN CAPITAL LETTER U WITH HORN AND HOOK ABOVE
        keysym2UCSHash.put( (long)0x1eed, (char)0x1eed); // XK_uhornhook --> LATIN SMALL LETTER U WITH HORN AND HOOK ABOVE
        keysym2UCSHash.put( (long)0x1eee, (char)0x1eee); // XK_Uhorntilde --> LATIN CAPITAL LETTER U WITH HORN AND TILDE
        keysym2UCSHash.put( (long)0x1eef, (char)0x1eef); // XK_uhorntilde --> LATIN SMALL LETTER U WITH HORN AND TILDE
        keysym2UCSHash.put( (long)0x1ef0, (char)0x1ef0); // XK_Uhornbelowdot --> LATIN CAPITAL LETTER U WITH HORN AND DOT BELOW
        keysym2UCSHash.put( (long)0x1ef1, (char)0x1ef1); // XK_uhornbelowdot --> LATIN SMALL LETTER U WITH HORN AND DOT BELOW
        keysym2UCSHash.put( (long)0x1ef4, (char)0x1ef4); // XK_Ybelowdot --> LATIN CAPITAL LETTER Y WITH DOT BELOW
        keysym2UCSHash.put( (long)0x1ef5, (char)0x1ef5); // XK_ybelowdot --> LATIN SMALL LETTER Y WITH DOT BELOW
        keysym2UCSHash.put( (long)0x1ef6, (char)0x1ef6); // XK_Yhook --> LATIN CAPITAL LETTER Y WITH HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ef7, (char)0x1ef7); // XK_yhook --> LATIN SMALL LETTER Y WITH HOOK ABOVE
        keysym2UCSHash.put( (long)0x1ef8, (char)0x1ef8); // XK_Ytilde --> LATIN CAPITAL LETTER Y WITH TILDE
        keysym2UCSHash.put( (long)0x1ef9, (char)0x1ef9); // XK_ytilde --> LATIN SMALL LETTER Y WITH TILDE
        keysym2UCSHash.put( (long)0x1efa, (char)0x01a0); // XK_Ohorn --> LATIN CAPITAL LETTER O WITH HORN
        keysym2UCSHash.put( (long)0x1efb, (char)0x01a1); // XK_ohorn --> LATIN SMALL LETTER O WITH HORN
        keysym2UCSHash.put( (long)0x1efc, (char)0x01af); // XK_Uhorn --> LATIN CAPITAL LETTER U WITH HORN
        keysym2UCSHash.put( (long)0x1efd, (char)0x01b0); // XK_uhorn --> LATIN SMALL LETTER U WITH HORN
        keysym2UCSHash.put( (long)0x20a0, (char)0x20a0); // XK_EcuSign --> EURO-CURRENCY SIGN
        keysym2UCSHash.put( (long)0x20a1, (char)0x20a1); // XK_ColonSign --> COLON SIGN
        keysym2UCSHash.put( (long)0x20a2, (char)0x20a2); // XK_CruzeiroSign --> CRUZEIRO SIGN
        keysym2UCSHash.put( (long)0x20a3, (char)0x20a3); // XK_FFrancSign --> FRENCH FRANC SIGN
        keysym2UCSHash.put( (long)0x20a4, (char)0x20a4); // XK_LiraSign --> LIRA SIGN
        keysym2UCSHash.put( (long)0x20a5, (char)0x20a5); // XK_MillSign --> MILL SIGN
        keysym2UCSHash.put( (long)0x20a6, (char)0x20a6); // XK_NairaSign --> NAIRA SIGN
        keysym2UCSHash.put( (long)0x20a7, (char)0x20a7); // XK_PesetaSign --> PESETA SIGN
        keysym2UCSHash.put( (long)0x20a8, (char)0x20a8); // XK_RupeeSign --> RUPEE SIGN
        keysym2UCSHash.put( (long)0x20a9, (char)0x20a9); // XK_WonSign --> WON SIGN
        keysym2UCSHash.put( (long)0x20aa, (char)0x20aa); // XK_NewSheqelSign --> NEW SHEQEL SIGN
        keysym2UCSHash.put( (long)0x20ab, (char)0x20ab); // XK_DongSign --> DONG SIGN
        keysym2UCSHash.put( (long)0x20ac, (char)0x20ac); // XK_EuroSign --> EURO SIGN
        keysym2UCSHash.put( (long)0x1004FF08, (char)0x0008); // osfXK_BackSpace --> <control>
        keysym2UCSHash.put( (long)0x1004FF1B, (char)0x001b); // osfXK_Escape --> <control>
        keysym2UCSHash.put( (long)0x1004FFFF, (char)0x007f); // osfXK_Delete --> <control>

        //XXX fill keysym2JavaKeycodeHash.

        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_a),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_A, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_b),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_B, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_c),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_C, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_d),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_D, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_e),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_E, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_f),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_g),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_G, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_h),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_H, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_i),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_I, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_j),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_J, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_k),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_K, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_l),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_L, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_m),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_M, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_n),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_N, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_o),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_O, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_p),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_P, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_q),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_Q, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_r),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_R, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_s),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_S, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_t),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_T, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_u),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_U, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_v),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_V, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_w),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_W, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_x),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_X, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_y),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_Y, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_z),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_Z, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* TTY Function keys */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_BackSpace),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BACK_SPACE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Tab),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_TAB, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_ISO_Left_Tab),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_TAB, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Clear),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CLEAR, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Return),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Linefeed),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Pause),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAUSE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F21),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAUSE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_R1),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAUSE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Scroll_Lock),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SCROLL_LOCK, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F23),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SCROLL_LOCK, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_R3),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SCROLL_LOCK, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Escape),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ESCAPE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Other vendor-specific versions of TTY Function keys */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_BackSpace),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BACK_SPACE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Clear),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CLEAR, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Escape),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ESCAPE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Modifier keys */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Shift_L),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SHIFT, java.awt.event.KeyEvent.KEY_LOCATION_LEFT));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Shift_R),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SHIFT, java.awt.event.KeyEvent.KEY_LOCATION_RIGHT));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Control_L),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CONTROL, java.awt.event.KeyEvent.KEY_LOCATION_LEFT));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Control_R),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CONTROL, java.awt.event.KeyEvent.KEY_LOCATION_RIGHT));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Alt_L),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ALT, java.awt.event.KeyEvent.KEY_LOCATION_LEFT));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Alt_R),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ALT, java.awt.event.KeyEvent.KEY_LOCATION_RIGHT));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Meta_L),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_META, java.awt.event.KeyEvent.KEY_LOCATION_LEFT));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Meta_R),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_META, java.awt.event.KeyEvent.KEY_LOCATION_RIGHT));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Caps_Lock),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CAPS_LOCK, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Misc Functions */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Print),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PRINTSCREEN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F22),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PRINTSCREEN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_R2),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PRINTSCREEN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Cancel),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CANCEL, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Help),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_HELP, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Num_Lock),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUM_LOCK, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));

            /* Other vendor-specific versions of Misc Functions */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Cancel),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CANCEL, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Help),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_HELP, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Rectangular Navigation Block */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Home),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_HOME, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_R7),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_HOME, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Page_Up),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Prior),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_R9),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Page_Down),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Next),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_R15),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_End),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_END, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_R13),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_END, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Insert),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_INSERT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Delete),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Keypad equivalents of Rectangular Navigation Block */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Home),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_HOME, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Page_Up),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Prior),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Page_Down),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Next),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_End),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_END, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Insert),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_INSERT, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Delete),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));

            /* Other vendor-specific Rectangular Navigation Block */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_PageUp),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Prior),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_PageDown),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Next),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_EndLine),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_END, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Insert),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_INSERT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Delete),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Triangular Navigation Block */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Left),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_LEFT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Up),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UP, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Right),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Down),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DOWN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Keypad equivalents of Triangular Navigation Block */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Left),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_KP_LEFT, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Up),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_KP_UP, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Right),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_KP_RIGHT, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Down),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_KP_DOWN, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));

            /* Other vendor-specific Triangular Navigation Block */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Left),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_LEFT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Up),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UP, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Right),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Down),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DOWN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Remaining Cursor control & motion */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Begin),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BEGIN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Begin),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BEGIN, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));

        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_0),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_0, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_1),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_1, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_2),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_2, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_3),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_3, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_4),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_4, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_5),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_5, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_6),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_6, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_7),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_7, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_8),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_8, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_9),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_9, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_space),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SPACE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_exclam),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_EXCLAMATION_MARK, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_quotedbl),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_QUOTEDBL, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_numbersign),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMBER_SIGN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dollar),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DOLLAR, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_ampersand),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_AMPERSAND, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_apostrophe),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_QUOTE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_parenleft),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_LEFT_PARENTHESIS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_parenright),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_RIGHT_PARENTHESIS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_asterisk),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ASTERISK, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_plus),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PLUS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_comma),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COMMA, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_minus),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_MINUS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_period),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PERIOD, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_slash),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SLASH, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_colon),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COLON, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_semicolon),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SEMICOLON, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_less),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_LESS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_equal),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_EQUALS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_greater),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_GREATER, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_at),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_AT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_bracketleft),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_OPEN_BRACKET, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_backslash),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BACK_SLASH, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_bracketright),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CLOSE_BRACKET, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_asciicircum),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CIRCUMFLEX, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_underscore),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UNDERSCORE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Super_L),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_WINDOWS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Super_R),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_WINDOWS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Menu),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CONTEXT_MENU, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_grave),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BACK_QUOTE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_braceleft),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BRACELEFT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_braceright),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BRACERIGHT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_exclamdown),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_INVERTED_EXCLAMATION_MARK, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Remaining Numeric Keypad Keys */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_0),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD0, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_1),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD1, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_2),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD2, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_3),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD3, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_4),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD4, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_5),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD5, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_6),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD6, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_7),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD7, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_8),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD8, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_9),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD9, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Space),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SPACE, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Tab),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_TAB, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Enter),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Equal),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_EQUALS, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_R4),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_EQUALS, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Multiply),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_MULTIPLY, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F26),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_MULTIPLY, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_R6),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_MULTIPLY, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Add),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ADD, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Separator),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SEPARATOR, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Subtract),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SUBTRACT, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F24),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SUBTRACT, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Decimal),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DECIMAL, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_KP_Divide),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DIVIDE, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F25),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DIVIDE, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_R5),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DIVIDE, java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));

            /* Function Keys */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F1),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F1, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F2),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F2, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F3),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F3, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F4),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F4, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F5),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F5, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F6),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F6, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F7),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F7, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F8),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F8, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F9),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F9, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F10),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F10, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F11),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F11, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_F12),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F12, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Sun vendor-specific version of F11 and F12 */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_F36),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F11, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_F37),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F12, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* X11 keysym names for input method related keys don't always
             * match keytop engravings or Java virtual key names, so here we
             * only map constants that we've found on real keyboards.
             */
            /* Type 5c Japanese keyboard: kakutei */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Execute),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ACCEPT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
            /* Type 5c Japanese keyboard: henkan */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Kanji),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CONVERT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
            /* Type 5c Japanese keyboard: nihongo */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Henkan_Mode),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_INPUT_METHOD_ON_OFF, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
            /* VK_KANA_LOCK is handled separately because it generates the
             * same keysym as ALT_GRAPH in spite of its different behavior.
             */

        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Multi_key),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COMPOSE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Mode_switch),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ALT_GRAPH, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_ISO_Level3_Shift),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ALT_GRAPH, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Editing block */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Redo),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_AGAIN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        // XXX XK_L2 == F12; TODO: add code to use only one of them depending on the keyboard type. For now, restore
        // good PC behavior and bad but old Sparc behavior.
        // keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_L2),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_AGAIN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Undo),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UNDO, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_L4),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UNDO, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_L6),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COPY, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_L8),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PASTE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_L10),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CUT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_Find),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_FIND, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_L9),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_FIND, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_L3),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PROPS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        // XXX XK_L1 == F11; TODO: add code to use only one of them depending on the keyboard type. For now, restore
        // good PC behavior and bad but old Sparc behavior.
        // keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_L1),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_STOP, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Sun vendor-specific versions for editing block */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_Again),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_AGAIN, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_Undo),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UNDO, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_Copy),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COPY, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_Paste),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PASTE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_Cut),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CUT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_Find),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_FIND, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_Props),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PROPS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_Stop),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_STOP, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Apollo (HP) vendor-specific versions for editing block */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.apXK_Copy),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COPY, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.apXK_Cut),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CUT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.apXK_Paste),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PASTE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Other vendor-specific versions for editing block */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Copy),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COPY, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Cut),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CUT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Paste),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PASTE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.osfXK_Undo),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UNDO, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Dead key mappings (for European keyboards) */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_grave),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_GRAVE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_acute),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ACUTE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_circumflex),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CIRCUMFLEX, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_tilde),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_TILDE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_macron),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_MACRON, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_breve),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_BREVE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_abovedot),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ABOVEDOT, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_diaeresis),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_DIAERESIS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_abovering),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ABOVERING, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_doubleacute),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_DOUBLEACUTE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_caron),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CARON, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_cedilla),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CEDILLA, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_ogonek),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_OGONEK, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_iota),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_IOTA, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_voiced_sound),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_VOICED_SOUND, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.XK_dead_semivoiced_sound),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_SEMIVOICED_SOUND, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Sun vendor-specific dead key mappings (for European keyboards) */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_FA_Grave),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_GRAVE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_FA_Circum),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CIRCUMFLEX, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_FA_Tilde),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_TILDE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_FA_Acute),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ACUTE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_FA_Diaeresis),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_DIAERESIS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.SunXK_FA_Cedilla),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CEDILLA, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* DEC vendor-specific dead key mappings (for European keyboards) */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.DXK_ring_accent),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ABOVERING, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.DXK_circumflex_accent),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CIRCUMFLEX, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.DXK_cedilla_accent),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CEDILLA, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.DXK_acute_accent),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ACUTE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.DXK_grave_accent),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_GRAVE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.DXK_tilde),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_TILDE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.DXK_diaeresis),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_DIAERESIS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

            /* Other vendor-specific dead key mappings (for European keyboards) */
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.hpXK_mute_acute),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ACUTE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.hpXK_mute_grave),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_GRAVE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.hpXK_mute_asciicircum),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CIRCUMFLEX, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.hpXK_mute_diaeresis),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_DIAERESIS, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
        keysym2JavaKeycodeHash.put( Long.valueOf(XKeySymConstants.hpXK_mute_asciitilde),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_TILDE, java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));

        keysym2JavaKeycodeHash.put( Long.valueOf(XConstants.NoSymbol),     new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UNDEFINED, java.awt.event.KeyEvent.KEY_LOCATION_UNKNOWN));

        /* Reverse search of keysym by keycode. */

        /* Add keyboard locking codes. */
        javaKeycode2KeysymHash.put( java.awt.event.KeyEvent.VK_CAPS_LOCK, XKeySymConstants.XK_Caps_Lock);
        javaKeycode2KeysymHash.put( java.awt.event.KeyEvent.VK_NUM_LOCK, XKeySymConstants.XK_Num_Lock);
        javaKeycode2KeysymHash.put( java.awt.event.KeyEvent.VK_SCROLL_LOCK, XKeySymConstants.XK_Scroll_Lock);
        javaKeycode2KeysymHash.put( java.awt.event.KeyEvent.VK_KANA_LOCK, XKeySymConstants.XK_Kana_Lock);
    };

}
