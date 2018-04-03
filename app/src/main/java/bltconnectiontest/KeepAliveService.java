package bltconnectiontest;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import org.bouncycastle.jcajce.provider.digest.Whirlpool;

import java.util.concurrent.TimeoutException;

public class KeepAliveService extends Service {
    private Handler handler;
    private Context context;

    public KeepAliveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(runnable, 1000);
        return START_STICKY;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Message echoResponseMsg;
            Config config = new ConfigManager().getConfig(context);
            MessageManager messageManager = MessageManager.GetMananager(context);
            for(int i = 0; i < 3; i++) {
                try {
                    Message echoRequestMsg = messageManager.createEchoRequest(config.getPhoneId());
                    messageManager.Send(echoRequestMsg);
                    messageManager.timeoutWait(Message.PayloadType.EchoResponse, 300);
                    echoResponseMsg = messageManager.getMessageFromQueue(Message.PayloadType.EchoResponse);
                    String echoRequestString = ((EchoRequest)echoRequestMsg.getPayload()).getRandomString();
                    String echoResponseString = ((EchoResponse)echoResponseMsg.getPayload()).getRandomString();

                    if(echoRequestString.equals(echoResponseString)) {
                        return;
                    } else {
                        BluetoothDevice device = messageManager.getBltThread().getBtDevice();
                        KeyManager keyManager = KeyManager.getKeyManager();
                        keyManager.doPairing(context, device);
                    }
                } catch (TimeoutException e) {
                    continue;
                }
            }

            messageManager.Connect(context);


            handler.postDelayed(this, 1000);
        }
    };
}
