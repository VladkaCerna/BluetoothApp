package bltconnectiontest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class MyDevicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Config config = (new ConfigManager()).getConfig(this);
        if (config.getName() != null) {
            displaySavedDevices(this, config);
        }
        startSensorService(this);
        plusBtnOnClick();
        minusBtnOnClick(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void searchingWindowShow() {
        Intent intent = new Intent(this, DiscoveringActivity.class);
        startActivity(intent);
    }

    private void startSensorService(Context context) {
        Intent mIntent = new Intent(context, SensorService.class);
        startService(mIntent);
        //finish();
    }

    private void plusBtnOnClick() {
        ImageButton plusBtn = findViewById(R.id.plusBtn);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchingWindowShow();
            }
        });
    }

    private void minusBtnOnClick(final Activity activity) {
        ImageButton minusBtn = findViewById(R.id.minusBtn);
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigManager configManager = new ConfigManager();
                Config config = configManager.getConfig(activity);
                config.setName("");
                config.setAddress(null);
                config.setAesKey(null);
                configManager.setConfig(activity, config);

                displaySavedDevices(activity, config);
            }
        });
    }

    private void displaySavedDevices(Activity activity, Config config) {
        if (config.getName() != null) {
            List<String> devicesList = Arrays.asList(config.getName());
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, R.layout.devices_list_view, devicesList);
            ListView mListView = activity.findViewById(R.id.listView);
            mListView.setAdapter(arrayAdapter);
        }
    }

}
