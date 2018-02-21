package com.examples.bltapp;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BtCommIntentService extends IntentService {
    public BluetoothSocket mSocket;
    public String address = "A4:34:D9:FC:72:17";
    public static BluetoothDevice BtDevice;
    private final UUID MY_UUID = UUID.fromString("60caf2ae-c940-4610-8d06-da4fd80b80ef");
    private final String TAG = null;

    public static final String ACTION_SEND_MESSAGE = "send_message";
    public static final String EXTRA_MESSAGE = "message";

    public BtCommIntentService() {
        super("BtCommIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (ACTION_SEND_MESSAGE.equals(action)) {
                final String mMessage = intent.getStringExtra(EXTRA_MESSAGE);
                sendMessage(mMessage, address);
            }
        }
    }

    private void sendMessage(String mMessage, String deviceAddress) {
        BluetoothAdapter BtAdapter = BluetoothAdapter.getDefaultAdapter();
        BtDevice = BtAdapter.getRemoteDevice(deviceAddress);
        if (BtDevice == null) {
            Toast.makeText(this, "Bluetooth zarizeni nenalezeno", Toast.LENGTH_LONG).show();
        } else {
            mSocket = createClientSocket(BtDevice, MY_UUID);

            if (mSocket == null) {
                Toast.makeText(this, "Socket null", Toast.LENGTH_LONG).show();
            } else {
                try {
                    mSocket.connect();
                    Toast.makeText(this, "Socket connected", Toast.LENGTH_LONG).show();
                    OutputStream outputStream = mSocket.getOutputStream();
                    outputStream.write(ToByteArray(mMessage));
                    outputStream.flush();
                    outputStream.close();
                    mSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Send message failed", e);
                }
            }
        }
    }

    private BluetoothSocket createClientSocket(BluetoothDevice device, UUID uuid) {
        BluetoothSocket tmp = null;
        try
        {
            tmp = device.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e)
        {
            Log.e(TAG, "Socket create() failed", e);
        }
        return tmp;
    }

    private byte[] ToByteArray (String message) {
        short length = (short) message.length();
        byte[] returnArray = new byte[length + 2];
        returnArray[1] = (byte)(length >>> 8);
        returnArray[0] = (byte) length;
        System.arraycopy(message.getBytes(),0, returnArray, 2, message.getBytes().length);

        return returnArray;
    }

}
