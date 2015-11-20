/*
 * Copyright (c) 1994, 2011, Oracle and/or its affiliates. All rights reserved.
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
package java.lang;

import dalvik.system.VMRuntime;
import dalvik.system.VMStack;
import android.system.StructPasswd;
import android.system.StructUtsname;
import android.system.ErrnoException;
import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.nio.channels.Channel;
import java.nio.channels.spi.SelectorProvider;
import libcore.icu.ICU;
import libcore.io.Libcore;

/**
 * The <code>System</code> class contains several useful class fields
 * and methods. It cannot be instantiated.
 *
 * <p>Among the facilities provided by the <code>System</code> class
 * are standard input, standard output, and error output streams;
 * access to externally defined properties and environment
 * variables; a means of loading files and libraries; and a utility
 * method for quickly copying a portion of an array.
 *
 * @author  unascribed
 * @since   JDK1.0
 */
public final class System {
    /** Don't let anyone instantiate this class */
    private System() {
    }

    /**
     * The "standard" input stream. This stream is already
     * open and ready to supply input data. Typically this stream
     * corresponds to keyboard input or another input source specified by
     * the host environment or user.
     */
    public final static InputStream in;

    /**
     * The "standard" output stream. This stream is already
     * open and ready to accept output data. Typically this stream
     * corresponds to display output or another output destination
     * specified by the host environment or user.
     * <p>
     * For simple stand-alone Java applications, a typical way to write
     * a line of output data is:
     * <blockquote><pre>
     *     System.out.println(data)
     * </pre></blockquote>
     * <p>
     * See the <code>println</code> methods in class <code>PrintStream</code>.
     *
     * @see     java.io.PrintStream#println()
     * @see     java.io.PrintStream#println(boolean)
     * @see     java.io.PrintStream#println(char)
     * @see     java.io.PrintStream#println(char[])
     * @see     java.io.PrintStream#println(double)
     * @see     java.io.PrintStream#println(float)
     * @see     java.io.PrintStream#println(int)
     * @see     java.io.PrintStream#println(long)
     * @see     java.io.PrintStream#println(java.lang.Object)
     * @see     java.io.PrintStream#println(java.lang.String)
     */
    public final static PrintStream out;

    /**
     * The "standard" error output stream. This stream is already
     * open and ready to accept output data.
     * <p>
     * Typically this stream corresponds to display output or another
     * output destination specified by the host environment or user. By
     * convention, this output stream is used to display error messages
     * or other information that should come to the immediate attention
     * of a user even if the principal output stream, the value of the
     * variable <code>out</code>, has been redirected to a file or other
     * destination that is typically not continuously monitored.
     */
    public final static PrintStream err;

    /**
     * Reassigns the "standard" input stream.
     *
     * <p>First, if there is a security manager, its <code>checkPermission</code>
     * method is called with a <code>RuntimePermission("setIO")</code> permission
     *  to see if it's ok to reassign the "standard" input stream.
     * <p>
     *
     * @param in the new standard input stream.
     *
     * @throws SecurityException
     *        if a security manager exists and its
     *        <code>checkPermission</code> method doesn't allow
     *        reassigning of the standard input stream.
     *
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     *
     * @since   JDK1.1
     */
    public static void setIn(InputStream in) {
        setIn0(in);
    }

    /**
     * Reassigns the "standard" output stream.
     *
     * <p>First, if there is a security manager, its <code>checkPermission</code>
     * method is called with a <code>RuntimePermission("setIO")</code> permission
     *  to see if it's ok to reassign the "standard" output stream.
     *
     * @param out the new standard output stream
     *
     * @throws SecurityException
     *        if a security manager exists and its
     *        <code>checkPermission</code> method doesn't allow
     *        reassigning of the standard output stream.
     *
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     *
     * @since   JDK1.1
     */
    public static void setOut(PrintStream out) {
        setOut0(out);
    }

    /**
     * Reassigns the "standard" error output stream.
     *
     * <p>First, if there is a security manager, its <code>checkPermission</code>
     * method is called with a <code>RuntimePermission("setIO")</code> permission
     *  to see if it's ok to reassign the "standard" error output stream.
     *
     * @param err the new standard error output stream.
     *
     * @throws SecurityException
     *        if a security manager exists and its
     *        <code>checkPermission</code> method doesn't allow
     *        reassigning of the standard error output stream.
     *
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     *
     * @since   JDK1.1
     */
    public static void setErr(PrintStream err) {
        setErr0(err);
    }

    private static Console cons = null;

    /**
     * Returns the unique {@link java.io.Console Console} object associated
     * with the current Java virtual machine, if any.
     *
     * @return  The system console, if any, otherwise <tt>null</tt>.
     *
     * @since   1.6
     */
     public static Console console() {
         synchronized (System.class) {
             if (cons == null) {
                 cons = Console.console();
             }

             return cons;
         }
     }

    /**
     * Returns the channel inherited from the entity that created this
     * Java virtual machine.
     *
     * <p> This method returns the channel obtained by invoking the
     * {@link java.nio.channels.spi.SelectorProvider#inheritedChannel
     * inheritedChannel} method of the system-wide default
     * {@link java.nio.channels.spi.SelectorProvider} object. </p>
     *
     * <p> In addition to the network-oriented channels described in
     * {@link java.nio.channels.spi.SelectorProvider#inheritedChannel
     * inheritedChannel}, this method may return other kinds of
     * channels in the future.
     *
     * @return  The inherited channel, if any, otherwise <tt>null</tt>.
     *
     * @throws  IOException
     *          If an I/O error occurs
     *
     * @throws  SecurityException
     *          If a security manager is present and it does not
     *          permit access to the channel.
     *
     * @since 1.5
     */
    public static Channel inheritedChannel() throws IOException {
        return SelectorProvider.provider().inheritedChannel();
    }

    private static native void setIn0(InputStream in);
    private static native void setOut0(PrintStream out);
    private static native void setErr0(PrintStream err);

    /**
     * Sets the System security.
     *
     * <p> If there is a security manager already installed, this method first
     * calls the security manager's <code>checkPermission</code> method
     * with a <code>RuntimePermission("setSecurityManager")</code>
     * permission to ensure it's ok to replace the existing
     * security manager.
     * This may result in throwing a <code>SecurityException</code>.
     *
     * <p> Otherwise, the argument is established as the current
     * security manager. If the argument is <code>null</code> and no
     * security manager has been established, then no action is taken and
     * the method simply returns.
     *
     * @param      s   the security manager.
     * @exception  SecurityException  if the security manager has already
     *             been set and its <code>checkPermission</code> method
     *             doesn't allow it to be replaced.
     * @see #getSecurityManager
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     */
    public static  void setSecurityManager(final SecurityManager s) {
        // No-op on android.
    }

    /**
     * Gets the system security interface.
     *
     * @return  if a security manager has already been established for the
     *          current application, then that security manager is returned;
     *          otherwise, <code>null</code> is returned.
     * @see     #setSecurityManager
     */
    public static SecurityManager getSecurityManager() {
        // No-op on android.
        return null;
    }

    /**
     * Returns the current time in milliseconds.  Note that
     * while the unit of time of the return value is a millisecond,
     * the granularity of the value depends on the underlying
     * operating system and may be larger.  For example, many
     * operating systems measure time in units of tens of
     * milliseconds.
     *
     * <p> See the description of the class <code>Date</code> for
     * a discussion of slight discrepancies that may arise between
     * "computer time" and coordinated universal time (UTC).
     *
     * @return  the difference, measured in milliseconds, between
     *          the current time and midnight, January 1, 1970 UTC.
     * @see     java.util.Date
     */
    public static native long currentTimeMillis();

    /**
     * Returns the current value of the running Java Virtual Machine's
     * high-resolution time source, in nanoseconds.
     *
     * <p>This method can only be used to measure elapsed time and is
     * not related to any other notion of system or wall-clock time.
     * The value returned represents nanoseconds since some fixed but
     * arbitrary <i>origin</i> time (perhaps in the future, so values
     * may be negative).  The same origin is used by all invocations of
     * this method in an instance of a Java virtual machine; other
     * virtual machine instances are likely to use a different origin.
     *
     * <p>This method provides nanosecond precision, but not necessarily
     * nanosecond resolution (that is, how frequently the value changes)
     * - no guarantees are made except that the resolution is at least as
     * good as that of {@link #currentTimeMillis()}.
     *
     * <p>Differences in successive calls that span greater than
     * approximately 292 years (2<sup>63</sup> nanoseconds) will not
     * correctly compute elapsed time due to numerical overflow.
     *
     * <p>The values returned by this method become meaningful only when
     * the difference between two such values, obtained within the same
     * instance of a Java virtual machine, is computed.
     *
     * <p> For example, to measure how long some code takes to execute:
     *  <pre> {@code
     * long startTime = System.nanoTime();
     * // ... the code being measured ...
     * long estimatedTime = System.nanoTime() - startTime;}</pre>
     *
     * <p>To compare two nanoTime values
     *  <pre> {@code
     * long t0 = System.nanoTime();
     * ...
     * long t1 = System.nanoTime();}</pre>
     *
     * one should use {@code t1 - t0 < 0}, not {@code t1 < t0},
     * because of the possibility of numerical overflow.
     *
     * @return the current value of the running Java Virtual Machine's
     *         high-resolution time source, in nanoseconds
     * @since 1.5
     */
    public static native long nanoTime();

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * A subsequence of array components are copied from the source
     * array referenced by <code>src</code> to the destination array
     * referenced by <code>dest</code>. The number of components copied is
     * equal to the <code>length</code> argument. The components at
     * positions <code>srcPos</code> through
     * <code>srcPos+length-1</code> in the source array are copied into
     * positions <code>destPos</code> through
     * <code>destPos+length-1</code>, respectively, of the destination
     * array.
     * <p>
     * If the <code>src</code> and <code>dest</code> arguments refer to the
     * same array object, then the copying is performed as if the
     * components at positions <code>srcPos</code> through
     * <code>srcPos+length-1</code> were first copied to a temporary
     * array with <code>length</code> components and then the contents of
     * the temporary array were copied into positions
     * <code>destPos</code> through <code>destPos+length-1</code> of the
     * destination array.
     * <p>
     * If <code>dest</code> is <code>null</code>, then a
     * <code>NullPointerException</code> is thrown.
     * <p>
     * If <code>src</code> is <code>null</code>, then a
     * <code>NullPointerException</code> is thrown and the destination
     * array is not modified.
     * <p>
     * Otherwise, if any of the following is true, an
     * <code>ArrayStoreException</code> is thrown and the destination is
     * not modified:
     * <ul>
     * <li>The <code>src</code> argument refers to an object that is not an
     *     array.
     * <li>The <code>dest</code> argument refers to an object that is not an
     *     array.
     * <li>The <code>src</code> argument and <code>dest</code> argument refer
     *     to arrays whose component types are different primitive types.
     * <li>The <code>src</code> argument refers to an array with a primitive
     *    component type and the <code>dest</code> argument refers to an array
     *     with a reference component type.
     * <li>The <code>src</code> argument refers to an array with a reference
     *    component type and the <code>dest</code> argument refers to an array
     *     with a primitive component type.
     * </ul>
     * <p>
     * Otherwise, if any of the following is true, an
     * <code>IndexOutOfBoundsException</code> is
     * thrown and the destination is not modified:
     * <ul>
     * <li>The <code>srcPos</code> argument is negative.
     * <li>The <code>destPos</code> argument is negative.
     * <li>The <code>length</code> argument is negative.
     * <li><code>srcPos+length</code> is greater than
     *     <code>src.length</code>, the length of the source array.
     * <li><code>destPos+length</code> is greater than
     *     <code>dest.length</code>, the length of the destination array.
     * </ul>
     * <p>
     * Otherwise, if any actual component of the source array from
     * position <code>srcPos</code> through
     * <code>srcPos+length-1</code> cannot be converted to the component
     * type of the destination array by assignment conversion, an
     * <code>ArrayStoreException</code> is thrown. In this case, let
     * <b><i>k</i></b> be the smallest nonnegative integer less than
     * length such that <code>src[srcPos+</code><i>k</i><code>]</code>
     * cannot be converted to the component type of the destination
     * array; when the exception is thrown, source array components from
     * positions <code>srcPos</code> through
     * <code>srcPos+</code><i>k</i><code>-1</code>
     * will already have been copied to destination array positions
     * <code>destPos</code> through
     * <code>destPos+</code><i>k</I><code>-1</code> and no other
     * positions of the destination array will have been modified.
     * (Because of the restrictions already itemized, this
     * paragraph effectively applies only to the situation where both
     * arrays have component types that are reference types.)
     *
     * @param      src      the source array.
     * @param      srcPos   starting position in the source array.
     * @param      dest     the destination array.
     * @param      destPos  starting position in the destination data.
     * @param      length   the number of array elements to be copied.
     * @exception  IndexOutOfBoundsException  if copying would cause
     *               access of data outside array bounds.
     * @exception  ArrayStoreException  if an element in the <code>src</code>
     *               array could not be stored into the <code>dest</code> array
     *               because of a type mismatch.
     * @exception  NullPointerException if either <code>src</code> or
     *               <code>dest</code> is <code>null</code>.
     */
    public static native void arraycopy(Object src,  int  srcPos,
                                        Object dest, int destPos,
                                        int length);


    // ----- BEGIN android -----
    /**
     * The char array length threshold below which to use a Java
     * (non-native) version of arraycopy() instead of the native
     * version. See b/7103825.
     */
    private static final int ARRAYCOPY_SHORT_CHAR_ARRAY_THRESHOLD = 32;

    /**
     * The char[] specialized version of arraycopy().
     *
     * @hide internal use only
     */
    public static void arraycopy(char[] src, int srcPos, char[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        if (dst == null) {
            throw new NullPointerException("dst == null");
        }
        if (srcPos < 0 || dstPos < 0 || length < 0 ||
            srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException(
                "src.length=" + src.length + " srcPos=" + srcPos +
                " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        }
        if (length <= ARRAYCOPY_SHORT_CHAR_ARRAY_THRESHOLD) {
            // Copy char by char for shorter arrays.
            if (src == dst && srcPos < dstPos && dstPos < srcPos + length) {
                // Copy backward (to avoid overwriting elements before
                // they are copied in case of an overlap on the same
                // array.)
                for (int i = length - 1; i >= 0; --i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            } else {
                // Copy forward.
                for (int i = 0; i < length; ++i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            }
        } else {
            // Call the native version for longer arrays.
            arraycopyCharUnchecked(src, srcPos, dst, dstPos, length);
        }
    }

    /**
     * The char[] specialized, unchecked, native version of
     * arraycopy(). This assumes error checking has been done.
     */
    private static native void arraycopyCharUnchecked(char[] src, int srcPos,
        char[] dst, int dstPos, int length);

    /**
     * The byte array length threshold below which to use a Java
     * (non-native) version of arraycopy() instead of the native
     * version. See b/7103825.
     */
    private static final int ARRAYCOPY_SHORT_BYTE_ARRAY_THRESHOLD = 32;

    /**
     * The byte[] specialized version of arraycopy().
     *
     * @hide internal use only
     */
    public static void arraycopy(byte[] src, int srcPos, byte[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        if (dst == null) {
            throw new NullPointerException("dst == null");
        }
        if (srcPos < 0 || dstPos < 0 || length < 0 ||
            srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException(
                "src.length=" + src.length + " srcPos=" + srcPos +
                " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        }
        if (length <= ARRAYCOPY_SHORT_BYTE_ARRAY_THRESHOLD) {
            // Copy byte by byte for shorter arrays.
            if (src == dst && srcPos < dstPos && dstPos < srcPos + length) {
                // Copy backward (to avoid overwriting elements before
                // they are copied in case of an overlap on the same
                // array.)
                for (int i = length - 1; i >= 0; --i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            } else {
                // Copy forward.
                for (int i = 0; i < length; ++i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            }
        } else {
            // Call the native version for longer arrays.
            arraycopyByteUnchecked(src, srcPos, dst, dstPos, length);
        }
    }

    /**
     * The byte[] specialized, unchecked, native version of
     * arraycopy(). This assumes error checking has been done.
     */
    private static native void arraycopyByteUnchecked(byte[] src, int srcPos,
        byte[] dst, int dstPos, int length);

    /**
     * The short array length threshold below which to use a Java
     * (non-native) version of arraycopy() instead of the native
     * version. See b/7103825.
     */
    private static final int ARRAYCOPY_SHORT_SHORT_ARRAY_THRESHOLD = 32;

    /**
     * The short[] specialized version of arraycopy().
     *
     * @hide internal use only
     */
    public static void arraycopy(short[] src, int srcPos, short[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        if (dst == null) {
            throw new NullPointerException("dst == null");
        }
        if (srcPos < 0 || dstPos < 0 || length < 0 ||
            srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException(
                "src.length=" + src.length + " srcPos=" + srcPos +
                " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        }
        if (length <= ARRAYCOPY_SHORT_SHORT_ARRAY_THRESHOLD) {
            // Copy short by short for shorter arrays.
            if (src == dst && srcPos < dstPos && dstPos < srcPos + length) {
                // Copy backward (to avoid overwriting elements before
                // they are copied in case of an overlap on the same
                // array.)
                for (int i = length - 1; i >= 0; --i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            } else {
                // Copy forward.
                for (int i = 0; i < length; ++i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            }
        } else {
            // Call the native version for longer arrays.
            arraycopyShortUnchecked(src, srcPos, dst, dstPos, length);
        }
    }

    /**
     * The short[] specialized, unchecked, native version of
     * arraycopy(). This assumes error checking has been done.
     */
    private static native void arraycopyShortUnchecked(short[] src, int srcPos,
        short[] dst, int dstPos, int length);

    /**
     * The short array length threshold below which to use a Java
     * (non-native) version of arraycopy() instead of the native
     * version. See b/7103825.
     */
    private static final int ARRAYCOPY_SHORT_INT_ARRAY_THRESHOLD = 32;

    /**
     * The int[] specialized version of arraycopy().
     *
     * @hide internal use only
     */
    public static void arraycopy(int[] src, int srcPos, int[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        if (dst == null) {
            throw new NullPointerException("dst == null");
        }
        if (srcPos < 0 || dstPos < 0 || length < 0 ||
            srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException(
                "src.length=" + src.length + " srcPos=" + srcPos +
                " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        }
        if (length <= ARRAYCOPY_SHORT_INT_ARRAY_THRESHOLD) {
            // Copy int by int for shorter arrays.
            if (src == dst && srcPos < dstPos && dstPos < srcPos + length) {
                // Copy backward (to avoid overwriting elements before
                // they are copied in case of an overlap on the same
                // array.)
                for (int i = length - 1; i >= 0; --i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            } else {
                // Copy forward.
                for (int i = 0; i < length; ++i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            }
        } else {
            // Call the native version for longer arrays.
            arraycopyIntUnchecked(src, srcPos, dst, dstPos, length);
        }
    }

    /**
     * The int[] specialized, unchecked, native version of
     * arraycopy(). This assumes error checking has been done.
     */
    private static native void arraycopyIntUnchecked(int[] src, int srcPos,
        int[] dst, int dstPos, int length);

    /**
     * The short array length threshold below which to use a Java
     * (non-native) version of arraycopy() instead of the native
     * version. See b/7103825.
     */
    private static final int ARRAYCOPY_SHORT_LONG_ARRAY_THRESHOLD = 32;

    /**
     * The long[] specialized version of arraycopy().
     *
     * @hide internal use only
     */
    public static void arraycopy(long[] src, int srcPos, long[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        if (dst == null) {
            throw new NullPointerException("dst == null");
        }
        if (srcPos < 0 || dstPos < 0 || length < 0 ||
            srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException(
                "src.length=" + src.length + " srcPos=" + srcPos +
                " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        }
        if (length <= ARRAYCOPY_SHORT_LONG_ARRAY_THRESHOLD) {
            // Copy long by long for shorter arrays.
            if (src == dst && srcPos < dstPos && dstPos < srcPos + length) {
                // Copy backward (to avoid overwriting elements before
                // they are copied in case of an overlap on the same
                // array.)
                for (int i = length - 1; i >= 0; --i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            } else {
                // Copy forward.
                for (int i = 0; i < length; ++i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            }
        } else {
            // Call the native version for longer arrays.
            arraycopyLongUnchecked(src, srcPos, dst, dstPos, length);
        }
    }

    /**
     * The long[] specialized, unchecked, native version of
     * arraycopy(). This assumes error checking has been done.
     */
    private static native void arraycopyLongUnchecked(long[] src, int srcPos,
        long[] dst, int dstPos, int length);

    /**
     * The short array length threshold below which to use a Java
     * (non-native) version of arraycopy() instead of the native
     * version. See b/7103825.
     */
    private static final int ARRAYCOPY_SHORT_FLOAT_ARRAY_THRESHOLD = 32;

    /**
     * The float[] specialized version of arraycopy().
     *
     * @hide internal use only
     */
    public static void arraycopy(float[] src, int srcPos, float[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        if (dst == null) {
            throw new NullPointerException("dst == null");
        }
        if (srcPos < 0 || dstPos < 0 || length < 0 ||
            srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException(
                "src.length=" + src.length + " srcPos=" + srcPos +
                " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        }
        if (length <= ARRAYCOPY_SHORT_FLOAT_ARRAY_THRESHOLD) {
            // Copy float by float for shorter arrays.
            if (src == dst && srcPos < dstPos && dstPos < srcPos + length) {
                // Copy backward (to avoid overwriting elements before
                // they are copied in case of an overlap on the same
                // array.)
                for (int i = length - 1; i >= 0; --i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            } else {
                // Copy forward.
                for (int i = 0; i < length; ++i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            }
        } else {
            // Call the native version for floater arrays.
            arraycopyFloatUnchecked(src, srcPos, dst, dstPos, length);
        }
    }

    /**
     * The float[] specialized, unchecked, native version of
     * arraycopy(). This assumes error checking has been done.
     */
    private static native void arraycopyFloatUnchecked(float[] src, int srcPos,
        float[] dst, int dstPos, int length);

    /**
     * The short array length threshold below which to use a Java
     * (non-native) version of arraycopy() instead of the native
     * version. See b/7103825.
     */
    private static final int ARRAYCOPY_SHORT_DOUBLE_ARRAY_THRESHOLD = 32;

    /**
     * The double[] specialized version of arraycopy().
     *
     * @hide internal use only
     */
    public static void arraycopy(double[] src, int srcPos, double[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        if (dst == null) {
            throw new NullPointerException("dst == null");
        }
        if (srcPos < 0 || dstPos < 0 || length < 0 ||
            srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException(
                "src.length=" + src.length + " srcPos=" + srcPos +
                " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        }
        if (length <= ARRAYCOPY_SHORT_DOUBLE_ARRAY_THRESHOLD) {
            // Copy double by double for shorter arrays.
            if (src == dst && srcPos < dstPos && dstPos < srcPos + length) {
                // Copy backward (to avoid overwriting elements before
                // they are copied in case of an overlap on the same
                // array.)
                for (int i = length - 1; i >= 0; --i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            } else {
                // Copy forward.
                for (int i = 0; i < length; ++i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            }
        } else {
            // Call the native version for floater arrays.
            arraycopyDoubleUnchecked(src, srcPos, dst, dstPos, length);
        }
    }

    /**
     * The double[] specialized, unchecked, native version of
     * arraycopy(). This assumes error checking has been done.
     */
    private static native void arraycopyDoubleUnchecked(double[] src, int srcPos,
        double[] dst, int dstPos, int length);

    /**
     * The short array length threshold below which to use a Java
     * (non-native) version of arraycopy() instead of the native
     * version. See b/7103825.
     */
    private static final int ARRAYCOPY_SHORT_BOOLEAN_ARRAY_THRESHOLD = 32;

    /**
     * The boolean[] specialized version of arraycopy().
     *
     * @hide internal use only
     */
    public static void arraycopy(boolean[] src, int srcPos, boolean[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        if (dst == null) {
            throw new NullPointerException("dst == null");
        }
        if (srcPos < 0 || dstPos < 0 || length < 0 ||
            srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException(
                "src.length=" + src.length + " srcPos=" + srcPos +
                " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        }
        if (length <= ARRAYCOPY_SHORT_BOOLEAN_ARRAY_THRESHOLD) {
            // Copy boolean by boolean for shorter arrays.
            if (src == dst && srcPos < dstPos && dstPos < srcPos + length) {
                // Copy backward (to avoid overwriting elements before
                // they are copied in case of an overlap on the same
                // array.)
                for (int i = length - 1; i >= 0; --i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            } else {
                // Copy forward.
                for (int i = 0; i < length; ++i) {
                    dst[dstPos + i] = src[srcPos + i];
                }
            }
        } else {
            // Call the native version for floater arrays.
            arraycopyBooleanUnchecked(src, srcPos, dst, dstPos, length);
        }
    }

    /**
     * The boolean[] specialized, unchecked, native version of
     * arraycopy(). This assumes error checking has been done.
     */
    private static native void arraycopyBooleanUnchecked(boolean[] src, int srcPos,
        boolean[] dst, int dstPos, int length);
    // ----- END android -----

    /**
     * Returns the same hash code for the given object as
     * would be returned by the default method hashCode(),
     * whether or not the given object's class overrides
     * hashCode().
     * The hash code for the null reference is zero.
     *
     * @param x object for which the hashCode is to be calculated
     * @return  the hashCode
     * @since   JDK1.1
     */
    public static native int identityHashCode(Object x);

    /**
     * System properties. The following properties are guaranteed to be defined:
     * <dl>
     * <dt>java.version         <dd>Java version number
     * <dt>java.vendor          <dd>Java vendor specific string
     * <dt>java.vendor.url      <dd>Java vendor URL
     * <dt>java.home            <dd>Java installation directory
     * <dt>java.class.version   <dd>Java class version number
     * <dt>java.class.path      <dd>Java classpath
     * <dt>os.name              <dd>Operating System Name
     * <dt>os.arch              <dd>Operating System Architecture
     * <dt>os.version           <dd>Operating System Version
     * <dt>file.separator       <dd>File separator ("/" on Unix)
     * <dt>path.separator       <dd>Path separator (":" on Unix)
     * <dt>line.separator       <dd>Line separator ("\n" on Unix)
     * <dt>user.name            <dd>User account name
     * <dt>user.home            <dd>User home directory
     * <dt>user.dir             <dd>User's current working directory
     * </dl>
     */

    private static Properties props;
    private static Properties unchangeableProps;

    private static native String[] specialProperties();

    static final class PropertiesWithNonOverrideableDefaults extends Properties {
        PropertiesWithNonOverrideableDefaults(Properties defaults) {
            super(defaults);
        }

        @Override
        public Object put(Object key, Object value) {
            if (defaults.containsKey(key)) {
                logE("Ignoring attempt to set property \"" + key +
                        "\" to value \"" + value + "\".");
                return defaults.get(key);
            }

            return super.put(key, value);
        }

        @Override
        public Object remove(Object key) {
            if (defaults.containsKey(key)) {
                logE("Ignoring attempt to remove property \"" + key + "\".");
                return null;
            }

            return super.remove(key);
        }
    }

    private static void parsePropertyAssignments(Properties p, String[] assignments) {
        for (String assignment : assignments) {
            int split = assignment.indexOf('=');
            String key = assignment.substring(0, split);
            String value = assignment.substring(split + 1);
            p.put(key, value);
        }
    }

    private static Properties initUnchangeableSystemProperties() {
        VMRuntime runtime = VMRuntime.getRuntime();
        Properties p = new Properties();

        String projectUrl = "http://www.android.com/";
        String projectName = "The Android Project";

        p.put("java.boot.class.path", runtime.bootClassPath());
        p.put("java.class.path", runtime.classPath());

        // None of these four are meaningful on Android, but these keys are guaranteed
        // to be present for System.getProperty. For java.class.version, we use the maximum
        // class file version that dx currently supports.
        p.put("java.class.version", "50.0");
        p.put("java.compiler", "");
        p.put("java.ext.dirs", "");

        // TODO: does this make any sense? Should we just leave java.home unset?
        String javaHome = getenv("JAVA_HOME");
        if (javaHome == null) {
            javaHome = "/system";
        }
        p.put("java.home", javaHome);

        p.put("java.specification.name", "Dalvik Core Library");
        p.put("java.specification.vendor", projectName);
        p.put("java.specification.version", "0.9");

        p.put("java.vendor", projectName);
        p.put("java.vendor.url", projectUrl);
        p.put("java.vm.name", "Dalvik");
        p.put("java.vm.specification.name", "Dalvik Virtual Machine Specification");
        p.put("java.vm.specification.vendor", projectName);
        p.put("java.vm.specification.version", "0.9");
        p.put("java.vm.vendor", projectName);
        p.put("java.vm.version", runtime.vmVersion());

        p.put("java.vm.vendor.url", projectUrl);

        p.put("java.net.preferIPv6Addresses", "false");

        p.put("file.encoding", "UTF-8");

        try {
            StructPasswd passwd = Libcore.os.getpwuid(Libcore.os.getuid());
            p.put("user.name", passwd.pw_name);
        } catch (ErrnoException exception) {
            throw new AssertionError(exception);
        }

        StructUtsname info = Libcore.os.uname();
        p.put("os.arch", info.machine);
        p.put("os.name", info.sysname);
        p.put("os.version", info.release);

        // Undocumented Android-only properties.
        p.put("android.icu.library.version", ICU.getIcuVersion());
        p.put("android.icu.unicode.version", ICU.getUnicodeVersion());
        p.put("android.icu.cldr.version", ICU.getCldrVersion());

        // Property override for ICU4J : this is the location of the ICU4C data. This
        // is prioritized over the properties in ICUConfig.properties. The issue with using
        // that is that it doesn't play well with jarjar and it needs complicated build rules
        // to change its default value.
        p.put("android.icu.impl.ICUBinary.dataPath", getenv("ANDROID_ROOT") + "/usr/icu");

        parsePropertyAssignments(p, specialProperties());

        // Override built-in properties with settings from the command line.
        parsePropertyAssignments(p, runtime.properties());

        if (p.containsKey("file.separator")) {
            logE("Ignoring command line argument: -Dfile.separator");
        }

        if (p.containsKey("line.separator")) {
            logE("Ignoring command line argument: -Dline.separator");
        }

        if (p.containsKey("path.separator")) {
            logE("Ignoring command line argument: -Dpath.separator");
        }

        p.put("file.separator", "/");
        p.put("line.separator", "\n");
        p.put("path.separator", ":");

        return p;
    }

    private static Properties initProperties() {
        Properties p = new PropertiesWithNonOverrideableDefaults(unchangeableProps);
        setDefaultChangeableProperties(p);
        return p;
    }

    private static Properties setDefaultChangeableProperties(Properties p) {
        // On Android, each app gets its own temporary directory.
        // (See android.app.ActivityThread.) This is just a fallback default,
        // useful only on the host.
        // We check first if the property has not been set already: note that it
        // can only be set from the command line through the '-Djava.io.tmpdir=' option.
        if (!unchangeableProps.containsKey("java.io.tmpdir")) {
            p.put("java.io.tmpdir", "/tmp");
        }

        // Android has always had an empty "user.home" (see docs for getProperty).
        // This is not useful for normal android apps which need to use android specific
        // APIs such as {@code Context.getFilesDir} and {@code Context.getCacheDir} but
        // we make it changeable for backward compatibility, so that they can change it
        // to a writeable location if required.
        // We check first if the property has not been set already: note that it
        // can only be set from the command line through the '-Duser.home=' option.
        if (!unchangeableProps.containsKey("user.home")) {
            p.put("user.home", "");
        }

        return p;
    }

    /**
     * Inits an unchangeable system property with the given value.
     *
     * This is called from native code when the environment needs to change under native
     * bridge emulation.
     *
     * @hide also visible for tests.
     */
    public static void setUnchangeableSystemProperty(String key, String value) {
        checkKey(key);
        unchangeableProps.put(key, value);
    }

    private static void addLegacyLocaleSystemProperties() {
        final String locale = getProperty("user.locale", "");
        if (!locale.isEmpty()) {
            Locale l = Locale.forLanguageTag(locale);
            setUnchangeableSystemProperty("user.language", l.getLanguage());
            setUnchangeableSystemProperty("user.region", l.getCountry());
            setUnchangeableSystemProperty("user.variant", l.getVariant());
        } else {
            // If "user.locale" isn't set we fall back to our old defaults of
            // language="en" and region="US" (if unset) and don't attempt to set it.
            // The Locale class will fall back to using user.language and
            // user.region if unset.
            final String language = getProperty("user.language", "");
            final String region = getProperty("user.region", "");

            if (language.isEmpty()) {
                setUnchangeableSystemProperty("user.language", "en");
            }

            if (region.isEmpty()) {
                setUnchangeableSystemProperty("user.region", "US");
            }
        }
    }

    /**
     * Determines the current system properties.
     * <p>
     * First, if there is a security manager, its
     * <code>checkPropertiesAccess</code> method is called with no
     * arguments. This may result in a security exception.
     * <p>
     * The current set of system properties for use by the
     * {@link #getProperty(String)} method is returned as a
     * <code>Properties</code> object. If there is no current set of
     * system properties, a set of system properties is first created and
     * initialized. This set of system properties always includes values
     * for the following keys:
     * <table summary="Shows property keys and associated values">
     * <tr><th>Key</th>
     *     <th>Description of Associated Value</th></tr>
     * <tr><td><code>java.version</code></td>
     *     <td>Java Runtime Environment version</td></tr>
     * <tr><td><code>java.vendor</code></td>
     *     <td>Java Runtime Environment vendor</td></tr
     * <tr><td><code>java.vendor.url</code></td>
     *     <td>Java vendor URL</td></tr>
     * <tr><td><code>java.home</code></td>
     *     <td>Java installation directory</td></tr>
     * <tr><td><code>java.vm.specification.version</code></td>
     *     <td>Java Virtual Machine specification version</td></tr>
     * <tr><td><code>java.vm.specification.vendor</code></td>
     *     <td>Java Virtual Machine specification vendor</td></tr>
     * <tr><td><code>java.vm.specification.name</code></td>
     *     <td>Java Virtual Machine specification name</td></tr>
     * <tr><td><code>java.vm.version</code></td>
     *     <td>Java Virtual Machine implementation version</td></tr>
     * <tr><td><code>java.vm.vendor</code></td>
     *     <td>Java Virtual Machine implementation vendor</td></tr>
     * <tr><td><code>java.vm.name</code></td>
     *     <td>Java Virtual Machine implementation name</td></tr>
     * <tr><td><code>java.specification.version</code></td>
     *     <td>Java Runtime Environment specification  version</td></tr>
     * <tr><td><code>java.specification.vendor</code></td>
     *     <td>Java Runtime Environment specification  vendor</td></tr>
     * <tr><td><code>java.specification.name</code></td>
     *     <td>Java Runtime Environment specification  name</td></tr>
     * <tr><td><code>java.class.version</code></td>
     *     <td>Java class format version number</td></tr>
     * <tr><td><code>java.class.path</code></td>
     *     <td>Java class path</td></tr>
     * <tr><td><code>java.library.path</code></td>
     *     <td>List of paths to search when loading libraries</td></tr>
     * <tr><td><code>java.io.tmpdir</code></td>
     *     <td>Default temp file path</td></tr>
     * <tr><td><code>java.compiler</code></td>
     *     <td>Name of JIT compiler to use</td></tr>
     * <tr><td><code>java.ext.dirs</code></td>
     *     <td>Path of extension directory or directories</td></tr>
     * <tr><td><code>os.name</code></td>
     *     <td>Operating system name</td></tr>
     * <tr><td><code>os.arch</code></td>
     *     <td>Operating system architecture</td></tr>
     * <tr><td><code>os.version</code></td>
     *     <td>Operating system version</td></tr>
     * <tr><td><code>file.separator</code></td>
     *     <td>File separator ("/" on UNIX)</td></tr>
     * <tr><td><code>path.separator</code></td>
     *     <td>Path separator (":" on UNIX)</td></tr>
     * <tr><td><code>line.separator</code></td>
     *     <td>Line separator ("\n" on UNIX)</td></tr>
     * <tr><td><code>user.name</code></td>
     *     <td>User's account name</td></tr>
     * <tr><td><code>user.home</code></td>
     *     <td>User's home directory</td></tr>
     * <tr><td><code>user.dir</code></td>
     *     <td>User's current working directory</td></tr>
     * </table>
     * <p>
     * Multiple paths in a system property value are separated by the path
     * separator character of the platform.
     * <p>
     * Note that even if the security manager does not permit the
     * <code>getProperties</code> operation, it may choose to permit the
     * {@link #getProperty(String)} operation.
     *
     * @return     the system properties
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkPropertiesAccess</code> method doesn't allow access
     *              to the system properties.
     * @see        #setProperties
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkPropertiesAccess()
     * @see        java.util.Properties
     */
    public static Properties getProperties() {
        return props;
    }

    /**
     * Returns the system-dependent line separator string.  It always
     * returns the same value - the initial value of the {@linkplain
     * #getProperty(String) system property} {@code line.separator}.
     *
     * <p>On UNIX systems, it returns {@code "\n"}; on Microsoft
     * Windows systems it returns {@code "\r\n"}.
     */
    public static String lineSeparator() {
        return lineSeparator;
    }

    private static String lineSeparator;


    // Comment replaced with android one.
    /**
     * Attempts to set all system properties. Copies all properties from
     * {@code p} and discards system properties that are read only and cannot
     * be modified. See {@link #getProperty} for a list of such properties.
     */
    public static void setProperties(Properties props) {
        Properties baseProperties = new PropertiesWithNonOverrideableDefaults(unchangeableProps);
        if (props != null) {
            baseProperties.putAll(props);
        } else {
            setDefaultChangeableProperties(baseProperties);
        }

        System.props = baseProperties;
    }

    /**
     * Gets the system property indicated by the specified key.
     * <p>
     * First, if there is a security manager, its
     * <code>checkPropertyAccess</code> method is called with the key as
     * its argument. This may result in a SecurityException.
     * <p>
     * If there is no current set of system properties, a set of system
     * properties is first created and initialized in the same manner as
     * for the <code>getProperties</code> method.
     *
     * @param      key   the name of the system property.
     * @return     the string value of the system property,
     *             or <code>null</code> if there is no property with that key.
     *
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkPropertyAccess</code> method doesn't allow
     *              access to the specified system property.
     * @exception  NullPointerException if <code>key</code> is
     *             <code>null</code>.
     * @exception  IllegalArgumentException if <code>key</code> is empty.
     * @see        #setProperty
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkPropertyAccess(java.lang.String)
     * @see        java.lang.System#getProperties()
     */
    public static String getProperty(String key) {
        checkKey(key);

        return props.getProperty(key);
    }

    /**
     * Gets the system property indicated by the specified key.
     * <p>
     * First, if there is a security manager, its
     * <code>checkPropertyAccess</code> method is called with the
     * <code>key</code> as its argument.
     * <p>
     * If there is no current set of system properties, a set of system
     * properties is first created and initialized in the same manner as
     * for the <code>getProperties</code> method.
     *
     * @param      key   the name of the system property.
     * @param      def   a default value.
     * @return     the string value of the system property,
     *             or the default value if there is no property with that key.
     *
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkPropertyAccess</code> method doesn't allow
     *             access to the specified system property.
     * @exception  NullPointerException if <code>key</code> is
     *             <code>null</code>.
     * @exception  IllegalArgumentException if <code>key</code> is empty.
     * @see        #setProperty
     * @see        java.lang.SecurityManager#checkPropertyAccess(java.lang.String)
     * @see        java.lang.System#getProperties()
     */
    public static String getProperty(String key, String def) {
        checkKey(key);

        return props.getProperty(key, def);
    }

    /**
     * Sets the system property indicated by the specified key.
     * <p>
     * First, if a security manager exists, its
     * <code>SecurityManager.checkPermission</code> method
     * is called with a <code>PropertyPermission(key, "write")</code>
     * permission. This may result in a SecurityException being thrown.
     * If no exception is thrown, the specified property is set to the given
     * value.
     * <p>
     *
     * @param      key   the name of the system property.
     * @param      value the value of the system property.
     * @return     the previous value of the system property,
     *             or <code>null</code> if it did not have one.
     *
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkPermission</code> method doesn't allow
     *             setting of the specified property.
     * @exception  NullPointerException if <code>key</code> or
     *             <code>value</code> is <code>null</code>.
     * @exception  IllegalArgumentException if <code>key</code> is empty.
     * @see        #getProperty
     * @see        java.lang.System#getProperty(java.lang.String)
     * @see        java.lang.System#getProperty(java.lang.String, java.lang.String)
     * @see        java.util.PropertyPermission
     * @see        SecurityManager#checkPermission
     * @since      1.2
     */
    public static String setProperty(String key, String value) {
        checkKey(key);

        return (String) props.setProperty(key, value);
    }

    /**
     * Removes the system property indicated by the specified key.
     * <p>
     * First, if a security manager exists, its
     * <code>SecurityManager.checkPermission</code> method
     * is called with a <code>PropertyPermission(key, "write")</code>
     * permission. This may result in a SecurityException being thrown.
     * If no exception is thrown, the specified property is removed.
     * <p>
     *
     * @param      key   the name of the system property to be removed.
     * @return     the previous string value of the system property,
     *             or <code>null</code> if there was no property with that key.
     *
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkPropertyAccess</code> method doesn't allow
     *              access to the specified system property.
     * @exception  NullPointerException if <code>key</code> is
     *             <code>null</code>.
     * @exception  IllegalArgumentException if <code>key</code> is empty.
     * @see        #getProperty
     * @see        #setProperty
     * @see        java.util.Properties
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkPropertiesAccess()
     * @since 1.5
     */
    public static String clearProperty(String key) {
        checkKey(key);

        return (String) props.remove(key);
    }

    private static void checkKey(String key) {
        if (key == null) {
            throw new NullPointerException("key can't be null");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("key can't be empty");
        }
    }

    /**
     * Gets the value of the specified environment variable. An
     * environment variable is a system-dependent external named
     * value.
     *
     * <p>If a security manager exists, its
     * {@link SecurityManager#checkPermission checkPermission}
     * method is called with a
     * <code>{@link RuntimePermission}("getenv."+name)</code>
     * permission.  This may result in a {@link SecurityException}
     * being thrown.  If no exception is thrown the value of the
     * variable <code>name</code> is returned.
     *
     * <p><a name="EnvironmentVSSystemProperties"><i>System
     * properties</i> and <i>environment variables</i></a> are both
     * conceptually mappings between names and values.  Both
     * mechanisms can be used to pass user-defined information to a
     * Java process.  Environment variables have a more global effect,
     * because they are visible to all descendants of the process
     * which defines them, not just the immediate Java subprocess.
     * They can have subtly different semantics, such as case
     * insensitivity, on different operating systems.  For these
     * reasons, environment variables are more likely to have
     * unintended side effects.  It is best to use system properties
     * where possible.  Environment variables should be used when a
     * global effect is desired, or when an external system interface
     * requires an environment variable (such as <code>PATH</code>).
     *
     * <p>On UNIX systems the alphabetic case of <code>name</code> is
     * typically significant, while on Microsoft Windows systems it is
     * typically not.  For example, the expression
     * <code>System.getenv("FOO").equals(System.getenv("foo"))</code>
     * is likely to be true on Microsoft Windows.
     *
     * @param  name the name of the environment variable
     * @return the string value of the variable, or <code>null</code>
     *         if the variable is not defined in the system environment
     * @throws NullPointerException if <code>name</code> is <code>null</code>
     * @throws SecurityException
     *         if a security manager exists and its
     *         {@link SecurityManager#checkPermission checkPermission}
     *         method doesn't allow access to the environment variable
     *         <code>name</code>
     * @see    #getenv()
     * @see    ProcessBuilder#environment()
     */
    public static String getenv(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        return Libcore.os.getenv(name);
    }


    /**
     * Returns an unmodifiable string map view of the current system environment.
     * The environment is a system-dependent mapping from names to
     * values which is passed from parent to child processes.
     *
     * <p>If the system does not support environment variables, an
     * empty map is returned.
     *
     * <p>The returned map will never contain null keys or values.
     * Attempting to query the presence of a null key or value will
     * throw a {@link NullPointerException}.  Attempting to query
     * the presence of a key or value which is not of type
     * {@link String} will throw a {@link ClassCastException}.
     *
     * <p>The returned map and its collection views may not obey the
     * general contract of the {@link Object#equals} and
     * {@link Object#hashCode} methods.
     *
     * <p>The returned map is typically case-sensitive on all platforms.
     *
     * <p>If a security manager exists, its
     * {@link SecurityManager#checkPermission checkPermission}
     * method is called with a
     * <code>{@link RuntimePermission}("getenv.*")</code>
     * permission.  This may result in a {@link SecurityException} being
     * thrown.
     *
     * <p>When passing information to a Java subprocess,
     * <a href=#EnvironmentVSSystemProperties>system properties</a>
     * are generally preferred over environment variables.
     *
     * @return the environment as a map of variable names to values
     * @throws SecurityException
     *         if a security manager exists and its
     *         {@link SecurityManager#checkPermission checkPermission}
     *         method doesn't allow access to the process environment
     * @see    #getenv(String)
     * @see    ProcessBuilder#environment()
     * @since  1.5
     */
    public static java.util.Map<String,String> getenv() {
        return ProcessEnvironment.getenv();
    }

    /**
     * Terminates the currently running Java Virtual Machine. The
     * argument serves as a status code; by convention, a nonzero status
     * code indicates abnormal termination.
     * <p>
     * This method calls the <code>exit</code> method in class
     * <code>Runtime</code>. This method never returns normally.
     * <p>
     * The call <code>System.exit(n)</code> is effectively equivalent to
     * the call:
     * <blockquote><pre>
     * Runtime.getRuntime().exit(n)
     * </pre></blockquote>
     *
     * @param      status   exit status.
     * @throws  SecurityException
     *        if a security manager exists and its <code>checkExit</code>
     *        method doesn't allow exit with the specified status.
     * @see        java.lang.Runtime#exit(int)
     */
    public static void exit(int status) {
        Runtime.getRuntime().exit(status);
    }

    /**
     * Runs the garbage collector.
     * <p>
     * Calling the <code>gc</code> method suggests that the Java Virtual
     * Machine expend effort toward recycling unused objects in order to
     * make the memory they currently occupy available for quick reuse.
     * When control returns from the method call, the Java Virtual
     * Machine has made a best effort to reclaim space from all discarded
     * objects.
     * <p>
     * The call <code>System.gc()</code> is effectively equivalent to the
     * call:
     * <blockquote><pre>
     * Runtime.getRuntime().gc()
     * </pre></blockquote>
     *
     * @see     java.lang.Runtime#gc()
     */
    public static void gc() {
        Runtime.getRuntime().gc();
    }

    /**
     * Runs the finalization methods of any objects pending finalization.
     * <p>
     * Calling this method suggests that the Java Virtual Machine expend
     * effort toward running the <code>finalize</code> methods of objects
     * that have been found to be discarded but whose <code>finalize</code>
     * methods have not yet been run. When control returns from the
     * method call, the Java Virtual Machine has made a best effort to
     * complete all outstanding finalizations.
     * <p>
     * The call <code>System.runFinalization()</code> is effectively
     * equivalent to the call:
     * <blockquote><pre>
     * Runtime.getRuntime().runFinalization()
     * </pre></blockquote>
     *
     * @see     java.lang.Runtime#runFinalization()
     */
    public static void runFinalization() {
        Runtime.getRuntime().runFinalization();
    }

    /**
     * Enable or disable finalization on exit; doing so specifies that the
     * finalizers of all objects that have finalizers that have not yet been
     * automatically invoked are to be run before the Java runtime exits.
     * By default, finalization on exit is disabled.
     *
     * <p>If there is a security manager,
     * its <code>checkExit</code> method is first called
     * with 0 as its argument to ensure the exit is allowed.
     * This could result in a SecurityException.
     *
     * @deprecated  This method is inherently unsafe.  It may result in
     *      finalizers being called on live objects while other threads are
     *      concurrently manipulating those objects, resulting in erratic
     *      behavior or deadlock.
     * @param value indicating enabling or disabling of finalization
     * @throws  SecurityException
     *        if a security manager exists and its <code>checkExit</code>
     *        method doesn't allow the exit.
     *
     * @see     java.lang.Runtime#exit(int)
     * @see     java.lang.Runtime#gc()
     * @see     java.lang.SecurityManager#checkExit(int)
     * @since   JDK1.1
     */
    @Deprecated
    public static void runFinalizersOnExit(boolean value) {
        Runtime.getRuntime().runFinalizersOnExit(value);
    }

    /**
     * Loads a code file with the specified filename from the local file
     * system as a dynamic library. The filename
     * argument must be a complete path name.
     * <p>
     * The call <code>System.load(name)</code> is effectively equivalent
     * to the call:
     * <blockquote><pre>
     * Runtime.getRuntime().load(name)
     * </pre></blockquote>
     *
     * @param      filename   the file to load.
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkLink</code> method doesn't allow
     *             loading of the specified dynamic library
     * @exception  UnsatisfiedLinkError  if the file does not exist.
     * @exception  NullPointerException if <code>filename</code> is
     *             <code>null</code>
     * @see        java.lang.Runtime#load(java.lang.String)
     * @see        java.lang.SecurityManager#checkLink(java.lang.String)
     */
    public static void load(String filename) {
        Runtime.getRuntime().load0(VMStack.getStackClass2(), filename);
    }

    /**
     * Loads the system library specified by the <code>libname</code>
     * argument. The manner in which a library name is mapped to the
     * actual system library is system dependent.
     * <p>
     * The call <code>System.loadLibrary(name)</code> is effectively
     * equivalent to the call
     * <blockquote><pre>
     * Runtime.getRuntime().loadLibrary(name)
     * </pre></blockquote>
     *
     * @param      libname   the name of the library.
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkLink</code> method doesn't allow
     *             loading of the specified dynamic library
     * @exception  UnsatisfiedLinkError  if the library does not exist.
     * @exception  NullPointerException if <code>libname</code> is
     *             <code>null</code>
     * @see        java.lang.Runtime#loadLibrary(java.lang.String)
     * @see        java.lang.SecurityManager#checkLink(java.lang.String)
     */
    public static void loadLibrary(String libname) {
        Runtime.getRuntime().loadLibrary0(VMStack.getCallingClassLoader(), libname);
    }

    /**
     * Maps a library name into a platform-specific string representing
     * a native library.
     *
     * @param      libname the name of the library.
     * @return     a platform-dependent native library name.
     * @exception  NullPointerException if <code>libname</code> is
     *             <code>null</code>
     * @see        java.lang.System#loadLibrary(java.lang.String)
     * @see        java.lang.ClassLoader#findLibrary(java.lang.String)
     * @since      1.2
     */
    public static native String mapLibraryName(String libname);

    /**
     * Initialize the system class.  Called after thread initialization.
     */
    static {
        unchangeableProps = initUnchangeableSystemProperties();
        props = initProperties();
        addLegacyLocaleSystemProperties();

        // TODO: Confirm that this isn't something super important.
        // sun.misc.VM.saveAndRemoveProperties(props);

        lineSeparator = props.getProperty("line.separator");
        sun.misc.Version.init();

        FileInputStream fdIn = new FileInputStream(FileDescriptor.in);
        FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
        FileOutputStream fdErr = new FileOutputStream(FileDescriptor.err);

        in = new BufferedInputStream(fdIn);
        out = new PrintStream(fdOut);
        err = new PrintStream(fdErr);

        // Initialize any miscellenous operating system settings that need to be
        // set for the class libraries. Currently this is no-op everywhere except
        // for Windows where the process-wide error mode is set before the java.io
        // classes are used.
        sun.misc.VM.initializeOSEnvironment();

        // Subsystems that are invoked during initialization can invoke
        // sun.misc.VM.isBooted() in order to avoid doing things that should
        // wait until the application class loader has been set up.
        // IMPORTANT: Ensure that this remains the last initialization action!
        sun.misc.VM.booted();
    }

    /**
     * @hide internal use only
     */
    public static void logE(String message) {
        log('E', message, null);
    }

    /**
     * @hide internal use only
     */
    public static void logE(String message, Throwable th) {
        log('E', message, th);
    }

    /**
     * @hide internal use only
     */
    public static void logI(String message) {
        log('I', message, null);
    }

    /**
     * @hide internal use only
     */
    public static void logI(String message, Throwable th) {
        log('I', message, th);
    }

    /**
     * @hide internal use only
     */
    public static void logW(String message) {
        log('W', message, null);
    }

    /**
     * @hide internal use only
     */
    public static void logW(String message, Throwable th) {
        log('W', message, th);
    }

    private static native void log(char type, String message, Throwable th);
}
