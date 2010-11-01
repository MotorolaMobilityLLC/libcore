/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package libcore.javax.net.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Set;
import javax.net.ssl.CertPathTrustManagerParameters;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import junit.framework.TestCase;
import libcore.java.security.StandardNames;
import libcore.java.security.TestKeyStore;

public class TrustManagerFactoryTest extends TestCase {

    private static final String [] KEY_TYPES = new String[] { "RSA", "DSA" };
    // note the rare usage of DSA keys here in addition to RSA
    private static final TestKeyStore TEST_KEY_STORE
            = TestKeyStore.create(KEY_TYPES,
                                  null,
                                  null,
                                  "rsa-dsa",
                                  TestKeyStore.localhost(),
                                  true,
                                  null);

    public void test_TrustManagerFactory_getDefaultAlgorithm() throws Exception {
        String algorithm = TrustManagerFactory.getDefaultAlgorithm();
        assertEquals(StandardNames.TRUST_MANAGER_FACTORY_DEFAULT, algorithm);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
        test_TrustManagerFactory(tmf, StandardNames.IS_RI);
    }

    private static class UseslessManagerFactoryParameters implements ManagerFactoryParameters {}

    private void test_TrustManagerFactory(TrustManagerFactory tmf,
                                          boolean supportsManagerFactoryParameters)
            throws Exception {
        assertNotNull(tmf);
        assertNotNull(tmf.getAlgorithm());
        assertNotNull(tmf.getProvider());

        // before init
        try {
            tmf.getTrustManagers();
            fail();
        } catch (IllegalStateException expected) {
        }

        // init with null ManagerFactoryParameters
        try {
            tmf.init((ManagerFactoryParameters) null);
            fail();
        } catch (InvalidAlgorithmParameterException expected) {
        }

        // init with useless ManagerFactoryParameters
        try {
            tmf.init(new UseslessManagerFactoryParameters());
            fail();
        } catch (InvalidAlgorithmParameterException expected) {
        }

        // init with PKIXParameters ManagerFactoryParameters
        try {
            PKIXParameters pp = new PKIXParameters(TEST_KEY_STORE.keyStore);
            CertPathTrustManagerParameters cptmp = new CertPathTrustManagerParameters(pp);
            tmf.init(cptmp);
            fail();
        } catch (InvalidAlgorithmParameterException expected) {
        }

        // init with PKIXBuilderParameters ManagerFactoryParameters
        X509CertSelector xcs = new X509CertSelector();
        PKIXBuilderParameters pbp = new PKIXBuilderParameters(TEST_KEY_STORE.keyStore, xcs);
        CertPathTrustManagerParameters cptmp = new CertPathTrustManagerParameters(pbp);
        if (supportsManagerFactoryParameters) {
            tmf.init(cptmp);
            test_TrustManagerFactory_getTrustManagers(tmf);
        } else {
            try {
                tmf.init(cptmp);
                fail();
            } catch (InvalidAlgorithmParameterException expected) {
            }
        }

        // init with null for default KeyStore
        tmf.init((KeyStore) null);
        test_TrustManagerFactory_getTrustManagers(tmf);

        // init with specific key store
        tmf.init(TEST_KEY_STORE.keyStore);
        test_TrustManagerFactory_getTrustManagers(tmf);
    }

    private void test_TrustManagerFactory_getTrustManagers(TrustManagerFactory tmf)
            throws Exception {
        TrustManager[] trustManagers = tmf.getTrustManagers();
        assertNotNull(trustManagers);
        assertTrue(trustManagers.length > 0);
        for (TrustManager trustManager : trustManagers) {
            assertNotNull(trustManager);
            if (trustManager instanceof X509TrustManager) {
                test_X509TrustManager((X509TrustManager) trustManager);
            }
        }
    }

    private void test_X509TrustManager(X509TrustManager tm) throws Exception {
        for (String keyType : KEY_TYPES) {
            X509Certificate[] issuers = tm.getAcceptedIssuers();
            assertNotNull(issuers);
            assertTrue(issuers.length > 1);
            boolean defaultTrustmanager = (issuers.length > KEY_TYPES.length);

            PrivateKeyEntry pke = TEST_KEY_STORE.getPrivateKey(keyType);
            X509Certificate[] chain = (X509Certificate[]) pke.getCertificateChain();
            if (defaultTrustmanager) {
                try {
                    tm.checkClientTrusted(chain, keyType);
                    fail();
                } catch (CertificateException expected) {
                }
                try {
                    tm.checkServerTrusted(chain, keyType);
                    fail();
                } catch (CertificateException expected) {
                }
            } else {
                tm.checkClientTrusted(chain, keyType);
                tm.checkServerTrusted(chain, keyType);
            }

        }
    }

    public void test_TrustManagerFactory_getInstance() throws Exception {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                String type = service.getType();
                if (!type.equals("TrustManagerFactory")) {
                    continue;
                }
                String algorithm = service.getAlgorithm();
                boolean supportsManagerFactoryParameters = algorithm.equals("PKIX");
                {
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
                    assertEquals(algorithm, tmf.getAlgorithm());
                    test_TrustManagerFactory(tmf, supportsManagerFactoryParameters);
                }

                {
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm,
                                                                          provider);
                    assertEquals(algorithm, tmf.getAlgorithm());
                    assertEquals(provider, tmf.getProvider());
                    test_TrustManagerFactory(tmf, supportsManagerFactoryParameters);
                }

                {
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm,
                                                                          provider.getName());
                    assertEquals(algorithm, tmf.getAlgorithm());
                    assertEquals(provider, tmf.getProvider());
                    test_TrustManagerFactory(tmf, supportsManagerFactoryParameters);
                }
            }
        }
    }

    public void test_TrustManagerFactory_intermediate() throws Exception {
        // chain should be server/intermediate/root
        PrivateKeyEntry pke = TestKeyStore.getServer().getPrivateKey("RSA");
        X509Certificate[] chain = (X509Certificate[])pke.getCertificateChain();
        assertEquals(3, chain.length);

        // keyStore should contain only the intermediate CA so we can
        // test proper validation even if there are extra certs after
        // the trusted one (in this case the original root is "extra")
        KeyStore keyStore = TestKeyStore.createKeyStore();
        keyStore.setCertificateEntry("alias", chain[1]);

        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                String type = service.getType();
                if (!type.equals("TrustManagerFactory")) {
                    continue;
                }
                String algorithm = service.getAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
                tmf.init(keyStore);
                TrustManager[] trustManagers = tmf.getTrustManagers();
                for (TrustManager trustManager : trustManagers) {
                    if (!(trustManager instanceof X509TrustManager)) {
                        continue;
                    }
                    X509TrustManager tm = (X509TrustManager) trustManager;
                    tm.checkClientTrusted(chain, "RSA");
                    tm.checkServerTrusted(chain, "RSA");
                }
            }
        }
    }

}
