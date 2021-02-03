/*
 * Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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
 *
 */

package sun.jvm.hotspot.tools.soql;

import sun.jvm.hotspot.debugger.JVMDebugger;
import sun.jvm.hotspot.tools.*;
import sun.jvm.hotspot.utilities.*;
import sun.jvm.hotspot.utilities.soql.*;

/** This is command line JavaScript debugger console */
public class JSDB extends Tool {

    public JSDB() {
        super();
    }

    public JSDB(JVMDebugger d) {
        super(d);
    }

    public static void main(String[] args) {
        JSDB jsdb = new JSDB();
        jsdb.execute(args);
    }

    public void run() {
        JSJavaScriptEngine engine = new JSJavaScriptEngine() {
                private ObjectReader objReader = new ObjectReader();
                private JSJavaFactory factory = new JSJavaFactoryImpl();

                public ObjectReader getObjectReader() {
                    return objReader;
                }

                public JSJavaFactory getJSJavaFactory() {
                    return factory;
                }
            };
        engine.startConsole();
    }
}
