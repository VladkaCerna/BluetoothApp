package bltconnectiontest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.examples.bltapp.R;

import java.util.ArrayList;
import java.util.List;


public class DiscoveringDevicesReceiver extends BroadcastReceiver{
    private final BluetoothAdapter BtAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayList<BluetoothDevice> BtDevices;

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            if (state == BluetoothAdapter.STATE_ON) {
                //Helpers.showToast(context, "ACTION_STATE_CHANGED: STATE_ON");
                BtAdapter.startDiscovery();
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            BtDevices = new ArrayList<>();
            //Helpers.showToast(context, "ACTION_DISCOVERY_STARTED");
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            context.unregisterReceiver(this);
            //Helpers.showToast(context,"ACTION_DISCOVERY_FINISHED");
        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {// When discovery finds a device
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BtDevices.add(device);
            List<String> mList = new ArrayList<>();
            for (BluetoothDevice bd : BtDevices) {
                mList.add(bd.getName());
            }
            ArrayAdapter<String> BtArrayAdapter = new ArrayAdapter<>(context, R.layout.devices_list_view, mList);
            //ListView mListView = findViewById(R.id.listView2);
            //mListView.setAdapter(BtArrayAdapter);
            //Helpers.showToast(context,"Device found = " + device.getName());

        }
    }
}
