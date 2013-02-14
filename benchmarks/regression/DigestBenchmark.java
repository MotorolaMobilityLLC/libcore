/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package benchmarks.regression;

import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;
import com.android.org.bouncycastle.crypto.Digest;

public class DigestBenchmark extends SimpleBenchmark {

    private static final int DATA_SIZE = 8192;
    private static final byte[] DATA = new byte[DATA_SIZE];
    static {
        for (int i = 0; i < DATA_SIZE; i++) {
            DATA[i] = (byte)i;
        }
    }

    @Param private Algorithm algorithm;

    public enum Algorithm { MD5, SHA1, SHA256,  SHA384, SHA512 };

    @Param private Implementation implementation;

    public enum Implementation { OPENSSL, BOUNCYCASTLE };

    private Class<? extends Digest> digestClass;

    @Override protected void setUp() throws Exception {
        String className = "com.android.org.bouncycastle.crypto.digests.";
        switch (implementation) {
            case OPENSSL:
                className += ("OpenSSLDigest$" + algorithm);
                break;
            case BOUNCYCASTLE:
                className += (algorithm + "Digest");
                break;
            default:
                throw new RuntimeException(implementation.toString());
        }
        this.digestClass = (Class<? extends Digest>)Class.forName(className);
    }

    public void time(int reps) throws Exception {
        for (int i = 0; i < reps; ++i) {
            Digest digest = digestClass.newInstance();
            digest.update(DATA, 0, DATA_SIZE);
            digest.doFinal(new byte[digest.getDigestSize()], 0);
        }
    }
}
