/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
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

/*
 * Copyright (c) 2013, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tck.java.time.chrono.serial;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import java.time.chrono.Chronology;
import java.time.chrono.HijrahChronology;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.MinguoChronology;
import java.time.chrono.ThaiBuddhistChronology;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import tck.java.time.AbstractTCKTest;

@Test
public class TCKChronologySerialization extends AbstractTCKTest {

    static final int CHRONO_TYPE = 1;            // java.time.chrono.Ser.CHRONO_TYPE

    //-----------------------------------------------------------------------
    // Regular data factory for available calendars
    //-----------------------------------------------------------------------
    @DataProvider(name = "calendars")
    Chronology[][] data_of_calendars() {
        return new Chronology[][]{
                    {HijrahChronology.INSTANCE},
                    {IsoChronology.INSTANCE},
                    {JapaneseChronology.INSTANCE},
                    {MinguoChronology.INSTANCE},
                    {ThaiBuddhistChronology.INSTANCE}};
    }

    //-----------------------------------------------------------------------
    // Test Serialization of Calendars
    //-----------------------------------------------------------------------
    @Test(dataProvider="calendars")
    public void test_chronoSerialization(Chronology chrono) throws Exception {
        assertSerializable(chrono);
    }

    //-----------------------------------------------------------------------
    // Test that serialization produces exact sequence of bytes
    //-----------------------------------------------------------------------
    @Test(dataProvider="calendars")
    private void test_serializationBytes(Chronology chrono) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos) ) {
            dos.writeByte(CHRONO_TYPE);
            dos.writeUTF(chrono.getId());
        }
        byte[] bytes = baos.toByteArray();
        assertSerializedBySer(chrono, bytes);
    }


    //-----------------------------------------------------------------------
    // Regular data factory for names and descriptions of available calendars
    //-----------------------------------------------------------------------
    @DataProvider(name = "invalidSerialformClasses")
    Object[][] invalid_serial_classes() {
        return new Object[][]{
            {IsoChronology.class},
            {JapaneseChronology.class},
            {MinguoChronology.class},
            {ThaiBuddhistChronology.class},
            {HijrahChronology.class},
        };
    }

    @Test(dataProvider="invalidSerialformClasses")
    public void test_invalid_serialform(Class<?> clazz) throws Exception {
        assertNotSerializable(clazz);
    }

}
