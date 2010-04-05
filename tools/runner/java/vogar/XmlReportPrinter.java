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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.kxml2.io.KXmlSerializer;


/**
 * Writes JUnit results to a series of XML files in a format consistent with
 * Ant's XMLJUnitResultFormatter.
 *
 * <p>Unlike Ant's formatter, this class does not report the execution time of
 * tests.
 *
 * TODO: unify this and com.google.coretests.XmlReportPrinter
 */
public class XmlReportPrinter {

    private static final String TESTSUITE = "testsuite";
    private static final String TESTCASE = "testcase";
    private static final String ERROR = "error";
    private static final String FAILURE = "failure";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_TIME = "time";
    private static final String ATTR_ERRORS = "errors";
    private static final String ATTR_FAILURES = "failures";
    private static final String ATTR_TESTS = "tests";
    private static final String ATTR_TYPE = "type";
    private static final String PROPERTIES = "properties";
    private static final String ATTR_CLASSNAME = "classname";
    private static final String TIMESTAMP = "timestamp";
    private static final String HOSTNAME = "hostname";

    /** the XML namespace */
    private static final String ns = null;

    private final File directory;
    private final ExpectationStore expectationStore;

    public XmlReportPrinter(File directory, ExpectationStore expectationStore) {
        this.directory = directory;
        this.expectationStore = expectationStore;
    }

    /**
     * Populates the directory with the report data from the completed tests.
     */
    public int generateReports(Collection<Outcome> results) {
        Map<String, Suite> suites = testsToSuites(results);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        dateFormat.setTimeZone(gmt);
        dateFormat.setLenient(true);
        String timestamp = dateFormat.format(new Date());

        for (Suite suite : suites.values()) {
            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(new File(directory, "TEST-" + suite.name + ".xml"));

                KXmlSerializer serializer = new KXmlSerializer();
                serializer.setOutput(stream, "UTF-8");
                serializer.startDocument("UTF-8", null);
                serializer.setFeature(
                        "http://xmlpull.org/v1/doc/features.html#indent-output", true);
                suite.print(serializer, timestamp);
                serializer.endDocument();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }

        return suites.size();
    }

    private Map<String, Suite> testsToSuites(Collection<Outcome> outcomes) {
        Map<String, Suite> result = new LinkedHashMap<String, Suite>();
        for (Outcome outcome : outcomes) {
            if (outcome.getResult() == Result.UNSUPPORTED) {
                continue;
            }

            String suiteName = outcome.getSuiteName();
            Suite suite = result.get(suiteName);
            if (suite == null) {
                suite = new Suite(suiteName);
                result.put(suiteName, suite);
            }

            suite.outcomes.add(outcome);

            Expectation expectation = expectationStore.get(outcome.getName());
            if (!expectation.matches(outcome)) {
                if (outcome.getResult() == Result.EXEC_FAILED) {
                    suite.failuresCount++;
                } else {
                    suite.errorsCount++;
                }
            }
        }
        return result;
    }

    class Suite {
        private final String name;
        private final List<Outcome> outcomes = new ArrayList<Outcome>();
        private int failuresCount;
        private int errorsCount;

        Suite(String name) {
            this.name = name;
        }

        void print(KXmlSerializer serializer, String timestamp) throws IOException {
            serializer.startTag(ns, TESTSUITE);
            serializer.attribute(ns, ATTR_NAME, name);
            serializer.attribute(ns, ATTR_TESTS, Integer.toString(outcomes.size()));
            serializer.attribute(ns, ATTR_FAILURES, Integer.toString(failuresCount));
            serializer.attribute(ns, ATTR_ERRORS, Integer.toString(errorsCount));
            serializer.attribute(ns, ATTR_TIME, "0");
            serializer.attribute(ns, TIMESTAMP, timestamp);
            serializer.attribute(ns, HOSTNAME, "localhost");
            serializer.startTag(ns, PROPERTIES);
            serializer.endTag(ns, PROPERTIES);

            for (Outcome outcome : outcomes) {
                print(serializer, outcome);
            }

            serializer.endTag(ns, TESTSUITE);
        }

        void print(KXmlSerializer serializer, Outcome outcome) throws IOException {
            serializer.startTag(ns, TESTCASE);
            serializer.attribute(ns, ATTR_NAME, outcome.getTestName());
            serializer.attribute(ns, ATTR_CLASSNAME, outcome.getSuiteName());
            serializer.attribute(ns, ATTR_TIME, "0");

            Expectation expectation = expectationStore.get(outcome.getName());
            if (!expectation.matches(outcome)) {
                String result = outcome.getResult() == Result.EXEC_FAILED ? FAILURE : ERROR;
                serializer.startTag(ns, result);
                serializer.attribute(ns, ATTR_TYPE, outcome.getResult().toString());
                String text = sanitize(Strings.join(outcome.getOutputLines(), "\n"));
                serializer.text(text);
                serializer.endTag(ns, result);
            }

            serializer.endTag(ns, TESTCASE);
        }

        /**
         * Returns the text in a format that is safe for use in an XML document.
         */
        private String sanitize(String text) {
            return text.replace("\0", "<\\0>");
        }
    }
}
