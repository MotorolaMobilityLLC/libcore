/*
 * Copyright (C) 2022 The Android Open Source Project
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

package libcore.java.net;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class URLClassLoaderTest {

    @Test
    public void testClose() throws IOException {
        URL[] urls = new URL[] { new URL("http://www.example.com.") };
        URLClassLoader classLoader = new URLClassLoader(urls);
        classLoader.close();
    }
}
