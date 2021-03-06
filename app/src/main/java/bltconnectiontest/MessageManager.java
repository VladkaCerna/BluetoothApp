package bltconnectiontest;

import android.content.Context;
import android.util.Base64;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by cernav1 on 13.3.2018.
 */

public class MessageManager {
    private static MessageManager inst = null;

    private static BluetoothThread bltThread = null;
    private static ArrayList<Message> messageQueue;
    private static byte[] secretKeyAes;
    private static AsymmetricKeyParameter publicKeyRsa;
    private static Context context;

    //implements singleton pattern
    private MessageManager(Context context) {
        messageQueue = new ArrayList<>();
        MessageManager.context = context;
    }

    public static MessageManager getMananager(Context context) {
        if (inst == null) {
            inst = new MessageManager(context);
        }

        return inst;
    }

    public byte[] getSecretKeyAes() {
        return secretKeyAes;
    }

    public void setSecretKeyAes(byte[] key) {
        secretKeyAes = key;
    }

    public void connect() {
        if (bltThread == null) {
            bltThread = new BluetoothThread(context);
            bltThread.start();
        }
    }

    public void disconnect() {
        bltThread.setShutdownFlag(true);
        if (bltThread.getmSocket() != null) {
            try {
                bltThread.getmSocket().close();
                bltThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bltThread = null;
    }

    public void send(Message message) {
        String messageToSend = buildMessage(message);

        if (messageToSend != null) {
            while (true) {
                if (bltThread.getmSocket() != null) {
                    bltThread.send(messageToSend);
                    break;
                }
            }
        } else {
            //TODO if message build fails
        }
    }

    // decrypt received message and parse to Message object
    public Message receive(String response) {
        Message m = new Message();

        //first byte of message indicates whether it is rsa encrypted, aes encrypted or plaintext
        //message with first byte decoded from Base64
        byte[] messageByteExtended = Base64.decode(response, Base64.DEFAULT);

        //removal of the first byte
        byte[] messageByte = Arrays.copyOfRange(messageByteExtended, 1, messageByteExtended.length);
        String messageDecrypted = null;

        //received messages can be only aes encrypted
        //decrypt or convert byte[] to string
        switch (Message.MessageFormat.valueOf(messageByteExtended[0])) {
            case EncryptedAes:
                if (secretKeyAes != null) {
                    AesCipherizer cipherizer = new AesCipherizer();
                    messageDecrypted = cipherizer.decryptMessage(messageByte, secretKeyAes);
                }
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

    public void processMessage(Message message) {
        //if message contains rsa public key, save it for further communication
        switch (message.getType()) {
            case RsaKeyResponse:
                RsaCipherizer cipherizer = new RsaCipherizer();
                publicKeyRsa = cipherizer.publicKeyFromString(((RsaKeyResponse) message.getPayload()).getPublicKey());
                break;

            case EchoRequest:
                Message echoResponse = createAnswer(message);
                send(echoResponse);
                break;

            case RsaKeyRequest:
                KeyManager.getKeyManager().doPairing(context, bltThread.getBtDevice());

                break;
        }
    }

    public Message createAnswer(Message message) {
        Message msg = new Message();

        switch (message.getType()) {
            case EchoRequest:
                String phoneId = ((EchoRequest) message.getPayload()).getPhoneId();
                String randomString = ((EchoRequest) message.getPayload()).getRandomString();
                msg = new Message(Message.MessageFormat.EncryptedAes);
                msg.setType(Message.PayloadType.EchoResponse);
                msg.setPayload(new EchoResponse(phoneId, randomString));
                break;
        }

        return msg;
    }

    public Message createAesKeyRequest(String phoneId) {
        Message message = new Message();

        //generation of new aes key
        AesCipherizer cipherizer = new AesCipherizer();
        secretKeyAes = cipherizer.getNewKey(AesKeySize.Aes_128);
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

    private String buildMessage(Message message) {
        //initialize Json serializer
        byte[] finalMessage;
        JsonSerializer serializer = new JsonSerializer();
        String messageSerialized = serializer.SerializeToJson(message);
        byte[] messageData = null;


        //encrypt message
        switch (message.getEncryption()) {
            case EncryptedAes:
                AesCipherizer aesCipherizer = new AesCipherizer();
                messageData = aesCipherizer.encryptMessage(messageSerialized, secretKeyAes);
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

    public void wait(int millis) {
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
