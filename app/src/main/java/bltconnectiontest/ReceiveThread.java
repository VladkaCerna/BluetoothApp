package bltconnectiontest;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by cernav1 on 7.3.2018.
 */

public class ReceiveThread extends Thread {
    private Context context;
    private BluetoothSocket socket;

    public ReceiveThread() {

    }

    public ReceiveThread(BluetoothSocket socket, Context context) {
        this.socket = socket;
        this.context = context;
    }

    public void run() {
//        try {
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//            //listening if there is any message to be received and processed
//            while (true) {
//                final String response = in.readLine();
//                if (response != null) {
//                    MessageManager messageManager = MessageManager.GetMananager(this.context);
//                    Message msg = messageManager.Receive(response);
//                    if (msg.getType() == Message.PayloadType.EchoRequest) {
//                        messageManager.processMessage(msg);
//                    }
//
//                }
//
//                if (!socket.isConnected()) {
//                    break;
//                }
//            }
//            //socket.close();
//        } catch (IOException e) {
//            if(e.getMessage().contains("close")) {
//                return;
//            }
//
//            e.printStackTrace();
//        }
    }
}
