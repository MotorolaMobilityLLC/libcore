/*
 * Copyright (c) 1998, 2003, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.jdi;

/**
 * An entity declared within a user defined
 * type (class or interface).
 * This interface is the root of the type
 * component hierarchy which
 * includes {@link Field} and {@link Method}.
 * Type components of the same name declared in different classes
 * (including those related by inheritance) have different
 * TypeComponent objects.
 * TypeComponents can be used alone to retrieve static information
 * about their declaration, or can be used in conjunction with a
 * {@link ReferenceType} or {@link ObjectReference} to access values
 * or invoke, as applicable.
 *
 * @author Robert Field
 * @author Gordon Hirsch
 * @author James McIlree
 * @since  1.3
 */
public interface TypeComponent extends Mirror, Accessible {

    /**
     * Gets the name of this type component.
     * <P>
     * Note: for fields, this is the field name; for methods,
     * this is the method name; for constructors, this is &lt;init&gt;;
     * for static initializers, this is &lt;clinit&gt;.
     *
     * @return a string containing the name.
     */
    String name();

    /**
     * Gets the JNI-style signature for this type component. The
     * signature is encoded type information as defined
     * in the JNI documentation. It is a convenient, compact format for
     * for manipulating type information internally, not necessarily
     * for display to an end user. See {@link Field#typeName} and
     * {@link Method#returnTypeName} for ways to help get a more readable
     * representation of the type.
     *
     * @see <a href="doc-files/signature.html">Type Signatures</a>
     * @return a string containing the signature
     */
    String signature();

    /**
     * Gets the generic signature for this TypeComponent if there is one.
     * Generic signatures are described in the
     * <cite>The Java&trade; Virtual Machine Specification</cite>.
     *
     * @return a string containing the generic signature, or <code>null</code>
     * if there is no generic signature.
     *
     * @since 1.5
     */
    String genericSignature();

    /**
     * Returns the type in which this component was declared. The
     * returned {@link ReferenceType} mirrors either a class or an
     * interface in the target VM.
     *
     * @return a {@link ReferenceType} for the type that declared
     * this type component.
     */
    ReferenceType declaringType();

    /**
     * Determines if this TypeComponent is static.
     * Return value is undefined for constructors and static initializers.
     *
     * @return <code>true</code> if this type component was declared
     * static; false otherwise.
     */
    boolean isStatic();

    /**
     * Determines if this TypeComponent is final.
     * Return value is undefined for constructors and static initializers.
     *
     * @return <code>true</code> if this type component was declared
     * final; false otherwise.
     */
    boolean isFinal();

    /**
     * Determines if this TypeComponent is synthetic. Synthetic members
     * are generated by the compiler and are not present in the source
     * code for the containing class.
     * <p>
     * Not all target VMs support this query. See
     * {@link VirtualMachine#canGetSyntheticAttribute} to determine if the
     * operation is supported.
     *
     * @return <code>true</code> if this type component is synthetic;
     * <code>false</code> otherwise.
     * @throws java.lang.UnsupportedOperationException if the target
     * VM cannot provide information on synthetic attributes.
     */
    boolean isSynthetic();
}
