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
* @author Boris V. Kuznetsov
* @version $Revision$
*/

package javax.net.ssl;

import java.io.Serializable;
import java.util.EventObject;

/**
 * @com.intel.drl.spec_ref
 * 
 */
public class SSLSessionBindingEvent extends EventObject implements Serializable {

    /**
     * @serial
     * The 5.0 spec. doesn't declare this serialVersionUID field
     * In order to be compatible it is explicitly declared here
     */
    private static final long serialVersionUID = 3989172637106345L;

    private String name;

    public SSLSessionBindingEvent(SSLSession session, String name) {
        super(session);
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public SSLSession getSession() {
        return (SSLSession)this.source;
    }
    


}