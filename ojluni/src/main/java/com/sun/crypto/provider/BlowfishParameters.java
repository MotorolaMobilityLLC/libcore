/*
 * Copyright (c) 1998, 2007, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.crypto.provider;

import java.util.*;
import java.io.*;
import sun.security.util.*;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;

/**
 * This class implements the parameter (IV) used with the Blowfish algorithm in
 * feedback-mode. IV is defined in the standards as follows:
 *
 * <pre>
 * IV ::= OCTET STRING  -- 8 octets
 * </pre>
 *
 * @author Jan Luehe
 *
 */

public final class BlowfishParameters extends AlgorithmParametersSpi {

    private BlockCipherParamsCore core;

    public BlowfishParameters() {
        core = new BlockCipherParamsCore
            (BlowfishConstants.BLOWFISH_BLOCK_SIZE);
    }
    protected void engineInit(AlgorithmParameterSpec paramSpec)
        throws InvalidParameterSpecException {
        core.init(paramSpec);
    }

    protected void engineInit(byte[] encoded)
        throws IOException {
        core.init(encoded);
    }

    protected void engineInit(byte[] encoded, String decodingMethod)
        throws IOException {
        core.init(encoded, decodingMethod);
    }

    protected AlgorithmParameterSpec engineGetParameterSpec(Class paramSpec)
        throws InvalidParameterSpecException {
        return core.getParameterSpec(paramSpec);
    }

    protected byte[] engineGetEncoded() throws IOException {
        return core.getEncoded();
    }

    protected byte[] engineGetEncoded(String encodingMethod)
        throws IOException {
        return core.getEncoded();
    }

    protected String engineToString() {
        return core.toString();
    }
}
