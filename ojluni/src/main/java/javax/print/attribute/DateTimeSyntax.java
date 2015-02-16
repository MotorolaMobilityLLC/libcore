/*
 * Copyright (c) 2000, 2006, Oracle and/or its affiliates. All rights reserved.
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


package javax.print.attribute;

import java.io.Serializable;

import java.util.Date;

/**
 * Class DateTimeSyntax is an abstract base class providing the common
 * implementation of all attributes whose value is a date and time.
 * <P>
 * Under the hood, a date-time attribute is stored as a value of class <code>
 * java.util.Date</code>. You can get a date-time attribute's Date value by
 * calling {@link #getValue() <CODE>getValue()</CODE>}. A date-time attribute's
 * Date value is established when it is constructed (see {@link
 * #DateTimeSyntax(Date) <CODE>DateTimeSyntax(Date)</CODE>}). Once
 * constructed, a date-time attribute's value is immutable.
 * <P>
 * To construct a date-time attribute from separate values of the year, month,
 * day, hour, minute, and so on, use a <code>java.util.Calendar</code>
 * object to construct a <code>java.util.Date</code> object, then use the
 * <code>java.util.Date</code> object to construct the date-time attribute.
 * To convert
 * a date-time attribute to separate values of the year, month, day, hour,
 * minute, and so on, create a <code>java.util.Calendar</code> object and
 * set it to the <code>java.util.Date</code> from the date-time attribute. Class
 * DateTimeSyntax stores its value in the form of a <code>java.util.Date
 * </code>
 * rather than a <code>java.util.Calendar</code> because it typically takes
 * less memory to store and less time to compare a <code>java.util.Date</code>
 * than a <code>java.util.Calendar</code>.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public abstract class DateTimeSyntax implements Serializable, Cloneable {

    private static final long serialVersionUID = -1400819079791208582L;

    // Hidden data members.

    /**
     * This date-time attribute's<code>java.util.Date</code> value.
     * @serial
     */
    private Date value;

    // Hidden constructors.

    /**
     * Construct a new date-time attribute with the given
     * <code>java.util.Date </code> value.
     *
     * @param  value   <code>java.util.Date</code> value.
     *
     * @exception  NullPointerException
     *     (unchecked exception) Thrown if <CODE>theValue</CODE> is null.
     */
    protected DateTimeSyntax(Date value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        this.value = value;
    }

    // Exported operations.

    /**
     * Returns this date-time attribute's <code>java.util.Date</code>
     * value.
     * @return the Date.
     */
    public Date getValue() {
        return new Date (value.getTime());
    }

    // Exported operations inherited and overridden from class Object.

    /**
     * Returns whether this date-time attribute is equivalent to the passed in
     * object. To be equivalent, all of the following conditions must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class DateTimeSyntax.
     * <LI>
     * This date-time attribute's <code>java.util.Date</code> value and
     * <CODE>object</CODE>'s <code>java.util.Date</code> value are
     * equal. </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this date-time
     *          attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return (object != null &&
                object instanceof DateTimeSyntax &&
                value.equals(((DateTimeSyntax) object).value));
    }

    /**
     * Returns a hash code value for this date-time attribute. The hashcode is
     * that of this attribute's <code>java.util.Date</code> value.
     */
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Returns a string value corresponding to this date-time attribute.
     * The string value is just this attribute's
     * <code>java.util.Date</code>  value
     * converted to a string.
     */
    public String toString() {
        return "" + value;
    }

}
