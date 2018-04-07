package bltconnectiontest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);


        if (!isBtAdapterAvailable()){
            Helpers.showToast(this, "NO BLUETOOTH ADAPTER FOUND.");
            //finish();
            //System.exit(0);
            //Helpers.killAppSafely(this);
        } else {
            getPermission(this);
            enableBluetoothAdapter();
            ConfigManager configManager = new ConfigManager();
            Config config = configManager.getConfig(this);
            if (config == null || config.getPhoneId() == null) {
                config = new Config();
                config.setPhoneId(UUID.randomUUID().toString());
                configManager.setConfig(this, config);
            }
            //Helpers.showToast(this, config.getPhoneId());
            MessageManager messageManager = MessageManager.GetMananager(this);

            if(config.getAesKey() != null) {
                byte[] key = Base64.decode(config.getAesKey(), Base64.NO_WRAP);
                messageManager.setSecretKeyAes(key);
            }

            messageManager.connect();

            registerDevicesButton(this);
            registerSettingsButton(this);
            //startSensorService(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private boolean isBtAdapterAvailable (){
        BluetoothAdapter BtAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean result = false;
        if (BtAdapter != null){
            result = true;
        }
        return result;
    }

    private void enableBluetoothAdapter() {
        BluetoothAdapter BtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!BtAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBT);
        }
    }

//    private void startSensorService(Context context) {
//        Intent mIntent = new Intent(context, SensorService.class);
//        startService(mIntent);
//    }

    private void getPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Only ask for these permissions on runtime when running Android 6.0 or higher
            switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:
                    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    break;
            }
        }
    }

    private void registerSettingsButton(Context context) {
        Button settingsBtn = findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO settings window
            }
        });
    }

    private void registerDevicesButton(final Context context) {
        Button devicesBtn = findViewById(R.id.devicesBtn);
        devicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MyDevicesActivity.class);
                startActivity(intent);
            }
        });
    }

}
