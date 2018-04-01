package com.examples.bltapp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyDevicesActivity extends AppCompatActivity {
    OnSharedPreferenceChangeListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displaySavedDevices(this, getSharedPrefs(this));
        startSensorService(this);
        plusBtnOnClick();
        minusBtnOnClick(this);
        registerOnSharedPreferenceChangeListener(getSharedPrefs(this), getNewSharedPreferencesListener(this));
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
                deleteData(getSharedPrefs(activity));
            }
        });
    }

    private void displaySavedDevices(Activity activity, SharedPreferences sharedPrefs) {
        Map<String, ?> pairedDevices = sharedPrefs.getAll();
        List<String> devicesList = new ArrayList<>();
        for (Map.Entry<String, ?> entry : pairedDevices.entrySet()) {
            devicesList.add(entry.getValue().toString());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, R.layout.devices_list_view, devicesList);
        ListView mListView = activity.findViewById(R.id.listView);
        mListView.setAdapter(arrayAdapter);
    }

    private OnSharedPreferenceChangeListener getNewSharedPreferencesListener(final Activity activity) {
        mListener = new OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                displaySavedDevices(activity, sharedPreferences);
            }
        };

        return mListener;
    }

    private void deleteData(SharedPreferences sharedPrefs) {
        Map<String, ?> pairedDevices = sharedPrefs.getAll();
        SharedPreferences.Editor editor = sharedPrefs.edit();

        for (Map.Entry<String, ?> entry : pairedDevices.entrySet()) {
            editor.remove(entry.getKey().toString());
        }
        editor.commit();
    }

    private void registerOnSharedPreferenceChangeListener(SharedPreferences sharedPrefs, OnSharedPreferenceChangeListener mListener) {
        sharedPrefs.registerOnSharedPreferenceChangeListener(mListener);
    }

    private SharedPreferences getSharedPrefs(Activity activity) {
        return activity.getSharedPreferences("com.examples.bltapp", Context.MODE_PRIVATE);
    }
}
