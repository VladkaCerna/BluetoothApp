package bltconnectiontest;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.x509.KeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.AES;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

import android.util.Base64;

/**
 * Created by cernav1 on 18.3.2018.
 */

public class RsaCipherizer {
    public enum RsaKeySize
    {
        Rsa_1024 (1024),
        Rsa_2048 (2048),
        Rsa_4096 (4096);

        private final int id;

        RsaKeySize(int id) {
            this.id = id;
        }

        public int getId()
        {
            return this.id;
        }
    }

    public RsaCipherizer(){

    }

    public AsymmetricCipherKeyPair GetNewKeyPair(RsaKeySize keySize) {
        final int CERTAINTY = 80;
        BigInteger publicExponent = new BigInteger("10001", 16);
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        RSAKeyGenerationParameters generationParameters = new RSAKeyGenerationParameters(publicExponent, random, keySize.getId(),CERTAINTY);
        RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
        keyPairGenerator.init(generationParameters);
        AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    //restoring public key from string
    public RSAKeyParameters publicKeyFromString(String publicKey) {
        RSAKeyParameters key = null;
        try {
            key = (RSAKeyParameters) PublicKeyFactory.createKey(Base64.decode(publicKey, Base64.NO_WRAP));
        } catch (IOException e) {
            e.printStackTrace();
        }
         return key;
    }

    //converts public key to string
    public static String publicKeyToString(AsymmetricKeyParameter publicKey) {
        String key = null;

        RSAKeyParameters rsaKey=(RSAKeyParameters)publicKey;
        ASN1EncodableVector encodable=new ASN1EncodableVector();
        encodable.add(new ASN1Integer(rsaKey.getModulus()));
        encodable.add(new ASN1Integer(rsaKey.getExponent()));
        byte[] keykey = KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption,new DERNull()),new DERSequence(encodable));
        key = Base64.encodeToString(keykey, Base64.NO_WRAP);

        return key;
    }

    public byte[] encryptWithPublicRSA(String msg, AsymmetricKeyParameter publicKey) {
        //cipherizer initialization
        RSAEngine rsaEngine = new RSAEngine();
        PKCS1Encoding rsaEncoder = new PKCS1Encoding(rsaEngine);
        rsaEncoder.init(true, publicKey);

        byte[] msgByte = msg.getBytes();
        byte[] msgEncrypted = new byte[0];

        int blockSize = 240;
        int blockNr = msgByte.length/blockSize + 1;

        //encrypts message block by block
        for (int i = 0; i < blockNr; i++)
        {
            byte[] encryptedBlock = null;
            //if the block to be encrypted is not the last
            if((msgByte.length - blockSize*i)> blockSize) {
                try {
                    encryptedBlock = rsaEncoder.processBlock(msgByte, i*blockSize, blockSize);
                } catch (InvalidCipherTextException e) {
                    e.printStackTrace();
                }
            //if the block to be encrypt is the last block in message
            } else {
                try {
                    encryptedBlock = rsaEncoder.processBlock(msgByte, i*blockSize, (msgByte.length-blockSize*i));
                } catch (InvalidCipherTextException e) {
                    e.printStackTrace();
                }
            }
            //copy encrypted block to message
            byte[] newArray = new byte[encryptedBlock.length + msgEncrypted.length];
            System.arraycopy(msgEncrypted, 0, newArray, 0, msgEncrypted.length);
            msgEncrypted = newArray;

            System.arraycopy(encryptedBlock,0 , msgEncrypted, i*blockSize, encryptedBlock.length);
        }

        return msgEncrypted;
    }

    public String decryptWithPrivateRSA(byte[] msgByte, AsymmetricKeyParameter privateKey){
        RSAEngine rsaEngine = new RSAEngine();
        PKCS1Encoding rsaEncoder = new PKCS1Encoding(rsaEngine);


        rsaEncoder.init(false, privateKey);

        byte[] msgDecryptedByte = null;
        try {
            msgDecryptedByte = rsaEncoder.processBlock(msgByte, 0, msgByte.length);
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }

        String msgDecryptedString = new String(msgDecryptedByte, StandardCharsets.UTF_8);

        return msgDecryptedString;
    }
}
