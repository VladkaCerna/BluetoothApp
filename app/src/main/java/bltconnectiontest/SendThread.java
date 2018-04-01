package bltconnectiontest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

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
