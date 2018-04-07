package bltconnectiontest;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;


public class UnlockIntentService extends IntentService {

    public UnlockIntentService() {
        super("UnlockIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(intent.getIntExtra("ID", 1));
            Config config = new ConfigManager().getConfig(this);
            MessageManager messageManager = MessageManager.GetMananager(this);
            Message unlockRequest = messageManager.createUnlockRequest(config.getPhoneId());
            messageManager.Send(unlockRequest);
        }
    }
}