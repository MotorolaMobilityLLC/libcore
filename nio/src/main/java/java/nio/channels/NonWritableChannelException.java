/* Licensed to the Apache Software Foundation (ASF) under one or more
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

package java.nio.channels;


/**
 * A {@code NonWritableChannelException} is thrown when attempting to write to a
 * channel that is not open for writing.
 * 
 * @since Android 1.0
 */
public class NonWritableChannelException extends IllegalStateException {

    private static final long serialVersionUID = -7071230488279011621L;

    /**
     * Constructs a {@code NonWritableChannelException}.
     * 
     * @since Android 1.0
     */
    public NonWritableChannelException() {
        super();
    }
}
