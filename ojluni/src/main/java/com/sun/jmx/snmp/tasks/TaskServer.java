// NPCTE fix for bugId 4510777, esc 532372, MR October 2001
// file TaskServer.java created for this bug fix

/*
 * Copyright (c) 2002, 2003, Oracle and/or its affiliates. All rights reserved.
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


package com.sun.jmx.snmp.tasks;

/**
 * This interface is implemented by objects that are able to execute
 * tasks. Whether the task is executed in the client thread or in another
 * thread depends on the TaskServer implementation.
 *
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 * @see com.sun.jmx.snmp.tasks.Task
 *
 * @since 1.5
 **/
public interface TaskServer {
    /**
     * Submit a task to be executed.
     * Once a task is submitted, it is guaranteed that either
     * {@link com.sun.jmx.snmp.tasks.Task#run() task.run()} or
     * {@link com.sun.jmx.snmp.tasks.Task#cancel() task.cancel()} will be called.
     * <p>Whether the task is executed in the client thread (e.g.
     * <code>public void submitTask(Task task) { task.run(); }</code>) or in
     * another thread (e.g. <code>
     * public void submitTask(Task task) { new Thrad(task).start(); }</code>)
     * depends on the TaskServer implementation.
     * @param task The task to be executed.
     **/
    public void submitTask(Task task);
}
