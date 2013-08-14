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

package java.lang;
import java.lang.reflect.ArtField;
import java.lang.reflect.ArtMethod;

/**
 * A dex cache holds resolved copies of strings, fields, methods, and classes from the dexfile.
 */
final class DexCache {
    // Only created by the VM.
    private DexCache() {}

    Object[] initializedStaticStorage;
    String location;
    ArtField[] resolvedFields;
    ArtMethod[] resolvedMethods;
    Class[] resolvedTypes;
    String[] strings;

    // Holds pointer to dexFile.
    private int dexFile;
}

