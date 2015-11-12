/*
 * reserved comment block
 * DO NOT REMOVE OR ALTER!
 */
/*
 * Copyright  2003-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.sun.org.apache.xml.internal.security.encryption;


import java.util.Iterator;
import org.w3c.dom.Element;


/**
 * <code>EncryptionMethod</code> describes the encryption algorithm applied to
 * the cipher data. If the element is absent, the encryption algorithm must be
 * known by the recipient or the decryption will fail.
 * <p>
 * It is defined as follows:
 * <xmp>
 * <complexType name='EncryptionMethodType' mixed='true'>
 *     <sequence>
 *         <element name='KeySize' minOccurs='0' type='xenc:KeySizeType'/>
 *         <element name='OAEPparams' minOccurs='0' type='base64Binary'/>
 *         <any namespace='##other' minOccurs='0' maxOccurs='unbounded'/>
 *     </sequence>
 *     <attribute name='Algorithm' type='anyURI' use='required'/>
 * </complexType>
 * </xmp>
 *
 * @author Axl Mattheus
 */
public interface EncryptionMethod {
    /**
     * Returns the algorithm applied to the cipher data.
     *
     * @return the encryption algorithm.
     */
    String getAlgorithm();

    /**
     * Returns the key size of the key of the algorithm applied to the cipher
     * data.
     *
     * @return the key size.
     */
    int getKeySize();

    /**
     * Sets the size of the key of the algorithm applied to the cipher data.
     *
     * @param size the key size.
     */
    void setKeySize(int size);

    /**
     * Returns the OAEP parameters of the algorithm applied applied to the
     * cipher data.
     *
     * @return the OAEP parameters.
     */
    byte[] getOAEPparams();

    /**
     * Sets the OAEP parameters.
     *
     * @param parameters the OAEP parameters.
     */
    void setOAEPparams(byte[] parameters);

    /**
     * Returns an iterator over all the additional elements contained in the
     * <code>EncryptionMethod</code>.
     *
     * @return an <code>Iterator</code> over all the additional infomation
     *   about the <code>EncryptionMethod</code>.
     */
    Iterator getEncryptionMethodInformation();

    /**
     * Adds encryption method information.
     *
     * @param information additional encryption method information.
     */
    void addEncryptionMethodInformation(Element information);

    /**
     * Removes encryption method information.
     *
     * @param information the information to remove from the
     *   <code>EncryptionMethod</code>.
     */
    void removeEncryptionMethodInformation(Element information);
}
