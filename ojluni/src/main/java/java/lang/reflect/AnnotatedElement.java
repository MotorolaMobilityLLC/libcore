/*
 * Copyright (C) 2016 The Android Open Source Project
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.Objects;
import libcore.reflect.AnnotatedElements;

/**
 * Represents an annotated element of the program currently running in this
 * VM.  This interface allows annotations to be read reflectively.  All
 * annotations returned by methods in this interface are immutable and
 * serializable.  It is permissible for the caller to modify the
 * arrays returned by accessors for array-valued enum members; it will
 * have no affect on the arrays returned to other callers.
 *
 * <p>If an annotation returned by a method in this interface contains
 * (directly or indirectly) a {@link Class}-valued member referring to
 * a class that is not accessible in this VM, attempting to read the class
 * by calling the relevant Class-returning method on the returned annotation
 * will result in a {@link TypeNotPresentException}.
 *
 * <p>Similarly, attempting to read an enum-valued member will result in
 * a {@link EnumConstantNotPresentException} if the enum constant in the
 * annotation is no longer present in the enum type.
 *
 * <p>Finally, Attempting to read a member whose definition has evolved
 * incompatibly will result in a {@link
 * java.lang.annotation.AnnotationTypeMismatchException} or an
 * {@link java.lang.annotation.IncompleteAnnotationException}.
 *
 * @see java.lang.EnumConstantNotPresentException
 * @see java.lang.TypeNotPresentException
 * @see java.lang.annotation.AnnotationFormatError
 * @see java.lang.annotation.AnnotationTypeMismatchException
 * @see java.lang.annotation.IncompleteAnnotationException
 * @since 1.5
 * @author Josh Bloch
 */
public interface AnnotatedElement {
    /**
     * Returns true if an annotation for the specified type
     * is present on this element, else false.  This method
     * is designed primarily for convenient access to marker annotations.
     *
     * @param annotationClass the Class object corresponding to the
     *        annotation type
     * @return true if an annotation for the specified annotation
     *     type is present on this element, else false
     * @throws NullPointerException if the given annotation class is null
     * @since 1.5
     */
     default boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
         return getAnnotation(annotationClass) != null;
     }

   /**
     * Returns this element's annotation for the specified type if
     * such an annotation is present, else null.
     *
     * @param annotationClass the Class object corresponding to the
     *        annotation type
     * @return this element's annotation for the specified annotation type if
     *     present on this element, else null
     * @throws NullPointerException if the given annotation class is null
     * @since 1.5
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    /**
     * Returns all annotations present on this element.  (Returns an array
     * of length zero if this element has no annotations.)  The caller of
     * this method is free to modify the returned array; it will have no
     * effect on the arrays returned to other callers.
     *
     * @return all annotations present on this element
     * @since 1.5
     */
    Annotation[] getAnnotations();

    /**
     * Returns an associated list of annotations on {@code this} element,
     * whose class is {@code annotationClass}, or an empty array if nothing was found.
     *
     * @since 1.8
     */
    default <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
        // This method does not handle inherited annotations and is intended for use for
        // {@code Method}, {@code Field}, {@code Package}. The {@link Class#getAnnotationsByType}
        // is implemented explicitly. Therefore this implementation does not fulfill the documented
        // default implementation for {@link AnnotatedElement#getAnnotationsByType(Class)} but in an
        // undetectable way because Class is final.
        return AnnotatedElements.getDirectOrIndirectAnnotationsByType(this, annotationClass);
    }

    /**
     * Returns a directly-present annotation on {@code this} element, whose class is
     * {@code annotationClass}, or {@code null} if nothing was found.
     *
     * @since 1.8
     */
    default <T extends Annotation> Annotation getDeclaredAnnotation(Class<T> annotationClass) {
        Objects.requireNonNull(annotationClass);
        // Loop over all directly-present annotations looking for a matching one
        for (Annotation annotation : getDeclaredAnnotations()) {
            if (annotationClass.equals(annotation.annotationType())) {
                // More robust to do a dynamic cast at runtime instead
                // of compile-time only.
                return annotationClass.cast(annotation);
            }
        }
        return null;
    }

    /**
     * Returns a directly or indirectly present list of annotations on {@code this} element,
     * whose class is {@code annotationClass}, or an empty array if nothing was found.
     *
     * @since 1.8
     */
    default <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
        return AnnotatedElements.getDirectOrIndirectAnnotationsByType(this, annotationClass);
    }

    /**
     * Returns all annotations that are directly present on this
     * element.  Unlike the other methods in this interface, this method
     * ignores inherited annotations.  (Returns an array of length zero if
     * no annotations are directly present on this element.)  The caller of
     * this method is free to modify the returned array; it will have no
     * effect on the arrays returned to other callers.
     *
     * @return All annotations directly present on this element
     * @since 1.5
     */
    Annotation[] getDeclaredAnnotations();
}
