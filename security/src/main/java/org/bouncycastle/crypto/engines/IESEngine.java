package org.bouncycastle.crypto.engines;

import java.math.BigInteger;

import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.IESParameters;
import org.bouncycastle.crypto.params.IESWithCipherParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * support class for constructing intergrated encryption ciphers
 * for doing basic message exchanges on top of key agreement ciphers
 */
public class IESEngine
{
    BasicAgreement      agree;
    DerivationFunction  kdf;
    Mac                 mac;
    BufferedBlockCipher cipher;
    byte[]              macBuf;

    boolean             forEncryption;
    CipherParameters    privParam, pubParam;
    IESParameters       param;

    /**
     * set up for use with stream mode, where the key derivation function
     * is used to provide a stream of bytes to xor with the message.
     *
     * @param agree the key agreement used as the basis for the encryption
     * @param kdf the key derivation function used for byte generation
     * @param mac the message authentication code generator for the message
     */
    public IESEngine(
        BasicAgreement      agree,
        DerivationFunction  kdf,
        Mac                 mac)
    {
        this.agree = agree;
        this.kdf = kdf;
        this.mac = mac;
        this.macBuf = new byte[mac.getMacSize()];
        this.cipher = null;
    }

    /**
     * set up for use in conjunction with a block cipher to handle the
     * message.
     *
     * @param agree the key agreement used as the basis for the encryption
     * @param kdf the key derivation function used for byte generation
     * @param mac the message authentication code generator for the message
     * @param cipher the cipher to used for encrypting the message
     */
    public IESEngine(
        BasicAgreement      agree,
        DerivationFunction  kdf,
        Mac                 mac,
        BufferedBlockCipher cipher)
    {
        this.agree = agree;
        this.kdf = kdf;
        this.mac = mac;
        this.macBuf = new byte[mac.getMacSize()];
        this.cipher = cipher;
    }

    /**
     * Initialise the encryptor.
     *
     * @param forEncryption whether or not this is encryption/decryption.
     * @param privParam our private key parameters
     * @param pubParam the recipient's/sender's public key parameters
     * @param param encoding and derivation parameters.
     */
    public void init(
        boolean                     forEncryption,
        CipherParameters            privParam,
        CipherParameters            pubParam,
        CipherParameters            param)
    {
        this.forEncryption = forEncryption;
        this.privParam = privParam;
        this.pubParam = pubParam;
        this.param = (IESParameters)param;
    }

    private byte[] decryptBlock(
        byte[]  in_enc,
        int     inOff,
        int     inLen,
        byte[]  z)
        throws InvalidCipherTextException
    {
        byte[]          M = null;
        KeyParameter    macKey = null;
        KDFParameters   kParam = new KDFParameters(z, param.getDerivationV());
        int             macKeySize = param.getMacKeySize();

        kdf.init(kParam);

        inLen -= mac.getMacSize();
    
        if (cipher == null)     // stream mode
        {
            byte[]  buf = new byte[inLen + (macKeySize / 8)];

            M = new byte[inLen];

            kdf.generateBytes(buf, 0, buf.length);

            for (int i = 0; i != inLen; i++)
            {
                M[i] = (byte)(in_enc[inOff + i] ^ buf[i]);
            }

            macKey = new KeyParameter(buf, inLen, (macKeySize / 8));
        }
        else
        {
            int     cipherKeySize = ((IESWithCipherParameters)param).getCipherKeySize();
            byte[]  buf = new byte[(cipherKeySize / 8) + (macKeySize / 8)];

            cipher.init(false, new KeyParameter(buf, 0, (cipherKeySize / 8)));

            byte[] tmp = new byte[cipher.getOutputSize(inLen)];

            int off = cipher.processBytes(in_enc, inOff, inLen, tmp, 0);

            off += cipher.doFinal(tmp, off);

            M = new byte[off];

            System.arraycopy(tmp, 0, M, 0, off);

            macKey = new KeyParameter(buf, (cipherKeySize / 8), (macKeySize / 8));
        }

        byte[]  macIV = param.getEncodingV();

        mac.init(macKey);
        mac.update(in_enc, inOff, inLen);
        mac.update(macIV, 0, macIV.length);
        mac.doFinal(macBuf, 0);
    
        inOff += inLen;

        for (int t = 0; t < macBuf.length; t++)
        {           
            if (macBuf[t] != in_enc[inOff + t])
            {
                throw (new InvalidCipherTextException("Mac codes failed to equal."));
            }
        }
       
        return M;
    }

    private byte[] encryptBlock(
        byte[]  in,
        int     inOff,
        int     inLen,
        byte[]  z)
        throws InvalidCipherTextException
    {
        byte[]          C = null;
        KeyParameter    macKey = null;
        KDFParameters   kParam = new KDFParameters(z, param.getDerivationV());
        int             c_text_length = 0;
        int             macKeySize = param.getMacKeySize();

        kdf.init(kParam);

        if (cipher == null)     // stream mode
        {
            byte[]  buf = new byte[inLen + (macKeySize / 8)];

            C = new byte[inLen + mac.getMacSize()];
            c_text_length = inLen;

            kdf.generateBytes(buf, 0, buf.length);

            for (int i = 0; i != inLen; i++)
            {
                C[i] = (byte)(in[inOff + i] ^ buf[i]);
            }

            macKey = new KeyParameter(buf, inLen, (macKeySize / 8));
        }
        else
        {
            int     cipherKeySize = ((IESWithCipherParameters)param).getCipherKeySize();
            byte[]  buf = new byte[(cipherKeySize / 8) + (macKeySize / 8)];

            cipher.init(true, new KeyParameter(buf, 0, (cipherKeySize / 8)));

            c_text_length = cipher.getOutputSize(inLen);

            C = new byte[c_text_length + mac.getMacSize()];

            int off = cipher.processBytes(in, inOff, inLen, C, 0);

            cipher.doFinal(C, off);

            macKey = new KeyParameter(buf, (cipherKeySize / 8), (macKeySize / 8));
        }

        byte[]  macIV = param.getEncodingV();

        mac.init(macKey);
        mac.update(C, 0, c_text_length);
        mac.update(macIV, 0, macIV.length);
        //
        // return the message and it's MAC
        //
        mac.doFinal(C, c_text_length);
        return C;
    }

    public byte[] processBlock(
        byte[]  in,
        int     inOff,
        int     inLen)
        throws InvalidCipherTextException
    {
        agree.init(privParam);

        BigInteger  z = agree.calculateAgreement(pubParam);

        if (forEncryption)
        {
            return encryptBlock(in, inOff, inLen, z.toByteArray());
        }
        else
        {
            return decryptBlock(in, inOff, inLen, z.toByteArray());
        }
    }
}
