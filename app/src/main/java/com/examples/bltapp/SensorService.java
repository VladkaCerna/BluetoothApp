package com.examples.bltapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import java.lang.reflect.Field;

public class SensorService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private final IBinder mBinder = new LocalBinder();
    private static final String NO_ACTION = "";
    private NotificationManager mNotifyMgr;

    public SensorService() {
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
//        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
//        Helpers.showToast(this, "Service turned on");
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        //Helpers.showToast(this, "Service turned on");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Helpers.showToast(this, "Sensor changed.");
        if (sensorEvent.values[0] == 1) {
            mSensorManager.unregisterListener(this);
            Intent mIntent = new Intent(this, NotificationActivity.class);
            startActivity(mIntent);
        }
    }

    public void unregisterSensorListener() {
        mSensorManager.unregisterListener(this);
    }

    public void registerSensorListener() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public class LocalBinder extends Binder {
        SensorService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SensorService.this;
        }
    }

    public void showActionButtonsNotification() {
        Intent mIntent = new Intent(this, NotificationActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
        PendingIntent dIntent = createOnDismissedIntent(this, 1);

        Notification mNotification  = new Notification.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name))
                .setSmallIcon(R.drawable.ic_action_name)
                .setContentTitle("Uzamceni plochy")
                .setContentText("Potahnutim rozbal")
                //.setContentIntent(pIntent)
                .setDeleteIntent(dIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_action_name, "Yes", pIntent)
                .addAction(R.drawable.ic_action_name, "No", pIntent)
                .addAction(R.drawable.ic_action_name, "Pozdeji", pIntent)
                .build();

        try {
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("customizedIcon");
            field.setAccessible(true);

            field.set(miuiNotification, true);
            field = mNotification.getClass().getField("extraNotification");
            field.setAccessible(true);

            field.set(mNotification, miuiNotification);
        } catch (Exception e) {
        }

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(0, mNotification);
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, 0);
        return pendingIntent;
    }



}


