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

package sun.security.x509;

import java.security.cert.*;
import sun.security.util.*;

@SuppressWarnings({"unchecked", "deprecation", "all"})
public class CertificateValidity implements sun.security.x509.CertAttrSet<java.lang.String> {

    public CertificateValidity() {
        throw new RuntimeException("Stub!");
    }

    @dalvik.annotation.compat.UnsupportedAppUsage
    public CertificateValidity(java.util.Date notBefore, java.util.Date notAfter) {
        throw new RuntimeException("Stub!");
    }

    public CertificateValidity(sun.security.util.DerInputStream in) throws java.io.IOException {
        throw new RuntimeException("Stub!");
    }

    private java.util.Date getNotBefore() {
        throw new RuntimeException("Stub!");
    }

    private java.util.Date getNotAfter() {
        throw new RuntimeException("Stub!");
    }

    private void construct(sun.security.util.DerValue derVal) throws java.io.IOException {
        throw new RuntimeException("Stub!");
    }

    public java.lang.String toString() {
        throw new RuntimeException("Stub!");
    }

    public void encode(java.io.OutputStream out) throws java.io.IOException {
        throw new RuntimeException("Stub!");
    }

    public void set(java.lang.String name, java.lang.Object obj) throws java.io.IOException {
        throw new RuntimeException("Stub!");
    }

    public java.util.Date get(java.lang.String name) throws java.io.IOException {
        throw new RuntimeException("Stub!");
    }

    public void delete(java.lang.String name) throws java.io.IOException {
        throw new RuntimeException("Stub!");
    }

    public java.util.Enumeration<java.lang.String> getElements() {
        throw new RuntimeException("Stub!");
    }

    public java.lang.String getName() {
        throw new RuntimeException("Stub!");
    }

    public void valid()
            throws java.security.cert.CertificateExpiredException,
                    java.security.cert.CertificateNotYetValidException {
        throw new RuntimeException("Stub!");
    }

    public void valid(java.util.Date now)
            throws java.security.cert.CertificateExpiredException,
                    java.security.cert.CertificateNotYetValidException {
        throw new RuntimeException("Stub!");
    }

    public static final java.lang.String IDENT = "x509.info.validity";

    public static final java.lang.String NAME = "validity";

    public static final java.lang.String NOT_AFTER = "notAfter";

    public static final java.lang.String NOT_BEFORE = "notBefore";

    private static final long YR_2050 = 2524636800000L; // 0x24bd0146400L

    private java.util.Date notAfter;

    private java.util.Date notBefore;
}
