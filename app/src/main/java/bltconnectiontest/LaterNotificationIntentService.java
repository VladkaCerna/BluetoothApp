package bltconnectiontest;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import java.lang.reflect.Field;


public class LaterNotificationIntentService extends IntentService {

    public LaterNotificationIntentService() {
        super("LaterNotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            showLaterNotification();
        }
    }

    public void showLaterNotification() {
        final int notificationId = 0;
        Intent serviceIntent = new Intent(this, LockIntentService.class);
        serviceIntent.putExtra("ID", notificationId);
        PendingIntent actionIntent = PendingIntent.getService(this, 0, serviceIntent, 0);
        PendingIntent deleteIntent = createOnDismissedIntentLaterBtn(this, notificationId);

        Notification mNotification  = new Notification.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name))
                .setSmallIcon(R.drawable.ic_action_name)
                .setContentTitle(getString(R.string.lock_desktop))
                .setContentText(getString(R.string.pull_see_options))
                .setContentIntent(actionIntent)
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

    private PendingIntent createOnDismissedIntentLaterBtn(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, 0);
        return pendingIntent;
    }


}
