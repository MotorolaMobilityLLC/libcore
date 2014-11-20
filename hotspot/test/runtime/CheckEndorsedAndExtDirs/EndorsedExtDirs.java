/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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
 * @test
 * @bug 8064667
 * @summary Sanity test for -XX:+CheckEndorsedAndExtDirs
 * @library /testlibrary
 * @run main/othervm -XX:+CheckEndorsedAndExtDirs EndorsedExtDirs
 */

import com.oracle.java.testlibrary.*;
import java.util.ArrayList;
import java.util.List;

public class EndorsedExtDirs {
    static final String cpath = System.getProperty("test.classes", ".");
    public static void main(String arg[]) throws Exception {
        fatalError("-XX:+CheckEndorsedAndExtDirs", "-Djava.endorsed.dirs=foo");
        fatalError("-XX:+CheckEndorsedAndExtDirs", "-Djava.ext.dirs=bar");
    }

    static void fatalError(String... args) throws Exception {
        List<String> commands = new ArrayList<>();
        String java = System.getProperty("java.home") + "/bin/java";
        commands.add(java);
        for (String s : args) {
            commands.add(s);
        }
        commands.add("-cp");
        commands.add(cpath);
        commands.add("EndorsedExtDirs");

        System.out.println("Launching " + commands);
        ProcessBuilder pb = new ProcessBuilder(commands);
        OutputAnalyzer output = new OutputAnalyzer(pb.start());
        output.shouldContain("Could not create the Java Virtual Machine");
        output.shouldHaveExitValue(1);
    }
}
