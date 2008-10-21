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

package javax.net.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @com.intel.drl.spec_ref
 * 
 */
public interface X509KeyManager extends KeyManager {

    /**
     * @com.intel.drl.spec_ref
     *  
     */
    public String chooseClientAlias(String[] keyType, Principal[] issuers,
            Socket socket);

    /**
     * @com.intel.drl.spec_ref
     *  
     */
    public String chooseServerAlias(String keyType, Principal[] issuers,
            Socket socket);

    /**
     * @com.intel.drl.spec_ref
     *  
     */
    public X509Certificate[] getCertificateChain(String alias);

    /**
     * @com.intel.drl.spec_ref
     *  
     */
    public String[] getClientAliases(String keyType, Principal[] issuers);

    /**
     * @com.intel.drl.spec_ref
     *  
     */
    public String[] getServerAliases(String keyType, Principal[] issuers);

    /**
     * @com.intel.drl.spec_ref
     *  
     */
    public PrivateKey getPrivateKey(String alias);
}