/*
 * Copyright (c) 2000, 2011, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.types.basic;

import java.util.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.types.*;
import sun.jvm.hotspot.runtime.VM;

/** <P> This is a basic implementation of the TypeDataBase interface.
    It allows an external type database builder to add types to be
    consumed by a client through the Type interfaces. It has no
    knowledge of symbol lookup; for example, the builder is
    responsible for providing the addresses of static fields. </P>

    <P> Among other things, the database builder is responsible for
    providing the Types for the Java primitive types, as well as their
    sizes. </P>
*/

public class BasicTypeDataBase implements TypeDataBase {
  private MachineDescription machDesc;
  private VtblAccess vtblAccess;
  /** Maps strings to Type objects. This does not contain the primitive types. */
  private Map nameToTypeMap = new HashMap();
  /** Maps strings to Integers, used for enums, etc. */
  private Map nameToIntConstantMap = new HashMap();
  /** Maps strings to Longs, used for 32/64-bit constants, etc. */
  private Map nameToLongConstantMap = new HashMap();
  /** Primitive types. */
  private Type jbooleanType;
  private Type jbyteType;
  private Type jcharType;
  private Type jdoubleType;
  private Type jfloatType;
  private Type jintType;
  private Type jlongType;
  private Type jshortType;

  /** For debugging */
  private static final boolean DEBUG;
  static {
    DEBUG = System.getProperty("sun.jvm.hotspot.types.basic.BasicTypeDataBase.DEBUG") != null;
  }

  public BasicTypeDataBase(MachineDescription machDesc, VtblAccess vtblAccess) {
    this.machDesc   = machDesc;
    this.vtblAccess = vtblAccess;
  }

  public Type lookupType(String cTypeName) {
    return lookupType(cTypeName, true);
  }

  public Type lookupType(String cTypeName, boolean throwException) {
    Type type = (Type) nameToTypeMap.get(cTypeName);
    if (type == null && throwException) {
      throw new RuntimeException("No type named \"" + cTypeName + "\" in database");
    }
    return type;
  }

  public Integer lookupIntConstant(String constantName) {
    return lookupIntConstant(constantName, true);
  }

  public Integer lookupIntConstant(String constantName, boolean throwException) {
    Integer i = (Integer) nameToIntConstantMap.get(constantName);
    if (i == null) {
      if (throwException) {
        throw new RuntimeException("No integer constant named \"" + constantName + "\" present in type database");
      }
    }
    return i;
  }

  public Long lookupLongConstant(String constantName) {
    return lookupLongConstant(constantName, true);
  }

  public Long lookupLongConstant(String constantName, boolean throwException) {
    Long i = (Long) nameToLongConstantMap.get(constantName);
    if (i == null) {
      if (throwException) {
        throw new RuntimeException("No long constant named \"" + constantName + "\" present in type database");
      }
    }
    return i;
  }

  public Type getJBooleanType() {
    return jbooleanType;
  }

  public Type getJByteType() {
    return jbyteType;
  }

  public Type getJCharType() {
    return jcharType;
  }

  public Type getJDoubleType() {
    return jdoubleType;
  }

  public Type getJFloatType() {
    return jfloatType;
  }

  public Type getJIntType() {
    return jintType;
  }

  public Type getJLongType() {
    return jlongType;
  }

  public Type getJShortType() {
    return jshortType;
  }

  public long getAddressSize() {
    return machDesc.getAddressSize();
  }

  public long getOopSize() {
    return VM.getVM().getOopSize();
  }

  static HashMap typeToVtbl = new HashMap();

  private Address vtblForType(Type type) {
    Address vtblAddr = (Address)typeToVtbl.get(type);
    if (vtblAddr == null) {
      vtblAddr = vtblAccess.getVtblForType(type);
      if (vtblAddr != null) {
        typeToVtbl.put(type, vtblAddr);
      }
    }
    return vtblAddr;
  }

  public boolean addressTypeIsEqualToType(Address addr, Type type) {
    if (addr == null) {
      return false;
    }

    // This implementation should be suitably platform-independent; we
    // search nearby memory for the vtbl value of the given type.

    Address vtblAddr = vtblForType(type);

    if (vtblAddr == null) {
      // Type was not polymorphic, or an error occurred during lookup
      if (DEBUG) {
        System.err.println("BasicTypeDataBase.addressTypeIsEqualToType: vtblAddr == null");
      }

      return false;
    }

    // The first implementation searched three locations for this vtbl
    // value; scanning through the entire object was considered, but
    // we thought we knew where we were looking, and looking only in
    // these specific locations should reduce the probability of
    // mistaking random bits as a pointer (although, realistically
    // speaking, the likelihood of finding a match between the bit
    // pattern of, for example, a double and the vtbl is vanishingly
    // small.)
    //    1. The first word of the object (should handle MSVC++ as
    //    well as the SparcWorks compilers with compatibility set to
    //    v5.0 or greater)
    //    2. and 3. The last two Address-aligned words of the part of
    //    the object defined by its topmost polymorphic superclass.
    //    This should handle the SparcWorks compilers, v4.2 or
    //    earlier, as well as any other compilers which place the vptr
    //    at the end of the user-defined fields of the first base
    //    class with virtual functions.
    //
    // Unfortunately this algorithm did not work properly for the
    // specific case of the ThreadShadow/Thread inheritance situation,
    // because the Solaris compiler seems to cleverly eliminate the
    // vtbl for ThreadShadow since the only virtual is empty. (We
    // should get rid of the ThreadShadow and fix the include
    // databases, but need to postpone this for the present.) The
    // current solution performs the three-location check for this
    // class and all of its known superclasses rather than just the
    // topmost polymorphic one.

    Type curType = type;

    try {
      while (curType != null) {
        // Using the size information we have for this type, check the
        // three locations described above.

        // (1)
        if (vtblAddr.equals(addr.getAddressAt(0))) {
          return true;
        }

        // (2)
        long offset = curType.getSize();
        // I don't think this should be misaligned under any
        // circumstances, but I'm not sure (FIXME: also not sure which
        // way to go here, up or down -- assuming down)
        offset -= (offset % getAddressSize());
        if (offset <= 0) {
          return false;
        }
        if (vtblAddr.equals(addr.getAddressAt(offset))) {
          return true;
        }
        offset -= getAddressSize();
        if (offset <= 0) {
          return false;
        }
        if (vtblAddr.equals(addr.getAddressAt(offset))) {
          return true;
        }

        curType = curType.getSuperclass();
      }
    }
    catch (Exception e) {
      // Any UnmappedAddressExceptions, etc. are a good indication
      // that the pointer is not of the specified type
      if (DEBUG) {
        System.err.println("BasicTypeDataBase.addressTypeIsEqualToType: exception occurred during lookup:");
        e.printStackTrace();
      }

      return false;
    }

    if (DEBUG) {
      System.err.println("BasicTypeDataBase.addressTypeIsEqualToType: all vptr tests failed for type " +
                         type.getName());
    }

    return false;
  }

  public Type findDynamicTypeForAddress(Address addr, Type baseType) {
    // This implementation should be suitably platform-independent; we
    // search nearby memory for the vtbl value of the given type.

    if (vtblForType(baseType) == null) {
      // Type was not polymorphic which is an error of some sort
      throw new InternalError(baseType + " does not appear to be polymorphic");
    }

    // This is a more restricted version of guessTypeForAddress since
    // that function has some limitations since it doesn't really know
    // where in the hierarchy a virtual type starts and just poking
    // around in memory is likely to trip over some vtable address,
    // resulting in false positives.  Eventually all uses should
    // switch to this logic but in the interests of stability it will
    // be separate for the moment.

    // Assuming that the base type is truly the first polymorphic type
    // then the vtbl for all subclasss should be at several defined
    // locations so only those locations will be checked.  It's also
    // required that the caller knows that the static type is at least
    // baseType.  See the notes in guessTypeForAddress for the logic of
    // the locations searched.

    Address loc1 = addr.getAddressAt(0);
    Address loc2 = null;
    Address loc3 = null;
    long offset2 = baseType.getSize();
    // I don't think this should be misaligned under any
    // circumstances, but I'm not sure (FIXME: also not sure which
    // way to go here, up or down -- assuming down)
    offset2 = offset2 - (offset2 % getAddressSize()) - getAddressSize();
    if (offset2 > 0) {
      loc2 = addr.getAddressAt(offset2);
    }
    long offset3 = offset2 - getAddressSize();
    if (offset3 > 0) {
      loc3 = addr.getAddressAt(offset3);
    }

    Type loc2Match = null;
    Type loc3Match = null;
    for (Iterator iter = getTypes(); iter.hasNext(); ) {
      Type type = (Type) iter.next();
      Type superClass = type;
      while (superClass != baseType && superClass != null) {
        superClass = superClass.getSuperclass();
      }
      if (superClass == null) continue;
      Address vtblAddr = vtblForType(type);
      if (vtblAddr == null) {
        // This occurs sometimes for intermediate types that are never
        // instantiated.
        if (DEBUG) {
          System.err.println("null vtbl for " + type);
        }
        continue;
      }
      // Prefer loc1 match
      if (vtblAddr.equals(loc1)) return type;
      if (loc2 != null && loc2Match == null && vtblAddr.equals(loc2)) {
          loc2Match = type;
      }
      if (loc3 != null && loc3Match == null && vtblAddr.equals(loc3)) {
          loc3Match = type;
      }
    }
    if (loc2Match != null) return loc2Match;
    if (loc3Match != null) return loc3Match;
    return null;
  }

  public Type guessTypeForAddress(Address addr) {
    for (Iterator iter = getTypes(); iter.hasNext(); ) {
      Type t = (Type) iter.next();
      if (addressTypeIsEqualToType(addr, t)) {
        return t;
      }
    }
    return null;
  }

  public long cIntegerTypeMaxValue(long sizeInBytes, boolean isUnsigned) {
    return machDesc.cIntegerTypeMaxValue(sizeInBytes, isUnsigned);
  }

  public long cIntegerTypeMinValue(long sizeInBytes, boolean isUnsigned) {
    return machDesc.cIntegerTypeMinValue(sizeInBytes, isUnsigned);
  }

  public Iterator getTypes() {
    return nameToTypeMap.values().iterator();
  }

  public Iterator getIntConstants() {
    return nameToIntConstantMap.keySet().iterator();
  }

  public Iterator getLongConstants() {
    return nameToLongConstantMap.keySet().iterator();
  }

  //--------------------------------------------------------------------------------
  // Public routines only for use by the database builder
  //

  /** This method should only be called by the builder of the
      TypeDataBase and at most once */
  public void setJBooleanType(Type type) {
    jbooleanType = type;
  }

  /** This method should only be called by the builder of the
      TypeDataBase and at most once */
  public void setJByteType(Type type) {
    jbyteType = type;
  }

  /** This method should only be called by the builder of the
      TypeDataBase and at most once */
  public void setJCharType(Type type) {
    jcharType = type;
  }

  /** This method should only be called by the builder of the
      TypeDataBase and at most once */
  public void setJDoubleType(Type type) {
    jdoubleType = type;
  }

  /** This method should only be called by the builder of the
      TypeDataBase and at most once */
  public void setJFloatType(Type type) {
    jfloatType = type;
  }

  /** This method should only be called by the builder of the
      TypeDataBase and at most once */
  public void setJIntType(Type type) {
    jintType = type;
  }

  /** This method should only be called by the builder of the
      TypeDataBase and at most once */
  public void setJLongType(Type type) {
    jlongType = type;
  }

  /** This method should only be called by the builder of the
      TypeDataBase and at most once */
  public void setJShortType(Type type) {
    jshortType = type;
  }

  /** This method should only be used by the builder of the
      TypeDataBase. Throws a RuntimeException if a class with this
      name was already present. */
  public void addType(Type type) {
    if (nameToTypeMap.get(type.getName()) != null) {
      throw new RuntimeException("type of name \"" + type.getName() + "\" already present");
    }

    nameToTypeMap.put(type.getName(), type);
  }

  /** This method should only be used by the builder of the
      TypeDataBase. Throws a RuntimeException if this class was not
      present. */
  public void removeType(Type type) {
    Type curType = (Type) nameToTypeMap.get(type.getName());
    if (curType == null) {
      throw new RuntimeException("type of name \"" + type.getName() + "\" not present");
    }

    if (!curType.equals(type)) {
      throw new RuntimeException("a different type of name \"" + type.getName() + "\" was present");
    }

    nameToTypeMap.remove(type.getName());
  }

  /** This method should only be used by the builder of the
      TypeDataBase. Throws a RuntimeException if an integer constant
      with this name was already present. */
  public void addIntConstant(String name, int value) {
    if (nameToIntConstantMap.get(name) != null) {
      throw new RuntimeException("int constant of name \"" + name + "\" already present");
    }

    nameToIntConstantMap.put(name, new Integer(value));
  }

  /** This method should only be used by the builder of the
      TypeDataBase. Throws a RuntimeException if an integer constant
      with this name was not present. */
  public void removeIntConstant(String name) {
    Integer curConstant = (Integer) nameToIntConstantMap.get(name);
    if (curConstant == null) {
      throw new RuntimeException("int constant of name \"" + name + "\" not present");
    }

    nameToIntConstantMap.remove(name);
  }

  /** This method should only be used by the builder of the
      TypeDataBase. Throws a RuntimeException if a long constant with
      this name was already present. */
  public void addLongConstant(String name, long value) {
    if (nameToLongConstantMap.get(name) != null) {
      throw new RuntimeException("long constant of name \"" + name + "\" already present");
    }

    nameToLongConstantMap.put(name, new Long(value));
  }

  /** This method should only be used by the builder of the
      TypeDataBase. Throws a RuntimeException if a long constant with
      this name was not present. */
  public void removeLongConstant(String name) {
    Long curConstant = (Long) nameToLongConstantMap.get(name);
    if (curConstant == null) {
      throw new RuntimeException("long constant of name \"" + name + "\" not present");
    }

    nameToLongConstantMap.remove(name);
  }
}
