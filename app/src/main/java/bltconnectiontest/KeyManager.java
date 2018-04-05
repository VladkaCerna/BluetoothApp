package bltconnectiontest;

import android.app.Activity;
import android.app.UiAutomation;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;

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

    public boolean isPaired(final Context context, BluetoothDevice device) {
        ConfigManager configManager = new ConfigManager();
        Config config = configManager.getConfig(context);
        if (config == null) {
            config = new Config();
            configManager.setConfig(context, config);
            config = configManager.getConfig(context);
        }

        if (config.getAesKey() == null || config.getAesKey().equals("") || !config.getAddress().equals(device.getAddress())) {
            return false;
        } else {
            return true;
        }
    }

    public void doPairing(final Context context, final BluetoothDevice device) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                processKeyExchange(context, device);
            }
        }).start();
    }

    public void processKeyExchange(final Context context, BluetoothDevice device) {
        MessageManager messageManager = MessageManager.GetMananager(context);
        Message rsaKeyResponseMsg;
        Message echoResponseMsg;

        ConfigManager configManager = new ConfigManager();
        Config config = configManager.getConfig(context);
        String phoneId = config.getPhoneId();

        for(int i = 0; i < 5; i++) {
            //RSA public key exchange
            Message rsaKeyRequestMsg = messageManager.createRsaKeyRequest(phoneId);
            messageManager.Send(rsaKeyRequestMsg);
            try {
                messageManager.timeoutWait(Message.PayloadType.RsaKeyResponse, 5000);
                rsaKeyResponseMsg = messageManager.getMessageFromQueue(Message.PayloadType.RsaKeyResponse);
            } catch (TimeoutException e) {
                continue;
            }
            messageManager.processMessage(rsaKeyResponseMsg);

            //AES key exchange
            Message aesKeyRequestMsg = messageManager.createAesKeyRequest(phoneId);
            messageManager.Send(aesKeyRequestMsg);

            //wait until message is delivered
            messageManager.wait(1000);

            //send echo message
            Message echoRequestMsg = messageManager.createEchoRequest(phoneId);
            messageManager.Send(echoRequestMsg);
            try {
                messageManager.timeoutWait(Message.PayloadType.EchoResponse, 5000);
                echoResponseMsg = messageManager.getMessageFromQueue(Message.PayloadType.EchoResponse);
                String echoRequestString = ((EchoRequest)echoRequestMsg.getPayload()).getRandomString();
                String echoResponseString = ((EchoResponse)echoResponseMsg.getPayload()).getRandomString();
                if(echoRequestString.equals(echoResponseString)) {
                    config.setName(device.getName());
                    config.setAddress(device.getAddress());
                    byte[] key = MessageManager.GetMananager(context).getSecretKeyAes();
                    config.setAesKey(Base64.encodeToString(key, Base64.NO_WRAP));
                    configManager.setConfig(context,config);

//                    ((Activity)context).runOnUiThread(new Runnable() {
//                        public void run() {
//                            Helpers.showToast(context, "SUCCESSFULLY PAIRED");
//                        }
//                    });
                    return;
                } else {
                    continue;
                }
            } catch (TimeoutException e) {
                continue;
            }
        }

        ((Activity)context).runOnUiThread(new Runnable() {
            public void run() {
                Helpers.showToast(context, "UNABLE TO CONNECT");
            }
        });
    }
}
