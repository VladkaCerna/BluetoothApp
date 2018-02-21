package com.examples.bltapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class NotificationActivity extends AppCompatActivity {
    SensorService mService;
    boolean mBound = false;
    public static final String EXTRA_MESSAGE = "message";
    public static final String MESSAGE = "lock";
    public static final String ACTION_SEND_MESSAGE = "send_message";

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SensorService.LocalBinder binder = (SensorService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Intent intent = new Intent(this, SensorService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        //Helpers.showToast(this, "Pruhledna aktivita");
        popUpWindowShow(this);
    }

    private void popUpWindowShow(final Context context) {
        AlertDialog.Builder mLockDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View mView = inflater.inflate(R.layout.lock_dialog, null);
        Button mBtnYes = mView.findViewById(R.id.btnYes);
        Button mBtnNo = mView.findViewById(R.id.btnNo);
        Button mBtnMaybe = mView.findViewById(R.id.btnMaybe);
        mLockDialog.setView(mView);
        final AlertDialog dialog = mLockDialog.create();
        dialog.show();

        mBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                mService.unregisterSensorListener();
                sendLockMessage();
                finish();
            }
        });
        mBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                mService.registerSensorListener();
                finish();
            }
        });
        mBtnMaybe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                mService.unregisterSensorListener();
                mService.showActionButtonsNotification();
                finish();
            }
        });
    }

    void sendLockMessage(){
        Intent intent = new Intent(this, BtCommIntentService.class);
        intent.putExtra(EXTRA_MESSAGE, MESSAGE);
        intent.setAction(ACTION_SEND_MESSAGE);
        startService(intent);
    }

}
