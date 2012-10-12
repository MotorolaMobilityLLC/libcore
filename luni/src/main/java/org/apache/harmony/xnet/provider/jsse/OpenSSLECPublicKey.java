/*
 * Copyright (C) 2012 The Android Open Source Project
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

package org.apache.harmony.xnet.provider.jsse;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.util.Arrays;

public final class OpenSSLECPublicKey implements ECPublicKey {
    private static final long serialVersionUID = 3215842926808298020L;

    private static final String ALGORITHM = "EC";

    protected transient OpenSSLKey key;

    protected transient OpenSSLECGroupContext group;

    public OpenSSLECPublicKey(OpenSSLECGroupContext group, OpenSSLKey key) {
        this.group = group;
        this.key = key;
    }

    public static OpenSSLKey getInstance(ECPublicKey ecPublicKey) throws InvalidKeyException {
        try {
            OpenSSLECGroupContext group = OpenSSLECGroupContext
                    .getInstance(ecPublicKey.getParams());
            OpenSSLECPointContext pubKey = OpenSSLECPointContext.getInstance(
                    NativeCrypto.get_EC_GROUP_type(group.getContext()), group, ecPublicKey.getW());
            return new OpenSSLKey(NativeCrypto.EVP_PKEY_new_EC_KEY(group.getContext(),
                    pubKey.getContext(), null));
        } catch (Exception e) {
            throw new InvalidKeyException(e);
        }
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    @Override
    public byte[] getEncoded() {
        return NativeCrypto.i2d_PUBKEY(key.getPkeyContext());
    }

    @Override
    public ECParameterSpec getParams() {
        return group.getECParameterSpec();
    }

    private ECPoint getPublicKey() {
        final OpenSSLECPointContext pubKey = new OpenSSLECPointContext(group,
                NativeCrypto.EC_KEY_get_public_key(key.getPkeyContext()));

        return pubKey.getECPoint();
    }

    @Override
    public ECPoint getW() {
        return getPublicKey();
    }

    public OpenSSLKey getOpenSSLKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof OpenSSLECPrivateKey) {
            OpenSSLECPrivateKey other = (OpenSSLECPrivateKey) o;
            return key.equals(other.key);
        }

        if (!(o instanceof ECPublicKey)) {
            return false;
        }

        final ECPublicKey other = (ECPublicKey) o;
        if (!getPublicKey().equals(other.getW())) {
            return false;
        }

        final ECParameterSpec spec = getParams();
        final ECParameterSpec otherSpec = other.getParams();

        return spec.getCurve().equals(otherSpec.getCurve())
                && spec.getGenerator().equals(otherSpec.getGenerator())
                && spec.getOrder().equals(otherSpec.getOrder())
                && spec.getCofactor() == otherSpec.getCofactor();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(NativeCrypto.i2d_PUBKEY(key.getPkeyContext()));
    }

    @Override
    public String toString() {
        return NativeCrypto.EVP_PKEY_print_public(key.getPkeyContext());
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();

        final ECParameterSpec params = (ECParameterSpec) stream.readObject();
        final BigInteger pubkeyX = (BigInteger) stream.readObject();
        final BigInteger pubkeyY = (BigInteger) stream.readObject();

        final OpenSSLECGroupContext group;
        try {
            group = OpenSSLECGroupContext.getInstance(params);
        } catch (InvalidAlgorithmParameterException e) {
            throw new ClassNotFoundException("cannot restore field type", e);
        }

        final int curveType = NativeCrypto.get_EC_GROUP_type(group.getContext());
        final ECPoint javaPubKey = new ECPoint(pubkeyX, pubkeyY);
        final OpenSSLECPointContext pubKey = OpenSSLECPointContext.getInstance(curveType, group,
                javaPubKey);
        key = new OpenSSLKey(NativeCrypto.EVP_PKEY_new_EC_KEY(group.getContext(),
                pubKey.getContext(), null));
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        if (key.isEngineBased()) {
            throw new NotSerializableException("engine-based keys can not be serialized");
        }

        stream.defaultWriteObject();
        stream.writeObject(getParams());
        final ECPoint pubKey = getPublicKey();
        stream.writeObject(pubKey.getAffineX());
        stream.writeObject(pubKey.getAffineY());
    }
}
