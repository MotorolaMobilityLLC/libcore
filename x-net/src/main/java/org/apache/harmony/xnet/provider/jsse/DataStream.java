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
 * @author Alexander Y. Kleymenov
 * @version $Revision$
 */

package org.apache.harmony.xnet.provider.jsse;

/**
 * This interface represents the ability of the
 * classes to provide the chunks of data.
 */
public interface DataStream {

    /**
     * Checks if there is data to be read.
     * @return true if there is the input data in the stream,
     * false otherwise
     */
    public boolean hasData();

    /**
     * Retrieves the data of specified length from the stream.
     * If the data size in the stream is less than specified length,
     * method returns all the data contained in the stream.
     * @return byte array containing the demanded data.
     */
    public byte[] getData(int length);

}

