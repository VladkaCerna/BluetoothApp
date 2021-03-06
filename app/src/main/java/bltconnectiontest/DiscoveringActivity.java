package bltconnectiontest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass.Device;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscoveringActivity extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH = 1;
    private BluetoothAdapter BtAdapter;
    private ArrayList<BluetoothDevice> BtDevices;
    private BluetoothDevice selectedDevice;
    private final List<Integer> computerDevices = Arrays.asList(Device.COMPUTER_DESKTOP, Device.COMPUTER_HANDHELD_PC_PDA,
            Device.COMPUTER_LAPTOP, Device.COMPUTER_PALM_SIZE_PC_PDA, Device.COMPUTER_SERVER, Device.COMPUTER_UNCATEGORIZED,
            Device.COMPUTER_WEARABLE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovering);
        BtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        searchBtnOnClick();
        bluetoothSearchForDevices();
    }

    @Override
    public void onResume() {
        super.onResume();
        onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        BtAdapter.cancelDiscovery();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent(this, MyDevicesActivity.class);
        startActivity(mIntent);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    BtAdapter.startDiscovery();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                ListView mListView = findViewById(R.id.listView2);
                mListView.setAdapter(null);
                BtDevices = new ArrayList<>();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // When discovery finds a device
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                int btDeviceType = device.getBluetoothClass().getDeviceClass();
                if (computerDevices.contains(btDeviceType)) {
                    BtDevices.add(device);
                }

                List<String> mList = new ArrayList<>();
                for (BluetoothDevice bd : BtDevices) {
                    mList.add(bd.getName());
                }
                ArrayAdapter<String> BtArrayAdapter = new ArrayAdapter<>(context, R.layout.devices_list_view, mList);
                ListView mListView = findViewById(R.id.listView2);
                mListView.setAdapter(BtArrayAdapter);

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        TextView textView = view.findViewById(R.id.txtV);
                        for (BluetoothDevice d : BtDevices) {
                            if (d.getName().equals(textView.getText().toString()))
                                selectedDevice = d;
                        }

                        KeyManager keyManager = KeyManager.getKeyManager();
                        if (keyManager.isPaired(context, selectedDevice)) {
                        } else {
                            keyManager.doPairing(context, selectedDevice);
                        }
                        BtAdapter.cancelDiscovery();
                    }
                });
            }
        }
    };

    private void bluetoothSearchForDevices() {
        if (!BtAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        } else {
            BtAdapter.startDiscovery();
        }
        registerReceiver();
    }

    private IntentFilter getNewFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        return filter;
    }

    private void searchBtnOnClick() {
        ImageButton searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BtAdapter.isDiscovering()) {
                    BtAdapter.cancelDiscovery();
                }
                BtAdapter.startDiscovery();
            }
        });
    }

    private void registerReceiver() {
        IntentFilter mFilter = getNewFilter();
        registerReceiver(mReceiver, mFilter);
    }
}
