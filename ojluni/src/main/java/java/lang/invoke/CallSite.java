/*
 * Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.lang.invoke;

// Android-changed: Not using Empty
//import sun.invoke.empty.Empty;
import static java.lang.invoke.MethodHandleStatics.*;
import static java.lang.invoke.MethodHandles.Lookup.IMPL_LOOKUP;

/**
 * A {@code CallSite} is a holder for a variable {@link MethodHandle},
 * which is called its {@code target}.
 * An {@code invokedynamic} instruction linked to a {@code CallSite} delegates
 * all calls to the site's current target.
 * A {@code CallSite} may be associated with several {@code invokedynamic}
 * instructions, or it may be "free floating", associated with none.
 * In any case, it may be invoked through an associated method handle
 * called its {@linkplain #dynamicInvoker dynamic invoker}.
 * <p>
 * {@code CallSite} is an abstract class which does not allow
 * direct subclassing by users.  It has three immediate,
 * concrete subclasses that may be either instantiated or subclassed.
 * <ul>
 * <li>If a mutable target is not required, an {@code invokedynamic} instruction
 * may be permanently bound by means of a {@linkplain ConstantCallSite constant call site}.
 * <li>If a mutable target is required which has volatile variable semantics,
 * because updates to the target must be immediately and reliably witnessed by other threads,
 * a {@linkplain VolatileCallSite volatile call site} may be used.
 * <li>Otherwise, if a mutable target is required,
 * a {@linkplain MutableCallSite mutable call site} may be used.
 * </ul>
 * <p>
 * A non-constant call site may be <em>relinked</em> by changing its target.
 * The new target must have the same {@linkplain MethodHandle#type() type}
 * as the previous target.
 * Thus, though a call site can be relinked to a series of
 * successive targets, it cannot change its type.
 * <p>
 * Here is a sample use of call sites and bootstrap methods which links every
 * dynamic call site to print its arguments:
<blockquote><pre>{@code
static void test() throws Throwable {
    // THE FOLLOWING LINE IS PSEUDOCODE FOR A JVM INSTRUCTION
    InvokeDynamic[#bootstrapDynamic].baz("baz arg", 2, 3.14);
}
private static void printArgs(Object... args) {
  System.out.println(java.util.Arrays.deepToString(args));
}
private static final MethodHandle printArgs;
static {
  MethodHandles.Lookup lookup = MethodHandles.lookup();
  Class thisClass = lookup.lookupClass();  // (who am I?)
  printArgs = lookup.findStatic(thisClass,
      "printArgs", MethodType.methodType(void.class, Object[].class));
}
private static CallSite bootstrapDynamic(MethodHandles.Lookup caller, String name, MethodType type) {
  // ignore caller and name, but match the type:
  return new ConstantCallSite(printArgs.asType(type));
}
}</pre></blockquote>
 * @author John Rose, JSR 292 EG
 */
abstract
public class CallSite {
    // Android-changed: not used.
    // static { MethodHandleImpl.initStatics(); }

    // The actual payload of this call site:
    /*package-private*/
    MethodHandle target;    // Note: This field is known to the JVM.  Do not change.

    /**
     * Make a blank call site object with the given method type.
     * An initial target method is supplied which will throw
     * an {@link IllegalStateException} if called.
     * <p>
     * Before this {@code CallSite} object is returned from a bootstrap method,
     * it is usually provided with a more useful target method,
     * via a call to {@link CallSite#setTarget(MethodHandle) setTarget}.
     * @throws NullPointerException if the proposed type is null
     */
    /*package-private*/
    CallSite(MethodType type) {
        // Android-changed: No cache for these so create uninitializedCallSite target here using
        // method handle transformations to create a method handle that has the expected method
        // type but throws an IllegalStateException.
        // target = makeUninitializedCallSite(type);
        this.target = MethodHandles.throwException(type.returnType(), IllegalStateException.class);
        this.target = MethodHandles.insertArguments(
            this.target, 0, new IllegalStateException("uninitialized call site"));
        if (type.parameterCount() > 0) {
            this.target = MethodHandles.dropArguments(this.target, 0, type.ptypes());
        }

        // Android-changed: Using initializer method for GET_TARGET
        // rather than complex static initializer.
        initializeGetTarget();
    }

    /**
     * Make a call site object equipped with an initial target method handle.
     * @param target the method handle which will be the initial target of the call site
     * @throws NullPointerException if the proposed target is null
     */
    /*package-private*/
    CallSite(MethodHandle target) {
        target.type();  // null check
        this.target = target;

        // Android-changed: Using initializer method for GET_TARGET
        // rather than complex static initializer.
        initializeGetTarget();
    }

    /**
     * Make a call site object equipped with an initial target method handle.
     * @param targetType the desired type of the call site
     * @param createTargetHook a hook which will bind the call site to the target method handle
     * @throws WrongMethodTypeException if the hook cannot be invoked on the required arguments,
     *         or if the target returned by the hook is not of the given {@code targetType}
     * @throws NullPointerException if the hook returns a null value
     * @throws ClassCastException if the hook returns something other than a {@code MethodHandle}
     * @throws Throwable anything else thrown by the hook function
     */
    /*package-private*/
    CallSite(MethodType targetType, MethodHandle createTargetHook) throws Throwable {
        this(targetType);
        ConstantCallSite selfCCS = (ConstantCallSite) this;
        MethodHandle boundTarget = (MethodHandle) createTargetHook.invokeWithArguments(selfCCS);
        checkTargetChange(this.target, boundTarget);
        this.target = boundTarget;

        // Android-changed: Using initializer method for GET_TARGET
        // rather than complex static initializer.
        initializeGetTarget();
    }

    /**
     * Returns the type of this call site's target.
     * Although targets may change, any call site's type is permanent, and can never change to an unequal type.
     * The {@code setTarget} method enforces this invariant by refusing any new target that does
     * not have the previous target's type.
     * @return the type of the current target, which is also the type of any future target
     */
    public MethodType type() {
        // warning:  do not call getTarget here, because CCS.getTarget can throw IllegalStateException
        return target.type();
    }

    /**
     * Returns the target method of the call site, according to the
     * behavior defined by this call site's specific class.
     * The immediate subclasses of {@code CallSite} document the
     * class-specific behaviors of this method.
     *
     * @return the current linkage state of the call site, its target method handle
     * @see ConstantCallSite
     * @see VolatileCallSite
     * @see #setTarget
     * @see ConstantCallSite#getTarget
     * @see MutableCallSite#getTarget
     * @see VolatileCallSite#getTarget
     */
    public abstract MethodHandle getTarget();

    /**
     * Updates the target method of this call site, according to the
     * behavior defined by this call site's specific class.
     * The immediate subclasses of {@code CallSite} document the
     * class-specific behaviors of this method.
     * <p>
     * The type of the new target must be {@linkplain MethodType#equals equal to}
     * the type of the old target.
     *
     * @param newTarget the new target
     * @throws NullPointerException if the proposed new target is null
     * @throws WrongMethodTypeException if the proposed new target
     *         has a method type that differs from the previous target
     * @see CallSite#getTarget
     * @see ConstantCallSite#setTarget
     * @see MutableCallSite#setTarget
     * @see VolatileCallSite#setTarget
     */
    public abstract void setTarget(MethodHandle newTarget);

    void checkTargetChange(MethodHandle oldTarget, MethodHandle newTarget) {
        MethodType oldType = oldTarget.type();
        MethodType newType = newTarget.type();  // null check!
        if (!newType.equals(oldType))
            throw wrongTargetType(newTarget, oldType);
    }

    private static WrongMethodTypeException wrongTargetType(MethodHandle target, MethodType type) {
        return new WrongMethodTypeException(String.valueOf(target)+" should be of type "+type);
    }

    /**
     * Produces a method handle equivalent to an invokedynamic instruction
     * which has been linked to this call site.
     * <p>
     * This method is equivalent to the following code:
     * <blockquote><pre>{@code
     * MethodHandle getTarget, invoker, result;
     * getTarget = MethodHandles.publicLookup().bind(this, "getTarget", MethodType.methodType(MethodHandle.class));
     * invoker = MethodHandles.exactInvoker(this.type());
     * result = MethodHandles.foldArguments(invoker, getTarget)
     * }</pre></blockquote>
     *
     * @return a method handle which always invokes this call site's current target
     */
    public abstract MethodHandle dynamicInvoker();

    /*non-public*/ MethodHandle makeDynamicInvoker() {
        // Android-changed: Use bindTo() rather than bindArgumentL() (not implemented).
        MethodHandle getTarget = GET_TARGET.bindTo(this);
        MethodHandle invoker = MethodHandles.exactInvoker(this.type());
        return MethodHandles.foldArguments(invoker, getTarget);
    }

    // Android-changed: no longer final. GET_TARGET assigned in initializeGetTarget().
    private static MethodHandle GET_TARGET = null;

    private void initializeGetTarget() {
        // Android-changed: moved from static initializer for
        // GET_TARGET to avoid issues with running early. Called from
        // constructors. CallSite creation is not performance critical.
        synchronized (CallSite.class) {
            if (GET_TARGET == null) {
                try {
                    GET_TARGET = IMPL_LOOKUP.
                            findVirtual(CallSite.class, "getTarget",
                                        MethodType.methodType(MethodHandle.class));
                } catch (ReflectiveOperationException e) {
                    throw new InternalError(e);
                }
            }
        }
    }

    // Android-changed: not used.
    // /** This guy is rolled into the default target if a MethodType is supplied to the constructor. */
    // /*package-private*/
    // static Empty uninitializedCallSite() {
    //     throw new IllegalStateException("uninitialized call site");
    // }

    // unsafe stuff:
    private static final long TARGET_OFFSET;
    static {
        try {
            TARGET_OFFSET = UNSAFE.objectFieldOffset(CallSite.class.getDeclaredField("target"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    /*package-private*/
    void setTargetNormal(MethodHandle newTarget) {
        // Android-changed: Set value directly.
        // MethodHandleNatives.setCallSiteTargetNormal(this, newTarget);
        target = newTarget;
    }
    /*package-private*/
    MethodHandle getTargetVolatile() {
        return (MethodHandle) UNSAFE.getObjectVolatile(this, TARGET_OFFSET);
    }
    /*package-private*/
    void setTargetVolatile(MethodHandle newTarget) {
        // Android-changed: Set value directly.
        // MethodHandleNatives.setCallSiteTargetVolatile(this, newTarget);
        UNSAFE.putObjectVolatile(this, TARGET_OFFSET, newTarget);
    }

    // Android-changed: not used.
    // this implements the upcall from the JVM, MethodHandleNatives.makeDynamicCallSite:
    // static CallSite makeSite(MethodHandle bootstrapMethod,
    //                          // Callee information:
    //                          String name, MethodType type,
    //                          // Extra arguments for BSM, if any:
    //                          Object info,
    //                          // Caller information:
    //                          Class<?> callerClass) {
    //     MethodHandles.Lookup caller = IMPL_LOOKUP.in(callerClass);
    //     CallSite site;
    //     try {
    //         Object binding;
    //         info = maybeReBox(info);
    //         if (info == null) {
    //             binding = bootstrapMethod.invoke(caller, name, type);
    //         } else if (!info.getClass().isArray()) {
    //             binding = bootstrapMethod.invoke(caller, name, type, info);
    //         } else {
    //             Object[] argv = (Object[]) info;
    //             maybeReBoxElements(argv);
    //             switch (argv.length) {
    //             case 0:
    //                 binding = bootstrapMethod.invoke(caller, name, type);
    //                 break;
    //             case 1:
    //                 binding = bootstrapMethod.invoke(caller, name, type,
    //                                                  argv[0]);
    //                 break;
    //             case 2:
    //                 binding = bootstrapMethod.invoke(caller, name, type,
    //                                                  argv[0], argv[1]);
    //                 break;
    //             case 3:
    //                 binding = bootstrapMethod.invoke(caller, name, type,
    //                                                  argv[0], argv[1], argv[2]);
    //                 break;
    //             case 4:
    //                 binding = bootstrapMethod.invoke(caller, name, type,
    //                                                  argv[0], argv[1], argv[2], argv[3]);
    //                 break;
    //             case 5:
    //                 binding = bootstrapMethod.invoke(caller, name, type,
    //                                                  argv[0], argv[1], argv[2], argv[3], argv[4]);
    //                 break;
    //             case 6:
    //                 binding = bootstrapMethod.invoke(caller, name, type,
    //                                                  argv[0], argv[1], argv[2], argv[3], argv[4], argv[5]);
    //                 break;
    //             default:
    //                 final int NON_SPREAD_ARG_COUNT = 3;  // (caller, name, type)
    //                 if (NON_SPREAD_ARG_COUNT + argv.length > MethodType.MAX_MH_ARITY)
    //                     throw new BootstrapMethodError("too many bootstrap method arguments");
    //                 MethodType bsmType = bootstrapMethod.type();
    //                 MethodType invocationType = MethodType.genericMethodType(NON_SPREAD_ARG_COUNT + argv.length);
    //                 MethodHandle typedBSM = bootstrapMethod.asType(invocationType);
    //                 MethodHandle spreader = invocationType.invokers().spreadInvoker(NON_SPREAD_ARG_COUNT);
    //                 binding = spreader.invokeExact(typedBSM, (Object)caller, (Object)name, (Object)type, argv);
    //             }
    //         }
    //         //System.out.println("BSM for "+name+type+" => "+binding);
    //         if (binding instanceof CallSite) {
    //             site = (CallSite) binding;
    //         }  else {
    //             throw new ClassCastException("bootstrap method failed to produce a CallSite");
    //         }
    //         if (!site.getTarget().type().equals(type))
    //             throw wrongTargetType(site.getTarget(), type);
    //     } catch (Throwable ex) {
    //         BootstrapMethodError bex;
    //         if (ex instanceof BootstrapMethodError)
    //             bex = (BootstrapMethodError) ex;
    //         else
    //             bex = new BootstrapMethodError("call site initialization exception", ex);
    //         throw bex;
    //     }
    //     return site;
    // }

    // private static Object maybeReBox(Object x) {
    //     if (x instanceof Integer) {
    //         int xi = (int) x;
    //         if (xi == (byte) xi)
    //             x = xi;  // must rebox; see JLS 5.1.7
    //     }
    //     return x;
    // }
    // private static void maybeReBoxElements(Object[] xa) {
    //     for (int i = 0; i < xa.length; i++) {
    //         xa[i] = maybeReBox(xa[i]);
    //     }
    // }
}
