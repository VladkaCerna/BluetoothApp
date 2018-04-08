package bltconnectiontest;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by cernav1 on 7.3.2018.
 */

public class SendThread extends Thread{
    private BluetoothSocket socket;
    private String message;

    public SendThread() {
    }

    public SendThread(BluetoothSocket socket, String message)
    {
        this.socket = socket;
        this.message = message;
    }

    public void run() {
        final PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.print(message + "\r\n");
            printWriter.flush();
        }catch (IOException e) {
                e.printStackTrace();
        }
    }
}
