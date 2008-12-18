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

/**
* @author Vera Y. Petrashkova
* @version $Revision$
*/

package java.security;

/**
 * {@code InvalidParameterException} indicates exceptional conditions, caused by
 * invalid parameters.
 * 
 * @since Android 1.0
 */
public class InvalidParameterException extends IllegalArgumentException {

    private static final long serialVersionUID = -857968536935667808L;

    /**
     * Constructs a new instance of {@code InvalidParameterException} with the
     * given message.
     * 
     * @param msg
     *            the detail message for this exception.
     * @since Android 1.0
     */
    public InvalidParameterException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new instance of {@code InvalidParameterException}.
     * 
     * @since Android 1.0
     */
    public InvalidParameterException() {
    }
}
