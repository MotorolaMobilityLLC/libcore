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
 * A {@code ClosedSelectorException} is thrown when a {@link Selector selector}
 * is closed and an I/O operation is attempted.
 * 
 * @since Android 1.0
 */
public class ClosedSelectorException extends IllegalStateException {

    private static final long serialVersionUID = 6466297122317847835L;
    
    /**
     * Constructs a {@code ClosedSelectorException}.
     * 
     * @since Android 1.0
     */
    public ClosedSelectorException() {
        super();
    }
}


