/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.targets.security.cert;

import dalvik.annotation.TestTargetClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@TestTargetClass(targets.CertPathValidators.PKIX.class)
public class CertPathValidatorTestPKIX extends CertPathValidatorTest {

    private CertPath certPath;
    private PKIXParameters params;

    public CertPathValidatorTestPKIX() {
        super("PKIX");
    }

    @Override
    CertPath getCertPath() {
        return certPath;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            fail(e.getMessage());
        }

        try {
            keyStore.load(null, null);
        } catch (NoSuchAlgorithmException e) {
            fail(e.getMessage());
        } catch (CertificateException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }

        CertificateFactory certificateFactory = null;
        try {
            certificateFactory = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            fail(e.getMessage());
        }

        X509Certificate selfSignedcertificate = null;
        try {
            selfSignedcertificate = (X509Certificate) certificateFactory
                    .generateCertificate(new ByteArrayInputStream(
                            selfSignedCert.getBytes()));
        } catch (CertificateException e) {
            fail(e.getMessage());
        }

        try {
            keyStore.setCertificateEntry("selfSignedCert",
                    selfSignedcertificate);
        } catch (KeyStoreException e) {
            fail(e.getMessage());
        }

        X509CertSelector targetConstraints = new X509CertSelector();
        targetConstraints.setCertificate(selfSignedcertificate);

        List<Certificate> certList = new ArrayList<Certificate>();
        certList.add(selfSignedcertificate);
        CertStoreParameters storeParams = new CollectionCertStoreParameters(
                certList);


        CertStore certStore = null;
        try {
            certStore = CertStore.getInstance("Collection", storeParams);
        } catch (InvalidAlgorithmParameterException e) {
            fail(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            fail(e.getMessage());
        }


        PKIXBuilderParameters parameters = null;
        try {
            parameters = new PKIXBuilderParameters(keyStore, targetConstraints);
            parameters.addCertStore(certStore);
            parameters.setRevocationEnabled(false);
        } catch (KeyStoreException e) {
            fail(e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            fail(e.getMessage());
        }

        CertPathBuilder pathBuilder = null;
        try {
            pathBuilder = CertPathBuilder.getInstance("PKIX");
        } catch (NoSuchAlgorithmException e) {
            fail(e.getMessage());
        }
        CertPathBuilderResult builderResult = null;
        try {
            builderResult = pathBuilder.build(parameters);
        } catch (CertPathBuilderException e) {
            fail(e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            fail(e.getMessage());
        }

        certPath = builderResult.getCertPath();

        try {
            params = new PKIXParameters(keyStore);
            params.setRevocationEnabled(false);
        } catch (KeyStoreException e) {
            fail(e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            fail(e.getMessage());
        }
    }

    @Override
    CertPathParameters getParams() {
        return params;
    }

    @Override
    void validateResult(CertPathValidatorResult validatorResult) {
        assertNotNull("validator result is null", validatorResult);
        assertTrue("validator result is not PKIX",
                validatorResult instanceof PKIXCertPathValidatorResult);

    }


    private String selfSignedCert = "-----BEGIN CERTIFICATE-----\n"
    + "MIICSDCCAbECBEk2ZvswDQYJKoZIhvcNAQEEBQAwazELMAkGA1UEBhMCQU4xEDAOBgNVBAgTB0Fu\n"
    + "ZHJvaWQxEDAOBgNVBAcTB0FuZHJvaWQxEDAOBgNVBAoTB0FuZHJvaWQxEDAOBgNVBAsTB0FuZHJv\n"
    + "aWQxFDASBgNVBAMTC0FuZHJvaWQgQ1RTMB4XDTA4MTIwMzExMDExNVoXDTM2MDQyMDExMDExNVow\n"
    + "azELMAkGA1UEBhMCQU4xEDAOBgNVBAgTB0FuZHJvaWQxEDAOBgNVBAcTB0FuZHJvaWQxEDAOBgNV\n"
    + "BAoTB0FuZHJvaWQxEDAOBgNVBAsTB0FuZHJvaWQxFDASBgNVBAMTC0FuZHJvaWQgQ1RTMIGfMA0G\n"
    + "CSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAMd+N1Bu2eiI4kukOLvFlpTSEHTGplN2vvw76T7jSZinx\n"
    + "WcrtLe6qH1uPffbVNW4/BRn6OywbcynazEdqEUa09hWtHYmUsXpRPyGUBScNnyF751SGA2JIQUfg\n"
    + "3gi3gT3h32Z64AIHnn5gsGDJkeWOHx6/uVOV7iqr7cwPdLp03QIDAQABMA0GCSqGSIb3DQEBBAUA\n"
    + "A4GBAGG46Udsh6U7bSkJsyPPmSCCEkGr14L8F431UuaWbLvQVDtyPv8vtdJilyUTVnlWM6JNGV/q\n"
    + "bgHuLbohkVXn9l68GtgQ7QDexHJE5hEDG/S7cYNi9GhrCfzAjEed13VMntZHZ0XQ4E7jBOmhcMAY\n"
    + "DC9BBx1sVKoji17RP4R8CTf1\n" + "-----END CERTIFICATE-----";
}
