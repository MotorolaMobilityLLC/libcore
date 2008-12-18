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

package java.io;

/**
 * Signals that a blocking I/O operation has been interrupted. The number of
 * bytes that were transferred successfully before the interruption took place
 * is stored in a field of the exception.
 * 
 * @since Android 1.0
 */
public class InterruptedIOException extends IOException {

    private static final long serialVersionUID = 4020568460727500567L;

    /**
     * The number of bytes transferred before the I/O interrupt occurred.
     * 
     * @since Android 1.0
     */
    public int bytesTransferred;

    /**
     * Constructs a new {@code InterruptedIOException} with its stack trace
     * filled in.
     * 
     * @since Android 1.0
     */
    public InterruptedIOException() {
        super();
    }

    /**
     * Constructs a new {@code InterruptedIOException} with its stack trace and
     * detail message filled in.
     * 
     * @param detailMessage
     *            the detail message for this exception.
     * @since Android 1.0
     */
    public InterruptedIOException(String detailMessage) {
        super(detailMessage);
    }
}
