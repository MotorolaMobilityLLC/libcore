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

package vogar;

import com.sun.javatest.TestDescription;
import com.sun.javatest.TestResult;
import com.sun.javatest.TestResultTable;
import com.sun.javatest.TestSuite;
import com.sun.javatest.WorkDirectory;
import com.sun.javatest.regtest.RegressionTestSuite;
import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;
import vogar.commands.Mkdir;
import vogar.target.JtregRunner;
import vogar.target.Runner;

/**
 * Create {@link Action}s for {@code .java} files with jtreg tests in them.
 */
class JtregSpec implements RunnerSpec {

    // TODO: add support for the  @library directive, as seen in
    //   test/com/sun/crypto/provider/Cipher/AES/TestKATForECB_VT.java

    private static final Logger logger = Logger.getLogger(JtregSpec.class.getName());

    /**
     * The subpath of a platform implementation under which tests live. Used to
     * derive relative test paths like {@code /java/io/Reader} from an absolute
     * path like {@code /home/jessewilson/platform_v6/test/java/io/Reader}.
     */
    static final String TEST_ROOT = "/test/";

    private final File localTemp;

    JtregSpec(File localTemp) {
        this.localTemp = localTemp;
    }

    /**
     * Returns the tests in {@code directoryToScan}.
     */
    public Set<Action> findActions(File directoryToScan) {
        // for now, jtreg doesn't know how to scan anything but directories
        if (!directoryToScan.isDirectory()) {
            return Collections.emptySet();
        }

        try {
            logger.fine("scanning " + directoryToScan + " for jtreg tests");
            File workDirectory = new File(localTemp, "JTwork");
            new Mkdir().mkdirs(workDirectory);

            /*
             * This code is capable of extracting test descriptions using jtreg 4.0
             * and its bundled copy of jtharness. As a command line tool, jtreg's
             * API wasn't intended for this style of use. As a consequence, this
             * code is fragile and may be incompatible with newer versions of jtreg.
             */
            TestSuite testSuite = new RegressionTestSuite(directoryToScan);
            WorkDirectory wd = WorkDirectory.convert(workDirectory, testSuite);
            TestResultTable resultTable = wd.getTestResultTable();

            Set<Action> result = new LinkedHashSet<Action>();
            for (Iterator i = resultTable.getIterator(); i.hasNext(); ) {
                TestResult testResult = (TestResult) i.next();
                TestDescription description = testResult.getDescription();
                String qualifiedName = qualifiedName(description);
                String testClass = description.getName();
                result.add(new Action(qualifiedName, testClass, description.getDir(), description.getFile(), this));
            }
            return result;
        } catch (Exception jtregFailure) {
            // jtreg shouldn't fail in practice
            throw new RuntimeException(jtregFailure);
        }
    }

    public boolean supports(String className) {
        // the jtreg runner cannot run prebuilt classes
        return false;
    }

    /**
     * Returns a fully qualified name of the form {@code
     * java.lang.Math.PowTests} from the given test description. The returned
     * name is appropriate for use in a filename.
     */
    String qualifiedName(TestDescription testDescription) {
        return suiteName(testDescription) + "." + escape(testDescription.getName());
    }

    /**
     * Returns the name of the class under test, such as {@code java.lang.Math}.
     */
    String suiteName(TestDescription testDescription) {
        String dir = testDescription.getDir().toString();
        int separatorIndex = dir.indexOf(TEST_ROOT);
        return separatorIndex != -1
                ? escape(dir.substring(separatorIndex + TEST_ROOT.length()))
                : escape(dir);
    }

    /**
     * Returns a similar string with filename-unsafe characters replaced by
     * filename-safe ones.
     */
    private String escape(String s) {
        return s.replace('/', '.');
    }

    public Class<? extends Runner> getRunnerClass() {
        return JtregRunner.class;
    }

    public File getSource() {
        return new File(Vogar.HOME_JAVA, "vogar/target/JtregRunner.java");
    }

    public Classpath getClasspath() {
        return new Classpath();
    }
}
