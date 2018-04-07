package bltconnectiontest;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;


public class LockIntentService extends IntentService {

    public LockIntentService() {
        super("LockIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(intent.getIntExtra("ID", 0));
            Config config = new ConfigManager().getConfig(this);
            MessageManager messageManager = MessageManager.GetMananager(this);
            Message lockRequest = messageManager.createLockRequest(config.getPhoneId());
            messageManager.Send(lockRequest);

            Intent notificationIntent = new Intent(this, UnlockNotificationIntentService.class);
            this.startService(notificationIntent);
        }
    }
}
