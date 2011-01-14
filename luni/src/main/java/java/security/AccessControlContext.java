/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.security;

import java.util.ArrayList;
import org.apache.harmony.security.fortress.PolicyUtils;

/**
 * {@code AccessControlContext} encapsulates the {@code ProtectionDomain}s on
 * which access control decisions are based.
 */
public final class AccessControlContext {

    // List of ProtectionDomains wrapped by the AccessControlContext
    // It has the following characteristics:
    //     - 'context' can not be null
    //     - never contains null(s)
    //     - all elements are unique (no dups)
    ProtectionDomain[] context;

    DomainCombiner combiner;

    // An AccessControlContext inherited by the current thread from its parent
    private AccessControlContext inherited;

    /**
     * Constructs a new instance of {@code AccessControlContext} with the
     * specified {@code AccessControlContext} and {@code DomainCombiner}.
     *
     * @param acc
     *            the {@code AccessControlContext} related to the given {@code
     *            DomainCombiner}
     * @param combiner
     *            the {@code DomainCombiner} related to the given {@code
     *            AccessControlContext}
     * @throws NullPointerException
     *             if {@code acc} is {@code null}
     */
    public AccessControlContext(AccessControlContext acc, DomainCombiner combiner) {
        // no need to clone() here as ACC is immutable
        this.context = acc.context;
        this.combiner = combiner;
    }

    /**
     * Constructs a new instance of {@code AccessControlContext} with the
     * specified array of {@code ProtectionDomain}s.
     *
     * @param context
     *            the {@code ProtectionDomain}s that are used to perform access
     *            checks in the context of this {@code AccessControlContext}
     * @throws NullPointerException
     *             if {@code context} is {@code null}
     */
    public AccessControlContext(ProtectionDomain[] context) {
        if (context == null) {
            throw new NullPointerException("context can not be null");
        }
        if (context.length != 0) {
            // remove dup entries
            ArrayList<ProtectionDomain> a = new ArrayList<ProtectionDomain>();
            for (int i = 0; i < context.length; i++) {
                if (context[i] != null && !a.contains(context[i])) {
                    a.add(context[i]);
                }
            }
            if (a.size() != 0) {
                this.context = new ProtectionDomain[a.size()];
                a.toArray(this.context);
            }
        }
        if (this.context == null) {
            // Prevent numerous checks for 'context==null'
            this.context = new ProtectionDomain[0];
        }
    }

    /**
     * Checks the specified permission against the vm's current security policy.
     * The check is based on this {@code AccessControlContext} as opposed to the
     * {@link AccessController#checkPermission(Permission)} method which
     * performs access checks based on the context of the current thread. This
     * method returns silently if the permission is granted, otherwise an
     * {@code AccessControlException} is thrown.
     * <p>
     * A permission is considered granted if every {@link ProtectionDomain} in
     * this context has been granted the specified permission.
     * <p>
     * If privileged operations are on the call stack, only the {@code
     * ProtectionDomain}s from the last privileged operation are taken into
     * account.
     * <p>
     * If inherited methods are on the call stack, the protection domains of the
     * declaring classes are checked, not the protection domains of the classes
     * on which the method is invoked.
     *
     * @param perm
     *            the permission to check against the policy
     * @throws AccessControlException
     *             if the specified permission is not granted
     * @throws NullPointerException
     *             if the specified permission is {@code null}
     * @see AccessController#checkPermission(Permission)
     */
    public void checkPermission(Permission perm) throws AccessControlException {
        if (perm == null) {
            throw new NullPointerException("Permission cannot be null");
        }
        for (int i = 0; i < context.length; i++) {
            if (!context[i].implies(perm)) {
                throw new AccessControlException("Permission check failed " + perm, perm);
            }
        }
        if (inherited != null) {
            inherited.checkPermission(perm);
        }
    }


    /**
     * Compares the specified object with this {@code AccessControlContext} for
     * equality. Returns {@code true} if the specified object is also an
     * instance of {@code AccessControlContext}, and the two contexts
     * encapsulate the same {@code ProtectionDomain}s. The order of the {@code
     * ProtectionDomain}s is ignored by this method.
     *
     * @param obj
     *            object to be compared for equality with this {@code
     *            AccessControlContext}
     * @return {@code true} if the specified object is equal to this {@code
     *         AccessControlContext}, otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AccessControlContext) {
            AccessControlContext that = (AccessControlContext) obj;
            if (!(PolicyUtils.matchSubset(context, that.context) && PolicyUtils
                    .matchSubset(that.context, context))) {
                return false;
            }
            if (combiner != null) {
                return combiner.equals(that.combiner);
            }
            return that.combiner == null;
        }
        return false;
    }

    /**
     * Returns the {@code DomainCombiner} associated with this {@code
     * AccessControlContext}.
     *
     * @return the {@code DomainCombiner} associated with this {@code
     *         AccessControlContext}
     */
    public DomainCombiner getDomainCombiner() {
        return combiner;
    }


    /**
     * Returns the hash code value for this {@code AccessControlContext}.
     * Returns the same hash code for {@code AccessControlContext}s that are
     * equal to each other as required by the general contract of
     * {@link Object#hashCode}.
     *
     * @return the hash code value for this {@code AccessControlContext}
     * @see Object#equals(Object)
     * @see AccessControlContext#equals(Object)
     */
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < context.length; i++) {
            hash ^= context[i].hashCode();
        }
        return hash;
    }

}
