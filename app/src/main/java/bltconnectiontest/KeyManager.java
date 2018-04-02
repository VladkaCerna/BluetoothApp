package bltconnectiontest;

import android.content.Context;

import java.util.concurrent.TimeoutException;

/**
 * Created by cernav1 on 1.4.2018.
 */

public class KeyManager {

    private static KeyManager inst = null;

    private KeyManager() {
    }

    public static KeyManager getKeyManager() {
        if (inst == null) {
            inst = new KeyManager();
        }

        return inst;
    }

    public boolean isPaired(final Context context) {
        ConfigManager configManager = new ConfigManager();
        Config config = configManager.getConfig(context);
        if (config == null) {
            config = new Config();
            configManager.setConfig(context, config);
            config = configManager.getConfig(context);
        }

        if (true || config.getAesKey() == null || config.getAesKey().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public void getPaired(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                processKeyExchange(context);
            }
        }).start();
    }

    public void processKeyExchange(Context context) {
        MessageManager messageManager = MessageManager.GetMananager(context);
        Message rsaKeyResponseMsg;
        Message echoResponseMsg;

        for(int i = 0; i < 5; i++) {
            //RSA public key exchange
            Message rsaKeyRequestMsg = messageManager.createRsaKeyRequest();
            messageManager.Send(rsaKeyRequestMsg);
            try {
                messageManager.timeoutWait(Message.PayloadType.RsaKeyResponse, 3000);
                rsaKeyResponseMsg = messageManager.getMessageFromQueue(Message.PayloadType.RsaKeyResponse);
            } catch (TimeoutException e) {
                continue;
            }
            messageManager.processMessage(rsaKeyResponseMsg);

            //AES key exchange
            Message aesKeyRequestMsg = messageManager.createAesKeyRequest();
            messageManager.Send(aesKeyRequestMsg);

            //wait until message is delivered
            messageManager.wait(500);

            //send echo message
            Message echoRequestMsg = messageManager.createEchoRequest();
            messageManager.Send(echoRequestMsg);
            try {
                messageManager.timeoutWait(Message.PayloadType.EchoResponse, 3000);
                echoResponseMsg = messageManager.getMessageFromQueue(Message.PayloadType.EchoResponse);
                String echoRequestString = ((EchoRequest)echoRequestMsg.getPayload()).getRandomString();
                String echoResponseString = ((EchoResponse)echoResponseMsg.getPayload()).getRandomString();
                if(echoRequestString.equals(echoResponseString)) {
                    ConfigManager configManager = new ConfigManager();
                    Config config = configManager.getConfig(context);
                    config.setAesKey(new String(MessageManager.GetMananager(context).getSecretKeyAes()));
                    configManager.setConfig(context,config);
                    return;
                } else {
                    continue;
                }
            } catch (TimeoutException e) {
                continue;
            }
        }
        //TODO if keyExchange fails
    }
}
