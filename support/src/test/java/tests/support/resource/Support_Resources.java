/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tests.support.resource;

import libcore.io.Streams;
import tests.support.Support_Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Support_Resources {

    public static final String RESOURCE_PACKAGE = "/tests/resources/";

    public static final String RESOURCE_PACKAGE_NAME = "tests.resources";

    public static InputStream getStream(String name) {
        String path = RESOURCE_PACKAGE + name;
        InputStream result = Support_Resources.class.getResourceAsStream(path);
        if (result == null) {
            throw new IllegalArgumentException("No such resource: " + path);
        }
        return result;
    }

    public static String getURL(String name) {
        String folder = null;
        String fileName = name;
        File resources = createTempFolder();
        int index = name.lastIndexOf("/");
        if (index != -1) {
            folder = name.substring(0, index);
            name = name.substring(index + 1);
        }
        copyFile(resources, folder, name);
        URL url = null;
        String resPath = resources.toString();
        if (resPath.charAt(0) == '/' || resPath.charAt(0) == '\\') {
            resPath = resPath.substring(1);
        }
        try {
            url = new URL("file:/" + resPath + "/" + fileName);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return url.toString();
    }

    public static File createTempFolder() {

        File folder = null;
        try {
            folder = File.createTempFile("hyts_resources", "", null);
            folder.delete();
            folder.mkdirs();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        folder.deleteOnExit();
        return folder;
    }

    public static File copyFile(File root, String folder, String file) {
        File f;
        if (folder != null) {
            f = new File(root.toString() + "/" + folder);
            if (!f.exists()) {
                f.mkdirs();
                f.deleteOnExit();
            }
        } else {
            f = root;
        }

        File dest = new File(f.toString() + "/" + file);

        InputStream in = Support_Resources.getStream(folder == null ? file
                : folder + "/" + file);
        try {
            copyLocalFileto(dest, in);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return dest;
    }

    public static File createTempFile(String suffix) throws IOException {
        return File.createTempFile("hyts_", suffix, null);
    }

    public static void copyLocalFileto(File dest, InputStream in) throws IOException {
        if (!dest.exists()) {
            FileOutputStream out = new FileOutputStream(dest);
            Streams.copy(in, out);
            out.close();
            dest.deleteOnExit();
        }
        in.close();
    }

    public static File getExternalLocalFile(String url) throws IOException {
        File resources = createTempFolder();
        InputStream in = new URL(url).openStream();
        File temp = new File(resources.toString() + "/local.tmp");
        copyLocalFileto(temp, in);
        return temp;
    }

    public static String getResourceURL(String resource) {
        return "http://" + Support_Configuration.TestResources + resource;
    }

    /**
     * Util method to load resource files
     *
     * @param name - name of resource file
     * @return - resource input stream
     */
    public static InputStream getResourceStream(String name) {
        InputStream is = Support_Resources.class.getResourceAsStream(name);

        if (is == null) {
            name = RESOURCE_PACKAGE + name;
            is = Support_Resources.class.getResourceAsStream(name);
            if (is == null) {
                throw new RuntimeException("Failed to load resource: " + name);
            }
        }

        return is;
    }

    public static File resourceToTempFile(String path) throws IOException {
        File f = File.createTempFile("out", ".xml");
        f.deleteOnExit();
        FileOutputStream out = new FileOutputStream(f);

        InputStream xml = Support_Resources.class.getResourceAsStream(path);
        int b;
        while ((b = xml.read()) != -1) {
            out.write(b);
        }
        out.flush();
        out.close();
        xml.close();
        return f;
    }
}
