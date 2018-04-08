package bltconnectiontest;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import java.lang.reflect.Field;

public class UnlockNotificationIntentService extends IntentService {

    public UnlockNotificationIntentService() {
        super("UnlockNotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            showUnlockNotification();
        }
    }

    public void showUnlockNotification() {
        final int notificationId = 1;
        Intent serviceIntent = new Intent(this, UnlockIntentService.class);
        serviceIntent.putExtra("ID", notificationId);
        PendingIntent actionIntent = PendingIntent.getService(this, 0, serviceIntent, 0);

        Intent thisIntent = new Intent(this, UnlockNotificationIntentService.class);
        PendingIntent deleteIntent = PendingIntent.getService(this, 0, thisIntent, 0);

        Notification mNotification = new Notification.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name))
                .setSmallIcon(R.drawable.ic_action_name)
                .setContentTitle(getString(R.string.unlock))
                .setDeleteIntent(deleteIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_action_name, getString(R.string.ano), actionIntent)
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

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(notificationId, mNotification);
    }
}
