package bltconnectiontest;

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
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Binder;
import android.os.IBinder;

import java.lang.reflect.Field;

public class SensorService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private TriggerEventListener mTriggerEventListener;
    private Sensor mSensor;
    private Sensor motionSensor;
    private final IBinder mBinder = new LocalBinder();
    private static final String NO_ACTION = "";
    private NotificationManager mNotifyMgr;
    private long timeStamp;
    private int steps;
    private long TWO_SECONDS = 2000;

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
        motionSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        mTriggerEventListener = new TriggerEventListener() {
            @Override
            public void onTrigger(TriggerEvent event) {
                //mSensorManager.requestTriggerSensor(mTriggerEventListener, motionSensor);
                //Intent mIntent = new Intent(SensorService.this, NotificationActivity.class);
                //startActivity(mIntent);
            }
        };
        mSensorManager.requestTriggerSensor(mTriggerEventListener, motionSensor);
        registerSensorListener();

        //Helpers.showToast(this, "Service turned on");
        timeStamp = System.nanoTime()/1500000;
        steps = 0;
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
        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            //if (sensorEvent.values[0] == 1) {
                long currentTime = System.nanoTime()/1000000;
                if ((currentTime - timeStamp) < TWO_SECONDS) {
                    steps++;
                    if (steps > 1)
                    {
                        steps = 0;
                        mSensorManager.unregisterListener(this);
                        Intent mIntent = new Intent(this, NotificationActivity.class);
                        startActivity(mIntent);
                    }
                } else {
                    steps = 0;
                }
                timeStamp = currentTime;
            //}
        } else if (sensor.getType() == Sensor.TYPE_SIGNIFICANT_MOTION) {

        } else {

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
                .setContentText("Potahnutim rozbal nabidku")
                //.setContentIntent(pIntent)
                .setDeleteIntent(dIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_action_name, getString(R.string.ano), pIntent)
                .addAction(R.drawable.ic_action_name, getString(R.string.ne), pIntent)
                .addAction(R.drawable.ic_action_name, getString(R.string.pozdeji), pIntent)
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


