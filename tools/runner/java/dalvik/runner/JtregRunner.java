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

import java.lang.reflect.Method;

/**
 * Runs a jtreg test.
 */
public final class JtregRunner extends TestRunner {

    private Method main;

    @Override public void prepareTest() {
        try {
            Class<?> test = Class.forName(testClass);
            main = test.getMethod("main", String[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override public boolean test() {
        try {
            main.invoke(null, new Object[] { new String[0] });
            return true;
        } catch (Throwable failure) {
            failure.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new JtregRunner().run();
    }
}
