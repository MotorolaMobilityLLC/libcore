/*
 * Copyright (C) 2009 The Android Open Source Project
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

package dalvik.runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Command line interface for running benchmarks and tests on dalvik.
 */
public final class DalvikRunner {

    private static class Options {

        private final List<File> testFiles = new ArrayList<File>();

        @Option(names = { "--expectations" })
        private Set<File> expectationFiles = new LinkedHashSet<File>();
        {
            expectationFiles.add(new File("dalvik/libcore/tools/runner/expectations.txt"));
        }

        private static String MODE_DEVICE = "device";
        private static String MODE_HOST = "host";
        private static String MODE_ACTIVITY = "activity";
        @Option(names = { "--mode" })
        private String mode = MODE_DEVICE;

        @Option(names = { "--timeout" })
        private long timeoutSeconds = 10 * 60; // default is ten minutes;

        @Option(names = { "--clean" })
        private boolean clean = true;

        @Option(names = { "--xml-reports-directory" })
        private File xmlReportsDirectory;

        @Option(names = { "--verbose" })
        private boolean verbose;

        @Option(names = { "--debug" })
        private Integer debugPort;

        @Option(names = { "--device-runner-dir" })
        private File deviceRunnerDir = new File("/sdcard/dalvikrunner");

        @Option(names = { "--vm-arg" })
        private List<String> vmArgs = new ArrayList<String>();

        @Option(names = { "--java-home" })
        private File javaHome;

        @Option(names = { "--sdk" })
        private File sdkJar = new File("/home/dalvik-prebuild/android-sdk-linux/platforms/android-2.0/android.jar");

        private void printUsage() {
            System.out.println("Usage: DalvikRunner [options]... <tests>...");
            System.out.println();
            System.out.println("  <tests>: a .java file containing a jtreg test, JUnit test,");
            System.out.println("      Caliper benchmark, or a directory of such tests.");
            System.out.println();
            System.out.println("GENERAL OPTIONS");
            System.out.println();
            System.out.println("  --expectations <file>: include the specified file when looking for");
            System.out.println("      test expectations. The file should include qualified test names");
            System.out.println("      and the corresponding expected output.");
            System.out.println("      Default is: " + expectationFiles);
            System.out.println();
            System.out.println("  --mode <device|host|activity>: specify which environment to run the");
            System.out.println("      tests in. Options are on the device VM, on the host VM, and on");
            System.out.println("      device within an android.app.Activity.");
            System.out.println("      Default is: " + mode);
            System.out.println();
            System.out.println("  --clean: remove temporary files (default). Disable with --no-clean");
            System.out.println("      and use with --verbose if you'd like to manually re-run");
            System.out.println("      commands afterwards.");
            System.out.println();
            System.out.println("  --timeout-seconds <seconds>: maximum execution time of each");
            System.out.println("      test before the runner aborts it.");
            System.out.println("      Default is: " + timeoutSeconds);
            System.out.println();
            System.out.println("  --xml-reports-directory <path>: directory to emit JUnit-style");
            System.out.println("      XML test results.");
            System.out.println();
            System.out.println("  --verbose: turn on verbose output");
            System.out.println();
            System.out.println("DEVICE OPTIONS");
            System.out.println();
            System.out.println("  --debug <port>: enable Java debugging on the specified port.");
            System.out.println("      This port must be free both on the device and on the local");
            System.out.println("      system.");
            System.out.println();
            System.out.println("  --device-runner-dir <directory>: use the specified directory for");
            System.out.println("      on-device temporary files and code.");
            System.out.println("      Default is: " + deviceRunnerDir);
            System.out.println();
            System.out.println("GENERAL VM OPTIONS");
            System.out.println();
            System.out.println("  --vm-arg <argument>: include the specified argument when spawning a");
            System.out.println("      virtual machine. Examples: -Xint:fast, -ea, -Xmx16M");
            System.out.println();
            System.out.println("HOST VM OPTIONS");
            System.out.println();
            System.out.println("  --java-home <java_home>: execute the tests on the local workstation");
            System.out.println("      using the specified java home directory. This does not impact");
            System.out.println("      which javac gets used. When unset, java is used from the PATH.");
            System.out.println();
            System.out.println("COMPILE OPTIONS");
            System.out.println();
            System.out.println("  --sdk <android jar>: the API jar file to compile against.");
            System.out.println("      Usually this is <SDK>/platforms/android-<X.X>/android.jar");
            System.out.println("      where <SDK> is the path to an Android SDK path and <X.X> is");
            System.out.println("      a release version like 1.5.");
            System.out.println("      Default is: " + sdkJar);
            System.out.println();
        }

        private boolean parseArgs(String[] args) {
            final List<String> testFilenames;
            try {
                testFilenames = new OptionParser(this).parse(args);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                return false;
            }

            //
            // Semantic error validation
            //

            boolean device;
            boolean vm;
            if (mode.equals(MODE_DEVICE)) {
                device = true;
                vm = true;
            } else if (mode.equals(MODE_HOST)) {
                device = false;
                vm = true;
            } else if (mode.equals(MODE_ACTIVITY)) {
                device = true;
                vm = false;
            } else {
                System.out.println("Unknown mode: " + mode);
                return false;
            }


            if (device) { // check device option consistency
                if (javaHome != null) {
                    System.out.println("java home " + javaHome + " should not be specified for mode " + mode);
                    return false;
                }

            } else { // check host (!device) option consistency
                if (javaHome != null && !new File(javaHome, "/bin/java").exists()) {
                    System.out.println("Invalid java home: " + javaHome);
                    return false;
                }
                if (debugPort != null) {
                    System.out.println("debug port " + debugPort + " should not be specified for mode " + mode);
                    return false;
                }
            }

            // check vm option consistency
            if (!vm) {
                if (!vmArgs.isEmpty()) {
                    System.out.println("vm args " + vmArgs + " should not be specified for mode " + mode);
                    return false;
                }
            }

            if (!sdkJar.exists()) {
                System.out.println("Could not find SDK jar: " + sdkJar);
                return false;
            }

            if (xmlReportsDirectory != null && !xmlReportsDirectory.isDirectory()) {
                System.out.println("Invalid XML reports directory: " + xmlReportsDirectory);
                return false;
            }

            if (testFilenames.isEmpty()) {
                System.out.println("No tests provided.");
                return false;
            }

            //
            // Post-processing arguments
            //

            for (String testFilename : testFilenames) {
                testFiles.add(new File(testFilename));
            }

            if (verbose) {
                Logger.getLogger("dalvik.runner").setLevel(Level.FINE);
            }

            return true;
        }

    }

    private final Options options = new Options();
    private final File localTemp = new File("/tmp/" + UUID.randomUUID());

    private DalvikRunner() {}

    private void prepareLogging() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new Formatter() {
            @Override public String format(LogRecord r) {
                return r.getMessage() + "\n";
            }
        });
        Logger logger = Logger.getLogger("dalvik.runner");
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    private void run() {
        Vm vm;
        if (options.mode.equals(Options.MODE_DEVICE)) {
            vm = new DeviceDalvikVm(
                    options.debugPort,
                    options.timeoutSeconds,
                    options.sdkJar,
                    localTemp,
                    options.vmArgs,
                    options.clean,
                    options.deviceRunnerDir);
        } else if (options.mode.equals(Options.MODE_HOST)) {
            vm = new JavaVm(
                    options.debugPort,
                    options.timeoutSeconds,
                    options.sdkJar,
                    localTemp,
                    options.javaHome,
                    options.vmArgs,
                    options.clean);
        } else if (options.mode.equals(Options.MODE_ACTIVITY)) {
            vm = null;
            System.out.println("Mode " + options.mode + " not currently supported.");
            return;
        } else {
            System.out.println("Unknown mode mode " + options.mode + ".");
            return;
        }

        List<CodeFinder> codeFinders = Arrays.asList(
                new JtregFinder(localTemp),
                new JUnitFinder(),
                new CaliperFinder(),
                new MainFinder());
        Driver driver = new Driver(
                localTemp,
                vm,
                options.expectationFiles,
                options.xmlReportsDirectory,
                codeFinders);
        try {
            driver.loadExpectations();
        } catch (IOException e) {
            System.out.println("Problem loading expectations: " + e);
            return;
        }

        driver.buildAndRunAllTests(options.testFiles);
        vm.shutdown();
    }

    public static void main(String[] args) {
        DalvikRunner dalvikRunner = new DalvikRunner();
        if (!dalvikRunner.options.parseArgs(args)) {
            dalvikRunner.options.printUsage();
            return;
        }
        dalvikRunner.prepareLogging();
        dalvikRunner.run();
    }
}
