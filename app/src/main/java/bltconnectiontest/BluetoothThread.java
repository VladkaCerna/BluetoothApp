package bltconnectiontest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * Created by cernav1 on 8.3.2018.
 */

public class BluetoothThread extends Thread {

    private Context context;
    public BluetoothSocket mSocket;
    public String address = "A4:34:D9:FC:72:17";
    private BluetoothDevice BtDevice;
    private final UUID MY_UUID = UUID.fromString("60caf2ae-c940-4610-8d06-da4fd80b80ef");

    public BluetoothThread() {
    }

    public BluetoothThread(Context context) {
        this.context = context;
    }

    public BluetoothDevice getBtDevice() {
        return BtDevice;
    }

    private BluetoothSocket connectAsClient(BluetoothDevice device, UUID uuid) {
        BluetoothSocket socket = null;

        try {
            //uses insecure communication
            socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
        }
        return socket;
    }

    public void send(String message) {
        if (mSocket.isConnected()) {
            new SendThread(mSocket, message).start();
        }
    }

    public void run() {
        BluetoothAdapter BtAdapter = BluetoothAdapter.getDefaultAdapter();
        BtDevice = BtAdapter.getRemoteDevice(address);
        if (BtDevice != null) {

            mSocket = connectAsClient(BtDevice, MY_UUID);

            if (mSocket != null) {
                while (true) {
                    try {
                        mSocket.connect();

                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

                            //listening if there is any message to be received and processed
                            while (true) {
                                final String response = in.readLine();
                                if (response != null) {
                                    MessageManager messageManager = MessageManager.GetMananager(this.context);
                                    Message msg = messageManager.Receive(response);
                                    if (msg.getType() == Message.PayloadType.EchoRequest) {
                                        messageManager.processMessage(msg);
                                    }
                                }
                            }
                        } catch (IOException e) {
                        }
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
}
