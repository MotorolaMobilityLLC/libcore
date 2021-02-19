/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
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
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 */

package sun.jvm.hotspot.ci;

import java.lang.reflect.Constructor;
import java.util.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.utilities.*;
import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.types.*;

public class ciObjectFactory extends VMObject {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type      = db.lookupType("ciObjectFactory");
    unloadedMethodsField = type.getAddressField("_unloaded_methods");
    ciObjectsField = type.getAddressField("_ci_objects");
    symbolsField = type.getAddressField("_symbols");

    ciObjectConstructor = new VirtualBaseConstructor<ciObject>(db, db.lookupType("ciObject"), "sun.jvm.hotspot.ci", ciObject.class);
    ciSymbolConstructor = new VirtualBaseConstructor<ciSymbol>(db, db.lookupType("ciSymbol"), "sun.jvm.hotspot.ci", ciSymbol.class);
  }

  private static AddressField unloadedMethodsField;
  private static AddressField ciObjectsField;
  private static AddressField symbolsField;

  private static VirtualBaseConstructor<ciObject> ciObjectConstructor;
  private static VirtualBaseConstructor<ciSymbol> ciSymbolConstructor;

  public static ciObject get(Address addr) {
    if (addr == null) return null;

    return (ciObject)ciObjectConstructor.instantiateWrapperFor(addr);
  }

  public GrowableArray<ciObject> objects() {
    return GrowableArray.create(ciObjectsField.getValue(getAddress()), ciObjectConstructor);
  }

  public GrowableArray<ciSymbol> symbols() {
    return GrowableArray.create(symbolsField.getValue(getAddress()), ciSymbolConstructor);
  }

  public ciObjectFactory(Address addr) {
    super(addr);
  }
}
