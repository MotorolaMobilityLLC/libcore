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

/**
* @author Alexander V. Astapchuk
* @version $Revision$
*/

package java.security;

import java.nio.ByteBuffer;
import java.util.HashMap;


/**
 * {@code SecureClassLoader} represents a {@code ClassLoader} which associates
 * the classes it loads with a code source and provide mechanisms to allow the
 * relevant permissions to be retrieved.
 * 
 * @since Android 1.0
 */
public class SecureClassLoader extends ClassLoader {

    // A cache of ProtectionDomains for a given CodeSource
    private HashMap pds = new HashMap();

    /**
     * Constructs a new instance of {@code SecureClassLoader}. The default
     * parent {@code ClassLoader} is used.
     * <p>
     * If a {@code SecurityManager} is installed, code calling this constructor
     * needs the {@code SecurityPermission} {@code checkCreateClassLoader} to be
     * granted, otherwise a {@code SecurityException} will be thrown.
     * </p>
     * 
     * @throws SecurityException
     *             if a {@code SecurityManager} is installed and the caller does
     *             not have permission to invoke this constructor.
     * @since Android 1.0
     */
    protected SecureClassLoader() {
        super();
    }

    /**
     * Constructs a new instance of {@code SecureClassLoader} with the specified
     * parent {@code ClassLoader}.
     * <p>
     * If a {@code SecurityManager} is installed, code calling this constructor
     * needs the {@code SecurityPermission} {@code checkCreateClassLoader} to be
     * granted, otherwise a {@code SecurityException} will be thrown.
     * </p>
     * 
     * @param parent
     *            the parent {@code ClassLoader}.
     * @throws SecurityException
     *             if a {@code SecurityManager} is installed and the caller does
     *             not have permission to invoke this constructor.
     * @since Android 1.0
     */
    protected SecureClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * Returns the {@code PermissionCollection} for the specified {@code
     * CodeSource}.
     * 
     * @param codesource
     *            the code source.
     * @return the {@code PermissionCollection} for the specified {@code
     *         CodeSource}.
     * @since Android 1.0
     */
    protected PermissionCollection getPermissions(CodeSource codesource) {
        // Do nothing by default, ProtectionDomain will take care about
        // permissions in dynamic
        return new Permissions();
    }

    /**
     * Constructs a new class from an array of bytes containing a class
     * definition in class file format with an optional {@code CodeSource}.
     * 
     * @param name
     *            the name of the new class.
     * @param b
     *            a memory image of a class file.
     * @param off
     *            the start offset in b of the class data.
     * @param len
     *            the length of the class data.
     * @param cs
     *            the {@code CodeSource}, or {@code null}.
     * @return a new class.
     * @throws IndexOutOfBoundsException
     *             if {@code off} or {@code len} are not valid in respect to
     *             {@code b}.
     * @throws ClassFormatError
     *             if the specified data is not valid class data.
     * @throws SecurityException
     *             if the package to which this class is to be added, already
     *             contains classes which were signed by different certificates,
     *             or if the class name begins with "java."
     * @since Android 1.0
     */
    protected final Class<?> defineClass(String name, byte[] b, int off, int len,
            CodeSource cs) {
        return cs == null ? defineClass(name, b, off, len) : defineClass(name,
                b, off, len, getPD(cs));
    }

    /**
     * Constructs a new class from an array of bytes containing a class
     * definition in class file format with an optional {@code CodeSource}.
     * 
     * @param name
     *            the name of the new class.
     * @param b
     *            a memory image of a class file.
     * @param cs
     *            the {@code CodeSource}, or {@code null}.
     * @return a new class.
     * @throws ClassFormatError
     *             if the specified data is not valid class data.
     * @throws SecurityException
     *             if the package to which this class is to be added, already
     *             contains classes which were signed by different certificates,
     *             or if the class name begins with "java."
     * @since Android 1.0
     */
    protected final Class<?> defineClass(String name, ByteBuffer b, CodeSource cs) {
        //FIXME 1.5 - remove b.array(), call super.defineClass(,ByteBuffer,)
        // directly
        byte[] data = b.array();
        return cs == null ? defineClass(name, data, 0, data.length)
                : defineClass(name, data, 0, data.length, getPD(cs));
    }

    // Constructs and caches ProtectionDomain for the given CodeSource 
    // object.<br>
    // It calls {@link getPermissions()} to get a set of permissions.
    //
    // @param cs CodeSource object
    // @return ProtectionDomain for the passed CodeSource object
    private ProtectionDomain getPD(CodeSource cs) {
        if (cs == null) {
            return null;
        }
        // need to cache PDs, otherwise every class from a given CodeSource 
        // will have it's own ProtectionDomain, which does not look right.
        ProtectionDomain pd;
        synchronized (pds) {
            if ((pd = (ProtectionDomain) pds.get(cs)) != null) {
                return pd;
            }
            PermissionCollection perms = getPermissions(cs);
            pd = new ProtectionDomain(cs, perms, this, null);
            pds.put(cs, pd);
        }
        return pd;
    }
}
