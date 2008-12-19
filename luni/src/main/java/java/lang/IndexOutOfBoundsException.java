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

/**
 * Thrown when a program attempts to access a value in an indexable collection
 * using a value which is outside of the range of valid indices.
 * 
 * @since Android 1.0
 */
public class IndexOutOfBoundsException extends RuntimeException {

    private static final long serialVersionUID = 234122996006267687L;

    /**
     * Constructs a new {@code IndexOutOfBoundsException} that includes the
     * current stack trace.
     * 
     * @since Android 1.0
     */
    public IndexOutOfBoundsException() {
        super();
    }

    /**
     * Constructs a new {@code IndexOutOfBoundsException} with the current stack
     * trace and the specified detail message.
     * 
     * @param detailMessage
     *            the detail message for this exception.
     * @since Android 1.0
     */
    public IndexOutOfBoundsException(String detailMessage) {
        super(detailMessage);
    }
}
