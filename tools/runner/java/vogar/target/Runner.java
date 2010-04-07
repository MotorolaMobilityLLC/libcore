/*
 * Copyright (C) 2010 The Android Open Source Project
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

package vogar.target;

/**
 * Interface between the generic TestRunner and the more specific
 * backend implementations that know about specific types of tests.
 */
public interface Runner {

    public void init(TargetMonitor monitor, String actionName,
            Class<?> testClass);

    public void run(String actionName, Class<?> testClass, String[] args);
}
