/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
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
 */

/*
 * @test TestAggressiveHeap
 * @key gc
 * @bug 8179084
 * @summary Test argument processing for -XX:+AggressiveHeap.
 * @library /testlibrary
 * @run driver TestAggressiveHeap
 */

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.oracle.java.testlibrary.OutputAnalyzer;
import com.oracle.java.testlibrary.ProcessTools;

public class TestAggressiveHeap {

    public static void main(String args[]) throws Exception {
        if (canUseAggressiveHeapOption()) {
            testFlag();
        }
    }

    // Note: Not a normal boolean flag; -XX:-AggressiveHeap is invalid.
    private static final String option = "-XX:+AggressiveHeap";

    // Option requires at least 256M, else error during option processing.
    private static final long minMemory = 256 * 1024 * 1024;

    // bool UseParallelGC := true {product}
    private static final String parallelGCPattern =
        " *bool +UseParallelGC *:= *true +\\{product\\}";

    private static void testFlag() throws Exception {
        ProcessBuilder pb = ProcessTools.createJavaProcessBuilder(
            option, "-XX:+PrintFlagsFinal", "-version");

        OutputAnalyzer output = new OutputAnalyzer(pb.start());

        output.shouldHaveExitValue(0);

        String value = output.firstMatch(parallelGCPattern);
        if (value == null) {
            throw new RuntimeException(
                option + " didn't set UseParallelGC");
        }
    }

    private static boolean haveRequiredMemory() throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName os = new ObjectName("java.lang", "type", "OperatingSystem");
        Object attr = server.getAttribute(os, "TotalPhysicalMemorySize");
        String value = attr.toString();
        long memory = Long.parseLong(value);
        return memory >= minMemory;
    }

    private static boolean canUseAggressiveHeapOption() throws Exception {
        if (!haveRequiredMemory()) {
            System.out.println(
                "Skipping test of " + option + " : insufficient memory");
            return false;
        }
        return true;
    }
}

