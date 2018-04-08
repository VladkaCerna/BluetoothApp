package bltconnectiontest;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by cernav1 on 12.3.2018.
 *
 */

public class AesCipherizer {

    public AesCipherizer() {
    }

    public byte[] getNewKey(AesKeySize keySize) {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGenerator.init(keySize.getId());
        SecretKey key = keyGenerator.generateKey();

        return key.getEncoded();
    }

    public byte[] encryptMessage(String message, byte[] key) {
        byte[] messageCombInitVect = null;
        try {
            //init cipherizer
            SecureRandom random = new SecureRandom();
            SecretKeySpec keyAes = new SecretKeySpec(key, "AES");
            byte[] initializationVectorByte = new byte[key.length];
            random.nextBytes(initializationVectorByte);
            IvParameterSpec initializationVector = new IvParameterSpec(initializationVectorByte);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keyAes, initializationVector);

            //encrypt message
            byte[] encryptedMessage = cipher.doFinal(message.getBytes());

            //copy initialization vector to message
            messageCombInitVect = new byte[encryptedMessage.length + initializationVectorByte.length];
            System.arraycopy(initializationVectorByte, 0, messageCombInitVect, 0, initializationVectorByte.length);
            System.arraycopy(encryptedMessage, 0, messageCombInitVect, initializationVectorByte.length, encryptedMessage.length);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return messageCombInitVect;
    }

    public String decryptMessage(byte[] encryptedMessage, byte[] secretKeyAes) {
        byte[] decryptedMessage = null;
        try {
            //create cipherizer
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyAes = new SecretKeySpec(secretKeyAes, "AES");

            //extract init vector from message and init cipherizer
            IvParameterSpec initializationVector = new IvParameterSpec(encryptedMessage, 0, secretKeyAes.length);
            cipher.init(Cipher.DECRYPT_MODE, keyAes, initializationVector);

            //decrypt message
            decryptedMessage = cipher.doFinal(encryptedMessage, secretKeyAes.length, encryptedMessage.length - secretKeyAes.length);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return new String(decryptedMessage, StandardCharsets.UTF_8);
    }

}
