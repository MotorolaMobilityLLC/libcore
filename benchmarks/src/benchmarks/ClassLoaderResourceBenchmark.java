/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package benchmarks;

import com.google.caliper.SimpleBenchmark;

import junit.framework.Assert;

public class ClassLoaderResourceBenchmark extends SimpleBenchmark {

  private static final String EXISTENT_RESOURCE = "java/util/logging/logging.properties";
  private static final String MISSING_RESOURCE = "missing_entry";

  public void timeGetBootResource_hit(int reps) {
    ClassLoader currentClassLoader = getClass().getClassLoader();
    Assert.assertNotNull(currentClassLoader.getResource(EXISTENT_RESOURCE));

    for (int rep = 0; rep < reps; ++rep) {
      currentClassLoader.getResource(EXISTENT_RESOURCE);
    }
  }

  public void timeGetBootResource_miss(int reps) {
    ClassLoader currentClassLoader = getClass().getClassLoader();
    Assert.assertNull(currentClassLoader.getResource(MISSING_RESOURCE));

    for (int rep = 0; rep < reps; ++rep) {
      currentClassLoader.getResource(MISSING_RESOURCE);
    }
  }

}
