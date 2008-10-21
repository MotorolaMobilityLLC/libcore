/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.net;

/**
 * Defines a scheme for mapping a filename type to a MIME content type. Mainly
 * used by <code>URLStreamHandler</code> for determining the right content
 * handler to handle the resource.
 * 
 * @see ContentHandler
 * @see URLConnection#getFileNameMap()
 * @see URLConnection#guessContentTypeFromName(String)
 * @see URLStreamHandler
 */
public interface FileNameMap {

    /**
     * Determines the MIME types for a file <code>fileName</code> of a
     * <code>URL</code>.
     * 
     * @param fileName
     *            the name of the file to consider.
     * 
     * @return the mime type
     */
    public String getContentTypeFor(String fileName);
}
