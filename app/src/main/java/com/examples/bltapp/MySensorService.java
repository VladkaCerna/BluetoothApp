package com.examples.bltapp;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MySensorService extends IntentService implements SensorEventListener{

    //private NotificationManager mNotifyMgr;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    public MySensorService() {
        super("SensorService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Helpers.showToast(this, "Sensor changed.");

        if (sensorEvent.values[0] == 1) {
            //popUpWindowShow();
            Intent mIntent = new Intent(this, NotificationActivity.class);
            startActivity(mIntent);

            //mSensorManager.unregisterListener(this);
        }
    }

//    private void popUpWindowShow(final Context context) {
//        AlertDialog.Builder mLockDialog = new AlertDialog.Builder(this);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//        View mView = inflater.inflate(R.layout.lock_dialog, null);
//        Button mBtnYes = mView.findViewById(R.id.btnYes);
//        Button mBtnNo = mView.findViewById(R.id.btnNo);
//        Button mBtnMaybe = mView.findViewById(R.id.btnMaybe);
//
//        mLockDialog.setView(mView);
//        final AlertDialog dialog = mLockDialog.create();
//        dialog.show();
//
//        mBtnYes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.cancel();
//                Toast.makeText(context, "Locked", Toast.LENGTH_SHORT).show();
//                mSensorManager.registerListener(context, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
//            }
//        });
//        mBtnNo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.cancel();
//                Toast.makeText(context, "Not locked", Toast.LENGTH_SHORT).show();
//                mSensorManager.registerListener(context, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
//            }
//        });
//        mBtnMaybe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.cancel();
//                Toast.makeText(context, "Notification triggered", Toast.LENGTH_SHORT).show();
//                //showActionButtonsNotification();
//            }
//        });
//    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

        }
    }















//
//    // TODO: Rename actions, choose action names that describe tasks that this
//    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
//    public static final String ACTION_FOO = "com.examples.bltapp.action.FOO";
//    public static final String ACTION_BAZ = "com.examples.bltapp.action.BAZ";
//
//    // TODO: Rename parameters
//    public static final String EXTRA_PARAM1 = "com.examples.bltapp.extra.PARAM1";
//    public static final String EXTRA_PARAM2 = "com.examples.bltapp.extra.PARAM2";
//
//    public SensorService() {
//        super("SensorService");
//    }
//

//
//    /**
//     * Handle action Foo in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionFoo(String param1, String param2) {
//        // TODO: Handle action Foo
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    /**
//     * Handle action Baz in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionBaz(String param1, String param2) {
//        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");
//    }


}
