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
 * Objects to perform validations on other objects read with serialization
 * should implement this interface. An object's <code>readObject()</code> can
 * call <code>ObjectInputStream.registerValidation()</code> passing an object
 * that implements ObjectInputValidation.
 * 
 * @see ObjectInputStream
 */
public interface ObjectInputValidation {
    /**
     * Validates an object read with serialization.
     * 
     * @throws InvalidObjectException
     *             if the receiver fails to validate the object read
     */
    public void validateObject() throws InvalidObjectException;
}
