package bltconnectiontest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by cernav1 on 8.3.2018.
 */

public class BluetoothThread extends Thread {

    private Context context;
    public BluetoothSocket mSocket;
    public String address = "A4:34:D9:FC:72:17";
    public BluetoothDevice BtDevice;
    private final UUID MY_UUID = UUID.fromString("60caf2ae-c940-4610-8d06-da4fd80b80ef");

    public BluetoothThread() {
    }

    public BluetoothThread(Context context) {
        this.context = context;
    }

    private BluetoothSocket connectAsClient (BluetoothDevice device, UUID uuid) {
        BluetoothSocket socket = null;

        try
        {
            //uses insecure communication
            socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e)
        {
        }
        return socket;
    }

    public void send(String message) {
        if (mSocket.isConnected())
        {
            new SendThread(mSocket, message).start();
        }
    }

    public void run() {
        BluetoothAdapter BtAdapter = BluetoothAdapter.getDefaultAdapter();
        BtDevice = BtAdapter.getRemoteDevice(address);
        if (BtDevice == null) {
        } else {
            mSocket = connectAsClient(BtDevice, MY_UUID);

            if (mSocket != null) {
                try {
                    mSocket.connect();

                    while (true)
                    {
                        if(mSocket.isConnected())
                        {
                            new ReceiveThread(mSocket, context).start();
                            break;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
