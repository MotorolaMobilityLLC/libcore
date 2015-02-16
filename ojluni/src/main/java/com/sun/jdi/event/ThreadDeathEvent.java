/*
 * Copyright (c) 1998, 1999, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.jdi.event;

import com.sun.jdi.*;

/**
 * Notification of a completed thread in the target VM. The
 * notification is generated by the dying thread before it terminates.
 * Because of this timing, it is possible
 * for {@link VirtualMachine#allThreads} to return this thread
 * after this event is received.
 * <p>
 * Note that this event gives no information
 * about the lifetime of the thread object. It may or may not be collected
 * soon depending on what references exist in the target VM.
 *
 * @see EventQueue
 * @see VirtualMachine
 * @see ThreadReference
 *
 * @author Robert Field
 * @since  1.3
 */
public interface ThreadDeathEvent extends Event {
    /**
     * Returns the thread which is terminating.
     *
     * @return a {@link ThreadReference} which mirrors the event's thread in
     * the target VM.
     */
    public ThreadReference thread();
}
