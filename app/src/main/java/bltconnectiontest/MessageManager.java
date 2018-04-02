package bltconnectiontest;

import android.content.Context;
import android.util.Base64;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by cernav1 on 13.3.2018.
 */

public class MessageManager {
    private static BluetoothThread bltThread;
    private static MessageManager inst = null;
    private static ArrayList<Message> messageQueue;
    private static byte[] secretKeyAes;
    private static AsymmetricKeyParameter publicKeyRsa;

    //implements singleton pattern
    private MessageManager(Context context) {
        bltThread = new BluetoothThread(context);
        bltThread.start();
        messageQueue = new ArrayList<>();
    }

    public static MessageManager GetMananager(Context context) {
        if (inst == null) {
            inst = new MessageManager(context);
        }

        return inst;
    }

    public ArrayList<Message> getMessageQue() {
        return messageQueue;
    }

    public byte[] getSecretKeyAes() {
        return secretKeyAes;
    }

    public void Send(Message message) {
        String messageToSend = BuildMessage(message);

        if (messageToSend != null) {
            while (true) {
                if (bltThread.mSocket.isConnected()) {
                    bltThread.send(messageToSend);
                    break;
                }
            }
        } else {
            //TODO if message build fails
        }
    }

    // decrypt received message and parse to Message object
    public Message Receive(String response) {
        Message m = new Message();

        //first byte of message indicates whether it is rsa encrypted, aes encrypted or plaintext
        //message with first byte decoded from Base64
        byte[] messageByteExtended = Base64.decode(response, Base64.DEFAULT);

        //removal of the first byte
        byte[] messageByte = Arrays.copyOfRange(messageByteExtended, 1, messageByteExtended.length);
        String messageDecrypted = null;

        //received messages can be only aes encrypted
        //decrypt or convert byte[] to string
        switch (Message .MessageFormat.valueOf(messageByteExtended[0])) {
            case EncryptedAes:
                AesCipherizer cipherizer = new AesCipherizer();
                messageDecrypted = cipherizer.aes_decryptMessage(messageByte, secretKeyAes);
                break;

            case PlainText:
            case EncryptedRsa:
            default:
                messageDecrypted = new String(messageByte);
                break;
        }

        //deserialize decrypted message and add it to the queue
        if (messageDecrypted != null) {
            JsonSerializer serializer = new JsonSerializer();
            m = (Message) serializer.DeserializeFromJson(messageDecrypted, Message.class);
            if (m != null) {
                messageQueue.add(m);
            }
        }

        return m;
    }

    public Message createAesKeyRequest(String phoneId) {
        Message message = new Message();

        //generation of new aes key
        AesCipherizer cipherizer = new AesCipherizer();
        secretKeyAes = cipherizer.aes_getNewKey(AesCipherizer.AesKeySize.Aes_128);
        String aesKeyBase64 = Base64.encodeToString(secretKeyAes, Base64.NO_WRAP);

        //final message will be encrypted with rsa public key
        message.setEncryption(Message.MessageFormat.EncryptedRsa);
        message.setType(Message.PayloadType.AesKeyRequest);
        message.setPayload(new AesKeyRequest(aesKeyBase64, phoneId));

        return message;
    }

    public Message createRsaKeyRequest(String phoneId) {
        Message message = new Message(Message.MessageFormat.PlainText);
        message.setPayload(new RsaKeyRequest(phoneId));
        message.setType(Message.PayloadType.RsaKeyRequest);

        return message;
    }

    public Message createEchoRequest(String phoneId) {
        //get random string
        String randomEchoMsg = UUID.randomUUID().toString();
        Message message = new Message(Message.MessageFormat.EncryptedAes);
        message.setPayload(new EchoRequest(phoneId, randomEchoMsg));
        message.setType(Message.PayloadType.EchoRequest);

        return message;
    }

    public Message createLockRequest(String phoneId) {
        Message message = new Message(Message.MessageFormat.EncryptedAes);
        message.setPayload(new LockRequest(phoneId));
        message.setType(Message.PayloadType.LockRequest);

        return message;
    }

    public Message createUnlockRequest(String phoneId) {
        Message message = new Message(Message.MessageFormat.EncryptedAes);
        message.setPayload(new LockRequest(phoneId));
        message.setType(Message.PayloadType.UnlockRequest);

        return message;
    }


    public void processMessage(Message message) {
        //if message contains rsa public key, save it for further communication
        switch (message.getType()) {
            case RsaKeyResponse:
                RsaCipherizer cipherizer = new RsaCipherizer();
                publicKeyRsa = cipherizer.publicKeyFromString(((RsaKeyResponse) message.getPayload()).getPublicKey());
                break;
        }
    }

    private String BuildMessage(Message message) {
        //initialize Json serializer
        byte[] finalMessage;
        JsonSerializer serializer = new JsonSerializer();
        String messageSerialized = serializer.SerializeToJson(message);
        byte[] messageData = null;

        //encrypt message
        switch (message.getEncryption()) {
            case EncryptedAes:
                AesCipherizer aesCipherizer = new AesCipherizer();
                messageData = aesCipherizer.aes_encryptMessage(messageSerialized, secretKeyAes);
                break;
            case PlainText:
                messageData = messageSerialized.getBytes();
                break;
            case EncryptedRsa:
                RsaCipherizer rsaCipherizer = new RsaCipherizer();
                messageData = rsaCipherizer.encryptWithPublicRSA(messageSerialized, publicKeyRsa);
                break;
        }

        //add encryption flag at the beginning of the message
        byte[] isEncryptedFlag = {message.getEncryption().getId()};
        finalMessage = new byte[messageData.length + 1];
        System.arraycopy(isEncryptedFlag, 0, finalMessage, 0, isEncryptedFlag.length);
        System.arraycopy(messageData, 0, finalMessage, isEncryptedFlag.length, messageData.length);

        //return encoded to Base64
        return Base64.encodeToString(finalMessage, Base64.NO_WRAP);
    }

    //measurement of timeout for response message from desktop computer
    public void timeoutWait(Message.PayloadType type, int timeoutMillis) throws TimeoutException {
        // wait 10 ms in each call
        int waitTime = 10;

        for (int i = 0; i < (timeoutMillis / waitTime); i++) {
            try {
                for (Message m : messageQueue) {
                    if (m.getType() == type) {
                        return;
                    }
                }

                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
            }
        }

        throw new TimeoutException("Timeout was reached while waiting for " + type.toString());
    }

    public void wait (int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Message getMessageFromQueue(Message.PayloadType type) {
        for (Message m : messageQueue) {
            if (m.getType() == type) {
                messageQueue.remove(m);
                return m;
            }
        }
        return null;
    }
}
