/*
 * Copyright (c) 2009, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.security.jgss;

import java.security.BasicPermission;

/**
 * This class is used to protect various attributes of an established
 * GSS security context that can be accessed using the
 * {@link com.sun.security.jgss.ExtendedGSSContext#inquireSecContext}
 * method.
 *
 * <p>The target name is the {@link InquireType} allowed.
 */
public final class InquireSecContextPermission extends BasicPermission {

    /**
     * Constructs a new {@code InquireSecContextPermission} object with
     * the specified name. The name is the symbolic name of the
     * {@link InquireType} allowed.
     *
     * @param name the {@link InquireType} allowed by this
     * permission. "*" means all {@link InquireType}s are allowed.
     *
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is empty.
     */
    public InquireSecContextPermission(String name) {
        super(name);
    }
}
