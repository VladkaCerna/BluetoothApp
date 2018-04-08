package bltconnectiontest;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class SensorService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long timeStamp;
    private int steps;
    private long TWO_SECONDS = 2000;

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        registerSensorListener();

        timeStamp = System.nanoTime() / 1500000;
        steps = 0;
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            long currentTime = System.nanoTime() / 1000000;
            if ((currentTime - timeStamp) < TWO_SECONDS) {
                steps++;
                if (steps > 1) {
                    steps = 0;
                    unregisterSensorListener();
                    Intent mIntent = new Intent(this, NotificationActivity.class);
                    startActivity(mIntent);
                    stopSelf();
                }
            } else {
                steps = 0;
            }
            timeStamp = currentTime;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterSensorListener();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
}


